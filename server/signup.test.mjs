import { test } from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import {
  validateSignup, webinarInvite, confirmationEmail, formatStart, rateLimiter, joinLinkEmail,
  reminderMessage, reminderDue, REMINDER_TEMPLATE_HTML,
} from './signup.mjs';

const webinar = JSON.parse(fs.readFileSync(new URL('../webinar.json', import.meta.url), 'utf8'));
const good = { name: ' Ada Lovelace ', email: 'Ada@Example.com', company: 'Analytical', consent: true };

test('accepts a complete signup and normalises it', () => {
  const r = validateSignup(good);
  assert.equal(r.ok, true);
  assert.deepEqual(r.signup, { name: 'Ada Lovelace', email: 'ada@example.com', company: 'Analytical', role: '', src: '' });
});

test('refuses missing name, bad email and missing consent with a readable error', () => {
  assert.equal(validateSignup({ ...good, name: '  ' }).error, 'Enter your name.');
  assert.equal(validateSignup({ ...good, email: 'ada@' }).error, 'Enter a valid email address.');
  assert.match(validateSignup({ ...good, consent: 'yes' }).error, /Tick the box/);
  assert.equal(validateSignup(null).status, 400);
});

test('a filled honeypot looks like success and carries no signup', () => {
  const r = validateSignup({ ...good, website: 'http://spam' });
  assert.deepEqual(r, { ok: true, bot: true });
});

test('the invite starts at 14:00 UTC on 8 December 2026 and lasts 45 minutes', () => {
  const ics = webinarInvite(webinar, 'https://w3t.se');
  assert.match(ics, /DTSTART:20261208T140000Z/);
  assert.match(ics, /DURATION:PT45M/);
  assert.match(ics, /UID:bare-metal-k8s-2026-12@w3t.se/);
});

test('the start reads in Stockholm time', () => {
  assert.equal(formatStart(webinar), 'Tuesday, 8 December 2026, 15:00');
});

test('the confirmation escapes the name in html', () => {
  const m = confirmationEmail(webinar, { name: '<b>Eve</b> X', email: 'e@x.io' }, 'https://w3t.se');
  assert.match(m.html, /&lt;b&gt;Eve&lt;\/b&gt;/);
  assert.match(m.text, /Hi <b>Eve<\/b>,/);
});

test('the limiter allows max hits per window, then resets', () => {
  let t = 0;
  const allow = rateLimiter({ max: 2, windowMs: 1000, now: () => t });
  assert.deepEqual([allow('a'), allow('a'), allow('a'), allow('b')], [true, true, false, true]);
  t = 1000;
  assert.equal(allow('a'), true);
});

const meet = 'https://meet.google.com/abc-defg-hij';

test('before a join link is set, the emails and invite say it follows', () => {
  const m = confirmationEmail(webinar, { name: 'Ada', email: 'a@b.co' }, 'https://w3t.se');
  assert.match(m.text, /We'll email you the join link/);
  assert.match(webinarInvite(webinar, 'https://w3t.se'), /LOCATION:Online. The join link follows by email./);
  assert.throws(() => joinLinkEmail(webinar, 'https://w3t.se'), /no join link/);
});

test('with a join link, the confirmation, invite and list email carry it', () => {
  const w = { ...webinar, joinUrl: meet };
  const m = confirmationEmail(w, { name: 'Ada', email: 'a@b.co' }, 'https://w3t.se');
  assert.match(m.text, /Where: online, https:\/\/meet\.google\.com\/abc-defg-hij/);
  assert.match(m.html, /href="https:\/\/meet\.google\.com\/abc-defg-hij"/);
  assert.match(webinarInvite(w, 'https://w3t.se'), /LOCATION:https:\/\/meet\.google\.com\/abc-defg-hij/);
  const j = joinLinkEmail(w, 'https://w3t.se');
  assert.match(j.subject, /^Your join link: /);
  assert.match(j.text, /Join: https:\/\/meet\.google\.com\/abc-defg-hij/);
  assert.match(j.text, /%mailing_list_unsubscribe_url%/);
});

const at = (iso) => new Date(iso).getTime();

test('the week reminder is due 6 to 8 days before, the day reminder 12 to 36 hours before', () => {
  // Start: 2026-12-08 15:00 Stockholm (14:00 UTC).
  assert.equal(reminderDue(webinar, 'week', at('2026-12-01T09:00:00Z')), true);
  assert.equal(reminderDue(webinar, 'week', at('2026-12-05T09:00:00Z')), false);
  assert.equal(reminderDue(webinar, 'day', at('2026-12-07T09:00:00Z')), true);
  assert.equal(reminderDue(webinar, 'day', at('2026-12-08T09:00:00Z')), false);
  // The CronJob fires again a year later: nothing is due.
  assert.equal(reminderDue(webinar, 'week', at('2027-12-01T09:00:00Z')), false);
  assert.throws(() => reminderDue(webinar, 'month'), /unknown reminder/);
});

test('reminder variables carry the join link and agenda, and match the template', () => {
  const r = reminderMessage({ ...webinar, joinUrl: meet }, 'day', 'https://w3t.se');
  assert.match(r.subject, /^Tomorrow: Leaving VMware/);
  assert.equal(r.variables.join_url, meet);
  assert.equal(r.variables.agenda.length, 4);
  for (const name of Object.keys(r.variables)) {
    assert.ok(REMINDER_TEMPLATE_HTML.includes(`{{${name}}}`) || REMINDER_TEMPLATE_HTML.includes(`{{#each ${name}}}`), name);
  }
  assert.throws(() => reminderMessage(webinar, 'week', 'https://w3t.se'), /no join link/);
});

test('the source tag is kept when it is a short tag, dropped otherwise', () => {
  assert.equal(validateSignup({ ...good, src: 'li-ads-a-cost' }).signup.src, 'li-ads-a-cost');
  assert.equal(validateSignup({ ...good, src: 'Mattias-Email' }).signup.src, 'mattias-email');
  assert.equal(validateSignup({ ...good, src: '<script>' }).signup.src, '');
  assert.equal(validateSignup({ ...good, src: 'x'.repeat(41) }).signup.src, '');
});
