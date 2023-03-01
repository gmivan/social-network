FROM openjdk:12-alpine

ARG JAVA_OPTS
ARG JAR_FILE=social-backend/target/social-backend.jar
ARG STATIC_DIST=social-frontend/dist/social-frontend

RUN apk add --no-cache bash gawk sed grep bc coreutils busybox-extras tzdata
RUN apk update && apk add nginx && mkdir -p /run/nginx

RUN adduser -D -g 'ivan' ivan

RUN mkdir /www && mkdir /app
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/

RUN chown -R ivan:ivan /var/lib/nginx && \
    chown -R ivan:ivan /www && \
    chown -R ivan:ivan /app && \
    touch /run/nginx/nginx.pid && \
    chown -R ivan:ivan /run/nginx/nginx.pid && \
    mkdir /var/cache/nginx && \
    chown -R ivan:ivan /var/cache/nginx && \
    chown -R ivan:ivan /var/tmp/nginx && \
    chown -R ivan:ivan /var/log/nginx


COPY ${STATIC_DIST}/ /www/
COPY ${JAR_FILE} /app/app.jar
RUN chown ivan:ivan /app/app.jar
COPY entrypoint.sh /

RUN chown ivan:ivan /entrypoint.sh
RUN ["chmod", "+x", "/entrypoint.sh"]
CMD ["/entrypoint.sh"]
