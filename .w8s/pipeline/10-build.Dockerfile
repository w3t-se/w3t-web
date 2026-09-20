FROM clojure:temurin-21-tools-deps-bookworm AS build
RUN apt-get update \
 && apt-get install -y --no-install-recommends nodejs npm \
 && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY deps.edn shadow-cljs.edn ./
RUN clojure -A:dev -P
COPY src src
COPY resources resources
RUN npx shadow-cljs release main

FROM nginxinc/nginx-unprivileged:1.27-alpine
COPY --from=build /app/resources/public /usr/share/nginx/html
RUN printf 'server {\n  listen 8080;\n  root /usr/share/nginx/html;\n  index index.html;\n  location / { try_files $uri $uri/ /index.html; }\n}\n' \
    > /etc/nginx/conf.d/default.conf
EXPOSE 8080
