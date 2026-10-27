// Mailgun, through its official client. One place that talks to it, so
// index.mjs and the tests can swap in a fake.
import fs from 'node:fs';
import Mailgun from 'mailgun.js';
import { LOGO_CID } from './signup.mjs';

// The logo every layout email shows, attached inline as cid:w3t-logo.png.
const LOGO = fs.readFileSync(new URL('./assets/w3t-logo.png', import.meta.url));
const logoInline = () => ({ inline: [{ filename: LOGO_CID, data: LOGO }] });

/**
 * A mailer for `domain`, or null when MAILGUN_API_KEY is not set (the
 * signup endpoint then answers 503 instead of pretending to register).
 * MAILGUN_DRY_RUN=1 logs instead of calling Mailgun, for local runs.
 */
export function createMailer(env = process.env) {
  const domain = env.MAILGUN_DOMAIN || 'mg.w3t.se';
  const from = env.MAILGUN_FROM || `W3T <webinar@${domain}>`;
  const replyTo = env.MAILGUN_REPLY_TO || 'info@w3t.se';

  if (env.MAILGUN_DRY_RUN === '1') {
    const sent = [];
    return {
      sent,
      async addToList(list, member) { sent.push({ kind: 'list', list, member }); },
      async send(msg) { sent.push({ kind: 'message', from, replyTo, ...msg }); console.log(`[dry run] mail to ${msg.to}: ${msg.subject}`); },
      async ensureTemplate(name) { console.log(`[dry run] template ${name} up to date`); },
      async sendTemplate(msg) { sent.push({ kind: 'template', ...msg }); console.log(`[dry run] template ${msg.template} to ${msg.to}: ${msg.subject} ${JSON.stringify(msg.variables)}`); },
    };
  }
  if (!env.MAILGUN_API_KEY) return null;

  const mg = new Mailgun(FormData).client({
    username: 'api',
    key: env.MAILGUN_API_KEY,
    url: env.MAILGUN_URL || 'https://api.eu.mailgun.net',
  });

  const knownLists = new Set();
  async function ensureList(address, name) {
    if (knownLists.has(address)) return;
    try {
      await mg.lists.get(address);
    } catch (err) {
      if (err?.status !== 404) throw err;
      await mg.lists.create({ address, name, access_level: 'readonly' });
    }
    knownLists.add(address);
  }

  return {
    async addToList(list, member, listName) {
      await ensureList(list, listName || list);
      await mg.lists.members.createMember(list, { ...member, subscribed: 'yes', upsert: 'yes' });
    },
    /**
     * Creates the stored template `name`, or adds a new active version when
     * its content changed, so what the Mailgun dashboard shows is what
     * the code sends.
     */
    async ensureTemplate(name, { description, html }) {
      let current = null;
      try {
        current = await mg.domains.domainTemplates.get(domain, name, { active: 'yes' });
      } catch (err) {
        if (err?.status !== 404) throw err;
      }
      if (!current) {
        await mg.domains.domainTemplates.create(domain, {
          name, description, template: html, engine: 'handlebars', comment: 'created by w3t-web', tag: 'v1',
        });
        return 'created';
      }
      if (current?.version?.template === html) return 'unchanged';
      await mg.domains.domainTemplates.createVersion(domain, name, {
        template: html, engine: 'handlebars', active: 'yes', comment: 'updated by w3t-web', tag: `v${Date.now()}`,
      });
      return 'updated';
    },
    async sendTemplate({ to, subject, template, variables }) {
      await mg.messages.create(domain, {
        from,
        to: [to],
        'h:Reply-To': replyTo,
        subject,
        template,
        'h:X-Mailgun-Variables': JSON.stringify(variables),
        ...logoInline(),
      });
    },
    async send({ to, subject, text, html, attachments = [] }) {
      await mg.messages.create(domain, {
        from,
        to: [to],
        'h:Reply-To': replyTo,
        subject,
        text,
        ...(html ? { html } : {}),
        ...(html && html.includes(`cid:${LOGO_CID}`) ? logoInline() : {}),
        ...(attachments.length
          ? { attachment: attachments.map((a) => ({ filename: a.filename, data: Buffer.from(a.data) })) }
          : {}),
      });
    },
  };
}
