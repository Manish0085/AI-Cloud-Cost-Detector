# =============================================================================
# Frontend — React 19 / Vite 8
# =============================================================================

FROM node:22-alpine AS build

WORKDIR /app

# Build arguments from docker-compose
ARG VITE_API_URL
ARG VITE_OAUTH_BASE_URL

# Make them available to Vite during npm run build
ENV VITE_API_URL=${VITE_API_URL}
ENV VITE_OAUTH_BASE_URL=${VITE_OAUTH_BASE_URL}

COPY package*.json ./
RUN npm ci

COPY . .

RUN npm run build

# =============================================================================
# Runtime
# =============================================================================

FROM nginxinc/nginx-unprivileged:1.27-alpine

COPY --chown=nginx:nginx nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=nginx:nginx /app/dist /usr/share/nginx/html

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=4s --retries=3 \
  CMD curl -f http://localhost:8080/ >/dev/null 2>&1 || exit 1