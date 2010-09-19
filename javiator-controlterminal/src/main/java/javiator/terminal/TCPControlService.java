/*
 * @(#) TCPControlService.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2008  Clemens Krainer
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
package javiator.terminal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Vector;

import javiator.util.CommandData;
import javiator.util.ControlParams;
import javiator.util.ICommandDataListener;
import javiator.util.ICommandDataProvider;
import javiator.util.IPacketContainer;
import javiator.util.IPacketListener;
import javiator.util.ISensorDataListener;
import javiator.util.Packet;
import javiator.util.PacketReceiver;
import javiator.util.PacketSender;
import javiator.util.PacketType;
import javiator.util.SensorData;
import at.uni_salzburg.cs.ckgroup.ui.ISetCourseList;

/**
 * This class implements a TCP control service. It opens a
 * <code>ServerSocket</code> at the specified port number and waits for control
 * clients to connect. It is a <code>WorkingThread</code> that takes an incoming
 * connection and does the actual receiving and sending.
 * 
 * @author Clemens Krainer
 */
public class TCPControlService extends Thread implements IPacketListener, ICommandDataProvider, ISensorDataListener {

	/**
	 * All registered listeners to <code>CommandData</code> objects.
	 */
	private Vector<ICommandDataListener> commandDataListeners = new Vector<ICommandDataListener> ();
	
	/**
	 * All active <code>PacketReceiver</code> threads.
	 */
	private Vector<PacketReceiver> packetRecerivers = new Vector<PacketReceiver> ();
	
	/**
	 * All active <code>PacketSender</code> threads.
	 */
	private Vector<PacketSender> packetSenders = new Vector<PacketSender> ();
	
    /**
     * A list of containers containing extra packets to be sent to clients.
     */
    private Vector<IPacketContainer> packetContainers = new Vector<IPacketContainer> ();
    
	/**
	 * If <code>active</code> equals to <code>true</code> the socket server
	 * thread waits for incoming connections.
	 */
	private boolean active;
	
	private ISetCourseList setCourseList = null;
	
	/**
	 * The <code>ServerSocket</code> waiting for new connections.
	 */
	private ServerSocket serverSocket;

	/**
	 * Construct a <code>TcpSocketServer</code>
	 * 
	 * @param port
	 *            the TCP/IP port number to listen to
	 * @throws IOException
	 */
	public TCPControlService(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			active = true;
			while (active) {
				Socket clientSocket = serverSocket.accept();

				if (!active)
					break;

				startWorkerThread(clientSocket);
			}
		} catch (IOException e) {
			if (e instanceof SocketException
					&& e.getMessage().equals("Socket closed"))
				System.out.println("TcpSocketServer: Client disconnected");
			else
				e.printStackTrace();
			try {
				serverSocket.close();
			} catch (IOException e1) {
			}
		}

		// System.out.println ("TcpSocketServer: end.");
	}

	/**
	 * Terminate the mail server thread.
	 */
	public void terminate() {
		active = false;
		try {
			serverSocket.close();
		} catch (IOException e) {;}
		yield();
	}

	/**
	 * Start a working thread that handles the incoming <code>Socket</code> from
	 * a newly connected client.
	 * 
	 * @param connection
	 *            the new incoming connection.
	 * @throws IOException 
	 */
	public void startWorkerThread(Socket connection) throws IOException {
		System.out.println ("TCPControlService: New client connected, name=" + this.getName());
		
		PacketReceiver receiver = new PacketReceiver(connection.getInputStream(), this);
		receiver.start ();
		packetRecerivers.add(receiver);
		
		PacketSender sender = new PacketSender(connection.getOutputStream());
		sender.start ();
		packetSenders.add(sender);
	}

	/* (non-Javadoc)
	 * @see javiator.util.IPacketListener#receive(javiator.util.Packet)
	 */
	public boolean receive(Packet packet) {
		
		if (packet.type == PacketType.COMM_COMMAND_DATA) {
			CommandData commandData = new CommandData(packet);
			Iterator<ICommandDataListener> listeners = commandDataListeners.iterator();
			while (listeners.hasNext()) {
				ICommandDataListener l = listeners.next();
				l.receive(commandData);
			}
		}
		
		if (packet.type == PacketType.COMM_PILOT_DATA) {
	        System.out.println ("PILOT_DATA@TCPControlService: " + new String (packet.payload));
	        String payload = new String (packet.payload);
	        if (payload.startsWith("AUTOPILOT FILE NAME ")) {
	                String reset = payload.substring(20,21);
	                if (reset.equals("r"))
	                	setCourseList.emptySetCourseList();
	                String fileName = payload.substring(21);
	                setCourseList.addSetCourseFileName (fileName);
	        }
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see javiator.util.ICommandDataProvider#addCommandDataListener(javiator.util.ICommandDataListener)
	 */
	public void addCommandDataListener(ICommandDataListener listener) {
		if (!commandDataListeners.contains(listener))
			commandDataListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see javiator.util.ICommandDataProvider#removeCommandDataListener(javiator.util.ICommandDataListener)
	 */
	public void removeCommandDataListener(ICommandDataListener listener) {
		while (commandDataListeners.remove(listener))
			continue;
	}

	/* (non-Javadoc)
	 * @see javiator.util.ISensorDataListener#receive(javiator.util.SensorData)
	 */
	public void receive(SensorData sensorData) {
		Packet packet = sensorData.toPacket(PacketType.COMM_SENSOR_DATA);
		
		Vector<PacketSender> brokenSenders = new Vector<PacketSender>();
		Iterator<PacketSender> senders = packetSenders.iterator();
		while (senders.hasNext()) {
			PacketSender sender = senders.next();
			if (!sender.receive(packet)) {
				brokenSenders.add(sender);
				break;
			}
	        
			for (int k=0; k < packetContainers.size(); k++)
	        {
                IPacketContainer container = (IPacketContainer)packetContainers.get(k);
                if (container.hasMoreElements()) {
                	if (!sender.receive(container.nextElement())) {
        				brokenSenders.add(sender);
        				break;
        			}
                }
	        }
		}
		
		senders = brokenSenders.iterator();
		while (senders.hasNext()) {
			PacketSender sender = senders.next();
			packetSenders.remove(sender);
		}
		

	}
	
	/**
	 * @param controlParams the control parameters to be forwarded.
	 */
	public void receive (ControlParams controlParams) {
		Packet packet = controlParams.toPacket(PacketType.COMM_X_Y_PARAMS);

		Iterator<PacketSender> senders = packetSenders.iterator();
		while (senders.hasNext()) {
			PacketSender sender = senders.next();
			sender.receive(packet);
		}
	
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
    
    /**
     * @param setCourseList (un)register the list of set course names.
     */
    public void setSetCourseList (ISetCourseList setCourseList) {
    	this.setCourseList = setCourseList;
    }
}
