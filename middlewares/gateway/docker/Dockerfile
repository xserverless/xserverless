FROM centos:7

WORKDIR /tmp
RUN yum install -y wget
RUN wget https://openresty.org/package/centos/openresty.repo
RUN mv openresty.repo /etc/yum.repos.d/
RUN yum update -y
RUN yum install -y openresty

VOLUME /root/nginx
VOLUME /root/log
EXPOSE 80

COPY main.sh /root/main.sh

WORKDIR /root
ENTRYPOINT [ "sh", "./main.sh" ]