/*****************************************************************************/
/*   This code is part of the JAviator project: javiator.cs.uni-salzburg.at  */
/*                                                                           */
/*   ControlTerminal.java	Runs the JAviator Control Terminal application.  */
/*                                                                           */
/*   Copyright (c) 2006-2009  Rainer Trummer                                 */
/*                                                                           */
/*   This program is free software; you can redistribute it and/or modify    */
/*   it under the terms of the GNU General Public License as published by    */
/*   the Free Software Foundation; either version 2 of the License, or       */
/*   (at your option) any later version.                                     */
/*                                                                           */
/*   This program is distributed in the hope that it will be useful,         */
/*   but WITHOUT ANY WARRANTY; without even the implied warranty of          */
/*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           */
/*   GNU General Public License for more details.                            */
/*                                                                           */
/*   You should have received a copy of the GNU General Public License       */
/*   along with this program; if not, write to the Free Software Foundation, */
/*   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.      */
/*                                                                           */
/*****************************************************************************/

package javiator.terminal;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javiator.signals.SignalsDialog;
import javiator.util.CommandData;
import javiator.util.ControlParams;
import javiator.util.MotorOffsets;
import javiator.util.MotorSignals;
import javiator.util.Packet;
import javiator.util.PacketType;
import javiator.util.RevvingParams;
import javiator.util.SensorData;
import javiator.util.TraceData;
import at.uni_salzburg.cs.ckgroup.ui.AutoPilotFrame;
import at.uni_salzburg.cs.ckgroup.ui.IAutoPilotController;

import com.centralnexus.input.Joystick;

/*****************************************************************************/
/*                                                                           */
/* Class ControlTerminal                                                     */
/*                                                                           */
/*****************************************************************************/

public class ControlTerminal extends Frame implements IAutoPilotController
{
    public static final long serialVersionUID = 1;

    public static final boolean SMALL_DISPLAY = true;
    private boolean show_3D_window = false;
    private boolean autoPilotMode = false;
    private TCPControlService controlService = null;
    private int controlServicePort = 0;
    private boolean externalAutopilot = false;
    private Timer autoPilotBlinker = null;

    public static final String  TRACES_FOLDER = "traces/system_tests/";

    public static final String  LOG_TITLE_STR = "c-roll,c-pitch,c-yaw,c-z," +
                                                "roll,pitch,yaw," +
                                                "droll,dpitch,dyaw," +
                                                "ddroll,ddpitch,ddyaw," +
                                                "ddx,ddy,ddz," +
                                                "front,right,rear,left," +
                                                "u-roll,u-pitch,u-yaw,u-z," +
                                                "sonar,laser," +
                                                "f-sonar,f-laser,f-ddz," +
                                                "est-z,est-dz," +
                                                "f-roll,f-pitch,f-yaw,f-z," +
                                                "p(uz),i(uz),d(uz),dd(uz)," +
                                                "ID";

    public ControlTerminal( )
    {
    	initWindow( );
    }

    public static void main( String args[] ) throws IOException
    {
        new ControlTerminal( );
    }

    public boolean isShow3D( )
    {
    	return ( show_3D_window );
    }
    
    public boolean isAutoPilotMode () {
    	return autoPilotMode;
    }
    
    public void setAutoPilotMode (boolean autoPilotMode) {
    	this.autoPilotMode = autoPilotMode;
    }


    public JAviator3DControlPanel getJaviator3D( )
    {
        return( visualization );
    }

    public void setJaviator3D( JAviator3DControlPanel visualization )
    {
        this.visualization = visualization;
    }

	public boolean isNew_R_P_Params( )
	{
		if( changedParamID[0] >= 0 && changedParamID[0] <= 3 )
		{
            new_R_P_Params = true;
		}

		return( new_R_P_Params );
	}

	public boolean isNew_Yaw_Params( )
	{
		if( changedParamID[0] >= 4 && changedParamID[0] <= 7 )
		{
			new_Yaw_Params = true;
		}

		return( new_Yaw_Params );
	}
	
	public boolean isNew_Alt_Params( )
	{
		if( changedParamID[0] >= 8 && changedParamID[0] <= 11 )
		{
			new_Alt_Params = true;
		}

		return( new_Alt_Params );
	}

	public boolean isNew_X_Y_Params( )
	{
		if( changedParamID[0] >= 12 && changedParamID[0] <= 15 )
		{
            new_X_Y_Params = true;
		}

		return( new_X_Y_Params );
	}

	public boolean isNew_Rev_Params( )
	{
		if( changedParamID[0] >= 16 && changedParamID[0] <= 19 )
		{
            new_Rev_Params = true;
		}

		return( new_Rev_Params );
	}

    public ControlParams getNew_R_P_Params( )
    {    	
    	ControlParams ctrlParams = new ControlParams( );

    	ctrlParams.kp  = (short) controlParams[0];
    	ctrlParams.ki  = (short) controlParams[1];
    	ctrlParams.kd  = (short) controlParams[2];
    	ctrlParams.kdd = (short) controlParams[3];
    	new_R_P_Params = false;

    	return( ctrlParams );
    }

    public ControlParams getNew_Yaw_Params( )
    {    	
    	ControlParams ctrlParams = new ControlParams( );

    	ctrlParams.kp  = (short) controlParams[4];
    	ctrlParams.ki  = (short) controlParams[5];
    	ctrlParams.kd  = (short) controlParams[6];
    	ctrlParams.kdd = (short) controlParams[7];
    	new_Yaw_Params = false;

    	return( ctrlParams );
    }

    public ControlParams getNew_Alt_Params( )
    {    	
    	ControlParams ctrlParams = new ControlParams( );

    	ctrlParams.kp  = (short) controlParams[8];
    	ctrlParams.ki  = (short) controlParams[9];
    	ctrlParams.kd  = (short) controlParams[10];
    	ctrlParams.kdd = (short) controlParams[11];
    	new_Alt_Params = false;

    	return( ctrlParams );
    }

    public ControlParams getNew_X_Y_Params( )
    {    	
    	ControlParams ctrlParams = new ControlParams( );

    	ctrlParams.kp  = (short) controlParams[12];
    	ctrlParams.ki  = (short) controlParams[13];
    	ctrlParams.kd  = (short) controlParams[14];
    	ctrlParams.kdd = (short) controlParams[15];
    	new_X_Y_Params = false;

    	if (externalAutopilot)
    		controlService.receive(ctrlParams);
    	
    	return( ctrlParams );
    }

    public RevvingParams getNew_Rev_Params( )
    {    	
    	RevvingParams revParams = new RevvingParams( );

    	revParams.idleLimit = (short) controlParams[16];
    	revParams.revUpStep = (short) controlParams[17];
    	revParams.revDnStep = (short) controlParams[18];
    	revParams.intDnStep = (short) controlParams[19];
    	new_Rev_Params = false;

    	return( revParams );
    }

    public void resetChangedParamID( )
    {
    	changedParamID[0] = -1;
    }

    public CommandData getCommandData( )
    {
        CommandData data = new CommandData( );

        data.roll  = (short) meterRoll     .getDesired( );
        data.pitch = (short) meterPitch    .getDesired( );
        data.yaw   = (short) meterYaw      .getDesired( );
        data.z     = (short) meterAltitude .getDesired( );

        return( data );
    }

    public int[] getMaximumValues( )
    {
        int[] values = { meterRoll     .getMaximum( ),
                         meterPitch    .getMaximum( ),
                         meterAltitude .getMaximum( ) };

        return( values );
    }

    public void setMaximumValues( int[] values )
    {
        meterRoll     .setMaximum( values[0] );
        meterPitch    .setMaximum( values[1] );
        meterAltitude .setMaximum( values[2] );
    }

	public void setSensorData( SensorData data )
    {
        meterRoll     .setCurrent( data.roll );
        meterPitch    .setCurrent( data.pitch );
        meterYaw      .setCurrent( data.yaw );
        meterAltitude .setCurrent( data.z );

        if( meterAltitude.getDesired( ) == 0 && meterYaw.getDesired( ) == 0 )
        {
        	meterYaw.setDesired( data.yaw );
        }

        if( isShow3D( ) && visualization != null )
        {
            SensorData desiredData = new SensorData( );

            desiredData.roll  = (short) meterRoll     .getDesired( );
            desiredData.pitch = (short) meterPitch    .getDesired( );
            desiredData.yaw   = (short) meterYaw      .getDesired( );
            desiredData.z     = (short) meterAltitude .getDesired( );

            visualization.sendSensorData( data, desiredData );
        }
        
        if( show_diagrams )
        {
        	if( signalsDialog == null )
        	{ 
        		signalsDialog = new SignalsDialog( );
        	}
        	else
        	if( signalsDialog.isClosed( ) )
        	{
        		show_diagrams = false;
        	}

            MotorSignals motor = digitalMeter.getMotorSignals( );

            signalsDialog.z         .add( data.z );
            signalsDialog.roll      .add( data.roll );
            signalsDialog.pitch     .add( data.pitch );
            signalsDialog.yaw       .add( data.yaw );
            signalsDialog.droll     .add( data.droll );
            signalsDialog.dpitch    .add( data.dpitch );
            signalsDialog.dyaw      .add( data.dyaw );
            signalsDialog.ddroll    .add( data.ddroll );
            signalsDialog.ddpitch   .add( data.ddpitch );
            signalsDialog.ddyaw     .add( data.ddyaw );
            signalsDialog.vx        .add( data.dx );
            signalsDialog.vy        .add( data.dy );
            signalsDialog.vz        .add( data.dz );
            signalsDialog.ddx       .add( data.ddx );
            signalsDialog.ddy       .add( data.ddy );
            signalsDialog.ddz       .add( data.ddz );
            signalsDialog.ref_roll  .add( meterRoll.getDesired( ) );
            signalsDialog.ref_pitch .add( meterPitch.getDesired( ) );
            signalsDialog.ref_yaw   .add( meterYaw.getDesired( ) );
            signalsDialog.ref_z     .add( meterAltitude.getDesired( ) );
            signalsDialog.front     .add( motor.front );
            signalsDialog.right     .add( motor.right );
            signalsDialog.rear      .add( motor.rear );
            signalsDialog.left      .add( motor.left );
        }

        batteryLabel.setText( NIL + ( (double)(data.battery / 100) / 10.0 ) );

        if( batteryLabel.getForeground( ) == Color.GREEN )
        {
	        if( data.battery < 14000 )
	        {
	        	batteryLabel.setForeground( Color.ORANGE );
	        }
        }
        else
        if( batteryLabel.getForeground( ) == Color.ORANGE )
        {
	        if( data.battery < 13500 )
	        {
	        	batteryLabel.setForeground( Color.RED );
	        }
	        else
	        if( data.battery > 14000 )
	        {
	        	batteryLabel.setForeground( Color.GREEN );
	        }
        }
        else
        if( batteryLabel.getForeground( ) == Color.RED )
        {
	        if( data.battery > 13500 )
	        {
	        	batteryLabel.setForeground( Color.ORANGE );
	        }
        }
        else
        {
	        batteryLabel.setForeground( Color.RED );
        }
    }

    public void writeLogData( CommandData  commandData,
                              SensorData   sensorData,
                              MotorSignals motorSignals,
                              MotorOffsets motorOffsets,
                              TraceData    traceData )
    {
        if( logData && logDataFile != null )
        {
	        String csv
	            /* Command Data */
                = NIL + (short) commandData.roll
                + ',' + (short) commandData.pitch
                + ',' + (short) commandData.yaw
                + ',' + (short) commandData.z
	            /* Sensor Data */
                + ',' + (short) sensorData.roll
                + ',' + (short) sensorData.pitch
                + ',' + (short) sensorData.yaw
                + ',' + (short) sensorData.droll
                + ',' + (short) sensorData.dpitch
                + ',' + (short) sensorData.dyaw
                + ',' + (short) sensorData.ddroll
                + ',' + (short) sensorData.ddpitch
                + ',' + (short) sensorData.ddyaw
                + ',' + (short) sensorData.ddx
                + ',' + (short) sensorData.ddy
                + ',' + (short) sensorData.ddz
	            /* Motor Signals */
                + ',' + (short) motorSignals.front
                + ',' + (short) motorSignals.right
                + ',' + (short) motorSignals.rear
                + ',' + (short) motorSignals.left
	            /* Motor Offsets */
                + ',' + (short) motorOffsets.roll
                + ',' + (short) motorOffsets.pitch
                + ',' + (short) motorOffsets.yaw
                + ',' + (short) motorOffsets.z
                /* Trace Data */
                + ',' + (short) traceData.value_1
                + ',' + (short) traceData.value_2
                + ',' + (short) traceData.value_3
                + ',' + (short) traceData.value_4
                + ',' + (short) traceData.value_5
                + ',' + (short) traceData.value_6
                + ',' + (short) traceData.value_7
                + ',' + (short) traceData.value_8
                + ',' + (short) traceData.value_9
                + ',' + (short) traceData.value_10
                + ',' + (short) traceData.value_11
                + ',' + (short) traceData.value_12
                + ',' + (short) traceData.value_13
                + ',' + (short) traceData.value_14
                + ',' + (short) traceData.value_15
                + ',' + (short) traceData.value_16
                + '\n';

	        try
	        {
	            logDataFile.write( csv );
	        }
	        catch( Exception e )
	        {
	            System.err.println( "ControlTerminal.setTraceData: " + e.getMessage( ) );
	        }
        }
    }

    public void resetMeterNeedles( )
    {
        if( connected )
        {
            meterRoll     .setDesired( 0 );
            meterPitch    .setDesired( 0 );
            meterYaw      .setDesired( meterYaw.getCurrent( ) );
        }
        else
        {
            meterRoll     .setDesired( 0 );
            meterPitch    .setDesired( 0 );
            meterYaw      .setDesired( 0 );
            meterAltitude .setDesired( 0 );

            meterRoll     .setCurrent( 0 );
            meterPitch    .setCurrent( 0 );
            meterYaw      .setCurrent( 0 );
            meterAltitude .setCurrent( 0 );

            digitalMeter  .resetMotorMeter( );

            if( isShow3D( ) && visualization != null )
            {
                visualization.resetModel( );
            }
        }
    }

    public void setDefaultLimits( )
    {
        meterRoll     .setMaximum( AnalogMeter.DEG_45 );
        meterPitch    .setMaximum( AnalogMeter.DEG_45 );
        meterAltitude .setMaximum( AnalogMeter.DEG_135 );
    }
    
    private static final String LIMIT_ROLL_METER_MAX            = "roll.meter.maximum";
    private static final String LIMIT_PITCH_METER_MAX           = "pitch.meter.maximum";
    private static final String LIMIT_YAW_METER_MAX             = "yaw.meter.maximum";
    private static final String LIMIT_ALTITUDE_METER_MAX        = "altitude.meter.maximum";

    private static final String CONTROL_PARAMETER_ROLLPITCH_KP  = "control.parameter.rollpitch.kp";
    private static final String CONTROL_PARAMETER_ROLLPITCH_KI  = "control.parameter.rollpitch.ki";
    private static final String CONTROL_PARAMETER_ROLLPITCH_KD  = "control.parameter.rollpitch.kd";
    private static final String CONTROL_PARAMETER_ROLLPITCH_KDD = "control.parameter.rollpitch.kdd";
    private static final String CONTROL_PARAMETER_YAW_KP        = "control.parameter.yaw.kp";
    private static final String CONTROL_PARAMETER_YAW_KI        = "control.parameter.yaw.ki";
    private static final String CONTROL_PARAMETER_YAW_KD        = "control.parameter.yaw.kd";
    private static final String CONTROL_PARAMETER_YAW_KDD       = "control.parameter.yaw.kdd";
    private static final String CONTROL_PARAMETER_ALTITUDE_KP   = "control.parameter.altitude.kp";
    private static final String CONTROL_PARAMETER_ALTITUDE_KI   = "control.parameter.altitude.ki";
    private static final String CONTROL_PARAMETER_ALTITUDE_KD   = "control.parameter.altitude.kd";
    private static final String CONTROL_PARAMETER_ALTITUDE_KDD  = "control.parameter.altitude.kdd";
    private static final String CONTROL_PARAMETER_POSITION_KP   = "control.parameter.position.kp";
    private static final String CONTROL_PARAMETER_POSITION_KI   = "control.parameter.position.ki";
    private static final String CONTROL_PARAMETER_POSITION_KD   = "control.parameter.position.kd";
    private static final String CONTROL_PARAMETER_POSITION_KDD  = "control.parameter.position.kdd";

    private static final String CONTROL_PARAMETER_IDLE_LIMIT    = "control.parameter.idle.limit";
    private static final String CONTROL_PARAMETER_REV_UP_STEP   = "control.parameter.rev.up.step";
    private static final String CONTROL_PARAMETER_REV_DOWN_STEP = "control.parameter.rev.down.step";
    private static final String CONTROL_PARAMETER_INT_DOWN_STEP = "control.parameter.int.down.step";
    
    private static final String PROP_RELAY_HOST                 = "relay.host";
    private static final String PROP_RELAY_PORT                 = "relay.port";
    private static final String PROP_CONTROL_SERVICE_PORT       = "control.service.port";
    private static final String EXTERNAL_AUTOPILOT              = "external.autopilot";
    
    private static final String SHOW_3D_WINDOW                  = "show.3d.window";

//    private static final String PROP_SET_COURSE_NAME            = "set.course.name";
    


    public boolean loadConfiguration( String fileName )
    {
    	Properties props = new Properties ();

		try {
			props.load(new FileInputStream(fileName));
		} catch (Exception e) {
			return false;
		}

        meterRoll.setMaximum (Integer.parseInt (props.getProperty (LIMIT_ROLL_METER_MAX, "0")));
        meterPitch.setMaximum (Integer.parseInt (props.getProperty (LIMIT_PITCH_METER_MAX, "0")));
        meterYaw.setMaximum (Integer.parseInt (props.getProperty (LIMIT_YAW_METER_MAX, "0")));
        meterAltitude.setMaximum (Integer.parseInt (props.getProperty (LIMIT_ALTITUDE_METER_MAX, "0")));

        int i=0;
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ROLLPITCH_KP, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ROLLPITCH_KI, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ROLLPITCH_KD, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ROLLPITCH_KDD, "0"));
        
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_YAW_KP, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_YAW_KI, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_YAW_KD, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_YAW_KDD, "0"));
        
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ALTITUDE_KP, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ALTITUDE_KI, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ALTITUDE_KD, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_ALTITUDE_KDD, "0"));
        
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_POSITION_KP, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_POSITION_KI, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_POSITION_KD, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_POSITION_KDD, "0"));

        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_IDLE_LIMIT, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_REV_UP_STEP, "0"));
       	controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_REV_DOWN_STEP, "0"));
        controlParams[i++] = Short.parseShort (props.getProperty (CONTROL_PARAMETER_INT_DOWN_STEP, "0"));
                
        relayHost = props.getProperty (PROP_RELAY_HOST, "localhost");
        relayPort = Integer.parseInt (props.getProperty (PROP_RELAY_PORT, "7000"));

//        autoPilotFrame.setSetCourse (props.getProperty (PROP_SET_COURSE_NAME));

        show_3D_window = Boolean.parseBoolean (props.getProperty (SHOW_3D_WINDOW, "false"));
        
        controlServicePort = Integer.parseInt (props.getProperty(PROP_CONTROL_SERVICE_PORT, "7001"));
        externalAutopilot = Boolean.parseBoolean (props.getProperty (EXTERNAL_AUTOPILOT, "false"));
        
        return( true );

    }

    public boolean saveConfiguration( String fileName )
    {
        Properties props = new Properties ();

        props.setProperty (LIMIT_ROLL_METER_MAX, String.valueOf(meterRoll.getMaximum ()));
        props.setProperty (LIMIT_PITCH_METER_MAX, String.valueOf(meterPitch.getMaximum ()));
        props.setProperty (LIMIT_YAW_METER_MAX, String.valueOf(meterYaw.getMaximum ()));
        props.setProperty (LIMIT_ALTITUDE_METER_MAX, String.valueOf(meterAltitude.getMaximum ()));

        int i=0;
        props.setProperty (CONTROL_PARAMETER_ROLLPITCH_KP,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_ROLLPITCH_KI,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_ROLLPITCH_KD,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_ROLLPITCH_KDD, Short.toString(controlParams[i++]));

        props.setProperty (CONTROL_PARAMETER_YAW_KP,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_YAW_KI,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_YAW_KD,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_YAW_KDD,  Short.toString(controlParams[i++]));

        props.setProperty (CONTROL_PARAMETER_ALTITUDE_KP,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_ALTITUDE_KI,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_ALTITUDE_KD,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_ALTITUDE_KDD,  Short.toString(controlParams[i++]));
        
        props.setProperty (CONTROL_PARAMETER_POSITION_KP,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_POSITION_KI,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_POSITION_KD,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_POSITION_KDD,  Short.toString(controlParams[i++]));
        
        props.setProperty (CONTROL_PARAMETER_IDLE_LIMIT,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_REV_UP_STEP,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_REV_DOWN_STEP,  Short.toString(controlParams[i++]));
        props.setProperty (CONTROL_PARAMETER_INT_DOWN_STEP,  Short.toString(controlParams[i++]));

        props.setProperty (PROP_RELAY_HOST, relayHost);
        props.setProperty (PROP_RELAY_PORT, (new Integer (relayPort)).toString());

//        props.setProperty(PROP_SET_COURSE_NAME, autoPilotFrame.getSetCourse());

        props.setProperty (SHOW_3D_WINDOW, Boolean.toString(show_3D_window));
        
        props.setProperty (PROP_CONTROL_SERVICE_PORT, Integer.toString(controlServicePort));
        props.setProperty (EXTERNAL_AUTOPILOT, Boolean.toString(externalAutopilot));

        try {
			props.store(new FileOutputStream(fileName), null);
		} catch (Exception e) {
			return false;
		}
		return true;
    }

    public synchronized void checkJAviatorSettled( MotorSignals motors )
    {
    	if( motors.front == 0 &&
    		motors.right == 0 &&
    		motors.rear  == 0 &&
    		motors.left  == 0 )
        {
            notifyAll( );
        }
    }

    /*************************************************************************/
    /*                                                                       */
    /* Protected Section                                                     */
    /*                                                                       */
    /*************************************************************************/

    protected static final String NIL              = "";
    protected static final String ZERO             = "0";
    protected static final String EXT_CFG          = ".cfg";
    protected static final String EXT_CSV          = ".csv";
    protected static final String ROLL             = "Roll";
    protected static final String PITCH            = "Pitch";
    protected static final String YAW              = "Yaw";
    protected static final String ALTITUDE         = "Altitude";
    protected static final String THRUST           = "Thrust";
    protected static final String JOYSTICK         = "Joystick";
    protected static final String KEYBOARD         = "Keyboard";
    protected static final String _MODE            = " Mode";
    protected static final String SWITCH           = "Switch";
    protected static final String _HELI            = " Heli";
    protected static final String TCP_             = "TCP ";
    protected static final String UDP_             = "UDP ";
    protected static final String CONNECTION       = "Connection";
    protected static final String CONNECT_TO       = "Connect to";
    protected static final String DISCONNECT       = "Disconnect";
    protected static final String SHUT_DOWN        = "Shut Down";
    protected static final String LOGGING          = "Logging";
    protected static final String _FAILED          = " failed";
    protected static final String _OK              = " ok";
    protected static final String _DATA            = " Data";
    protected static final String PORT_SETTINGS    = "Port Settings";
    protected static final String SET_PARAMETERS   = "Set Parameters";
    protected static final String RESET_NEEDLES    = "Reset Needles";
    protected static final String KEY_ASSISTANCE   = "Key Assistance";
    protected static final String ABOUT_TERMINAL   = "About Terminal";
    protected static final String _CONFIGURATION_  = " configuration ...";
    protected static final String AUTO_PILOT       = "Auto Pilot";
    
    protected AnalogMeter         meterRoll        = null;
    protected AnalogMeter         meterPitch       = null;
    protected AnalogMeter         meterYaw         = null;
    protected AnalogMeter         meterAltitude    = null;
    protected DigitalMeter        digitalMeter     = null;
    protected HiddenButton        toggleControl    = null;
    protected HiddenButton        toggleConnMode   = null;
    protected HiddenButton        toggleConnect    = null;
    protected HiddenButton        switchHeliMode   = null;
    protected HiddenButton        shutDownHeli     = null;
    protected HiddenButton        showAutoPilot    = null;
    protected HiddenButton        setPortAndHost   = null;
    protected HiddenButton        setParameters    = null;
    protected HiddenButton        resetNeedles     = null;
    protected HiddenButton        showKeyAssist    = null;
    protected HiddenButton        showAboutInfo    = null;
    protected FileWriter          logDataFile      = null;
    protected String              logFileName      = null;
    protected String              relayHost        = "192.168.2.3";
    protected int                 relayPort        = 7000;
    protected int                 motionDelay      = 10;
    protected boolean             stickControl     = false;
    protected boolean             udpConnect       = true;
    protected boolean             connected        = false;
    protected boolean             logData          = false;
    protected boolean             traceDialogOpen  = false;
    protected AutoPilotFrame       autoPilotFrame   = null;
    
    protected void finalize( )
    {
        motion.terminate( );
    }

    protected void processWindowEvent( WindowEvent we )
    {
        if( we.getID( ) == WindowEvent.WINDOW_CLOSING )
        {
            System.out.print( "Saving" +  _CONFIGURATION_ );

            if( !saveConfiguration( getClass( ).getSimpleName( ) + EXT_CFG ) )
            {
                System.err.println( _FAILED );
            }
            else
            {
                System.out.println( _OK );
            }

            dispose( );
            System.exit( 0 );
        }
    }

    protected void processFocusEvent( FocusEvent fe )
    {
        motion.setOffsetMaximumRollPitch( 0 );
        motion.setOffsetMaximumAltitude ( 0 );
        motion.setOffsetDesiredRoll     ( 0 );
        motion.setOffsetDesiredPitch    ( 0 );
        motion.setOffsetDesiredYaw      ( 0 );
        motion.setOffsetDesiredAltitude ( 0 );

        meterRoll     .processFocusEvent( fe );
        meterPitch    .processFocusEvent( fe );
        meterYaw      .processFocusEvent( fe );
        meterAltitude .processFocusEvent( fe );
        digitalMeter  .processFocusEvent( fe );
    }

    protected void processKeyEvent( KeyEvent ke )
    {
        switch( ke.getID( ) )
        {
            case KeyEvent.KEY_PRESSED:
                processKeyPressed( ke.getKeyCode( ) );
                break;

            case KeyEvent.KEY_RELEASED:
                processKeyReleased( ke.getKeyCode( ) );
                break;

            default:
                return;
        }
    }

    protected void processKeyPressed( int keyCode )
    {
        switch( keyCode )
        {
            /* command keys assigned to the right hand */
            case KeyEvent.VK_LEFT: /* roll left */
                motion.setOffsetDesiredRoll( -MOTION_STEP );
                break;

            case KeyEvent.VK_RIGHT: /* roll right */
                motion.setOffsetDesiredRoll( MOTION_STEP );
                break;

            case KeyEvent.VK_UP: // pitch forward
                motion.setOffsetDesiredPitch( -MOTION_STEP );
                break;

            case KeyEvent.VK_DOWN: /* pitch backward */
                motion.setOffsetDesiredPitch( MOTION_STEP );
                break;

            /* command keys assigned to the left hand */
            case KeyEvent.VK_A: /* yaw left */
                motion.setOffsetDesiredYaw( -MOTION_STEP );
                break;

            case KeyEvent.VK_D: /* yaw right */
                motion.setOffsetDesiredYaw( MOTION_STEP );
                break;

            case KeyEvent.VK_S: /* descend */
                motion.setOffsetDesiredAltitude( -MOTION_STEP );
                break;

            case KeyEvent.VK_W: /* ascend */
                motion.setOffsetDesiredAltitude( MOTION_STEP );
                break;

            case KeyEvent.VK_F1:
                if( showKeyAssist.isEnabled( ) )
                {
                    doShowKeyAssist( );
                }
                break;

            case KeyEvent.VK_F2:
                if( showAboutInfo.isEnabled( ) )
                {
                    doShowAboutInfo( );
                }
                break;
            case KeyEvent.VK_F3:
                doToggleDiagram( );
                break;

                
            case KeyEvent.VK_F4:
                doToggleLogData( );
                break;

            case KeyEvent.VK_F5:
                if( toggleControl.isEnabled( ) )
                {
                    doToggleControl( );
                }
                break;

            case KeyEvent.VK_F6:
                if( toggleConnMode.isEnabled( ) )
                {
                    doToggleConnMode( );
                }
                break;

            case KeyEvent.VK_F7:
                if( toggleConnect.isEnabled( ) )
                {
                    doToggleConnect( );
                }
                break;

            case KeyEvent.VK_F8:
                if( switchHeliMode.isEnabled( ) )
                {
                    doSwitchHeliMode( );
                }
                break;

            case KeyEvent.VK_F9:
                if( shutDownHeli.isEnabled( ) )
                {
                    doShutDownHeli( );
                }
                break;

            case KeyEvent.VK_F10:
                if( setPortAndHost.isEnabled( ) )
                {
                    doSetPortAndHost( );
                }
                break;

            case KeyEvent.VK_F11:
                if( setParameters.isEnabled( ) )
                {
                    doSetParameters( );
                }
                break;

            case KeyEvent.VK_F12:
                if( resetNeedles.isEnabled( ) )
                {
                    doResetNeedles( );
                }
                break;

            default:
                return;
        }
    }

    protected void processKeyReleased( int keyCode )
    {
        switch( keyCode )
        {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                motion.setOffsetDesiredRoll( 0 );
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                motion.setOffsetDesiredPitch( 0 );
                break;

            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                motion.setOffsetDesiredYaw( 0 );
                break;

            case KeyEvent.VK_S:
            case KeyEvent.VK_W:
                motion.setOffsetDesiredAltitude( 0 );
                break;

            default:
                return;
        }
    }

    protected void setConnected( boolean connected )
    {
        if( this.connected = connected )
        {
            new_R_P_Params = true;
            new_Yaw_Params = true;
            new_Alt_Params = true;
            new_X_Y_Params = true;
            new_Rev_Params = true;
            autoPilotFrame.emptySetCourseList( );
            sendSetCourseFileNames( );
        }

        toggleConnMode .setEnabled( !connected );
        switchHeliMode .setEnabled(  connected && !stickControl );
        shutDownHeli   .setEnabled(  connected );
        setPortAndHost .setEnabled( !connected );
    }

    /*************************************************************************/
    /*                                                                       */
    /* Private Section                                                       */
    /*                                                                       */
    /*************************************************************************/

    private static final int       MOTION_STEP    = 5; // mrad
    private static final int       CTRL_PARAMS    = 20;

    private Image                  iconImage      = null;
    private JAviator3DControlPanel visualization  = null;
    private SignalsDialog		   signalsDialog  = null;
    private MotionThread           motion         = null;
    private Thread                 remoteThread   = null;
    private Transceiver            remote         = null;
    private Joystick               stick          = null;
    private Label                  batteryLabel   = null;
    private Label                  logDataLabel   = null;
    private short[]                controlParams  = null;
    private int[]                  changedParamID = null;
    private boolean                new_R_P_Params = false;
    private boolean                new_Yaw_Params = false;
    private boolean                new_Alt_Params = false;
    private boolean                new_X_Y_Params = false;
    private boolean                new_Rev_Params = false;
    private boolean                show_diagrams  = false;
    private PacketQueue            packetContainer = new PacketQueue ();

    private void initWindow( )
    {
        controlParams  = new short[ CTRL_PARAMS ];
        changedParamID = new int[ 1 ];

        for( int i = 0; i < controlParams.length; ++i )
        {
            controlParams[i] = 0;
        }

        changedParamID[0] = -1;

        setTitle( "JAviator Control Terminal" );
        setBackground( Color.WHITE );
        setLayout( new BorderLayout( ) );
        add( makeNorthPanel( ), BorderLayout.NORTH );
        add( makeCenterPanel( ), BorderLayout.CENTER );
        add( makeSouthPanel( ), BorderLayout.SOUTH );
        setConnected( false );
        pack( );

        System.out.print( "Loading" + _CONFIGURATION_ );

        if( !loadConfiguration( getClass( ).getSimpleName( ) + EXT_CFG ) )
        {
            System.err.println( _FAILED );
            setDefaultLimits( );
            System.out.println( "Default limits have been set" );
        }
        else
        {
            System.out.println( _OK );
        }

        enableEvents( AWTEvent.WINDOW_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK );
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment( ).getCenterPoint( );
        setLocation( center.x - ( getWidth( ) >> 1 ), center.y - ( getHeight( ) >> 1 ) );
        setVisible( true );

        motion = new MotionThread( );
        
        autoPilotFrame = new AutoPilotFrame (AUTO_PILOT, this);
        
        if( isShow3D( ) )
        {
            visualization = new JAviator3DControlPanel( );
            visualization.createModel( );
        }        
       
        try
        {
            iconImage = AnalogMeter.getImage( "pilot_icon.jpg" );
        }
        catch( Exception e )
        {
            System.err.println( "ControlTerminal.initWindow: " + e.getMessage( ) );
            return;
        }

        MediaTracker tracker = new MediaTracker( this );
        tracker.addImage( iconImage, 0 );

        try
        {
            tracker.waitForID( 0 );

            if( tracker.isErrorID( 0 ) )
            {
                System.err.println( "ControlTerminal.initWindow: tracker.isErrorID" );
                return;
            }
        }
        catch( Exception e )
        {
            System.err.println( "ControlTerminal.initWindow: " + e.getMessage( ) );
            return;
        }

        setIconImage( iconImage );
        
        try {
			controlService = new TCPControlService (controlServicePort);
			controlService.start();
			controlService.setSetCourseList(autoPilotFrame);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }

    private Panel makeCenterPanel( )
    {
        meterRoll     = new AnalogMeter( SMALL_DISPLAY, AnalogMeter.TYPE_ROLL );
        meterPitch    = new AnalogMeter( SMALL_DISPLAY, AnalogMeter.TYPE_PITCH );
        meterYaw      = new AnalogMeter( SMALL_DISPLAY, AnalogMeter.TYPE_YAW );
        meterAltitude = new AnalogMeter( SMALL_DISPLAY, AnalogMeter.TYPE_ALTITUDE );
        digitalMeter  = new DigitalMeter( this );
        batteryLabel  = new Label( ZERO, Label.CENTER );
        logDataLabel  = new Label( LOGGING + _DATA, Label.CENTER );
        logDataLabel.setForeground( Color.LIGHT_GRAY );

        HiddenButton incAltitudeLimit = new HiddenButton( HiddenButton.SYMBOL_PLUS );
        incAltitudeLimit.setForeground( Color.GRAY );
        incAltitudeLimit.addMouseListener( new MouseAdapter( )
        {
            public void mousePressed( MouseEvent me )
            {
                motion.setOffsetMaximumAltitude( MOTION_STEP );
            }

            public void mouseReleased( MouseEvent me )
            {
                motion.setOffsetMaximumAltitude( 0 );
            }
        } );

        HiddenButton decAltitudeLimit = new HiddenButton( HiddenButton.SYMBOL_MINUS );
        decAltitudeLimit.setForeground( Color.GRAY );
        decAltitudeLimit.addMouseListener( new MouseAdapter( )
        {
            public void mousePressed( MouseEvent me )
            {
                motion.setOffsetMaximumAltitude( -MOTION_STEP );
            }

            public void mouseReleased( MouseEvent me )
            {
                motion.setOffsetMaximumAltitude( 0 );
            }
        } );

        HiddenButton incRollPitchLimit = new HiddenButton( HiddenButton.SYMBOL_PLUS );
        incRollPitchLimit.setForeground( Color.GRAY );
        incRollPitchLimit.addMouseListener( new MouseAdapter( )
        {
            public void mousePressed( MouseEvent me )
            {
                motion.setOffsetMaximumRollPitch( MOTION_STEP );
            }

            public void mouseReleased( MouseEvent me )
            {
                motion.setOffsetMaximumRollPitch( 0 );
            }
        } );

        HiddenButton decRollPitchLimit = new HiddenButton( HiddenButton.SYMBOL_MINUS );
        decRollPitchLimit.setForeground( Color.GRAY );
        decRollPitchLimit.addMouseListener( new MouseAdapter( )
        {
            public void mousePressed( MouseEvent me )
            {
                motion.setOffsetMaximumRollPitch( -MOTION_STEP );
            }

            public void mouseReleased( MouseEvent me )
            {
                motion.setOffsetMaximumRollPitch( 0 );
            }
        } );

        Panel altitudeLimitButtons = new Panel( new GridLayout( 1, 6 ) );
        altitudeLimitButtons.add( new Label( ) );
        altitudeLimitButtons.add( new Label( ) );
        altitudeLimitButtons.add( decAltitudeLimit );
        altitudeLimitButtons.add( incAltitudeLimit );
        altitudeLimitButtons.add( new Label( ) );
        altitudeLimitButtons.add( new Label( ) );

        Panel rollPitchLimitButtons = new Panel( new GridLayout( 1, 6 ) );
        rollPitchLimitButtons.add( new Label( ) );
        rollPitchLimitButtons.add( new Label( ) );
        rollPitchLimitButtons.add( decRollPitchLimit );
        rollPitchLimitButtons.add( incRollPitchLimit );
        rollPitchLimitButtons.add( new Label( ) );
        rollPitchLimitButtons.add( new Label( ) );

        Panel yawAltitudePanel = new Panel( new BorderLayout( ) );
        yawAltitudePanel.add( meterYaw, BorderLayout.NORTH );
        yawAltitudePanel.add( meterAltitude, BorderLayout.CENTER );
        yawAltitudePanel.add( altitudeLimitButtons, BorderLayout.SOUTH );

        Panel rollPitchPanel = new Panel( new BorderLayout( ) );
        rollPitchPanel.add( meterRoll, BorderLayout.NORTH );
        rollPitchPanel.add( meterPitch, BorderLayout.CENTER );
        rollPitchPanel.add( rollPitchLimitButtons, BorderLayout.SOUTH );

        Panel batteryPanel = new Panel( new BorderLayout( ) );
        batteryPanel.add( new Label( "Battery", Label.LEFT ), BorderLayout.WEST );
        batteryPanel.add( batteryLabel, BorderLayout.CENTER );
        batteryPanel.add( new Label( "Volts", Label.RIGHT ), BorderLayout.EAST );
        
        Panel motorMeterPanel = new Panel( new BorderLayout( ) );
        motorMeterPanel.add( batteryPanel, BorderLayout.NORTH );
        motorMeterPanel.add( digitalMeter, BorderLayout.CENTER );
        motorMeterPanel.add( logDataLabel, BorderLayout.SOUTH );

        Panel centerPanel = new Panel( new BorderLayout( ) );
        centerPanel.add( yawAltitudePanel, BorderLayout.WEST );
        centerPanel.add( motorMeterPanel, BorderLayout.CENTER );
        centerPanel.add( rollPitchPanel, BorderLayout.EAST );

        return( centerPanel );
    }

    private Panel makeNorthPanel( )
    {
        toggleControl = new HiddenButton( JOYSTICK + _MODE );
        toggleControl.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doToggleControl( );
            }
        } );

        toggleConnMode = new HiddenButton( (udpConnect ? UDP_ : TCP_) + CONNECTION );
        toggleConnMode.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doToggleConnMode( );
            }
        } );

        toggleConnect = new HiddenButton( CONNECT_TO + _HELI );
        toggleConnect.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doToggleConnect( );
            }
        } );

        switchHeliMode = new HiddenButton( SWITCH + _HELI + _MODE );
        switchHeliMode.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doSwitchHeliMode( );
            }
        } );

        shutDownHeli = new HiddenButton( SHUT_DOWN + _HELI );
        shutDownHeli.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doShutDownHeli( );
            }
        } );
        
        showAutoPilot = new HiddenButton( AUTO_PILOT );
        showAutoPilot.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doShowAutoPilot ();
            }
        } );
        AutoPilotBlinker blinker = new AutoPilotBlinker ();
        autoPilotBlinker = new Timer();
        autoPilotBlinker.schedule(blinker, 0, 500);


        Panel buttonPanel = new Panel( new GridLayout( 1, 5 ) );
        buttonPanel.add( toggleControl );
        buttonPanel.add( toggleConnMode );
        buttonPanel.add( toggleConnect );
        buttonPanel.add( switchHeliMode );
        buttonPanel.add( shutDownHeli );
        buttonPanel.add( showAutoPilot );

        Panel northPanel = new Panel( new BorderLayout( ) );
        northPanel.add( buttonPanel, BorderLayout.NORTH );
        northPanel.add( new Label( ), BorderLayout.SOUTH );

        return( northPanel );
    }

    private Panel makeSouthPanel( )
    {
        setPortAndHost = new HiddenButton( PORT_SETTINGS );
        setPortAndHost.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doSetPortAndHost( );
            }
        } );

        setParameters = new HiddenButton( SET_PARAMETERS );
        setParameters.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doSetParameters( );
            }
        } );

        resetNeedles = new HiddenButton( RESET_NEEDLES );
        resetNeedles.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doResetNeedles( );
            }
        } );

        showKeyAssist = new HiddenButton( KEY_ASSISTANCE );
        showKeyAssist.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doShowKeyAssist( );
            }
        } );

        showAboutInfo = new HiddenButton( ABOUT_TERMINAL );
        showAboutInfo.addMouseListener( new MouseAdapter( )
        {
            public void mouseClicked( MouseEvent me )
            {
                doShowAboutInfo( );
            }
        } );

        Panel buttonPanel = new Panel( new GridLayout( 1, 5 ) );
        buttonPanel.add( setPortAndHost );
        buttonPanel.add( setParameters );
        buttonPanel.add( resetNeedles );
        buttonPanel.add( showKeyAssist );
        buttonPanel.add( showAboutInfo );

        Panel southPanel = new Panel( new BorderLayout( ) );
        southPanel.add( new Label( ), BorderLayout.NORTH );
        southPanel.add( buttonPanel, BorderLayout.SOUTH );

        return( southPanel );
    }

    private void doToggleControl( )
    {
        if( !stickControl )
        {
            try
            {
                stick = Joystick.createInstance( );
                stick.setPollInterval( motionDelay );
                stick.setDeadZone( 0.0f );
            }
            catch( Exception e )
            {
                toggleControl.setForeground( Color.RED );
                toggleControl.setText( JOYSTICK + _FAILED );
                System.err.println( "ControlTerminal.doToggleControl: " + e.getMessage( ) );
                return;
            }

            stickControl = true;
            toggleControl.setForeground( Color.BLACK );
            toggleControl.setText( KEYBOARD + _MODE );
        }
        else
        {
            stickControl = false;
            toggleControl.setForeground( Color.BLACK );
            toggleControl.setText( JOYSTICK + _MODE );
        }
        
        switchHeliMode.setEnabled( connected && !stickControl );
    }

    private void doToggleConnMode( )
    {
        	toggleConnMode.setText( ( (udpConnect = !udpConnect) ? UDP_ : TCP_) + CONNECTION );   
    }

    private void doToggleConnect( )
    {
        if( !connected )
        {
        	System.out.println ("doToggleConnect: udpConnect=" + udpConnect);
            resetMeterNeedles( );
            
            remote = udpConnect ? new UDPTransceiver(this, relayHost, relayPort)
            					: new TCPTransceiver(this, relayHost, relayPort);
            remote.connect( );
            if (!externalAutopilot)
            	remote.addPacketContainer(packetContainer);

            if( remote.isConnected( ) )
            {
            	System.out.println ("doToggleConnect: connection established. ext=" + externalAutopilot);
                remoteThread = new Thread( remote );
                remoteThread.start( );
                if (controlService != null) {
	                controlService.addCommandDataListener(remote);
	                if (externalAutopilot) {
	                	controlService.addPacketContainer(packetContainer);
	                	System.out.println ("doToggleConnect: packet container registered.");
	                }
	                remote.addSensorDataListener(controlService);
	                
                }
            } else {
            	System.out.println ("doToggleConnect: connection NOT established.");
            	remote.disconnect();
            }
        }
        else
        {
        	System.out.println ("doToggleConnect: disconnect.");
        	
        	if (controlService != null) {
	        	controlService.removeCommandDataListener(remote);
	        	remote.removeSensorDataListener(controlService);
        	}
            remote.terminate( );

            try
            {
                remoteThread.join( );
            }
            catch( Exception e )
            {
                System.err.println( "ControlTerminal.doToggleConnect: " + e.getMessage( ) );
            }

            remote       = null;
            remoteThread = null;
        }
    }

    private Packet switchPacket = new Packet( PacketType.COMM_SWITCH_MODE, null );
    private void doSwitchHeliMode( )
    {
    	meterYaw.setDesired( meterYaw.getCurrent( ) );

    	if( remote != null )
    	{
            remote.sendPacket( switchPacket );
    	}
    }

    private Packet shutDownPacket = new Packet( PacketType.COMM_SHUT_DOWN, null );
    private void doShutDownHeli( )
    {
    	if( remote != null )
    	{
    		remote.sendPacket( shutDownPacket );
    	}
    }
    
    private void doShowAutoPilot ()
    {
        sendSetCourseFileNames ();
        autoPilotFrame.setVisible (true);
    }

	private void doSetPortAndHost( )
    {
        PortDialog.createInstance( this, PORT_SETTINGS );
    }

    private void doSetParameters( )
    {
        ParamDialog.createInstance( this, SET_PARAMETERS, controlParams, changedParamID );
    }

    private void doResetNeedles( )
    {
        resetMeterNeedles( );
    }

    private void doShowKeyAssist( )
    {
        InfoDialog.createInstance( this, KEY_ASSISTANCE, InfoDialog.TYPE_KEY_ASSISTANCE );
    }

    private void doShowAboutInfo( )
    {
        InfoDialog.createInstance( this, ABOUT_TERMINAL, InfoDialog.TYPE_ABOUT_TERMINAL );
    }

    private void doToggleDiagram( )
    {    	
    	if( !( show_diagrams = !show_diagrams ) )
    	{
    		if( signalsDialog == null )
    		{
    			signalsDialog = new SignalsDialog( );
    		}

    		signalsDialog.open( );
    	}
    }
    
    private void doToggleLogData( )
    {
        if( traceDialogOpen )
        {
            return;
        }

        if( ( logData = !logData ) )
        {
            logFileName = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US ).format( new Date( ) );

	        try
	        {
                logDataFile = new FileWriter( TRACES_FOLDER + logFileName + EXT_CSV, true );
	            logDataFile.write( LOG_TITLE_STR + '\n' );
	            logDataLabel.setForeground( Color.RED );
	        }
	        catch( Exception e )
	        {
	            System.err.println( "ControlTerminal.doToggleLogData: " + e.getMessage( ) );
	        }
        }
        else
        {
	        if( logDataFile != null )
	        {
		        try
		        {
		        	logDataFile.close( );
		        	logDataLabel.setForeground( Color.LIGHT_GRAY );
		        }
		        catch( Exception e )
		        {
		            System.err.println( "ControlTerminal.doToggleLogData: " + e.getMessage( ) );
		        }
		        
		        logDataFile = null;
	        }

            TraceDialog.createInstance( this, "Trace Description" );
        }
    }

    /*************************************************************************/
    /*                                                                       */
    /* Class MotionThread                                                    */
    /*                                                                       */
    /*************************************************************************/

    private class MotionThread extends Thread
    {
        public MotionThread( )
        {
            start( );
        }

        public void run( )
        {
            while( true )
            {
                synchronized( this )
                {
                    if( halt )
                    {
                        break;
                    }
                }

                if( stickControl )
                {
                    processJoystick( );
                }
                else
                {
                    processKeyboard( );
                }

                // animate the three maximum-value needles
                //
                if( offsetMaximumRollPitch != 0 )
                {
                    meterPitch .setMaximum( meterPitch .getMaximum( ) + offsetMaximumRollPitch );
                	meterRoll  .setMaximum( meterRoll  .getMaximum( ) + offsetMaximumRollPitch );
                }

                if( offsetMaximumAltitude != 0 )
                {
                    meterAltitude.setMaximum( meterAltitude.getMaximum( ) + offsetMaximumAltitude );
                }

                try
                {
                    sleep( motionDelay );
                }
                catch( InterruptedException e )
                {
                    System.err.println( "MotionThread.run: " + e.getMessage( ) );
                    break;
                }
            }
        }

        public void setOffsetMaximumRollPitch( int offsetMaximumRollPitch )
        {
            this.offsetMaximumRollPitch = offsetMaximumRollPitch;
        }

        public void setOffsetMaximumAltitude( int offsetMaximumAltitude )
        {
            this.offsetMaximumAltitude = offsetMaximumAltitude;
        }

        public void setOffsetDesiredRoll( int offsetDesiredRoll )
        {
            this.offsetDesiredRoll = offsetDesiredRoll;
        }

        public void setOffsetDesiredPitch( int offsetDesiredPitch )
        {
            this.offsetDesiredPitch = offsetDesiredPitch;
        }

        public void setOffsetDesiredYaw( int offsetDesiredYaw )
        {
            this.offsetDesiredYaw = offsetDesiredYaw;
        }

        public void setOffsetDesiredAltitude( int offsetDesiredAltitude )
        {
            this.offsetDesiredAltitude = offsetDesiredAltitude;
        }

        public void terminate( )
        {
            synchronized( this )
            {
                halt = true;
            }

            try
            {
                join( );
            }
            catch( Exception e )
            {
                System.err.println( "MotionThread.terminate: " + e.getMessage( ) );
            }
        }

        /*********************************************************************/
        /*                                                                   */
        /* Private Section                                                   */
        /*                                                                   */
        /*********************************************************************/

        private int     offsetMaximumRollPitch = 0;
        private int     offsetMaximumAltitude  = 0;
        private int     offsetDesiredRoll      = 0;
        private int     offsetDesiredPitch     = 0;
        private int     offsetDesiredYaw       = 0;
        private int     offsetDesiredAltitude  = 0;
        private int     buttonsPressed         = 0;
        private int     buttonNotPressed       = 0;
        private boolean alreadyToggled         = false;
        private boolean halt                   = false;

        private int correct_yaw( int yaw )
        {
            if( yaw < -AnalogMeter.DEG_180 )
	        {
                yaw += AnalogMeter.DEG_360;
            }
            else
            if( yaw > AnalogMeter.DEG_180 )
	        {
                yaw -= AnalogMeter.DEG_360;
            }

            return( yaw );
        }

        private void processJoystick( )
        {
            stick.poll( );
            buttonsPressed = stick.getButtons( );

			if( !stick.isButtonDown( Joystick.BUTTON1 ) ) 
            {
				if( ++buttonNotPressed > 5 )
				{
					doShutDownHeli( );
				}
			}
            else
            if( (buttonsPressed & Joystick.BUTTON10) != 0 )
            {
				// toggle the helicopter mode button
				//
				if( !alreadyToggled )
                {
                    alreadyToggled = true;
                    doSwitchHeliMode( );
				}

				buttonNotPressed = 0;
			}
            else
            {
            	buttonNotPressed = 0;
                alreadyToggled = false;
            }

            // reset the green meter needles
            //
            if( ( buttonsPressed & Joystick.BUTTON7 ) != 0 )
            {
                resetMeterNeedles( );   
            }

            // update the four desired-value needles
            //
            meterRoll  .setDesired( (int)( meterRoll  .getMaximum( ) * stick.getX( ) ) );
            meterPitch .setDesired( (int)( meterPitch .getMaximum( ) * stick.getY( ) ) );

            if( ( buttonsPressed & Joystick.BUTTON2 ) != 0 )
            {
	            meterYaw.setDesired( correct_yaw( meterYaw.getDesired( ) + (int)( stick.getZ( ) * 10.0f ) ) );
            }

            meterAltitude.setDesired( (int)( meterAltitude.getMaximum( ) * ( stick.getR( ) - 1.0f ) * -0.5f ) );
        }

        private void processKeyboard( )
        {
            // animate the four desired-value needles
            //
            if( offsetDesiredRoll != 0 )
	        {
	            meterRoll.setDesired( meterRoll.getDesired( ) + offsetDesiredRoll );
	        }
	
	        if( offsetDesiredPitch != 0 )
	        {
	            meterPitch.setDesired( meterPitch.getDesired( ) + offsetDesiredPitch );
            }

	        if( offsetDesiredYaw != 0 )
	        {
                meterYaw.setDesired( correct_yaw( meterYaw.getDesired( ) + offsetDesiredYaw ) );
	        }
	
	        if( offsetDesiredAltitude != 0 )
	        {
	            meterAltitude.setDesired( meterAltitude.getDesired( ) + offsetDesiredAltitude );
            }
        }
    }
    
    /* (non-Javadoc)
     * @see at.uni_salzburg.cs.ckgroup.ui.IAutoPilotController#startAutoPilot()
     */
    public void startAutoPilot(String setCourse)
    {
        System.out.println ("startAutoPilot() '" + setCourse +"'");
        StringBuffer b = new StringBuffer ();
        b.append("CMD,AUTOPILOT START," + setCourse);
        packetContainer.add(PacketType.COMM_PILOT_DATA, b.toString().getBytes());
        autoPilotMode = true;
    }

    /* (non-Javadoc)
     * @see at.uni_salzburg.cs.ckgroup.ui.IAutoPilotController#stopAutoPilot()
     */
        public void stopAutoPilot()
    {
        System.out.println ("stopAutoPilot()");
        packetContainer.add(PacketType.COMM_PILOT_DATA, "CMD,AUTOPILOT STOP".getBytes());
        autoPilotMode = false;
    }

    /* (non-Javadoc)
     * @see at.uni_salzburg.cs.ckgroup.ui.IAutoPilotController#sendSetCourseFileNames()
     */
    public void sendSetCourseFileNames ()
    {
        System.out.println ("sendSetCourseFileNames()");
        packetContainer.add(PacketType.COMM_PILOT_DATA, "CMD,AUTOPILOT SEND SET COURSE FILE NAMES".getBytes());
    }

    private class AutoPilotBlinker extends TimerTask {

    	private boolean flag = false;
    	
		@Override
		public void run() {
			flag = !flag;
			showAutoPilot.setBackground(autoPilotMode && flag ? Color.RED : Color.WHITE);
		}
    	
    }
}

// End of file.