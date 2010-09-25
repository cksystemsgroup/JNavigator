/*
 * @(#) LocationMessageSimulator.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2007  Clemens Krainer
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
package javiator.simulation;

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

import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.util.StringUtils;

public class LocationMessageSimulator extends TimerTask implements IConnection {

	/**
	 * This is the property for the message buffer length.  
	 */
	public static final String PROP_BUFFER_LENGTH = "buffer.length";
	
	/**
	 * This is the property for the file containing the simulation data.  
	 */
	
	public static final String PROP_DATA_FILE_NAME = "data.file.name";
	
	/**
	 * 
	 */
	public static final String PROP_TAG_TYPE = "tag.type";
	
	/**
	 * 
	 */
	public static final String PROP_TAG_ID = "tag.id";
	
	/**
	 * The current course. The calculateSpeedAndCourseOverGround() method of the
	 * used geodetic system may or may not return a course. The simulator stores
	 * new course values in this variable. If the estimation is not able to
	 * provide a new course, e.g. because the speed is zero, the simulator uses
	 * the value of this variable instead.
	 */
//	private double currentCourse = 0;
	
	/**
	 * The ring buffer for the NMEA 0183 messages. The receiver simulator
	 * creates new messages and inserts them to this buffer. Whether or not some
	 * LocationMessageSimulatorInputStream instances read messages from this buffer, the
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
	 * <code>precision</code> variable.
	 */
	private CartesianCoordinate[] deviation;
	
	/**
	 * This variable contains the precision data for the corresponding deviation
	 * value. This array has the same length as the <code>deviation</code>
	 * variable.
	 */
	private String[] precision;
	
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
//	private Date oldDateAndTime;
	
	/**
	 * The date and time of the current invocation of the <code>run()</code>
	 * method.
	 */
	private Date dateAndTime;
	
	/**
	 * The difference between <code>dateAndTime</code> and
	 * <code>oldDateAndTime</code> in milliseconds.
	 */
//	private long cycleTime;
	
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
	 * The current time in "YYYY-MM-DD hh:mm:ss.sss" format ready to use for the creation of
	 * NMEA 0183 messages.
	 */
	private String currentTimeStamp;
	
	/**
	 * The new estimated coordinates.
	 */
	private CartesianCoordinate estimatedCoordinates;

	/**
	 * The new estimated X ready to use for the creation of location
	 * messages.
	 */
	private String estimatedX;
	
	/**
	 * The new estimated Y ready to use for the creation of location
	 * messages.
	 */
	private String estimatedY;
	
	/**
	 * The new estimated Z ready to use for the creation of location
	 * messages.
	 */
	private String estimatedZ;
	
	/**
	 * The new estimated precision ready to use for the
	 * creation of location messages.
	 */
	private String estimatedPrecision;
	
	/**
	 * The currently used position provider. The simulator uses a
	 * DummyPositionProvider as default.
	 */
	private IPositionProvider positionProvider;
	
	/**
	 * 
	 */
	private String tagType;
	
	/**
	 * 
	 */
	private String tagID;
	
	/**
	 * Construct a LocationMessageSimulator by means of properties.
	 * 
	 * @param properties the properties to be used. 
	 * @throws IOException thrown in case of errors.
	 */
	public LocationMessageSimulator (Properties props) throws IOException {
		int bufferLength = Integer.parseInt (props.getProperty (PROP_BUFFER_LENGTH,"100"));
		messageBuffer = new byte [bufferLength][];
		
		String dataFileName = props.getProperty (PROP_DATA_FILE_NAME,"locsim.dat");
		InputStream dataFileStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream(dataFileName);
		if (dataFileStream == null)
			throw new FileNotFoundException (dataFileName);
		loadSimulationData (dataFileStream);
		
		calendar = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		locale = new Locale ("en","US");
		
		tagType = props.getProperty (PROP_TAG_TYPE);
		if (tagType == null)
			throw new IOException("Property " + PROP_TAG_TYPE + " not defined.");
		
		tagID = props.getProperty (PROP_TAG_ID);
		if (tagID == null)
			throw new IOException("Property " + PROP_TAG_ID + " not defined.");

	}
	
	/**
	 * Load the simulation data from a given InputStream. This load procedure
	 * interprets the simulation data line by line. A line starting with '#' is
	 * considered as a comment and therefore skipped. Every other line must
	 * contain the values for x, y, z and precision separation. Semicolons (';')
	 * separate the fields from each other.
	 * 
	 * @param simulationData the simulation data as InputStream.
	 * @throws IOException thrown in case of errors.
	 */
	public void loadSimulationData (InputStream simulationData) throws IOException {
		BufferedReader reader = new BufferedReader (new InputStreamReader (simulationData));
		String line;
		Vector lines = new Vector ();
		
		while ((line = reader.readLine()) != null) {
			
			if (line.matches ("\\s*#.*") || line.matches ("\\s*$"))
				continue;
			
			lines.add (line);
		}

		deviation = new CartesianCoordinate [lines.size ()];
		precision = new String [lines.size ()];
		
		for (int k=0; k < lines.size (); k++) {
			String[] x = StringUtils.splitOnCharAndTrim(';',(String)lines.get (k));
			deviation [k] = new CartesianCoordinate (Double.parseDouble (x[0]), Double.parseDouble (x[1]), Double.parseDouble (x[2]) );
			precision [k] = x[3];
		}
	}
	
	/**
	 * A timer invokes this method every timer tick.
	 * 
	 * @see java.util.TimerTask#run()
	 */
	public void run () {
		
		CartesianCoordinate currentPosition = null;
		
		prepareCurrentDateAndTime ();
		
		if (++messageIndex >= deviation.length)
			messageIndex = 0;
		
		if (positionProvider != null) {
			PolarCoordinate p = positionProvider.getCurrentPosition ();
			currentPosition = p == null ? null : new CartesianCoordinate (p.latitude, p.longitude, p.altitude);
		}

		estimateCoordinates (currentPosition);
		
		messageBuffer [writeIndex] = createPnqMessage ();
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
	 * Calculate the checksum of a given message and append the
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

	 * @return the message as an array of bytes.
	 */
	public byte[] createPnqMessage () {
		
		StringBuffer msg = new StringBuffer ("$LOCPNQ,");
		// TODO
		msg.append (tagType).append(",");
		msg.append (tagID).append(",");
		msg.append (currentTimeStamp).append (",");
		msg.append (estimatedPrecision).append(",");
		msg.append ("1").append(",");	// Status
		
		msg.append (estimatedX).append (",");
		msg.append (estimatedY).append (",");
		msg.append (estimatedZ).append (",");
		msg.append ("1.0,0,0,0");
		
		return calculateAndAppendChecksum (msg);
	}
	
	/**
	 * Prepare the current date and time as Strings to be used in the
	 * createXxxMessage() methods. Additionally, calculate the time difference
	 * between the last and the current invocation.
	 */
	public void prepareCurrentDateAndTime () {
		
		dateAndTime = new Date ();		
		calendar.setTime (dateAndTime);
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		nf.setMinimumIntegerDigits(2);
		nf.setGroupingUsed(false);
		
		StringBuffer buf = new StringBuffer ();
				
		nf.setMinimumIntegerDigits(4);
		buf.append (nf.format(calendar.get (Calendar.YEAR))).append ('-');
		nf.setMinimumIntegerDigits(2);
		buf.append (nf.format(calendar.get (Calendar.MONTH)+1)).append ('-');
		buf.append (nf.format(calendar.get (Calendar.DAY_OF_MONTH))).append (' ');
		buf.append (nf.format(calendar.get (Calendar.HOUR_OF_DAY))).append (':');
		buf.append (nf.format(calendar.get (Calendar.MINUTE))).append (':');
		buf.append (nf.format(calendar.get (Calendar.SECOND))).append ('.');
		nf.setMinimumIntegerDigits(3);
		buf.append (nf.format(calendar.get (Calendar.MILLISECOND)));
		currentTimeStamp = buf.toString();
	}
	
	/**
	 * Estimate the current coordinates. This method estimates the new values
	 * for latitude, longitude, altitude, speed over ground and course over
	 * ground.
	 */
	private void estimateCoordinates (CartesianCoordinate currentPosition) {				
		
		if (currentPosition == null) {
			estimatedX = "";
			estimatedY = "";
			estimatedZ = "";
			estimatedPrecision = "1";
			return;
		}

		CartesianCoordinate delta = deviation[messageIndex];
		estimatedCoordinates = delta.add(currentPosition);
		
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(5);
		nf.setMinimumFractionDigits(5);
		nf.setMinimumIntegerDigits(1);
		nf.setGroupingUsed(false);
		estimatedX = nf.format(estimatedCoordinates.x);
		estimatedY = nf.format(estimatedCoordinates.y);
		estimatedZ = nf.format(estimatedCoordinates.z);

//		if (positionProviderHasSpeedOverGround) {
//			Double s = positionProvider.getSpeedOverGround(); 
//			if (s != null)
//				speed = s.doubleValue();
//		}
//		
//		if (positionProviderHasCourseOverGround) {
//			Double c = positionProvider.getCourseOverGround();
//			if (c != null)
//				course = c.doubleValue();
//		}
//
//		if (!positionProviderHasSpeedOverGround || !positionProviderHasCourseOverGround) {
//			CourseData m = geodeticSystem.calculateSpeedAndCourse (oldPolarCoordinates, estimatedCoordinates, cycleTime);
//			if (m != null) {
//				if (!positionProviderHasSpeedOverGround)
//					speed = m.speed;
//				if (!positionProviderHasCourseOverGround)
//					course = m.courseIsValid ? m.course : currentCourse;
//			}
//		}
//		currentCourse = course;
//		
//		nf.setMaximumFractionDigits(2);
//		nf.setMinimumFractionDigits(2);
//		nf.setMinimumIntegerDigits(1);
//		
//		estimatedSpeedOverGroundKnots = nf.format(speed*1.9438444);
//		estimatedSpeedOverGroundKmph = nf.format(speed*3.6);
//		estimatedCourseOverGround = nf.format(course);
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
		// Intentionally empty
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
		
		// TODO
//		positionProviderHasSpeedOverGround = false;
//		positionProviderHasCourseOverGround = false;
//		
//		if (positionProvider == null)
//			return;
//		
//		try {
//			positionProvider.getSpeedOverGround();
//			positionProviderHasSpeedOverGround = true;
//		} catch (NotImplementedException e) {
//			System.out.println ("LocationMessageSimulator.setPositionProvider(): IPositionProvider method getSpeedOverGround() is not implemented");
//		}
//		
//		try {
//			positionProvider.getCourseOverGround();
//			positionProviderHasCourseOverGround = true;
//		} catch (NotImplementedException e) {
//			System.out.println ("LocationMessageSimulator.setPositionProvider(): IPositionProvider method getCourseOverGround() is not implemented");
//		}
	}
	

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream () throws IOException {
		return new LocationMessageSimulatorInputStream ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream () throws IOException {
		return new LocationMessageSimulatorOutputStream ();
	}
	
	
	/**
	 * This subclass implements the OutputStream of the location message simulator. Actually
	 * it is a dummy that does nothing.
	 * 
	 * @author Clemens Krainer
	 */
	private class LocationMessageSimulatorOutputStream extends OutputStream {

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write (int b) throws IOException {
			// Intentionally empty.			
		}		
	}
	
	
	/**
	 * This subclass implements the InputStream of the location message simulator.
	 * 
	 * @author Clemens Krainer
	 */
	private class LocationMessageSimulatorInputStream extends InputStream {
		
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
				while (readIndex == writeIndex) {
					Thread.yield ();
					if (counter++ > 3)
						try { Thread.sleep (100); }
						catch (Exception e) {
							System.out.println ("LocationMessageSimulatorInputStream.read(): sleep interrupted.");
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
