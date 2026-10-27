// Sends a webinar reminder to the Mailgun list, through the stored
// Mailgun template "webinar-reminder" (created or updated first, so the
// dashboard shows what is sent). Run by the chart's CronJobs one week and
// one day before, or by hand in the pod:
//
//   kubectl -n w3t-web exec deploy/w3t-web -- node server/send-reminder.mjs week --test you@w3t.se
//   kubectl -n w3t-web exec deploy/w3t-web -- node server/send-reminder.mjs day --yes
//
// week|day        which reminder.
// --test ADDRESS  sends one preview to ADDRESS, whatever the date.
// --yes           sends to the whole list, if the reminder is due now
//                 (week: 6-8 days before, day: 12-36 hours before).
// --force         with --yes, sends even when it is not due.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { createMailer } from './mailer.mjs';
import {
  REMINDER_TEMPLATE, REMINDER_TEMPLATE_HTML, reminderMessage, reminderDue,
} from './signup.mjs';

const here = path.dirname(fileURLToPath(import.meta.url));
const WEBINAR_FILE = process.env.WEBINAR_FILE || path.resolve(here, '../webinar.json');
const SITE_URL = (process.env.SITE_URL || 'https://w3t.se').replace(/\/$/, '');

function fail(msg) {
  console.error(`send-reminder: ${msg}`);
  process.exit(1);
}

const args = process.argv.slice(2);
const kind = args[0];
if (kind !== 'week' && kind !== 'day') fail('say which reminder: week or day');
const testIdx = args.indexOf('--test');
const test = testIdx >= 0 ? args[testIdx + 1] : null;
const yes = args.includes('--yes');
const force = args.includes('--force');
if (testIdx >= 0 && !test) fail('--test needs an address');
if (!test && !yes) fail('nothing sent: add --test ADDRESS for a preview, or --yes to send to the list');

const webinar = {
  ...JSON.parse(fs.readFileSync(WEBINAR_FILE, 'utf8')),
  joinUrl: process.env.WEBINAR_JOIN_URL || '',
};

if (!test && !force && !reminderDue(webinar, kind)) {
  console.log(`the ${kind} reminder is not due now (webinar starts ${webinar.start}); nothing sent`);
  process.exit(0);
}

let message;
try {
  message = reminderMessage(webinar, kind, SITE_URL);
} catch (err) {
  fail(err.message);
}
const mailer = createMailer();
if (!mailer) fail('MAILGUN_API_KEY is not set');

const to = test || webinar.mailingList;
try {
  const state = await mailer.ensureTemplate(REMINDER_TEMPLATE, {
    description: 'Webinar reminder (week and day before), managed by w3t-web',
    html: REMINDER_TEMPLATE_HTML,
  });
  if (state) console.log(`template ${REMINDER_TEMPLATE}: ${state}`);
  await mailer.sendTemplate({ to, template: REMINDER_TEMPLATE, ...message });
} catch (err) {
  fail(`Mailgun refused it: ${err?.status || ''} ${err?.message || err}`);
}
console.log(`sent the ${kind} reminder to ${to}`);
