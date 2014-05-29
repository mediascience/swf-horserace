# SWF Horserace

Demonstrate Amazon SWF and Flow Framework for Java with a simple
horse race.

## What

This project uses SWF to model a horse race. Each horse in the race runs
the track independently.  The work can be run in parallel on multiple
hosts.

The demonstration consists of four Java entry points:

* The flow worker is a long-running process that handles decision
tasks for the entire workflow.
* The announcer worker is a long-running process that handles
activity tasks corresponding to public announcements.
* The horse worker is a long-running process that handles activities
corresponding to horse behavior.
* The horse race is a one shot program that request a race to be run.

## Run

* Download the pre-built jar from https://s3.amazonaws.com/com-msiops-artifact/demo-swf-horserace-1.0.1-jar-with-dependencies.jar it alread has bytecode enhancements applied.
* Put the jar in the same directory as the scripts. 
* Create the "Demo" SWF domain through your AWS console
* Obtain credentials to operate in the "Demo" SWF domain and add them to the
credentials.shinc file
* Launch the worker scripts in the background
* Run the horse-race script.

You can also run each worker on a distinct host and start the race from a completely
different host. There is no need for any direct coordination between the worker
hosts.


## Build

* see the pom.xml for information on the SWF tool jar that is not in Maven Central
* build with Maven. This will apply all the AspectJ enhancements
* So far, this cannot be built with any javac except for the one included with
Oracle JDK 7. Will try to get around that restriction in a future version


