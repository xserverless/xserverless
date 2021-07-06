#!/usr/bin/bash

./4.stop-compose.sh
./0.clear-all.sh
./1.build.sh
./2.copy-files.sh
./3.start-compose.sh
