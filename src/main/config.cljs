(ns config
  "Site settings that change without touching the components.
   The webinar's details are in webinar.json at the repository root.")

;; Your HubSpot Meetings link (HubSpot → Library → Meetings → Copy link),
;; e.g. "https://meetings-eu1.hubspot.com/daniel". Empty shows email and
;; phone on /book instead of the calendar.
(def hubspot-meetings-url "https://meetings-eu1.hubspot.com/dhermansson")

(def email "info@w3t.se")
(def phone "+46 73 032 24 99")
(def phone-e164 "+46730322499")
