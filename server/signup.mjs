// The webinar signup, as pure functions: validating a request, and the
// confirmation email and calendar invite it produces. No I/O here, so it
// is tested directly (signup.test.mjs).
import { createEvent } from 'ics';

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

function text(v, max) {
  return typeof v === 'string' ? v.trim().slice(0, max) : '';
}

/**
 * Checks a signup body. Returns { ok: true, signup } or
 * { ok: false, status, error } with an error the form shows as is.
 * A filled honeypot (`website`) returns { ok: true, bot: true } so bots
 * see success and nothing is stored or sent.
 */
export function validateSignup(body) {
  if (!body || typeof body !== 'object') {
    return { ok: false, status: 400, error: 'Send the form as JSON.' };
  }
  if (text(body.website, 200)) return { ok: true, bot: true };

  const name = text(body.name, 120);
  const email = text(body.email, 254).toLowerCase();
  const company = text(body.company, 120);
  const role = text(body.role, 120);

  if (!name) return { ok: false, status: 400, error: 'Enter your name.' };
  if (!EMAIL.test(email)) return { ok: false, status: 400, error: 'Enter a valid email address.' };
  if (body.consent !== true) {
    return { ok: false, status: 400, error: 'Tick the box so we can email you the link.' };
  }
  return { ok: true, signup: { name, email, company, role } };
}

function utcParts(date) {
  return [
    date.getUTCFullYear(),
    date.getUTCMonth() + 1,
    date.getUTCDate(),
    date.getUTCHours(),
    date.getUTCMinutes(),
  ];
}

/**
 * Where to join, or null before it is set. The server puts it on the
 * webinar from WEBINAR_JOIN_URL (chart: app.webinarJoinUrl), never from
 * webinar.json: that file is bundled into the public site.
 */
export function joinUrl(webinar) {
  const url = typeof webinar.joinUrl === 'string' ? webinar.joinUrl.trim() : '';
  return url || null;
}

/** The calendar invite (.ics text) for the webinar. */
export function webinarInvite(webinar, siteUrl) {
  const join = joinUrl(webinar);
  const { error, value } = createEvent({
    uid: `${webinar.id}@w3t.se`,
    title: `W3T webinar: ${webinar.title}`,
    description: join
      ? `${webinar.summary}\n\nJoin: ${join}\n${siteUrl}/webinar`
      : `${webinar.summary}\n\nThe join link follows by email before the webinar.\n${siteUrl}/webinar`,
    location: join || 'Online. The join link follows by email.',
    url: join || `${siteUrl}/webinar`,
    start: utcParts(new Date(webinar.start)),
    startInputType: 'utc',
    startOutputType: 'utc',
    duration: { minutes: webinar.durationMinutes },
    organizer: { name: 'W3T', email: 'info@w3t.se' },
    status: 'CONFIRMED',
    busyStatus: 'BUSY',
  });
  if (error) throw error;
  return value;
}

/** "Tuesday, 8 December 2026, 15:00" in Stockholm time. */
export function formatStart(webinar) {
  const d = new Date(webinar.start);
  const day = d.toLocaleDateString('en-GB', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric', timeZone: 'Europe/Stockholm',
  });
  const time = d.toLocaleTimeString('en-GB', {
    hour: '2-digit', minute: '2-digit', timeZone: 'Europe/Stockholm',
  });
  return `${day}, ${time}`;
}

function escapeHtml(s) {
  return s.replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

/** The confirmation email: subject, text and html. */
export function confirmationEmail(webinar, signup, siteUrl) {
  const when = formatStart(webinar);
  const first = signup.name.split(/\s+/)[0];
  const agenda = webinar.agenda.map(([t, d]) => `  ${t}  ${d}`).join('\n');
  const join = joinUrl(webinar);
  const where = join ? `online, ${join}` : "online. We'll email you the join link before we start.";
  const subject = `You're registered: ${webinar.title}`;
  const textBody = `Hi ${first},

You're registered for the W3T webinar "${webinar.title}".

When: ${when} (Stockholm time), ${webinar.durationMinutes} minutes
Where: ${where}

The calendar invite is attached.

Agenda
${agenda}

If you can't make it, reply to this email and we'll send you the recording.

W3T
${siteUrl}
`;
  const html = `<p>Hi ${escapeHtml(first)},</p>
<p>You're registered for the W3T webinar <strong>${escapeHtml(webinar.title)}</strong>.</p>
<p><strong>When:</strong> ${escapeHtml(when)} (Stockholm time), ${webinar.durationMinutes} minutes<br>
<strong>Where:</strong> ${join ? `online, <a href="${escapeHtml(join)}">${escapeHtml(join)}</a>` : "online. We'll email you the join link before we start."}</p>
<p>The calendar invite is attached.</p>
<p><strong>Agenda</strong></p>
<ul>${webinar.agenda.map(([t, d]) => `<li>${escapeHtml(t)}: ${escapeHtml(d)}</li>`).join('')}</ul>
<p>If you can't make it, reply to this email and we'll send you the recording.</p>
<p>W3T<br><a href="${siteUrl}">${siteUrl.replace(/^https?:\/\//, '')}</a></p>`;
  return { subject, text: textBody, html };
}

/**
 * The email with the join link, sent to the whole mailing list by
 * send-join-link.mjs. Mailgun fills in the %...% variables per member.
 */
export function joinLinkEmail(webinar, siteUrl) {
  const join = joinUrl(webinar);
  if (!join) throw new Error('no join link: set app.webinarJoinUrl in the chart (WEBINAR_JOIN_URL)');
  const when = formatStart(webinar);
  const subject = `Your join link: ${webinar.title}, ${when}`;
  const textBody = `Hi,

Here is your link for the W3T webinar "${webinar.title}".

When: ${when} (Stockholm time), ${webinar.durationMinutes} minutes
Join: ${join}

Join from your browser a few minutes early; you don't need to install anything.
If you can't make it, reply to this email and we'll send you the recording.

W3T
${siteUrl}

You get this because you registered for the webinar. Unsubscribe: %mailing_list_unsubscribe_url%
`;
  const html = `<p>Hi,</p>
<p>Here is your link for the W3T webinar <strong>${escapeHtml(webinar.title)}</strong>.</p>
<p><strong>When:</strong> ${escapeHtml(when)} (Stockholm time), ${webinar.durationMinutes} minutes<br>
<strong>Join:</strong> <a href="${escapeHtml(join)}">${escapeHtml(join)}</a></p>
<p>Join from your browser a few minutes early; you don't need to install anything.
If you can't make it, reply to this email and we'll send you the recording.</p>
<p>W3T<br><a href="${siteUrl}">${siteUrl.replace(/^https?:\/\//, '')}</a></p>
<p style="color:#888;font-size:12px">You get this because you registered for the webinar.
<a href="%mailing_list_unsubscribe_url%">Unsubscribe</a></p>`;
  return { subject, text: textBody, html };
}

/** The short note W3T gets for each signup. */
export function notificationEmail(webinar, signup) {
  return {
    subject: `Webinar signup: ${signup.name}${signup.company ? ` (${signup.company})` : ''}`,
    text: `${signup.name} <${signup.email}>
Company: ${signup.company || '-'}
Role: ${signup.role || '-'}
Webinar: ${webinar.title}, ${formatStart(webinar)}
List: ${webinar.mailingList}
`,
  };
}

/** A fixed-window limiter: at most `max` hits per key per `windowMs`. */
export function rateLimiter({ max, windowMs, now = () => Date.now() }) {
  const hits = new Map();
  return (key) => {
    const t = now();
    const entry = hits.get(key);
    if (!entry || t - entry.start >= windowMs) {
      hits.set(key, { start: t, count: 1 });
      if (hits.size > 10000) {
        for (const [k, v] of hits) if (t - v.start >= windowMs) hits.delete(k);
      }
      return true;
    }
    entry.count += 1;
    return entry.count <= max;
  };
}
