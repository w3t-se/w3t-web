// Guards the chart values the server depends on. A YAML slip here once
// left the join link null, so every deploy shipped without it and the
// reminder CronJobs were never created.
import { test } from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';

const values = fs.readFileSync(new URL('../chart/w3t-web/values.yaml', import.meta.url), 'utf8');

function value(key) {
  const m = values.match(new RegExp(`^\\s+${key}:(.*)$`, 'm'));
  return m ? m[1].replace(/#.*/, '').trim().replace(/^"(.*)"$/, '$1') : undefined;
}

test('the webinar join link is set to a Google Meet URL', () => {
  assert.match(value('webinarJoinUrl') ?? '', /^https:\/\/meet\.google\.com\/[a-z]{3}-[a-z]{4}-[a-z]{3}$/);
});

test('both reminders have a schedule', () => {
  assert.match(value('week') ?? '', /^\S+ \S+ \S+ \S+ \S+$/);
  assert.match(value('day') ?? '', /^\S+ \S+ \S+ \S+ \S+$/);
});
