FROM tomcat:8.5-jre11-temurin
ADD ./webapp.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]


# metadata
LABEL email="rldndco@mju.ac.kr"
LABEL name="Gi-Woong"
LABEL version="1.0"
LABEL description="tomcat deployment"