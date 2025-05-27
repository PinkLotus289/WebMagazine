# 1. Сборка React
FROM node:18 as build
WORKDIR /app
COPY . .
RUN npm install && npm run build

# 2. Публикация через nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
