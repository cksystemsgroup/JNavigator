/*
 * @(#) GpsReceiverSimulator.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2009  Clemens Krainer
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package at.uni_salzburg.cs.ckgroup.simulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.SimpleTimeZone;
import java.util.TimerTask;
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.NotImplementedException;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.CourseData;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.util.StringUtils;

/**
 * This class implements a simulator for a GPS receiver. It simulates the following
 * NMEA 0183 messages:
 *  * GGA: Time, position and fix type data
 *  * GLL: Latitude, longitude, UTC time of precision fix and status.
 *  * RMC: Time, date, position, course and speed data.
 *  * VTG: Course and speed information relative to the ground.
 *  * ZDA: PPS timing message
 *
 * This GPS receiver simulator does not simulate the following NMEA 0183 messages:
 *  * GSA: GPS receiver operating mode, satellites used in the position solution,
 *              and DOP values
 *  * GSV: The number of satellites in view, satellite ID numbers, elevation,
 *              azimuth and SNR values.
 *  * MSS: Signal-to-noise ratio, signal strength, frequency, and bit rate from a
 *              radio bacon receiver.
 *
 * This GPS receiver simulator estimates new coordinates by reading position data
 * from an instance of <code>IPositionProvider</code> and modifying this data with
 * inaccuracies. In order to simulate satellite coherent inaccuracies the simulator
 * utilises deviation information from recorded GPS position data.
 *
 * The limitations of this GPS receiver are:
 *  * Both, speed and course over ground are derived from the provided data samples.
 *     The course may change rapidly up to 180 degrees on slow total speed over
 *     ground.
 *
 * @author Clemens Krainer
 */
public class GpsReceiverSimulator extends TimerTask implements IConnection
{
	/**
	 * This is the property for the message buffer length.  
	 */
	public static final String PROP_BUFFER_LENGTH = "buffer.length";
	
	/**
	 * This is the property for the file containing the simulation data.  
	 */
	public static final String PROP_DATA_FILE_NAME = "data.file.name";
	
	/**
	 * This is the property for the difference reference station identification. 
	 */
	public static final String PROP_REFERENCE_STATION_ID = "diff.ref.station.id";
	
	/**
	 * This is the property for the position fix indicator. 
	 */
	public static final String PROP_POSITION_FIX_INDICATOR = "position.fix.indicator";
	
	/**
	 * This is the property for the number of used satellites. 
	 */
	public static final String PROP_SATELLITES_USED = "satellites.used";
	
	/**
	 * This is the property for the horizontal dilution of precision (HDOP). 
	 */
	public static final String PROP_HDOP = "horizontal.dilution.of.precision";
	
	/**
	 * This is the property for the age of the differential correction data. 
	 */
	public static final String PROP_AGE_OF_DIFF_CORR = "age.of.differential.correction";
	
	/**
	 * The currently used position provider. The simulator uses a
	 * DummyPositionProvider as default.
	 */
	private IPositionProvider positionProvider;
	
	/**
	 * This variable indicates if a <code>positionProvider</code> implements
	 * the <code>getSpeedOverGround()</code> method.
	 */
	private boolean positionProviderHasSpeedOverGround = false;
	
	/**
	 * This variable indicates if a <code>positionProvider</code> implements
	 * the <code>getCourseOverGround()</code> method.
	 */
	private boolean positionProviderHasCourseOverGround = false;
	
	/**
	 * The currently used geodetic system. The simulator uses a WGS84 system as
	 * default.
	 */
	private IGeodeticSystem geodeticSystem;
	
	/**
	 * The current course. The calculateSpeedAndCourseOverGround() method of the
	 * used geodetic system may or may not return a course. The simulator stores
	 * new course values in this variable. If the estimation is not able to
	 * provide a new course, e.g. because the speed is zero, the simulator uses
	 * the value of this variable instead.
	 */
	private double currentCourse = 0;
	
	/**
	 * The ring buffer for the NMEA 0183 messages. The receiver simulator
	 * creates new messages and inserts them to this buffer. Whether or not some
	 * GpsReceiverInputStream instances read messages from this buffer, the
	 * simulator continues to insert messages into this buffer. If the buffer is
	 * full, the simulator overwrites the eldest messages with new ones.
	 */
	private byte[][] messageBuffer;

	/**
	 * The write index of the messageBuffer. 
	 */
	private int writeIndex = 0;
	
	/**
	 * This variable contains the deviation data for x, y and z directions in
	 * meters. This array has the same length as the
	 * <code>geoidSeparation</code> variable.
	 * 
	 * @uml.property name="deviation"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private CartesianCoordinate[] deviation;
	
	/**
	 * This variable contains the geoid separation data in meters. This array
	 * has the same length as the <code>deviation</code> variable.
	 */
	private double[] geoidSeparation;
	
	/**
	 * This is the current index of the <code>deviation</code> and
	 * <code>geoidSeparation</code> variables.
	 */
	private int messageIndex = 0;
	
	/**
	 * Date and Time conversion variables.
	 */
	
	/**
	 * The date and time of the last invocation of the <code>run()</code>
	 * method.
	 */
	private Date oldDateAndTime;
	
	/**
	 * The date and time of the current invocation of the <code>run()</code>
	 * method.
	 */
	private Date dateAndTime;
	
	/**
	 * The difference between <code>dateAndTime</code> and
	 * <code>oldDateAndTime</code> in milliseconds.
	 */
	private long cycleTime;
	
	/**
	 * A UTC calendar. It is this calendar that performs the time estimations.
	 */
	private Calendar calendar;
	
	/**
	 * This variable contains a <b>en_US</b> schema. The simulator uses this
	 * locale for converting numbers into Strings.
	 */
	private Locale locale;
	
	/**
	 * The current time in "hhmmss.sss" format ready to use for the creation of
	 * NMEA 0183 messages.
	 */
	private String currentTime;

	/**
	 * The current date in "ddmmyy" format ready to use for the creation of NMEA
	 * 0183 messages.
	 */
	private String currentDate;
	
	/**
	 * The current day of the month in "dd" format ready to use for the creation
	 * of NMEA 0183 messages.
	 */
	private String currentDay;

	/**
	 * The current month in "mm" format ready to use for the creation of NMEA
	 * 0183 messages.
	 */
	private String currentMonth;
	
	/**
	 * The current year in "yyyy" format ready to use for the creation of NMEA
	 * 0183 messages.
	 */
	private String currentYear;

	/**
	 * The new estimated coordinates.
	 */
	private PolarCoordinate estimatedCoordinates;

	/**
	 * The new estimated latitude ready to use for the creation of NMEA 0183
	 * messages.
	 */
	private String estimatedLatitude;
	
	/**
	 * The new estimated longitude ready to use for the creation of NMEA 0183
	 * messages.
	 */
	private String estimatedLongitude;
	
	/**
	 * The new estimated altitude ready to use for the creation of NMEA 0183
	 * messages.
	 */
	private String estimatedAltitude;
	
	/**
	 * The new estimated speed over ground in knots ready to use for the
	 * creation of NMEA 0183 messages.
	 */
	private String estimatedSpeedOverGroundKnots;
	
	/**
	 * The new estimated speed over ground in kilometers per hour ready to use
	 * for the creation of NMEA 0183 messages.
	 */
	private String estimatedSpeedOverGroundKmph;
	
	/**
	 * The new estimated course over ground in degrees ready to use for the
	 * creation of NMEA 0183 messages.
	 */
	private String estimatedCourseOverGround;

	/**
	 * The property value for the reference station identification ready to use
	 * for the creation of NMEA 0183 messages.
	 */
	private String referenceStationId;
	
	/**
	 * The property value for the position fix indicator ready to use for the
	 * creation of NMEA 0183 messages.
	 */
	private String positionFixIndicator;
	
	/**
	 * The property value for the number of used satellites ready to use for the
	 * creation of NMEA 0183 messages.
	 */
	private String satellitesUsed;
	
	/**
	 * The property value for the horizontal dilution of precision ready to use
	 * for the creation of NMEA 0183 messages.
	 */
	private String hdop;
	
	/**
	 * The property value for the age of the difference correction data ready to
	 * use for the creation of NMEA 0183 messages.
	 */
	private String ageOfDiffCorr;
	
	/**
	 * True as long as the GpsReceiverInputStream is active.
	 */
	private boolean inputStreamRunning = true;
	
	/**
	 * Construct a GpsReceiverSimulator by means of properties.
	 * 
	 * @param properties the properties to be used. 
	 * @throws IOException thrown in case of errors.
	 */
	public GpsReceiverSimulator (Properties properties) throws IOException {
		init (properties);		
	}

	/**
	 * Initialize the GpsReceiverSimulator.
	 * 
	 * @param props the properties to be used.
	 * @throws IOException thrown in case of errors.
	 */
	private void init (Properties props) throws IOException {
		int bufferLength = Integer.parseInt (props.getProperty (PROP_BUFFER_LENGTH,"100"));
		messageBuffer = new byte [bufferLength][];
		
		String dataFileName = props.getProperty (PROP_DATA_FILE_NAME,"gpssim.dat");
		InputStream dataFileStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream(dataFileName);
		if (dataFileStream == null)
			throw new FileNotFoundException (dataFileName);
		loadSimulationData (dataFileStream);
		
		calendar = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		locale = new Locale ("en","US");
		
		referenceStationId = props.getProperty (PROP_REFERENCE_STATION_ID,"");
		positionFixIndicator = props.getProperty (PROP_POSITION_FIX_INDICATOR,"1");
		satellitesUsed = props.getProperty (PROP_SATELLITES_USED, "04");
		hdop = props.getProperty (PROP_HDOP, "3.0");
		ageOfDiffCorr = props.getProperty (PROP_AGE_OF_DIFF_CORR, "");
		
		geodeticSystem = new WGS84 ();
	}

	/**
	 * Load the simulation data from a given InputStream. This load procedure
	 * interprets the simulation data line by line. A line starting with '#' is
	 * considered as a comment and therefore skipped. Every other line must
	 * contain the values for latitude, longitude, altitude and geoid
	 * separation. Semicolons (';') separate the fields from each other.
	 * 
	 * @param simulationData the simulation data as InputStream.
	 * @throws IOException thrown in case of errors.
	 */
	public void loadSimulationData (InputStream simulationData) throws IOException {
		BufferedReader reader = new BufferedReader (new InputStreamReader (simulationData));
		String line;
		Vector<String> lines = new Vector<>();
		
		while ((line = reader.readLine()) != null) {
			
			if (line.matches ("\\s*#.*") || line.matches ("\\s*$"))
				continue;
			
			lines.add (line);
		}

		deviation = new CartesianCoordinate [lines.size ()];
		geoidSeparation = new double[lines.size ()];
		
		for (int k=0; k < lines.size (); k++) {
//			String[] x = ((String)lines.get (k)).split (";");
			String[] x = StringUtils.splitOnCharAndTrim(';',(String)lines.get (k));
			deviation [k] = new CartesianCoordinate (Double.parseDouble (x[0]), Double.parseDouble (x[1]), Double.parseDouble (x[2]) );
			geoidSeparation[k] = Double.parseDouble (x[3]);
		}
	}
	
	/**
	 * A timer invokes this method every timer tick.
	 * 
	 * @see java.util.TimerTask#run()
	 */
	public void run () {
		
		PolarCoordinate currentPosition = null;
		
		prepareCurrentDateAndTime ();
		
		if (++messageIndex >= deviation.length)
			messageIndex = 0;
		
		if (positionProvider != null)
			currentPosition = positionProvider.getCurrentPosition ();

		estimateCoordinates (currentPosition);
		
		messageBuffer [writeIndex] = createGgaMessage ();
		incrementWriteIndex ();

		messageBuffer [writeIndex] = createRmcMessage ();
		incrementWriteIndex ();
		
		messageBuffer [writeIndex] = createVtgMessage ();
		incrementWriteIndex ();
		
		messageBuffer [writeIndex] = createGllMessage ();
		incrementWriteIndex ();
		
		messageBuffer [writeIndex] = createZdaMessage ();
		incrementWriteIndex ();
	}
	
	/**
	 * Increment the writeIndex of the messageBuffer.
	 */
	private void incrementWriteIndex () {
		
		if (writeIndex+1 >= messageBuffer.length)
			writeIndex = 0;
		else
			++writeIndex;		
	}
	
	/**
	 * Calculate the NMEA 0183 checksum of a given message and append the
	 * checksum to the message.
	 * 
	 * @param msg the message as a StringBuffer.
	 * @return the message with the appended checksum as a byte array.
	 */
	private byte[] calculateAndAppendChecksum (StringBuffer msg) {
		
		char[] cs = calculateChecksum (msg.toString ().getBytes ());
		msg.append (cs);
		msg.append ("\r\n");
		return msg.toString ().getBytes ();
	}
	
	/**
	 * Create a NMEA 0183 Global Positioning System Fixed Data (GGA) message.
	 * e.g. $GPGGA,203122.00,4759.42520,N,01256.20036,E,1,06,1.90,438.5,M,46.6,M,,*55
	 * 
	 * @return the message as an array of bytes.
	 */
	public byte[] createGgaMessage () {

		StringBuffer msg = new StringBuffer ("$GPGGA,");
		msg.append (currentTime).append (",");
		
		msg.append (estimatedLatitude).append (",N,");
		msg.append (estimatedLongitude).append (",E,");
		msg.append (positionFixIndicator).append (",");
		msg.append (satellitesUsed).append (",");
		msg.append (hdop).append (",");
		msg.append (estimatedAltitude).append (",M,");

//		Object[] args = new Object[1];
//		args[0] = new Double (geoidSeparation[messageIndex]);
//		String geoidSep = String.format (locale, "%1.4f", args);
		
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		nf.setMinimumIntegerDigits(1);
		nf.setGroupingUsed(false);
		String geoidSep = nf.format(geoidSeparation[messageIndex]);
		
		msg.append (geoidSep).append (",M,");
		msg.append (ageOfDiffCorr).append (",");
		msg.append (referenceStationId);

		return calculateAndAppendChecksum (msg);
	}
	
	/**
	 * Create a NMEA 0183 Recommended Minimum Specific GNSS Data (RMC) message.
	 * e.g. $GPRMC,203129.00,A,4759.42489,N,01256.20029,E,0.032,199.74,021207,,,A*65
	 * 
	 * @return the message as an array of bytes.
	 */
	public byte[] createRmcMessage () {
		
		StringBuffer msg = new StringBuffer ("$GPRMC,");
		msg.append (currentTime).append (",");
		msg.append ("A,");
	   
		msg.append (estimatedLatitude).append (",N,");
		msg.append (estimatedLongitude).append (",E,");
		msg.append (estimatedSpeedOverGroundKnots).append (",");
		msg.append (estimatedCourseOverGround).append (",");
		msg.append (currentDate).append (",,");
		
		return calculateAndAppendChecksum (msg);
	}

	/**
	 * Create a NMEA 0183 Course Over Ground and Ground Speed (VTG) message.
	 * e.g. $GPVTG,205.00,T,,M,0.038,N,0.070,K,A*36
	 * 
	 * @return the message as an array of bytes.
	 */
	public byte[] createVtgMessage () {
		
		StringBuffer msg = new StringBuffer ("$GPVTG,");
		msg.append (estimatedCourseOverGround).append (",T,,M,");
		msg.append (estimatedSpeedOverGroundKnots).append (",N,");
		msg.append (estimatedSpeedOverGroundKmph).append (",K,A");
		
		return calculateAndAppendChecksum (msg);
	}
	
	/**
	 * Create a NMEA 0183 Geographic Position - Latitude/Longitude (GLL) message.
	 * e.g. $GPGLL,4759.42746,N,01256.20483,E,202358.00,A,A*66
	 * 
	 * @return the message as an array of bytes.
	 */
	public byte[] createGllMessage () {
		
		StringBuffer msg = new StringBuffer ("$GPGLL,");
		msg.append (estimatedLatitude).append (",N,");
		msg.append (estimatedLongitude).append (",E,");
		msg.append (currentTime).append (",A,A");

		return calculateAndAppendChecksum (msg);
	}
	
	/**
	 * Create a NMEA 0183 Timing (ZDA) message.
	 * e.g. $GPZDA,202358.00,02,12,2007,00,00*6C
	 * 
	 * @return the message as an array of bytes.
	 */
	public byte[] createZdaMessage () {
		
		StringBuffer msg = new StringBuffer ("$GPZDA,");
		msg.append (currentTime).append (",");
		msg.append (currentDay).append (",");
		msg.append (currentMonth).append (",");
		msg.append (currentYear).append (",00,00");
		
		return calculateAndAppendChecksum (msg);		
	}
	
	/**
	 * Prepare the current date and time as Strings to be used in the
	 * createXxxMessage() methods. Additionally, calculate the time difference
	 * between the last and the current invocation.
	 */
	public void prepareCurrentDateAndTime () {
		
		oldDateAndTime = dateAndTime == null ? new Date () : dateAndTime;
		dateAndTime = new Date ();		
		calendar.setTime (oldDateAndTime);
		long oldMilliSeconds = calendar.getTimeInMillis ();
		calendar.setTime (dateAndTime);
		cycleTime = calendar.getTimeInMillis () - oldMilliSeconds;
		
//		Object[] args = new Object [4];
//		args[0] = new Integer (calendar.get (Calendar.HOUR_OF_DAY));
//		args[1] = new Integer (calendar.get (Calendar.MINUTE));
//		args[2] = new Integer (calendar.get (Calendar.SECOND));
//		args[3] = new Integer (calendar.get (Calendar.MILLISECOND));		
//		currentTime = String.format ("%02d%02d%02d.%03d", args);
//		
//		args = new Object [3];
//		args[0] = new Integer (calendar.get (Calendar.DAY_OF_MONTH));
//		args[1] = new Integer (calendar.get (Calendar.MONTH)+1);
//		args[2] = new Integer (calendar.get (Calendar.YEAR) % 100);
//		currentDate = String.format ("%02d%02d%02d", args);
//		
//		args = new Object [1];
//		args[0] = new Integer (calendar.get (Calendar.DAY_OF_MONTH));
//		currentDay = String.format ("%02d", args);
//		
//		args = new Object [1];
//		args[0] = new Integer (calendar.get (Calendar.MONTH)+1);
//		currentMonth = String.format ("%02d", args);
//		
//		args = new Object [1];
//		args[0] = new Integer (calendar.get (Calendar.YEAR));
//		currentYear = String.format ("%04d", args);
		
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		nf.setMinimumIntegerDigits(2);
		nf.setGroupingUsed(false);
		
		currentTime = nf.format(calendar.get (Calendar.HOUR_OF_DAY));
		currentTime += nf.format(calendar.get (Calendar.MINUTE));
		currentTime += nf.format(calendar.get (Calendar.SECOND)) + '.';
		nf.setMinimumIntegerDigits(3);
		currentTime += nf.format(calendar.get (Calendar.MILLISECOND));
		
		nf.setMinimumIntegerDigits(2);
		currentDate = nf.format(calendar.get (Calendar.DAY_OF_MONTH));
		currentDate += nf.format(calendar.get (Calendar.MONTH)+1);
		currentDate += nf.format(calendar.get (Calendar.YEAR) % 100);
		
		currentDay = nf.format(calendar.get (Calendar.DAY_OF_MONTH));
		
		currentMonth = nf.format(calendar.get (Calendar.MONTH)+1);
		
		nf.setMinimumIntegerDigits(4);
		currentYear = nf.format(calendar.get (Calendar.YEAR));
	}
	
	/**
	 * Convert an angle from a double value to a NMEA 0183 string value.
	 *   
	 * @param angle the angle to be converted.
	 * @return the angle as a NMEA 0183 conform String.
	 */
	public String convertAngleToString (double angle) {
	
		int degrees = (int)angle;
		double minutes = 60 * Math.abs (angle - degrees);
		
		/*
		 * Reality is odd. We need this little piece of code to correctly
		 * convert the double value 12.99999999999 to the String
		 * "1300.00000000". Omitting this code would result in "1260.00000000",
		 * i.e. 12 degrees and 60 minutes :-(
		 */
		if ((60.0 - minutes) < 1E-9) {
			degrees += degrees < 0 ? -1 : 1;
			minutes = 0;
		}
		
//		Object[] args = new Object [2];
//		args [0] = new Integer ((int)degrees);
//		args [1] = new Double (minutes);
//		return String.format (locale, "%d%011.8f", args);	
		
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		nf.setMinimumIntegerDigits(1);
		nf.setGroupingUsed(false);
		
		String angleString = nf.format((int)degrees);
		nf.setMaximumFractionDigits(8);
		nf.setMinimumFractionDigits(8);
		nf.setMinimumIntegerDigits(2);
		angleString += nf.format(minutes);
		
		return angleString;
	}
	
	/**
	 * Estimate the current coordinates. This method estimates the new values
	 * for latitude, longitude, altitude, speed over ground and course over
	 * ground.
	 */
	private void estimateCoordinates (PolarCoordinate currentPosition) {				
		
		if (currentPosition == null) {
			estimatedLatitude = "";
			estimatedLongitude = "";
			estimatedAltitude = "";
			estimatedSpeedOverGroundKnots = "";
			estimatedSpeedOverGroundKmph = "";
			estimatedCourseOverGround = "";
			return;
		}

		PolarCoordinate oldPolarCoordinates = estimatedCoordinates;
		
		CartesianCoordinate delta = deviation[messageIndex];		
		estimatedCoordinates = geodeticSystem.walk (currentPosition, delta.x, delta.y, delta.z);
		
		estimatedLatitude = convertAngleToString (estimatedCoordinates.latitude);
		estimatedLongitude = convertAngleToString (estimatedCoordinates.longitude);
//		Object[] args = new Object[1];
//		args[0] = new Double (estimatedCoordinates.altitude);
//		estimatedAltitude = String.format (locale, "%1.5f", args);
		
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(5);
		nf.setMinimumFractionDigits(5);
		nf.setMinimumIntegerDigits(1);
		nf.setGroupingUsed(false);
		estimatedAltitude = nf.format(estimatedCoordinates.altitude);
		
		double speed = 0;
		double course = 0;

		if (positionProviderHasSpeedOverGround) {
			Double s = positionProvider.getSpeedOverGround(); 
			if (s != null)
				speed = s.doubleValue();
		}
		
		if (positionProviderHasCourseOverGround) {
			Double c = positionProvider.getCourseOverGround();
			if (c != null)
				course = c.doubleValue();
		}

		if (!positionProviderHasSpeedOverGround || !positionProviderHasCourseOverGround) {
			CourseData m = geodeticSystem.calculateSpeedAndCourse (oldPolarCoordinates, estimatedCoordinates, cycleTime);
			if (m != null) {
				if (!positionProviderHasSpeedOverGround)
					speed = m.speed;
				if (!positionProviderHasCourseOverGround)
					course = m.courseIsValid ? m.course : currentCourse;
			}
		}
		currentCourse = course;
		
//		estimatedSpeedOverGroundKnots = String.format (locale, "%1.2f", new Object[] {new Double (speed*1.9438444)});
//		estimatedSpeedOverGroundKmph = String.format (locale, "%1.2f", new Object[] {new Double (speed*3.6)});
//		estimatedCourseOverGround = String.format (locale, "%1.2f", new Object[] {new Double (course)});
		
//		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setMinimumIntegerDigits(1);
		
		estimatedSpeedOverGroundKnots = nf.format(speed*1.9438444);
		estimatedSpeedOverGroundKmph = nf.format(speed*3.6);
		estimatedCourseOverGround = nf.format(course);
	}
	
	/**
	 * This method calculates the NMEA 0183 checksum to a given message.
	 * 
	 * @param message the message.
	 * @return the checksum of the message.
	 */
	public static char[] calculateChecksum (byte[] message) {
		
		int checkSum = 0;
		int k = 0;
		
		while (++k < message.length)
			checkSum ^= message[k];

		int checkSumHigh = 48 + checkSum/16;
		if (checkSumHigh > 57) checkSumHigh += 7;
		
		int checkSumLow = 48 + checkSum%16;
		if (checkSumLow > 57) checkSumLow += 7;
		
		char[] cs = new char[3];
		cs[0] = '*';
		cs[1] = (char)checkSumHigh;
		cs[2] = (char)checkSumLow;
		return cs;
	}
	
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close () throws IOException {
		inputStreamRunning = false;
	}
	
	/**
	 * Set the Position Provider.
	 * 
	 * @param positionProvider the new Position Provider. If this parameter is
	 *            null, a DummyPositionProvider will be used as default.
	 * @uml.property name="positionProvider"
	 */
	public void setPositionProvider (IPositionProvider positionProvider) {
		this.positionProvider = positionProvider;
		
		positionProviderHasSpeedOverGround = false;
		positionProviderHasCourseOverGround = false;
		
		if (positionProvider == null)
			return;
		
		try {
			positionProvider.getSpeedOverGround();
			positionProviderHasSpeedOverGround = true;
		} catch (NotImplementedException e) {
			System.out.println ("GpsReceiverSimulator.setPositionProvider(): IPositionProvider method getSpeedOverGround() is not implemented");
		}
		
		try {
			positionProvider.getCourseOverGround();
			positionProviderHasCourseOverGround = true;
		} catch (NotImplementedException e) {
			System.out.println ("GpsReceiverSimulator.setPositionProvider(): IPositionProvider method getCourseOverGround() is not implemented");
		}
	}
	
	/**
	 * Set the Geodetic System, e.g. WGS84 or SphericEarth.
	 * 
	 * @param geodeticSystem the geodetic system to be used. If this parameter
	 *            is null, a WGS84 will be used as default.
	 * @uml.property name="geodeticSystem"
	 */
	public void setGeodeticSystem (IGeodeticSystem geodeticSystem) {
		this.geodeticSystem = geodeticSystem == null ? new WGS84 () : geodeticSystem;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream () throws IOException {
		return new GpsReceiverInputStream ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream () throws IOException {
		return new GpsReceiverOutputStream ();
	}
	
	/**
	 * This subclass implements the OutputStream of the GPS simulator. Actually
	 * it is a dummy that does nothing.
	 * 
	 * @author Clemens Krainer
	 */
	private class GpsReceiverOutputStream extends OutputStream {

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write (int b) throws IOException {
			// Intentionally empty.			
		}		
	}
	
	/**
	 * This subclass implements the InputStream of the GPS simulator.
	 * 
	 * @author Clemens Krainer
	 */
	private class GpsReceiverInputStream extends InputStream {
		
		/**
		 * This variable indicates the index of the next message to get from the
		 * messageBuffer.
		 */
		private int readIndex = 0;
		
		/**
		 * This variable is an index in the currentLine to the next byte to be
		 * read.
		 */
		private int lineIndex = 0;
		
		/**
		 * The current line taken from the messageBuffer.
		 */
		private byte [] currentLine = null;
		
		/**
		 * This method gets new messages from the global messageBuffer and
		 * returns it byte wise to the caller.
		 * 
		 * @see java.io.InputStream#read()
		 */
		public int read () throws IOException {
			
			if (currentLine != null && lineIndex >= currentLine.length) {
				lineIndex = 0;
				currentLine = null;
			}

			if (currentLine == null) {

				int counter = 0;
				while (readIndex == writeIndex && inputStreamRunning) {
					Thread.yield ();
					if (counter++ > 3)
						try { Thread.sleep (100); }
						catch (Exception e) {
							System.out.println ("GpsReceiverInputStream.read(): sleep interrupted.");
						}
				}
				
				currentLine = messageBuffer[readIndex];

				if (++readIndex >= messageBuffer.length)
					readIndex = 0;
			}
			
			return currentLine[lineIndex++];
		}
	}

}
