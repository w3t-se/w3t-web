// Serves the built site (SPA fallback to index.html) and the webinar
// signup API. Configuration is environment variables; see README.md.
import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import sirv from 'sirv';
import { createMailer } from './mailer.mjs';
import {
  validateSignup, webinarInvite, confirmationEmail, notificationEmail, rateLimiter,
} from './signup.mjs';
import { headFor, renderPage, robotsTxt, sitemapXml } from './pages.mjs';

const here = path.dirname(fileURLToPath(import.meta.url));
const SITE_DIR = process.env.SITE_DIR || path.resolve(here, '../dist');
const WEBINAR_FILE = process.env.WEBINAR_FILE || path.resolve(here, '../webinar.json');
const SITE_URL = (process.env.SITE_URL || 'https://w3t.se').replace(/\/$/, '');
const NOTIFY_TO = process.env.NOTIFY_TO ?? 'info@w3t.se';
const PORT = Number(process.env.PORT || 8080);

const webinar = {
  ...JSON.parse(fs.readFileSync(WEBINAR_FILE, 'utf8')),
  joinUrl: process.env.WEBINAR_JOIN_URL || '',
};
const mailer = createMailer();
const allow = rateLimiter({ max: 5, windowMs: 10 * 60 * 1000 });

if (!mailer) console.warn('MAILGUN_API_KEY is not set: webinar signups answer 503');

// index.html as built; each route gets its own head from pages.mjs.
const indexHtml = fs.readFileSync(path.join(SITE_DIR, 'index.html'), 'utf8');

function page(res, pathname) {
  const { status, head } = headFor(pathname, { siteUrl: SITE_URL, webinar });
  res.writeHead(status, { 'Content-Type': 'text/html; charset=utf-8', 'Cache-Control': 'no-cache' });
  res.end(renderPage(indexHtml, head));
}

function text(res, type, body) {
  res.writeHead(200, { 'Content-Type': type, 'Cache-Control': 'public, max-age=3600' });
  res.end(body);
}

const assets = sirv(SITE_DIR, {
  // A path that is not a file is a page (or a 404), rendered by page().
  onNoMatch: (req, res) => page(res, new URL(req.url, 'http://localhost').pathname),
  etag: true,
  setHeaders(res, pathname) {
    res.setHeader(
      'Cache-Control',
      pathname.startsWith('/assets/') ? 'public, max-age=31536000, immutable' : 'no-cache',
    );
  },
});

function json(res, status, body) {
  res.writeHead(status, { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' });
  res.end(JSON.stringify(body));
}

function clientIp(req) {
  return req.headers['cf-connecting-ip']
    || String(req.headers['x-forwarded-for'] || '').split(',')[0].trim()
    || req.socket.remoteAddress;
}

async function readJson(req, limit = 16 * 1024) {
  let size = 0;
  const chunks = [];
  for await (const chunk of req) {
    size += chunk.length;
    if (size > limit) throw Object.assign(new Error('too large'), { status: 413 });
    chunks.push(chunk);
  }
  try {
    return JSON.parse(Buffer.concat(chunks).toString('utf8') || 'null');
  } catch {
    return null;
  }
}

async function signup(req, res) {
  if (!allow(clientIp(req))) {
    return json(res, 429, { error: 'Too many signups from your network. Try again in a few minutes.' });
  }
  if (new Date(webinar.start).getTime() < Date.now()) {
    return json(res, 410, { error: 'This webinar has already taken place. Email info@w3t.se for the recording.' });
  }
  if (!mailer) {
    return json(res, 503, { error: 'Signups are not open yet. Email info@w3t.se and we will add you.' });
  }

  let body;
  try {
    body = await readJson(req);
  } catch (err) {
    return json(res, err.status || 400, { error: 'The form was too large to send.' });
  }
  const result = validateSignup(body);
  if (!result.ok) return json(res, result.status, { error: result.error });
  if (result.bot) return json(res, 200, { ok: true });

  const s = result.signup;
  try {
    await mailer.addToList(webinar.mailingList, {
      address: s.email,
      name: s.name,
      vars: { company: s.company, role: s.role, src: s.src, webinar: webinar.id, signed_up_at: new Date().toISOString() },
    }, `Webinar: ${webinar.title}`);
    const mail = confirmationEmail(webinar, s, SITE_URL);
    await mailer.send({
      to: s.email,
      ...mail,
      attachments: [{ filename: 'w3t-webinar.ics', data: webinarInvite(webinar, SITE_URL) }],
    });
  } catch (err) {
    console.error('signup failed:', err?.status || '', err?.message || err);
    return json(res, 502, { error: 'We could not register you just now. Try again, or email info@w3t.se.' });
  }

  if (NOTIFY_TO) {
    mailer.send({ to: NOTIFY_TO, ...notificationEmail(webinar, s) })
      .catch((err) => console.error('notification failed:', err?.message || err));
  }
  return json(res, 200, { ok: true });
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');
  if (url.pathname === '/healthz') {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    return res.end('ok\n');
  }
  if (url.pathname === '/api/webinar' && req.method === 'GET') {
    const { mailingList, joinUrl, ...pub } = webinar;
    return json(res, 200, { ...pub, signupOpen: Boolean(mailer) });
  }
  if (url.pathname === '/api/webinar/signup') {
    if (req.method !== 'POST') return json(res, 405, { error: 'Use POST.' });
    return signup(req, res).catch((err) => {
      console.error(err);
      json(res, 500, { error: 'Something failed on our side. Email info@w3t.se and we will add you.' });
    });
  }
  if (url.pathname.startsWith('/api/')) return json(res, 404, { error: 'Not found.' });
  if (url.pathname === '/robots.txt') return text(res, 'text/plain; charset=utf-8', robotsTxt(SITE_URL));
  if (url.pathname === '/sitemap.xml') return text(res, 'application/xml; charset=utf-8', sitemapXml(SITE_URL, webinar));
  // One URL per page: /webinar/ and /webinar are the same page to a crawler.
  if (url.pathname.length > 1 && url.pathname.endsWith('/')) {
    res.writeHead(301, { Location: url.pathname.replace(/\/+$/, '') + url.search });
    return res.end();
  }
  // Pages are rendered here, not by sirv, which would serve index.html's
  // generic head for "/" and "/index.html".
  if (url.pathname === '/index.html') { res.writeHead(301, { Location: '/' }); return res.end(); }
  if (url.pathname === '/') return page(res, '/');
  return assets(req, res);
});

server.listen(PORT, () => console.log(`w3t-web on :${PORT}, site ${SITE_DIR}`));
for (const sig of ['SIGTERM', 'SIGINT']) {
  process.on(sig, () => server.close(() => process.exit(0)));
}
