package javiator.simulation;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.communication.Dispatcher;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider;
import at.uni_salzburg.cs.ckgroup.communication.ISender;
import at.uni_salzburg.cs.ckgroup.communication.TcpServer;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.EnableSensors;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.ShutdownEvent;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * A simulation of the code that normally runs in the Robostix "on" the
 * JAviator. Communication with the JControl is via a socket rather than RS-232.
 * The MockJaviator implements the listening end.
 */
public class MockJAviator implements Runnable, IDataTransferObjectListener, IDataTransferObjectProvider
{
	private static final Logger LOG = Logger.getLogger(MockJAviator.class.getName());

	public static final String PROP_CONTROLLER_PERIOD = "controller.period";
	public static final String PROP_CONNECTOR_PREFIX = "connector.";
	public static final String PROP_PLANT_PREFIX = "plant.";
	public static final String PROP_REPORT_RATE = "mockJAviatorReportRate";
	
//	private static final boolean DEBUG = false;

	/** Use UDP to communicate with control program, otherwise TCP */
//	private boolean useUDP;

	/** The port that the control program listens on iff useUDP */
//	private int controllerPort;

	/** The host on which the control program is running (ignored unless useUDP) */
//	private String controllerHost;

	/** For reading things from the JControl when TCP is used */
//	private InputStream input;

	/** For writing things to the JControl when TCP is used */
//	private OutputStream output;

	/** The current sensor data */
//	private SensorData sensorData;

	/** The current motor signals */
//	private MotorSignals motorSignals;

	/** The port that the MockJaviator listens on */
//	private int port;

	/** The class that is used to simulate the physics of the javiator */
	private JAviatorPhysicalModel physicalModel;

	/** to control verbose output */
	private int reportRate;
	private int reportCounter;
	
  	private Dispatcher dispatcher;
	private TcpServer javiator;

	/** A JControl (used only to apply reverse scaling) */
	// private JControlAlgorithm controller;
	/** A set of <code>SensorDataListeners</code> */
//	private Vector sensorDataListeners = new Vector();

	/**
	 * The MockJaviator's own properties.
	 */
	private Properties props;
	
	/**
	 * Create a new MockJaviator
	 * @throws InstantiationException 
	 */
	protected MockJAviator (Properties props) throws InstantiationException
	{
		this.props = props;
		init ();
	}

	/**
	 * Initialize the MockJAviator from provided arguments
	 * 
	 * @throws InstantiationException 
	 */
	public void init () throws InstantiationException {

		reportRate = Integer.parseInt(props.getProperty(PROP_REPORT_RATE, "0"));

		physicalModel = (JAviatorPhysicalModel) ObjectFactory.getInstance().instantiateObject (PROP_PLANT_PREFIX, JAviatorPhysicalModel.class, props);
		
	  	dispatcher = new Dispatcher ();
		javiator = (TcpServer) ObjectFactory.getInstance ().instantiateObject (PROP_CONNECTOR_PREFIX, TcpServer.class, props);
		javiator.setDtoProvider (dispatcher);
		
		dispatcher.addDataTransferObjectListener (this, EnableSensors.class);
		dispatcher.addDataTransferObjectListener (this, CommandData.class);
		dispatcher.addDataTransferObjectListener (this, MotorSignals.class);
		dispatcher.addDataTransferObjectListener (this, ShutdownEvent.class);
	}

	// @see java.lang.Runnable#run()
	public void run () {
		javiator.run ();
	}

	public void receive(IDataTransferObject dto) throws IOException {
		
		if (dto instanceof EnableSensors) {
			EnableSensors enableSensors = (EnableSensors) dto;
			LOG.info("Sensors received: " + enableSensors);
			if (!enableSensors.getEnableSensors()) {
				MotorSignals motorSignals = new MotorSignals();
				physicalModel.reset();
//				log_motor_signals(motorSignals);
				SensorData sensorData = computeSimulatedData (motorSignals);
//				log_sensor_data(sensorData);
				dispatcher.dispatch (null, sensorData);
			}
			return;
		}
		
		if (dto instanceof CommandData) {
			CommandData commandData = (CommandData) dto;
			physicalModel.setCommandData (commandData);
			return;
		}

		if (dto instanceof MotorSignals) {
			MotorSignals motorSignals = (MotorSignals) dto;
//			log_motor_signals(motorSignals);
			SensorData sensorData = computeSimulatedData (motorSignals);
//			log_sensor_data(sensorData);
			dispatcher.dispatch (null, sensorData);
			return;
		}
		
		if (dto instanceof ShutdownEvent) {
			LOG.info("Shutdown received.");
			MotorSignals motorSignals = new MotorSignals();
			computeSimulatedData (motorSignals);
			return;
		}
		
		throw new IOException ("Can not handle IDataTransferObject object of class " + dto.getClass().getName()); 
	}


	/**
	 * Subroutine to compute simulated sensorData information somewhat
	 * reminiscent of what the real JAviator might send
	 */
	private SensorData computeSimulatedData(MotorSignals motorSignals) {
		physicalModel.setMotorSignals(motorSignals);
		physicalModel.simulate();

		SensorData sensorData = physicalModel.getSensorData();
		sensorData.setZ(sensorData.getZ() + 0.12);

		if (reportRate > 0) {
			reportCounter++;
			if (reportCounter == reportRate) {
				System.out.println("Received--" + motorSignals + ", Sent--" + sensorData);
				reportCounter = 0;
			}
		}
		
		return sensorData;
	}
	

	public void addDataTransferObjectListener(IDataTransferObjectListener listener, Class dtoType) {
		dispatcher.addDataTransferObjectListener(listener, dtoType);
	}

	public void removeIDataTransferObjectListener(IDataTransferObjectListener listener) {
		dispatcher.removeIDataTransferObjectListener(listener);
	}

	public void dispatch(ISender sender, IDataTransferObject dto) throws IOException {
		dispatcher.dispatch(sender, dto);
	}
  
	
  /**
   * Run the MockJAviator over UDP
   * @throws IOException 
   */
//	private void UDPrun() throws IOException {
//		System.err.println("Using UDP: will receive on port " + port
//				+ " and send to host " + controllerHost + ", port "
//				+ controllerPort);
//		DatagramSocket recvSocket = new DatagramSocket(port);
//		DatagramSocket sendSocket = new DatagramSocket();
//		sensorData = new SensorData();
//		motorSignals = new MotorSignals();
//		Packet recv = new Packet(MotorSignals.PACKET_SIZE + LeanPacket.OVERHEAD);
//		Packet send = new Packet(SensorData.PACKET_SIZE + LeanPacket.OVERHEAD);
//		LeanPacket.fillHeader(send.payload, PacketType.COMM_SENSOR_DATA,
//				SensorData.PACKET_SIZE);
//		boolean first = true;
//		DatagramPacket sendPacket = new DatagramPacket(send.payload, 0,
//				send.size,
//				new InetSocketAddress(controllerHost, controllerPort));
//		DatagramPacket recvPacket = new DatagramPacket(recv.payload, 0,
//				recv.size);
//		for (;;) {
//			if (first) {
//				System.err.println("Waiting for initial contact");
//			}
//			recvSocket.receive(recvPacket);
//			if (first) {
//				System.err.println("Got first packet");
//			}
//			if (recvPacket.getLength() != recv.size) {
//				throw new IOException("Short receive " + recvPacket.getLength()
//						+ " using UDP");
//			}
//			if (!LeanPacket.checksOut(recv.payload)) {
//				throw new IOException(
//						"Received data fails integrity check using UDP");
//			}
//			if (recv.payload[LeanPacket.TYPE_OFFSET] != PacketType.COMM_MOTOR_SIGNALS) {
//				throw new IOException("Unexpected packet type "
//						+ recv.payload[LeanPacket.TYPE_OFFSET] + " using UDP");
//			}
//			if (DEBUG) {
//				System.err.println("Got packet");
//			}
//			motorSignals.decode(recv, LeanPacket.PAYLOAD_OFFSET);
//			log_motor_signals(motorSignals);
//			computeSimulatedData();
//			log_sensor_data(sensorData);
//			fireSensorData(sensorData);
//			sensorData.encode(send, LeanPacket.PAYLOAD_OFFSET);
//			LeanPacket.addChecksum(send.payload);
//			sendSocket.send(sendPacket);
//			if (DEBUG) {
//				System.err.println("Sent response");
//			}
//			if (sendPacket.getLength() != send.size) {
//				throw new IOException("Short send " + sendPacket.getLength()
//						+ " using UDP");
//			}
//			if (first) {
//				System.err
//						.println("Sent first response, presumably synchronized");
//				first = false;
//			}
//		}
//	}


	/**
   * Run the MockJAviator when we have a TCP connection
   */
//	private void TCPrun() {
//		ServerSocket ls;
//		Socket s = null;
//		try {
//			ls = new ServerSocket(port);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//		for (;;) {
//			try {
//				System.err.println("Waiting for connection on port " + port);
//				s = ls.accept();
//				input = new BufferedInputStream(s.getInputStream());
//				output = new BufferedOutputStream(s.getOutputStream());
//				sensorData = new SensorData();
//				motorSignals = new MotorSignals();
//				System.err.println("Connection established");
//				while (true) {
//					receiveAndProcessPacket();
//				}
//			} catch (IOException e) {
//				/*
//				 * Failure of any kind causes connection close and iteration of
//				 * outer loop
//				 */
//				System.err.println("Connection broke");
//				e.printStackTrace();
//				try {
//					if (input != null) {
//						input.close();
//					}
//					if (output != null) {
//						output.close();
//					}
//					if (s != null) {
//						s.close();
//					}
//				} catch (IOException e1) {
//				}
//			}
//		}
//	}

//	private boolean sendTCP() {
//		try {
//			sendPacket(sensorData.toPacket(PacketType.COMM_SENSOR_DATA));
//			return true;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//  
//  private boolean sendAckName()
//  {
//	  
//	  byte[] name = new byte[2];
//	  name[0] = 'J';
//	  name[1] = '1';
//	  Packet packet = new Packet(PacketType.COMM_ACK_NAME, name);
//	  packet.calcChecksum();
//	  try {
//		  
//		  sendPacket(packet);
//		  return true;
//	  } catch (IOException e) {
//		  e.printStackTrace();
//		  return false;
//	  }
//  }

//	/**
//	 * Subroutine to send a packet
//	 * 
//	 * @param packet
//	 *            the packet to send
//	 * @throws IOException
//	 */
//	private void sendPacket(Packet packet) throws IOException {
//		output.write(new byte[] { (byte) 0xff, (byte) 0xff, packet.type,
//				packet.size });
//		if (packet.size > 0)
//			output.write(packet.payload, 0, packet.size);
//		output.write(new byte[] { (byte) (packet.checksum >> 8),
//				(byte) packet.checksum });
//		output.flush();
//	}
//
//  /**
//     * Subroutine to receive and parse one packet, then act upon it. Assuming
//     * reliable communication.
//     * @throws IOException
//     *             on real IO errors and also on ny data corruption, which
//     *             should not occur since communication is reliable
//     */
//  private void receiveAndProcessPacket() throws IOException
//  {
//		byte[] header = new byte[4];
//		if ((input.read(header)) != 4) {
//			throw new IOException(
//					"Unexpected end of file while reading packet header");
//		}
//		if (header[0] != -1 || header[1] != -1) {
//			throw new IOException("Bad packet boundary mark");
//		}
//		byte type = header[2];
//		int size = header[3] & 0xff;
//		if (type < 1 || type > PacketType.COMM_PACKET_LIMIT) {
//			throw new IOException("Bad packet type " + header[2]);
//		}
//		Packet packet = new Packet(type, size);
//		if (size > 0) {
//			input.read(packet.payload);
//		}
//		if ((input.read(header, 0, 2)) != 2) {
//			throw new IOException(
//					"Unexpected end of file while reading checksum");
//		}
//		int checksum = (header[0] << 8) + (header[1] & 0xFF);
//		if (checksum != packet.calcChecksum()) {
//			throw new IOException("Bad checksum: recorded=" + checksum
//					+ ", calculated=" + packet.calcChecksum());
//		}
//		if (type == PacketType.COMM_COMMAND_DATA) {
//			physicalModel.setCommandData (new CommandData(packet));
//		} else if (type == PacketType.COMM_MOTOR_SIGNALS) {
//			motorSignals = new MotorSignals();
//			motorSignals.fromPacket(packet);
//			log_motor_signals(motorSignals);
//			computeSimulatedData();
//			log_sensor_data(sensorData);
//			fireSensorData (sensorData);
//			sendTCP();
//		} else if (type == PacketType.COMM_SHUT_DOWN) {
//			System.err.print("Shutdown received.");
//			motorSignals = new MotorSignals();
//		} else if (type == PacketType.COMM_EN_SENSORS) {
//			System.err.println((packet.payload[0] != 0 ?"Enable":"Disable") + "Sensors received: ");
//			if (packet.payload[0] == 0) {
//				motorSignals = new MotorSignals();
//				physicalModel.reset();
//				computeSimulatedData();
//				fireSensorData (sensorData);
//				sendTCP();
//			}
//		} else {
//			System.err.print("Packet type = " + type + " data: ");
//			for (int i = 0; i < packet.size; i++) {
//				String digit = Integer.toHexString(packet.payload[i] & 0xff);
//				if (digit.length() < 2) {
//					digit = "0" + digit;
//				}
//				System.err.print(digit);
//			}
//			System.err.println();
//		}
//	}



//    private void log_sensor_data (SensorData sensorData) {
//  	  if (LOG.isDebugEnabled())
//  		  LOG.debug("SensorData: " + 
//  			  ", roll=" + sensorData.roll +
//  			  ", pitch=" + sensorData.pitch +
//  			  ", yaw=" + sensorData.yaw +
//  			  ", droll=" + sensorData.droll +
//  			  ", dpitch=" + sensorData.dpitch +
//  			  ", dyaw=" + sensorData.dyaw +
//  			  ", ddroll=" + sensorData.ddroll +
//  			  ", ddpitch=" + sensorData.ddpitch +
//  			  ", ddyaw=" + sensorData.ddyaw +
//  			  ", x=" + sensorData.x +
//  			  ", y=" + sensorData.y +
//  			  ", z=" + sensorData.z +
//  			  ", dx=" + sensorData.dx +
//  			  ", dy=" + sensorData.dy +
//  			  ", dz=" + sensorData.dz +
//  			  ", ddx=" + sensorData.ddx +
//  			  ", ddy=" + sensorData.ddy +
//  			  ", ddz=" + sensorData.ddz +
//  			  ", battery=" + sensorData.battery
//  		  );
//    }
//
//    private void log_motor_signals (MotorSignals motorSignals) {
//  	  if (LOG.isDebugEnabled())
//  		  LOG.debug("MotorSignals: " +
//  			  "front=" + motorSignals.front +
//  			  ", right=" + motorSignals.right +
//  			  ", rear=" + motorSignals.rear +
//  			  ", left=" + motorSignals.left +
//  			  ", id=" + motorSignals.id
//  		  );
//    }
}
