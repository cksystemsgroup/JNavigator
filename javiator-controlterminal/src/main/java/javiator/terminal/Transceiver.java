/*****************************************************************************/
/*   This code is part of the JAviator project: javiator.cs.uni-salzburg.at  */
/*                                                                           */
/*   Transceiver.java    Establishes and runs the serial connection between  */
/*                       the JAviator and the control terminal.              */
/*                                                                           */
/*   Copyright (c) 2006-2009  Rainer Trummer, Harald Roeck                   */
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

import java.awt.Color;
import java.util.Vector;

import javiator.util.CommandData;
import javiator.util.ICommandDataListener;
import javiator.util.IPacketContainer;
import javiator.util.ISensorDataListener;
import javiator.util.ISensorDataProvider;
import javiator.util.MotorOffsets;
import javiator.util.MotorSignals;
import javiator.util.Packet;
import javiator.util.PacketType;
import javiator.util.ReportToGround;
import javiator.util.SensorData;
import javiator.util.TraceData;

/*****************************************************************************/
/*                                                                           */
/*   Class Transceiver                                                       */
/*                                                                           */
/*****************************************************************************/

public abstract class Transceiver extends javiator.util.Transceiver implements ISensorDataProvider, ICommandDataListener
{
    public static final long serialVersionUID = 1;
    public static final int  UDP_CONNECT      = 1;
	public static final int  TCP_CONNECT      = 2;
	
	/** A set of <code>SensorDataListeners</code> */
    private Vector<ISensorDataListener> sensorDataListeners = new Vector<ISensorDataListener> ();

    /** A container of possible extra packets to send when calling sendPacket() */
    private Vector<IPacketContainer> packetContainers = new Vector<IPacketContainer> ();


    public void run( )
    {
        receive( );
    }

    public void terminate( )
    {
        super.terminate( );
        parent.setConnected( false );
        parent.toggleConnect.setForeground( Color.BLACK );
        parent.toggleConnect.setText( ControlTerminal.CONNECT_TO + ControlTerminal._HELI );
//        Instance = null;
    }

    /*************************************************************************/
    /*                                                                       */
    /*   Protected Section                                                   */
    /*                                                                       */
    /*************************************************************************/

    protected ControlTerminal parent       = null;
    protected CommandData     commandData  = null;
    protected SensorData      sensorData   = null;
    protected MotorSignals    motorSignals = null;
    protected MotorOffsets    motorOffsets = null;
    protected TraceData       traceData    = null;
    
    protected static final int MAX_COMMAND_DATA_IDLE_CYCLES = 20;
    protected int commandDataIdleCycles = MAX_COMMAND_DATA_IDLE_CYCLES+1;

    protected Transceiver( ControlTerminal parent )
    {
        super( parent.getClass( ).getSimpleName( ) );

        this.parent  = parent;
        commandData  = new CommandData( );
        sensorData   = new SensorData( );
        motorSignals = new MotorSignals( );
        motorOffsets = new MotorOffsets( );
        traceData    = new TraceData( );
    }

    protected void setLinked( boolean linked )
    {
        parent.setConnected( linked ); // enable connection-dependent buttons

        try
        {
            if( linked )
            {
                parent.toggleConnect.setForeground( Color.BLUE );
                parent.toggleConnect.setText( "JAviator connected" );
                Thread.sleep( 2000 );
                parent.toggleConnect.setForeground( Color.BLACK );
                parent.toggleConnect.setText( ControlTerminal.DISCONNECT + ControlTerminal._HELI );
            }
            else
            {
                parent.toggleConnect.setText( ControlTerminal.CONNECTION + ControlTerminal._FAILED );

                for( int i = 0; i < 5; ++i )
                {
                    parent.toggleConnect.setForeground( Color.RED );
                    Thread.sleep( 500 );
                    parent.toggleConnect.setForeground( Color.WHITE );
                    Thread.sleep( 250 );
                }

                parent.toggleConnect.setForeground( Color.BLACK );
                parent.toggleConnect.setText( ControlTerminal.CONNECT_TO + ControlTerminal._HELI );
            }
        }
        catch( InterruptedException e )
        {
            System.err.println( "Transceiver.setLinked: " + e.getMessage( ) );
        }
    }

    protected void processPacket( Packet packet )
    {
        switch( packet.type )
        {
	        case PacketType.COMM_SENSOR_DATA:
	        	
	        	if (parent.isAutoPilotMode() && commandDataIdleCycles > MAX_COMMAND_DATA_IDLE_CYCLES)
	        		parent.setAutoPilotMode(false);
	        	
	        	if (parent.isAutoPilotMode()) {
	        		++commandDataIdleCycles;
	        	} else {
	        		parent.getCommandData( ).copyTo( commandData );
	        	}
                sendPacket( commandData.toPacket( PacketType.COMM_COMMAND_DATA ) );
                if( parent.isNew_R_P_Params( ) )
                {
            	    sendPacket( parent.getNew_R_P_Params( ).toPacket( PacketType.COMM_R_P_PARAMS ) );
                }
                if( parent.isNew_Yaw_Params( ) )
                {
            	    sendPacket( parent.getNew_Yaw_Params( ).toPacket( PacketType.COMM_YAW_PARAMS ) );
                }
                if( parent.isNew_Alt_Params( ) )
                {
            	    sendPacket( parent.getNew_Alt_Params( ).toPacket( PacketType.COMM_ALT_PARAMS ) );
                }
                if( parent.isNew_X_Y_Params( ) )
                {
            	    sendPacket( parent.getNew_X_Y_Params( ).toPacket( PacketType.COMM_X_Y_PARAMS ) );
                }
                if( parent.isNew_Rev_Params( ) )
                {
            	    sendPacket( parent.getNew_Rev_Params( ).toPacket( PacketType.COMM_IDLE_LIMIT ) );
                }
                parent.resetChangedParamID( );
	        	sensorData.fromPacket( packet );
	            parent.setSensorData( sensorData );
	            fireSensorData (sensorData);                                /* send one packet only from each of the packet containers */
                for (int k=0; k < packetContainers.size(); k++)
                {
                        IPacketContainer container = (IPacketContainer)packetContainers.get(k);
                        if (container.hasMoreElements())
                                sendPacket ((Packet)container.nextElement());
                }
            break;

            case PacketType.COMM_MOTOR_SIGNALS:
                motorSignals.fromPacket( packet );
                parent.digitalMeter.setMotorSignals( motorSignals );
                parent.checkJAviatorSettled( motorSignals );
                break;

            case PacketType.COMM_MOTOR_OFFSETS:
                motorOffsets.fromPacket( packet );
                parent.digitalMeter.setMotorOffsets( motorOffsets );
                break;

            case PacketType.COMM_STATE_MODE:
                parent.digitalMeter.setStateAndMode( packet.payload[0], packet.payload[1] );
                break;

            case PacketType.COMM_GROUND_REPORT:
            	
	        	if (parent.isAutoPilotMode() && commandDataIdleCycles > MAX_COMMAND_DATA_IDLE_CYCLES)
	        		parent.setAutoPilotMode(false);
	        	
	        	if (parent.isAutoPilotMode()) {
	        		++commandDataIdleCycles;
	        	} else {
	        		parent.getCommandData( ).copyTo( commandData );
	        	}
	        	// parent.getCommandData( ).copyTo( commandData );
                sendPacket( commandData.toPacket( PacketType.COMM_COMMAND_DATA ) );
                if( parent.isNew_R_P_Params( ) )
                {
            	    sendPacket( parent.getNew_R_P_Params( ).toPacket( PacketType.COMM_R_P_PARAMS ) );
                }
                if( parent.isNew_Yaw_Params( ) )
                {
            	    sendPacket( parent.getNew_Yaw_Params( ).toPacket( PacketType.COMM_YAW_PARAMS ) );
                }
                if( parent.isNew_Alt_Params( ) )
                {
            	    sendPacket( parent.getNew_Alt_Params( ).toPacket( PacketType.COMM_ALT_PARAMS ) );
                }
                if( parent.isNew_X_Y_Params( ) )
                {
            	    sendPacket( parent.getNew_X_Y_Params( ).toPacket( PacketType.COMM_X_Y_PARAMS ) );
                }
                if( parent.isNew_Rev_Params( ) )
                {
            	    sendPacket( parent.getNew_Rev_Params( ).toPacket( PacketType.COMM_IDLE_LIMIT ) );
                }
                parent.resetChangedParamID( );
                ReportToGround report = new ReportToGround( );
                report.fromPacket( packet );
                report.sensorData.copyTo( sensorData );
                parent.setSensorData( sensorData );
	            fireSensorData (sensorData);                                /* send one packet only from each of the packet containers */
                for (int k=0; k < packetContainers.size(); k++)
                {
                        IPacketContainer container = (IPacketContainer)packetContainers.get(k);
                        if (container.hasMoreElements())
                                sendPacket ((Packet)container.nextElement());
                }
                report.motorSignals.copyTo( motorSignals ); 
                parent.digitalMeter.setMotorSignals( motorSignals );
                parent.checkJAviatorSettled( motorSignals );
                report.commandData.copyTo( commandData );
                parent.digitalMeter.setMotorOffsets( motorOffsets );
                parent.digitalMeter.setStateAndMode( report.stateAndMode.state, report.stateAndMode.mode );
                break;

            case PacketType.COMM_TRACE_DATA:
                traceData.fromPacket( packet );
	            parent.writeLogData( commandData, sensorData, motorSignals, motorOffsets, traceData );
                break;

            case PacketType.COMM_PILOT_DATA:
                System.out.println ("PILOT_DATA@Transceiver: " + new String (packet.payload));
                String payload = new String (packet.payload);
                if (payload.startsWith("AUTOPILOT FILE NAME ")) {
                        String reset = payload.substring(20,21);
                        if (reset.equals("r"))
                        	parent.autoPilotFrame.emptySetCourseList();
                        String fileName = payload.substring(21);
                        parent.autoPilotFrame.addSetCourseFileName (fileName);
                }
                break;

            default:
                return;
        }
    }
    
	/* (non-Javadoc)
	 * @see javiator.util.ISensorDataProvider#addSensorDataListener(javiator.util.ISensorDataListener)
	 */
	public void addSensorDataListener(ISensorDataListener listener) {
		sensorDataListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see javiator.util.ISensorDataProvider#removeSensorDataListener(javiator.util.ISensorDataListener)
	 */
	public void removeSensorDataListener(ISensorDataListener listener) {
		while (sensorDataListeners.remove(listener))
			continue;
	}

	/**
	 * Distribute the newly available <code>SensorData</code> to all listeners.
	 * 
	 * @param sensorData
	 *            the newly available <code>SensorData</code>
	 */
	private void fireSensorData(SensorData sensorData) {
		for (int k = 0; k < sensorDataListeners.size(); k++)
			sensorDataListeners.get(k).receive(sensorData);
	}
	
	/* (non-Javadoc)
	 * @see javiator.util.ICommandDataListener#receive(javiator.util.CommandData)
	 */
//	private long counter = 0;
	public void receive(CommandData data) {
		commandDataIdleCycles = 0;
		data.copyTo (commandData);
//		if (counter > 50) {
//			System.out.print('.');
//			counter = 0;
//		}
	}
	
	/* (non-Javadoc)
	 * @see javiator.util.ICommandDataListener#setProcessData(boolean)
	 */
	public void setProcessData (boolean processData) {
		// intentionally empty.
	}

    /**
     * Add a <code>Packet</code> container.
     *
     * @param container the packet container to be added.
     */
    public void addPacketContainer(IPacketContainer container)
    {
        if (packetContainers.contains(container))
            return;

        packetContainers.add(container);
    }

    /**
     * Remove a <code>Packet</code> container.
     *
     * @param container the packet container to be removed.
     */
    public void removePacketContainer(IPacketContainer container)
    {
        while (packetContainers.remove(container))
            continue;
    }

}

// End of file.