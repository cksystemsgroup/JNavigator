/*
 * @(#) AutoPilotFrame.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2009  Clemens Krainer
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
package at.uni_salzburg.cs.ckgroup.ui;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;

import org.apache.log4j.Logger;

public class AutoPilotFrame extends Frame implements ISetCourseList
{
	Logger LOG = Logger.getLogger(AutoPilotFrame.class);
	
	private static final long serialVersionUID = -4688973371884329398L;

	private Button startButton = new Button ("Start");
	private Button stopButton = new Button ("Stop");
//	private TextField setCourseTextField = new TextField ("");
	private Choice setCourseChoice = new Choice (); 
	private IAutoPilotController controller;
	
	public AutoPilotFrame (String name, IAutoPilotController autoPilotController)
	{
		super (name);
		controller = autoPilotController;
		setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
		add (startButton);
		add (stopButton);
		add (setCourseChoice);
//		add (setCourseTextField);
		
		startButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				controller.startAutoPilot (setCourseChoice.getSelectedItem());
			}
		});
		
		stopButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				controller.stopAutoPilot ();
//				controller.sendSetCourseFileNames ();
			}
		});

		setSize(200, 150);
		setVisible (false);
		
		addWindowListener (new WindowAdapter(){
			public void windowClosing (WindowEvent we){
				invisible ();
				}
			});
	}
	
	public void invisible () {
		setVisible (false);
	}
	
//	public String getSetCourse () {
//		return setCourseTextField.getText();
//	}
//	
//	public void setSetCourse (String setCourse) {
//		setCourseTextField.setText (setCourse);
//	}
	
	public void emptySetCourseList () {
		setCourseChoice.removeAll();
		LOG.info ("AutoPilotFrame.emptySetCourseList");
	}
	
	public void addSetCourseFileName (String fileName) {
		setCourseChoice.add(fileName);
		LOG.info ("AutoPilotFrame.addSetCourseFileName: " + fileName);
	}
}
