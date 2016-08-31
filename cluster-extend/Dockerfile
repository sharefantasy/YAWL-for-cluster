FROM daocloud.io/daocloud/dao-tomcat:v7.0.55
MAINTAINER kenny.fan fankainang@gmail.com
ADD /target/artifacts/cluster_extend /tomcat/webapps/yawl
EXPOSE 8080
CMD sh /tomcat/bin/startup.sh && tail -f /tomcat/logs/catalina.out