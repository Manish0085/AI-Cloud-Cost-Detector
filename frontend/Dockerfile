# =============================================================================
# Frontend — React 19 / Vite 8 (multi-stage: build → static, non-root Nginx)
# =============================================================================

# ---- Stage 1: build the static bundle ------------------------------------- #
FROM node:22-alpine AS build
WORKDIR /app

# Same-origin in prod: the host Nginx proxies /api → backend, so the API base
# is just "/api/v1". Overridable at build time via --build-arg.
ARG VITE_API_URL=/api/v1
ENV VITE_API_URL=$VITE_API_URL

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---- Stage 2: serve the static files (rootless Nginx) --------------------- #
# nginx-unprivileged runs entirely as a non-root user and listens on :8080.
FROM nginxinc/nginx-unprivileged:1.27-alpine AS runtime

COPY --chown=nginx:nginx nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=nginx:nginx /app/dist /usr/share/nginx/html

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=4s --retries=3 \
  CMD wget -qO- http://localhost:8080/ >/dev/null 2>&1 || exit 1