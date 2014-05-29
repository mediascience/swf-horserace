#!/bin/bash

# naiive is good enough
here=$(dirname $(readlink -f $0))

# source the AWS credentials
. ${here}/credentials.shinc

# start a race with defaults
# assume jar is in same directory as script
java -cp ${here}/demo-swf-0.1-SNAPSHOT-jar-with-dependencies.jar com.msiops.demo.swf.horserace.HorseRace


# start a race with your own horses and laps
# assume jar is in same directory as script
#java -cp ${here}/demo-swf-0.1-SNAPSHOT-jar-with-dependencies.jar com.msiops.demo.swf.horserace.HorseRace 'john' 'paul' 'george' 'ringo' 3


