#!/bin/sh

cd $(dirname $0)

CLASSPATH="classes:lib/jnavigator-ui.jar:lib/jnavigator-course.jar:lib/jnavigator-gps.jar:lib/jnavigator-io-java.jar:lib/jnavigator-util.jar:lib/libJNavigator.so:lib/junit:lib/commons-codec.jar";

java -cp $CLASSPATH at.uni_salzburg.cs.ckgroup.ui.NavigationMain

