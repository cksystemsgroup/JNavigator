#/bin/sh

set JAVA3D_DIR=C:\Programme\Java\Java3D\1.5.2
set PATH=%PATH%;%JAVA3D_DIR%\bin

set CLASSPATH="classes;lib\j3dcore.jar;lib\j3dutils.jar;lib\javiator3d.jar;lib\javiator-controlterminal.jar;lib\jnavigator-ui.jar;lib\javiator-util.jar;lib\joystick.jar;lib\starfireExt.jar;lib\vecmath.jar;%JAVA3D_DIR%\lib\ext"

java -cp %CLASSPATH% javiator.terminal.ControlTerminal classes\ControlTerminal.properties

