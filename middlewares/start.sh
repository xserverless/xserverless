#!/bin/bash

cd database || exit
docker-compose up -d

cd ..
cd fileserver || exit
docker-compose up -d

cd ..
cd redis || exit
docker-compose up -d