FROM openjdk:11
LABEL maintainer="i@liufang.org.cn" \
	  version="v1.0" \
	  description="Java11; 打包soho-admin服务"
COPY ./soho-admin/soho-admin-biz/target/soho-admin-biz-1.0-SNAPSHOT.jar /root/
WORKDIR /root/
ARG CONFIG_PROFILE="dev"
ENV SPRING_CLOUD_CONFIG_PROFILE="-Dspring.profiles.active=${CONFIG_PROFILE}"
CMD java -jar ${SPRING_CLOUD_CONFIG_PROFILE} /root/soho-admin-biz-1.0-SNAPSHOT.jar
