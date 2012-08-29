/*
 * @(#) Parser.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

public class Parser {
	
	private static final Logger LOG = Logger.getLogger(Parser.class);
	
	private List<Boolean> errors = new ArrayList<Boolean>();
	private List<String> source = new ArrayList<String>();
	private List<ICommand> script = new CopyOnWriteArrayList<ICommand>(); // new ArrayList<ICommand>();
	private boolean scriptOk = false;
	
	@SuppressWarnings("serial")
	private static final List<CmdEntry> commands = new ArrayList<Parser.CmdEntry>() {{
		add(new CmdEntry (CommandNoop.class, "\\s*(#.*)?", null));
		add(new CmdEntry (CommandGoAuto.class, "go\\s+auto", null));
		add(new CmdEntry (CommandGoManual.class, "go\\s+manual", null));
		add(new CmdEntry (CommandFlyToAbs.class, "fly\\s+to\\s*\\(" +
				"\\s*(-?\\d+.\\d+)\\s*," +
				"\\s*(-?\\d+.\\d+)\\s*," +
				"\\s*(-?\\d+)(.\\d+)?\\s*\\)\\s*abs\\s+" +
				"precision\\s+(\\d+)(.\\d+)?m" +
				"\\s+(\\d+)(.\\d+)?mps", "$1:$2:$3$4:$5$6:$7$8"));
		add(new CmdEntry (CommandFlyToAbsOld.class, "fly2\\s+to\\s*\\(" +
				"\\s*(-?\\d+.\\d+)\\s*," +
				"\\s*(-?\\d+.\\d+)\\s*," +
				"\\s*(-?\\d+)(.\\d+)?\\s*\\)\\s*abs\\s+" +
				"precision\\s+(\\d+)(.\\d+)?m" +
				"\\s+(\\d+)(.\\d+)?mps", "$1:$2:$3$4:$5$6:$7$8"));
		add(new CmdEntry (CommandJumpToAbs.class, "jump\\s+to\\s*\\(" +
				"\\s*(-?\\d+.\\d+)\\s*," +
				"\\s*(-?\\d+.\\d+)\\s*," +
				"\\s*(-?\\d+)(.\\d+)?\\s*\\)\\s*abs\\s+" +
				"precision\\s+(\\d+)(.\\d+)?m", "$1:$2:$3$4:$5$6"));
		add(new CmdEntry (CommandHover.class, "hover\\s+for\\s+(\\d+)s", "$1"));
		add(new CmdEntry (CommandLand.class, "land", null));
		add(new CmdEntry (CommandTakeOff.class, "takeoff (\\d+)(.\\d+)?m? for (\\d+)s", "$1$2:$3"));
		add(new CmdEntry (CommandWaitForGo.class, "waitfor\\s+go", null));
		add(new CmdEntry (CommandFollowDistance.class, "follow\\s+(\\S+)\\s+distance\\((\\d+)m\\s*,\\s*(\\d+)deg,(\\d+)m\\)", "$1:$2:$3:$4"));
	}};
	
	public void parse(InputStream inStream) throws IOException {
		errors.clear();
		source.clear();
		script.clear();
		scriptOk = true;
		
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(inStream));
		
		String line;
		while ( (line = reader.readLine()) != null) {
			Class<?> cmdClass = null;
			String[] cmdParams = null;
			line = line.trim();
			for (CmdEntry cmd : commands) {
				if (line.matches(cmd.pattern)) {
					cmdClass = cmd.eClass;
					if (cmd.replacement != null) {
						String params = line.replaceAll(cmd.pattern, cmd.replacement);
						cmdParams = params.split(":");
					}
					break;
				}
			}
			
			ICommand command = CommandFactory.build(cmdClass, cmdParams);
			
			source.add(line);
			errors.add(command == null);
			script.add(command);
			if (command == null) {
				LOG.info("Script error in line " + reader.getLineNumber() + ": " + line);
				scriptOk = false;
			}
		}
		reader.close();
	}
	
	public List<Boolean> getErrors() {
		return errors;
	}

	public List<String> getSource() {
		return source;
	}

	public List<ICommand> getScript() {
		return script;
	}

	public boolean isScriptOk() {
		return scriptOk;
	}

	private static class CmdEntry {
		Class<?> eClass;
		String pattern;
		String replacement;
		
		public CmdEntry (Class<?> eClass, String pattern, String replacement) {
			this.eClass = eClass;
			this.pattern = pattern;
			this.replacement = replacement;
		}
	}
}
