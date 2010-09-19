@echo on

set CLASSPATH="classes;lib/jnavigator-ui.jar;lib/jnavigator-course.jar;lib/jnavigator-gps.jar;lib/jnavigator-io-java.jar;lib/jnavigator-util.jar;lib/libJNavigator.dll;lib/junit;lib/commons-codec.jar";

java -cp %CLASSPATH% at.uni_salzburg.cs.ckgroup.ui.NavigationMain

