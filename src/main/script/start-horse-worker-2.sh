#!/bin/bash

# naiive is good enough
here=$(dirname $(readlink -f $0))

# source the AWS credentials
. ${here}/credentials.shinc

# assume jar is in same directory as script
java -cp ${here}/demo-swf-0.1-SNAPSHOT-jar-with-dependencies.jar com.msiops.demo.swf.horserace.worker.HorseActivitiesWorker 2

