/**
 * 
 */
package javiator.util;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author Daniel Iercan (diercan@aut.upt.ro)
 *
 **/
public class SignalWriter {
	/**
	 * Stream in which the values will be writen.
	 */
	private PrintStream writer;
	/**
	 * The signale name;
	 */
	private String name;
	
	/**
	 * This is a flag which indicates if a comma should be written
	 */
	private boolean writeComma;
	
	/**
	 * Create a new instance of SignalWriter
	 * @param name
	 */
	public SignalWriter(String name){
		this.name = name;
		writeComma = false;
		
		try{
			writer = new PrintStream(new FileOutputStream(this.name + ".dat"));
		}
		catch(Exception e){
			writer = null;
			e.printStackTrace();
		}
	}
	
	/***
	 * Close the writer.
	 *
	 */
	public void close(){
		writer.close();
	}
	
	public void writeSignalValue(double d){
		if(writeComma)
			writer.print(",");
		writer.print(d);
		writeComma = true;
		writer.flush();
	}
}
