// Emails the webinar's join link to everyone on its Mailgun list.
// Run it in the w3t-web pod, which has the Mailgun key and the link:
//
//   kubectl -n w3t-web exec deploy/w3t-web -- node server/send-join-link.mjs --test you@w3t.se
//   kubectl -n w3t-web exec deploy/w3t-web -- node server/send-join-link.mjs --yes
//
// --test ADDRESS  sends one preview to ADDRESS (the unsubscribe link only
//                 works in the real send).
// --yes           sends to the whole list. Without it, nothing is sent.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { createMailer } from './mailer.mjs';
import { joinLinkEmail, joinUrl } from './signup.mjs';

const here = path.dirname(fileURLToPath(import.meta.url));
const WEBINAR_FILE = process.env.WEBINAR_FILE || path.resolve(here, '../webinar.json');
const SITE_URL = (process.env.SITE_URL || 'https://w3t.se').replace(/\/$/, '');

function fail(msg) {
  console.error(`send-join-link: ${msg}`);
  process.exit(1);
}

const args = process.argv.slice(2);
const testIdx = args.indexOf('--test');
const test = testIdx >= 0 ? args[testIdx + 1] : null;
const yes = args.includes('--yes');
if (testIdx >= 0 && !test) fail('--test needs an address');
if (!test && !yes) fail('nothing sent: add --test ADDRESS for a preview, or --yes to send to the list');

const webinar = {
  ...JSON.parse(fs.readFileSync(WEBINAR_FILE, 'utf8')),
  joinUrl: process.env.WEBINAR_JOIN_URL || '',
};
if (!joinUrl(webinar)) fail('no join link: set app.webinarJoinUrl in the chart (WEBINAR_JOIN_URL)');

const mailer = createMailer();
if (!mailer) fail('MAILGUN_API_KEY is not set');

const to = test || webinar.mailingList;
try {
  await mailer.send({ to, ...joinLinkEmail(webinar, SITE_URL) });
} catch (err) {
  fail(`Mailgun refused the send: ${err?.status || ''} ${err?.message || err}`);
}
console.log(`sent the join link (${joinUrl(webinar)}) to ${to}`);
