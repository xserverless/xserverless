#! /usr/bin/bash

sudo echo hello

cd ../../
ROOT=`pwd`
echo $ROOT

sudo rm -rf ~/.m2/repository/io/xserverless
sudo rm -rf ~/.m2/repository/org/csource

sudo rm -rf $ROOT/xserverless/trail/compose/*

cd $ROOT/xserverless

git reset HEAD --hard

