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

// --- Email layout ----------------------------------------------------------

/** Content id of the logo, which the mailer attaches inline. */
export const LOGO_CID = 'w3t-logo.png';

const P = 'margin:0 0 14px';

/**
 * Wraps an email body in the W3T layout: a black band with the logo, a
 * white card, a footer. Tables and inline styles only, since that is what
 * Gmail and Outlook render. The logo is `cid:` so it shows without the
 * reader allowing remote images.
 */
export function emailLayout(content, { footer = '' } = {}) {
  return `<!doctype html>
<html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="color-scheme" content="light only"></head>
<body style="margin:0;padding:0;background:#f2f2f4">
<table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="background:#f2f2f4">
<tr><td align="center" style="padding:24px 12px">
<table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="max-width:560px">
<tr><td bgcolor="#000000" style="background:#000000;padding:22px 28px;border-radius:8px 8px 0 0">
<a href="https://w3t.se"><img src="cid:${LOGO_CID}" width="84" height="25" alt="W3T" style="display:block;border:0;width:84px;height:25px"></a>
</td></tr>
<tr><td bgcolor="#ffffff" style="background:#ffffff;padding:28px;border-radius:0 0 8px 8px;font-family:-apple-system,'Segoe UI',Helvetica,Arial,sans-serif;font-size:15px;line-height:1.55;color:#1a1a1a">
${content}
</td></tr>
<tr><td style="padding:16px 28px;font-family:-apple-system,'Segoe UI',Helvetica,Arial,sans-serif;font-size:12px;line-height:1.5;color:#7a7a85">
W3T AB · <a href="https://w3t.se" style="color:#7a7a85">w3t.se</a> · <a href="mailto:info@w3t.se" style="color:#7a7a85">info@w3t.se</a>${footer ? `<br>${footer}` : ''}
</td></tr>
</table>
</td></tr>
</table>
</body></html>`;
}

/** The blue call-to-action button, as email clients render buttons. */
function button(href, label) {
  return `<table role="presentation" cellspacing="0" cellpadding="0" border="0" style="margin:6px 0 20px"><tr><td bgcolor="#2f6bff" style="border-radius:4px"><a href="${href}" style="display:inline-block;padding:12px 20px;font-family:-apple-system,'Segoe UI',Helvetica,Arial,sans-serif;font-size:15px;font-weight:600;color:#ffffff;text-decoration:none">${label}</a></td></tr></table>`;
}

const UNSUBSCRIBE = 'You get this because you registered for the webinar. <a href="%mailing_list_unsubscribe_url%" style="color:#7a7a85">Unsubscribe</a>';

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
  const html = emailLayout(`<p style="${P}">Hi ${escapeHtml(first)},</p>
<p style="${P}">You're registered for the W3T webinar <strong>${escapeHtml(webinar.title)}</strong>.</p>
<p style="${P}"><strong>When:</strong> ${escapeHtml(when)} (Stockholm time), ${webinar.durationMinutes} minutes<br>
<strong>Where:</strong> ${join ? `online, <a href="${escapeHtml(join)}" style="color:#2f6bff">${escapeHtml(join)}</a>` : "online. We'll email you the join link before we start."}</p>
${join ? button(escapeHtml(join), 'Join with Google Meet') : ''}<p style="${P}">The calendar invite is attached.</p>
<p style="margin:0 0 6px"><strong>Agenda</strong></p>
<ul style="margin:0 0 14px;padding-left:20px">${webinar.agenda.map(([t, d]) => `<li>${escapeHtml(t)}: ${escapeHtml(d)}</li>`).join('')}</ul>
<p style="margin:0">If you can't make it, reply to this email and we'll send you the recording.</p>`);
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
  const html = emailLayout(`<p style="${P}">Hi,</p>
<p style="${P}">Here is your link for the W3T webinar <strong>${escapeHtml(webinar.title)}</strong>.</p>
<p style="${P}"><strong>When:</strong> ${escapeHtml(when)} (Stockholm time), ${webinar.durationMinutes} minutes<br>
<strong>Join:</strong> <a href="${escapeHtml(join)}" style="color:#2f6bff">${escapeHtml(join)}</a></p>
${button(escapeHtml(join), 'Join with Google Meet')}<p style="${P}">Join from your browser a few minutes early; you don't need to install anything.</p>
<p style="margin:0">If you can't make it, reply to this email and we'll send you the recording.</p>`, { footer: UNSUBSCRIBE });
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

// --- Reminders -----------------------------------------------------------

/** The stored Mailgun template both reminders use. */
export const REMINDER_TEMPLATE = 'webinar-reminder';

/**
 * The template's HTML (Handlebars). Variables: lead ("is next week" /
 * "is tomorrow"), title, when, duration, join_url, page_url, agenda (a
 * list of {time, topic}). Mailgun fills %mailing_list_unsubscribe_url%
 * per member, which works only when sent to the list.
 */
export const REMINDER_TEMPLATE_HTML = emailLayout(`<p style="${P}">Hi,</p>
<p style="${P}">The W3T webinar <strong>{{title}}</strong> {{lead}}.</p>
<p style="${P}"><strong>When:</strong> {{when}} (Stockholm time), {{duration}} minutes<br>
<strong>Join:</strong> <a href="{{join_url}}" style="color:#2f6bff">{{join_url}}</a></p>
${button('{{join_url}}', 'Join with Google Meet')}<p style="margin:0 0 6px"><strong>Agenda</strong></p>
<ul style="margin:0 0 14px;padding-left:20px">{{#each agenda}}<li>{{this.time}}: {{this.topic}}</li>{{/each}}</ul>
<p style="${P}">Join from your browser a few minutes early; you don't need to install anything. Microphones are off for attendees, so ask your questions in the chat.</p>
<p style="margin:0">Can't make it? Reply to this email and we'll send you the recording. More at <a href="{{page_url}}" style="color:#2f6bff">{{page_url}}</a>.</p>`, { footer: UNSUBSCRIBE });

const REMINDERS = {
  week: { lead: 'is next week', subject: 'Next week', before: 7 * 24, window: [6 * 24, 8 * 24] },
  day: { lead: 'is tomorrow', subject: 'Tomorrow', before: 24, window: [12, 36] },
};

/** Subject and template variables for reminder `kind` ("week" or "day"). */
export function reminderMessage(webinar, kind, siteUrl) {
  const r = REMINDERS[kind];
  if (!r) throw new Error(`unknown reminder "${kind}": use week or day`);
  const join = joinUrl(webinar);
  if (!join) throw new Error('no join link: set app.webinarJoinUrl in the chart (WEBINAR_JOIN_URL)');
  const when = formatStart(webinar);
  return {
    subject: `${r.subject}: ${webinar.title}, ${when}`,
    variables: {
      lead: r.lead,
      title: webinar.title,
      when,
      duration: webinar.durationMinutes,
      join_url: join,
      page_url: `${siteUrl}/webinar`,
      agenda: webinar.agenda.map(([time, topic]) => ({ time, topic })),
    },
  };
}

/**
 * Whether reminder `kind` is due at `now`: within its window before the
 * start (week: 6 to 8 days, day: 12 to 36 hours). A scheduled run outside
 * it (a CronJob firing again next year) sends nothing.
 */
export function reminderDue(webinar, kind, now = Date.now()) {
  const r = REMINDERS[kind];
  if (!r) throw new Error(`unknown reminder "${kind}": use week or day`);
  const hours = (new Date(webinar.start).getTime() - now) / 3_600_000;
  return hours >= r.window[0] && hours <= r.window[1];
}
