/*
 * @(#) CommandWAitForGo.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandWaitForGo implements ICommand {
	
    public final static Logger LOG = LoggerFactory.getLogger(CommandWaitForGo.class);
	
	boolean running = false;
	boolean forcedTermination = false;

	public void execute(IInterpreter interpreter) {
		LOG.info("Waiting for take off clearance.");
		running = true;
		while (running && !interpreter.isClearanceForTakeOffGranted()) {
			try { Thread.sleep(500); } catch (InterruptedException e) { }
		}
		running = false;
		if (!forcedTermination)
			LOG.info("Clearance for take off has been granted.");
	}

	public void terminate() {
		LOG.info("Forced termination");
		forcedTermination = true;
		running = false;
	}

	public void waitForTermination() {
		// intentionally empty
		LOG.info("waitForTermination() not implemented.");
		running = false;
	}
}
