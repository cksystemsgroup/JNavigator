#!/bin/sh

cd $(dirname $0);

CLASSPATH="classes:lib/jnavigator-jcontrol.jar:lib/jnavigator-communication.jar:lib/jnavigator-course.jar:lib/jnavigator-gps.jar:lib/jnavigator-io-java.jar:lib/jnavigator-util.jar:lib/commons-codec.jar:lib/log4j.jar"

java -cp $CLASSPATH -Djava.library.path=lib at.uni_salzburg.cs.ckgroup.control.JControlPosOnlyMain

