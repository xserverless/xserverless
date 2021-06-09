#!/bin/bash

cd database || exit
docker-compose down

cd ..
cd fileserver || exit
docker-compose down

cd ..
cd redis || exit
docker-compose down