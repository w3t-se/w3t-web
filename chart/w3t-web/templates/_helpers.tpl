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
