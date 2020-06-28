#!/usr/bin/env bash

for i in {1..2}
do
   echo "This script will fail $i "
   sleep 1
done
exit 1