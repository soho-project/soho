FROM eclipse-temurin:11-jre-jammy
LABEL maintainer="i@liufang.org.cn" \
	  version="v1.0" \
	  description="Java11; 打包soho-admin服务"

ARG XRAY_VERSION="v26.1.13"
ARG TARGETARCH
ARG APT_MIRROR_HOST="mirrors.aliyun.com"
ARG XRAY_DOWNLOAD_BASE="https://github.com/XTLS/Xray-core/releases/download"

RUN set -eux; \
    unset http_proxy https_proxy HTTP_PROXY HTTPS_PROXY all_proxy ALL_PROXY no_proxy NO_PROXY; \
    sed -i "s@http://archive.ubuntu.com/ubuntu@https://${APT_MIRROR_HOST}/ubuntu@g" /etc/apt/sources.list; \
    sed -i "s@http://security.ubuntu.com/ubuntu@https://${APT_MIRROR_HOST}/ubuntu@g" /etc/apt/sources.list; \
    APT_OPTS="-o Acquire::Retries=5 -o Acquire::http::Proxy=false -o Acquire::https::Proxy=false"; \
    apt-get ${APT_OPTS} update; \
    apt-get ${APT_OPTS} install -y --no-install-recommends ca-certificates curl unzip tzdata; \
    rm -rf /var/lib/apt/lists/*; \
    case "${TARGETARCH:-amd64}" in \
      amd64) XRAY_ARCH="64" ;; \
      386) XRAY_ARCH="32" ;; \
      arm64) XRAY_ARCH="arm64-v8a" ;; \
      arm) XRAY_ARCH="arm32-v7a" ;; \
      *) echo "unsupported TARGETARCH: ${TARGETARCH}" && exit 1 ;; \
    esac; \
    XRAY_FILE="Xray-linux-${XRAY_ARCH}.zip"; \
    for XRAY_BASE in \
      "${XRAY_DOWNLOAD_BASE}" \
      "https://ghfast.top/https://github.com/XTLS/Xray-core/releases/download" \
      "https://mirror.ghproxy.com/https://github.com/XTLS/Xray-core/releases/download"; do \
      if env -u http_proxy -u https_proxy -u HTTP_PROXY -u HTTPS_PROXY -u all_proxy -u ALL_PROXY \
        curl --noproxy '*' --retry 5 --retry-delay 2 --retry-all-errors --connect-timeout 10 --max-time 300 -fL -o /tmp/xray.zip "${XRAY_BASE}/${XRAY_VERSION}/${XRAY_FILE}"; then \
        break; \
      fi; \
    done; \
    test -s /tmp/xray.zip; \
    unzip -q /tmp/xray.zip -d /tmp/xray; \
    install -m 0755 /tmp/xray/xray /usr/local/bin/xray; \
    rm -rf /tmp/xray /tmp/xray.zip; \
    /usr/local/bin/xray version

COPY ./soho-admin-web/target/soho-admin-web-1.0-SNAPSHOT.jar /root/
WORKDIR /root/
ARG CONFIG_PROFILE="dev"
ENV TZ="Asia/Shanghai"
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Shanghai -Xmx380m"
ENV SPRING_CLOUD_CONFIG_PROFILE="-Dspring.profiles.active=${CONFIG_PROFILE}"
ENV AI_RELAY_XRAY_BIN="/usr/local/bin/xray"
ENV AI_RELAY_WORKDIR="/tmp/soho-ai-relay"

CMD java ${SPRING_CLOUD_CONFIG_PROFILE} -jar /root/soho-admin-web-1.0-SNAPSHOT.jar
