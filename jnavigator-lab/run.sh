#!/bin/sh -x

mvn clean package -Dmaven.test.skip

mkdir -p target/nix

cd target/nix

tar -xzvf ../jnavigator-lab-1.1-SNAPSHOT.tar.gz

./ubs2gps.sh
