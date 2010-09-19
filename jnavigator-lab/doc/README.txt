
JNavigator Lab Package
======================


Unpacking the distribution
--------------------------

1. Unpack the JNavigator Lab Package on Linux

	cd /your/desired/directory
	tar -xzvf /path/to/package/jnavigator-lab-0.5.tar.gz

2. Unpack the JNavigator Lab Package on Windows by right klicking
   the Windows distribution jnavigator-lab-0.5.zip and selecting
   the "Extract All..." menu item.


How to start the package
------------------------

1. Start the uLocationDaemon on beta.cs.uni-salzburg.at

	cd /home/ckrainer/ubisense/CAPI/src
	./uLocationDaemon -p 9001

2. Start the Ubisense to GPS converting application

	./ubs2gps

   On Windows start the ubs2gps.exe application


3. Start the JNavigator User Interface application in a separate shell

	./jnavigatorUI

   On Windows start the jnavigatorUI.exe application



Configuration
-------------

The unpacked subfolder "resources" contains all configuration files that might
need adaption.

1. File ubs2gps.properties

 - Adapt the "position.provider.tag.one.id" and "position.provider.tag.one.type"
   properties to the tag on the south end of the helicopter.

 - Adapt the "position.provider.tag.two.id" and "position.provider.tag.two.type"
   properties to the tag on the north end of the helicopter.

 - Adapt the "gps.simulator.host" and "gps.simulator.port" to the host and port
   number the ubs2gps daemon listens for TCP/IP connections. The JNavigator
   User Interface application uses this host and port to connect to.

 - Adapt the "gps.simulator.cycle" to the cycle time the Ubisense to GPS
   converting application generates GPS position messages.


2. File jnavigator-ui.properties

 - Change the "receiver.lan.host" and "receiver.lan.port" properties to connect
   to the Ubisense to GPS converting application.




Problems
--------

1. Check that the uLocationDaemon sends data

   On a Linux box connect to the uLocationDaemon via Telnet:

	telnet beta.cs.uni-salzburg.at 9001

   Here is an example output of what to expect from the uLocationDaemon:

	$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021117,2008-07-16 08:17:44.773837456,0.0594234,1,1.6849,3.98713,0.781285,1,0,0,0*56
	$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-07-16 08:17:44.854905907,0.0587439,1,0.560215,3.09499,0.952912,1,0,0,0*50


2. Check that the tag type and id of both tags are correctly configured in file
   ubs2gps.properties. Use the telnet output from above for verification.

3. Check that the Ubisense to GPS converting application sends position data by
   starting the JNavigator User Interface application or use telnet on the
   machine the Ubisense to GPS converting application runs.

	telnet localhost 3333

   Here is an example output of what to expect from the Ubisense to GPS
   converting application:

	$GPGGA,203226.026,4749.31366007,N,1302.45269293,E,1,07,1.0,440.87597,M,46.5988,M,1.9,0120*7A
	$GPRMC,203226.026,A,4749.31366007,N,1302.45269293,E,3.12,42.79,131008,,*0F
	$GPVTG,42.79,T,,M,3.12,N,5.78,K,A*0F
	$GPGLL,4749.31366007,N,1302.45269293,E,203226.026,A,A*66
	$GPZDA,203226.026,13,10,2008,00,00*5C
	$GPGGA,203226.129,4749.31364805,N,1302.45269803,E,1,07,1.0,440.85059,M,46.5988,M,1.9,0120*7A
	$GPRMC,203226.129,A,4749.31364805,N,1302.45269803,E,0.65,36.91,131008,,*0C

4. Check that the setup works without having a working uLocationDaemon

   Login to beta.cs.uni-salzburg.at and start netcat instead of the
   uLocationDaemon as follows:

	cd /home/ckrainer/ubisense/CAPI/src
	netcat -l -p 9001 < data-20080716-2.out

   Then proceed with steps two and three from section "How to start the package"
   above.

