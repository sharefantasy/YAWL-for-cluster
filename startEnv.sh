#!/usr/bin/env bash

echo "YAWL Cluster Environment starting..."

sh /cygdrive/c/dev/component/zkServer.sh start
sh /cygdrive/c/dev/component/zkCli.sh
