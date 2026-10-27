import { test } from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import {
  validateSignup, webinarInvite, confirmationEmail, formatStart, rateLimiter,
} from './signup.mjs';

const webinar = JSON.parse(fs.readFileSync(new URL('../webinar.json', import.meta.url), 'utf8'));
const good = { name: ' Ada Lovelace ', email: 'Ada@Example.com', company: 'Analytical', consent: true };

test('accepts a complete signup and normalises it', () => {
  const r = validateSignup(good);
  assert.equal(r.ok, true);
  assert.deepEqual(r.signup, { name: 'Ada Lovelace', email: 'ada@example.com', company: 'Analytical', role: '' });
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
