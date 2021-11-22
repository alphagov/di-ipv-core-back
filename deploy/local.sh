#!/usr/bin/env bash
set -eu

printf "👷🐑 building & deploying lambdas\n------------------------\n"

pushd ..
./gradlew -q buildZip
popd
sam build && sam local start-api -p 3100 &