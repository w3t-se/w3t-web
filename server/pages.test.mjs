import { test } from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import { headFor, renderPage, robotsTxt, sitemapXml, webinarEvent } from './pages.mjs';

const webinar = JSON.parse(fs.readFileSync(new URL('../webinar.json', import.meta.url), 'utf8'));
const index = fs.readFileSync(new URL('../index.html', import.meta.url), 'utf8');
const site = { siteUrl: 'https://w3t.se', webinar };

test('each page gets one title, one description and a canonical link', () => {
  for (const p of ['/', '/webinar', '/book']) {
    const { status, head } = headFor(p, site);
    const html = renderPage(index, head);
    assert.equal(status, 200);
    assert.equal(html.match(/<title>/g).length, 1, p);
    assert.equal(html.match(/<meta name="description"/g).length, 1, p);
    assert.match(html, new RegExp(`<link rel="canonical" href="https://w3t.se${p}">`));
  }
  assert.match(headFor('/webinar', site).head, /<title>Leaving VMware for bare metal Kubernetes \| W3T webinar<\/title>/);
});

test('an unknown path is a noindex 404 without a canonical', () => {
  const { status, head } = headFor('/blog/old-post', site);
  assert.equal(status, 404);
  assert.match(head, /noindex/);
  assert.doesNotMatch(head, /canonical/);
});

test('the webinar page carries Event data until the webinar has started', () => {
  assert.match(headFor('/webinar', site).head, /"@type":"Event"/);
  const past = { ...site, webinar: { ...webinar, start: '2020-01-01T10:00:00+01:00' } };
  assert.doesNotMatch(headFor('/webinar', past).head, /"@type":"Event"/);
  const e = webinarEvent(webinar, 'https://w3t.se');
  assert.equal(e.startDate, '2026-12-08T16:30:00+01:00');
  assert.equal(e.endDate, '2026-12-08T16:15:00.000Z');
  assert.equal(e.location.url, 'https://w3t.se/webinar');
});

test('structured data cannot close its script tag', () => {
  const evil = { ...site, webinar: { ...webinar, title: '</script><b>x' } };
  assert.doesNotMatch(headFor('/webinar', evil).head, /<\/script><b>/);
});

test('robots.txt points at the sitemap, which lists every page', () => {
  assert.match(robotsTxt('https://w3t.se'), /^Sitemap: https:\/\/w3t\.se\/sitemap\.xml$/m);
  const xml = sitemapXml('https://w3t.se', webinar);
  for (const p of ['/', '/webinar', '/book']) assert.ok(xml.includes(`<loc>https://w3t.se${p}</loc>`), p);
});
