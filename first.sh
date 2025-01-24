#!/bin/bash

# Print hello world
set -e
param1=$1
param2=$2

#echo "Shell script executed successfully with the following parameters:"
#echo "Parameter 1: $param1"
#echo "Parameter 2: $param2"
printf "Hello, world!"
mkdir -v  /home/csedc-kiran/testing/$param1
cd /home/csedc-kiran/testing/$param1
touch $param2.txt
