@echo off

docker rm -f mjdonor_back
ren *.war webapp.war
docker build -t mjdonor_back .
docker run -it -d --name mjdonor_back -p 8888:8080 mjdonor_back
