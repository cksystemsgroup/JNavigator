@echo on

set CLASSPATH "classes;lib/jnavigator-jcontrol.jar;lib/jnavigator-communication.jar;lib/jnavigator-course.jar;lib/jnavigator-gps.jar;lib/jnavigator-io-java.jar;lib/jnavigator-util.jar;lib/libJNavigator.so;lib/commons-codec.jar;lib/log4j.jar"

java -cp %CLASSPATH% at.uni_salzburg.cs.ckgroup.control.JControlMain

