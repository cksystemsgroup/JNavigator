#!/bin/sh

cd $(dirname $0)

CLASSPATH="classes:lib/bcel.jar:lib/commons-codec.jar:lib/exotasks.jar:lib/javiator-mockjaviator.jar:lib/javiator-util.jar:lib/jnavigator-communication.jar:lib/jnavigator-course.jar:lib/jnavigator-gps.jar:lib/jnavigator-io-java.jar:lib/jnavigator-lab.jar:lib/jnavigator-location.jar:lib/jnavigator-ui.jar:lib/jnavigator-util.jar:lib/junit.jar:lib/rxtx.jar:lib/tuningForkTraceGeneration.jar:lib/log4j.jar"

java -cp $CLASSPATH -Djava.library.path=lib javiator.simulation.MockJAviatorMain

