#! /usr/bin/bash

cd ../../
ROOT=`pwd`
echo $ROOT

### gateway
cp $ROOT/gateway/target/*.jar $ROOT/xserverless/trail/compose/gateway/

### dev-center
cp $ROOT/dev-center/target/*.jar $ROOT/xserverless/trail/compose/dev-center/

### runtime
cp $ROOT/runtime/target/*.jar $ROOT/xserverless/trail/compose/runtime/

### dev-center-web
cp -r $ROOT/dev-center-web/dist/* $ROOT/xserverless/trail/compose/dev-center-web/www/
