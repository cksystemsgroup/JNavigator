#!/bin/sh

set CLASSPATH="classes;lib/bcel.jar;lib/commons-codec.jar;lib/jcontrol.jar;lib/exotasks.jar;lib/javiator3d.jar;lib/jcontrol.jar;lib/jnavigator-course.jar;lib/jnavigator-gps.jar;lib/jnavigator-io-java.jar;lib/jnavigator-lab.jar;lib/jnavigator-location.jar;lib/jnavigator-ui.jar;lib/jnavigator-communication.jar;lib/jnavigator-util.jar;lib/joystick.jar;lib/junit.jar;lib/rxtx.jar;lib/starfireExt.jar;lib/tuningForkTraceGeneration.jar;lib/log4j.jar"

java -cp %CLASSPATH% javiator.simulation.MockJAviatorMain
