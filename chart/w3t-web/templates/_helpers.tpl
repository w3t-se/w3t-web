{{- define "w3t-web.labels" -}}
app.kubernetes.io/name: w3t-web
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Values.image.tag | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "w3t-web.selector" -}}
app.kubernetes.io/name: w3t-web
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "w3t-web.env" -}}
- { name: SITE_URL, value: {{ .Values.app.siteUrl | quote }} }
- { name: NOTIFY_TO, value: {{ .Values.app.notifyTo | quote }} }
- { name: WEBINAR_JOIN_URL, value: {{ .Values.app.webinarJoinUrl | quote }} }
- { name: MAILGUN_DOMAIN, value: {{ .Values.app.mailgun.domain | quote }} }
- { name: MAILGUN_URL, value: {{ .Values.app.mailgun.url | quote }} }
- name: MAILGUN_API_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Values.app.mailgun.apiKeySecret }}
      key: api-key
      optional: true
{{- end }}
