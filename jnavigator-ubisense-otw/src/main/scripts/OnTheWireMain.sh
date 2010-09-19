#!/bin/sh

cd $(dirname $0);

CLASSPATH="classes:lib/jnavigator-location.jar:lib/jnavigator-course.jar:lib/jnavigator-util.jar:lib/jnavigator-io-java.jar:lib/log4j.jar:lib/jnavigator-ubisense-otw.jar"


java -cp $CLASSPATH -Djava.library.path=lib at.uni_salzburg.cs.ckgroup.location.ubisense.OnTheWireMain

