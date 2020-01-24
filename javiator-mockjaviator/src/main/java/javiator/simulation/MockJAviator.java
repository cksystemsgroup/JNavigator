package javiator.simulation;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.uni_salzburg.cs.ckgroup.communication.Dispatcher;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectForwarder;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider;
import at.uni_salzburg.cs.ckgroup.communication.ISender;
import at.uni_salzburg.cs.ckgroup.communication.data.JaviatorData;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.ShutdownEvent;
import at.uni_salzburg.cs.ckgroup.communication.data.SimulationData;
import at.uni_salzburg.cs.ckgroup.communication.data.SwitchState;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * A simulation of the code that normally runs in the Robostix "on" the
 * JAviator. Communication with the JControl is via a socket rather than RS-232.
 * The MockJaviator implements the listening end.
 */
public class MockJAviator extends Thread implements IDataTransferObjectListener, IDataTransferObjectProvider
{
    public final static Logger LOG = LoggerFactory.getLogger(MockJAviator.class);
	
	public static final String PROP_SEND_JAVIATOR_DATA = "mock-javiator.send.javiator-data";
	public static final String PROP_SEND_SENSOR_DATA = "mock-javiator.send.sensor-data";
	public static final String PROP_SEND_SIMULATION_DATA = "mock-javiator.send.simulation-data";

	public static final String PROP_CONTROLLER_PERIOD = "controller.period";
	public static final String PROP_CONNECTOR_PREFIX = "connector.";
	public static final String PROP_PLANT_PREFIX = "plant.";
	public static final String PROP_REPORT_RATE = "mock-javiator.report.rate";

	/** The class that is used to simulate the physics of the javiator */
	private JAviatorPhysicalModel physicalModel;

	/** to control verbose output */
	private int reportRate;
	private int reportCounter;
	
  	private Dispatcher dispatcher;
	private IDataTransferObjectForwarder javiator;

	/**
	 * The MockJaviator's own properties.
	 */
	private Properties props;

	/**
	 * True if <code>MockJAviator</code> should send <code>JAviatorData</code> DTOs to the controller.
	 */
	private boolean sendJAviatorData;

	/**
	 * True if <code>MockJAviator</code> should send <code>SensorData</code> DTOs to the controller.
	 */
	private boolean sendSensorData;

	/**
	 * True if <code>MockJAviator</code> should send <code>SimulationData</code> DTOs to the controller.
	 */
	private boolean sendSimulationData;
	
	/**
	 * Create a new MockJaviator
	 * @throws InstantiationException 
	 */
	public MockJAviator (Properties props) throws InstantiationException {
		this.props = props;
		init ();
	}
	
	protected static short packetCounter = 0;

	/**
	 * Initialize the MockJAviator from provided arguments
	 * 
	 * @throws InstantiationException 
	 */
	public void init () throws InstantiationException {
		sendJAviatorData = Boolean.parseBoolean(props.getProperty("mock-javiator.send.javiator-data","true"));
		sendSensorData = Boolean.parseBoolean(props.getProperty("mock-javiator.send.sensor-data","false"));
		sendSimulationData = Boolean.parseBoolean(props.getProperty("mock-javiator.send.simulation-data","true"));

		reportRate = Integer.parseInt(props.getProperty(PROP_REPORT_RATE, "0"));

		physicalModel = (JAviatorPhysicalModel) ObjectFactory.getInstance().instantiateObject (PROP_PLANT_PREFIX, JAviatorPhysicalModel.class, props);
		
	  	dispatcher = new Dispatcher ();
		javiator = (IDataTransferObjectForwarder) ObjectFactory.getInstance ().instantiateObject (PROP_CONNECTOR_PREFIX, IDataTransferObjectForwarder.class, props);
		javiator.setDtoProvider (dispatcher);
		
//		dispatcher.addDataTransferObjectListener (this, CommandData.class);
		dispatcher.addDataTransferObjectListener (this, MotorSignals.class);
		dispatcher.addDataTransferObjectListener (this, ShutdownEvent.class);
		dispatcher.addDataTransferObjectListener (this, SwitchState.class);
	}

	// @see java.lang.Runnable#run()
	public void run () {
		javiator.run ();
	}
	
	/**
	 * Terminate the network thread.
	 */
	public void terminate () {
		javiator.terminate();
	}

	public void receive(IDataTransferObject dto) throws IOException {
		
		if (dto instanceof MotorSignals) {
			MotorSignals motorSignals = (MotorSignals) dto;
			computeSimulatedData (motorSignals);
			return;
		}
		
		if (dto instanceof ShutdownEvent) {
			LOG.info("Shutdown received.");
			MotorSignals motorSignals = new MotorSignals();
			computeSimulatedData (motorSignals);
			return;
		}
		
		if (dto instanceof SwitchState) {
			LOG.info("SwitchState received.");
			MotorSignals motorSignals = new MotorSignals();
			physicalModel.reset();
			computeSimulatedData (motorSignals);
			return;
		}
		
		throw new IOException ("Can not handle IDataTransferObject object of class " + dto.getClass().getName()); 
	}

	/**
	 * Subroutine to compute simulated sensorData information somewhat
	 * reminiscent of what the real JAviator might send
	 * @throws IOException 
	 */
	private void computeSimulatedData (MotorSignals motorSignals) throws IOException {
		
		boolean writeToLog = false;
		
		if (reportRate > 0 && reportCounter++ == reportRate) {
			writeToLog = LOG.isDebugEnabled();
			reportCounter = 0;
		}
		
		physicalModel.setMotorSignals(motorSignals);
		physicalModel.simulate();
		if (writeToLog)
			LOG.debug("Received: " + motorSignals);
		
		if (sendJAviatorData) {
			JaviatorData javiatorData = physicalModel.getJaviatorData();
			javiatorData.setSonar(javiatorData.getSonar() + 0.12);
			javiatorData.setId(++packetCounter);
			dispatcher.dispatch (null, javiatorData);
			if (writeToLog)
				LOG.debug("Sent: " + javiatorData);
		}
		
		if (sendSensorData) {
			SensorData sensorData = physicalModel.getSensorData();
			dispatcher.dispatch (null, sensorData);
			if (writeToLog)
				LOG.debug("Sent: " + sensorData);
		}
		
		if (sendSimulationData) {
			SimulationData simulationData = physicalModel.getSimulationData();
			dispatcher.dispatch (null, simulationData);
			if (writeToLog)
				LOG.debug("Sent: " + simulationData);
		}
		
	}
	

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider#addDataTransferObjectListener(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener, java.lang.Class)
	 */
	public void addDataTransferObjectListener(IDataTransferObjectListener listener, @SuppressWarnings("rawtypes") Class dtoType) {
		dispatcher.addDataTransferObjectListener(listener, dtoType);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider#removeIDataTransferObjectListener(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener)
	 */
	public void removeIDataTransferObjectListener(IDataTransferObjectListener listener) {
		dispatcher.removeIDataTransferObjectListener(listener);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider#dispatch(at.uni_salzburg.cs.ckgroup.communication.ISender, at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void dispatch(ISender sender, IDataTransferObject dto) throws IOException {
		dispatcher.dispatch(sender, dto);
	}

	public Dispatcher getDispatcher() {
		return dispatcher;
	}
	
}
