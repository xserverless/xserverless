#! /usr/bin/bash

sudo echo hello

cd ../../
ROOT=`pwd`
echo $ROOT

### build all java projects
### 1. fastdfs-client-java
cd $ROOT/fastdfs-client-java
mvn clean install -Dmaven.test.skip=true

### 2. xserverless
cd $ROOT/xserverless
mvn clean install -Dmaven.test.skip=true

### 3. dev-center
cd $ROOT/dev-center
mvn clean install -Dmaven.test.skip=true

### 4. gateway
cd $ROOT/gateway
mvn clean install -Dmaven.test.skip=true

### 5. runtime
cd $ROOT/runtime
mvn clean install -Dmaven.test.skip=true

### 6. runtime/docker
cd $ROOT/runtime/docker
sudo bash build.sh

### 7. gateway/docker
cd $ROOT/gateway/docker
sudo bash build.sh

### 8. dev-center-web/docker
cd $ROOT/dev-center-web/docker
sudo bash build.sh

### 9. dev-center-web
cd $ROOT/dev-center-web
npm run build:prod
