#/bin/sh

cd $(dirname $0)

CLASSPATH="classes:lib/j3dcore.jar:lib/j3dutils.jar:lib/javiator3d.jar:lib/javiator-controlterminal.jar:lib/jnavigator-ui.jar:lib/javiator-util.jar:lib/joystick.jar:lib/starfireExt.jar:lib/vecmath.jar"

java -cp $CLASSPATH -Djava.library.path=lib javiator.terminal.ControlTerminal classes/ControlTerminal.properties

