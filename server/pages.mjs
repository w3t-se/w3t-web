// What search engines see: per-page <head> tags, robots.txt and the
// sitemap. The site renders in the browser, so the server fills in the
// head of index.html for each route; everything else stays client-side.

export function pages(webinar) {
  return {
    '/': {
      title: 'W3T | Your Kubernetes, DevOps and operations partner',
      description: 'W3T is your Kubernetes, DevOps and operations partner in Sweden: bare metal Kubernetes, VMware exits and platform engineering.',
    },
    '/webinar': {
      title: `${webinar.title} | W3T webinar`,
      description: webinar.summary,
    },
    '/book': {
      title: 'Book a call | W3T',
      description: 'Book a 30-minute call with W3T about Kubernetes, VMware exits or platform engineering.',
    },
  };
}

const escapeHtml = (s) => String(s)
  .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');

// JSON inside <script> must not contain "</script>".
const jsonLd = (obj) => `<script type="application/ld+json">${JSON.stringify(obj).replace(/</g, '\\u003c')}</script>`;

export function organization(siteUrl) {
  return {
    '@context': 'https://schema.org',
    '@type': 'Organization',
    name: 'W3T',
    legalName: 'W3T AB',
    url: `${siteUrl}/`,
    logo: `${siteUrl}/images/w3t-white.png`,
    email: 'info@w3t.se',
  };
}

export function webinarEvent(webinar, siteUrl) {
  const start = new Date(webinar.start);
  const end = new Date(start.getTime() + webinar.durationMinutes * 60_000);
  return {
    '@context': 'https://schema.org',
    '@type': 'Event',
    name: webinar.title,
    description: webinar.summary,
    // Keep the source's +01:00 offset: Google shows the time as written.
    startDate: webinar.start,
    endDate: end.toISOString(),
    eventStatus: 'https://schema.org/EventScheduled',
    eventAttendanceMode: 'https://schema.org/OnlineEventAttendanceMode',
    location: { '@type': 'VirtualLocation', url: `${siteUrl}/webinar` },
    organizer: { '@type': 'Organization', name: 'W3T', url: `${siteUrl}/` },
    isAccessibleForFree: true,
    offers: {
      '@type': 'Offer', price: 0, priceCurrency: 'SEK', availability: 'https://schema.org/InStock',
      url: `${siteUrl}/webinar`, validFrom: '2026-10-01T00:00:00+02:00',
    },
  };
}

// The head tags for one path. `null` page means a 404: noindex, no canonical.
export function headFor(path, { siteUrl, webinar }) {
  const page = pages(webinar)[path];
  if (!page) {
    return { status: 404, head: '<title>Page not found | W3T</title>\n<meta name="robots" content="noindex">' };
  }
  const url = `${siteUrl}${path}`;
  const tags = [
    `<title>${escapeHtml(page.title)}</title>`,
    `<meta name="description" content="${escapeHtml(page.description)}">`,
    `<link rel="canonical" href="${url}">`,
    `<meta property="og:type" content="website">`,
    `<meta property="og:site_name" content="W3T">`,
    `<meta property="og:url" content="${url}">`,
    `<meta property="og:title" content="${escapeHtml(page.title)}">`,
    `<meta property="og:description" content="${escapeHtml(page.description)}">`,
    `<meta name="twitter:card" content="summary">`,
  ];
  if (path === '/') tags.push(jsonLd(organization(siteUrl)));
  if (path === '/webinar' && new Date(webinar.start).getTime() > Date.now()) {
    tags.push(jsonLd(webinarEvent(webinar, siteUrl)));
  }
  return { status: 200, head: tags.join('\n') };
}

// Replace index.html's own <title> and description with the page's tags.
export function renderPage(indexHtml, head) {
  return indexHtml
    .replace(/<title>[\s\S]*?<\/title>\s*/, '')
    .replace(/<meta name="description"[^>]*>\s*/, '')
    .replace('</head>', `${head}\n</head>`);
}

export function robotsTxt(siteUrl) {
  return `User-agent: *\nAllow: /\nDisallow: /api/\n\nSitemap: ${siteUrl}/sitemap.xml\n`;
}

export function sitemapXml(siteUrl, webinar) {
  const urls = Object.keys(pages(webinar))
    .map((p) => `  <url><loc>${siteUrl}${p}</loc></url>`).join('\n');
  return `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n${urls}\n</urlset>\n`;
}
