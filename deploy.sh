#!/bin/sh
docker rm -f mjdonor_back

# 이름 변경
mv ./*.war webapp.war
# 이미지 빌드
sudo docker build -t mjdonor_back .
# 이미지를 컨테이너에 배포
docker run -it -d --name mjdonor_back -p 8888:8080 mjdonor_back
