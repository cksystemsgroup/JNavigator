/*
 * @(#) CommandHover.java
 *
 * This code is part of the JNavigator project.
 * Copyright (c) 2011  Clemens Krainer
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
package at.uni_salzburg.cs.ckgroup.pilot.vcl;

import org.apache.log4j.Logger;


public class CommandHover implements ICommand {
	
	Logger LOG = Logger.getLogger(CommandHover.class);
	
	private long time;
	
	private boolean running = false;
		
	public CommandHover (long time) {
		this.time = 1000 * time;
	}

	public long getTime() {
		return time;
	}

	public void execute(IInterpreter interpreter) {
		LOG.info("Hovering for " + (long)(time/1000) + "s.");
		long timeSlice = 500;
		running = true;
		long waitingTime = time;
		while (running && waitingTime > 0) {
			try { Thread.sleep(timeSlice); } catch (InterruptedException e) { }
			waitingTime -= timeSlice;
		}
		running = false;
	}

	public void terminate() {
		running = false;
		LOG.info("Forced termination");
	}

	public void waitForTermination() {
		LOG.info("Waiting for termination.");
		while (running) {
			try { Thread.sleep(500); } catch (InterruptedException e) { }
		}
		LOG.info("Termination completed.");
	}

}
