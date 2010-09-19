/*****************************************************************************/
/*   This code is part of the JAviator project: javiator.cs.uni-salzburg.at  */
/*                                                                           */
/*   TraceDialog.java   Constructs a non-modal dialog that allows to enter   */
/*                      a trace description that is sed as file name.        */
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

import java.awt.Dialog;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.Checkbox;
import java.awt.Button;
import java.awt.Label;
import java.awt.Color;
import java.awt.Point;
import java.awt.GraphicsEnvironment;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.io.File;

/*****************************************************************************/
/*                                                                           */
/*   Class TraceDialog                                                       */
/*                                                                           */
/*****************************************************************************/

public class TraceDialog extends Dialog
{
    public static final long serialVersionUID = 1;

    public static TraceDialog createInstance( ControlTerminal parent, String title )
    {
        if( Instance == null )
        {
            Instance = new TraceDialog( parent, title );
        }

        return( Instance );
    }

    /*************************************************************************/
    /*                                                                       */
    /*   Private Section                                                     */
    /*                                                                       */
    /*************************************************************************/

    private static TraceDialog     Instance   = null;
    private        ControlTerminal parent     = null;
    private        TextField       traceName  = null;
    private        Checkbox        saveConfig = null;

    private TraceDialog( ControlTerminal parent, String title )
    {
        super( parent, title, false );

        this.parent = parent;
        traceName   = new TextField( "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" );
        saveConfig  = new Checkbox( "  Save Configuration File", true );

        setBackground( Color.WHITE );
        makePanel( );
        pack( );

        traceName.setText( parent.logFileName );

        addWindowListener( new WindowAdapter( )
        {
            public void windowClosing( WindowEvent we )
            {
                closeDialog( false );
            }
        } );

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment( ).getCenterPoint( );
        setLocation( center.x - ( getWidth( ) >> 1 ), center.y - ( getHeight( ) >> 1 ) );
        setResizable( false );
        setVisible( true );

        parent.traceDialogOpen = true;
    }

    private void makePanel( )
    {
        Panel northPanel = new Panel( new BorderLayout( ) );
        northPanel.add( new Label( ), BorderLayout.NORTH );
        northPanel.add( new Label( ), BorderLayout.WEST );
        northPanel.add( traceName, BorderLayout.CENTER );
        northPanel.add( new Label( ), BorderLayout.SOUTH );
        northPanel.add( new Label( ), BorderLayout.EAST );

        Panel checkboxPanel = new Panel( new BorderLayout( ) );
        checkboxPanel.add( saveConfig, BorderLayout.WEST );

        Panel centerPanel = new Panel( new BorderLayout( ) );
        centerPanel.add( new Label( ), BorderLayout.WEST );
        centerPanel.add( checkboxPanel, BorderLayout.CENTER );
        centerPanel.add( new Label( ), BorderLayout.SOUTH );
        centerPanel.add( new Label( ), BorderLayout.EAST );

        Button okButton = new Button( "Ok" );
        okButton.addActionListener( new ActionListener( )
        {
            public void actionPerformed( ActionEvent ae )
            {
                closeDialog( true );
            }
        } );
        okButton.addKeyListener( new KeyListener( )
        {
            public void keyPressed( KeyEvent ke )
            {
            	if( ke.getKeyCode( ) == KeyEvent.VK_ENTER )
            	{
                    closeDialog( true );
            	}
            }

            public void keyReleased( KeyEvent ke )
            {
            }

            public void keyTyped( KeyEvent ke )
            {
            }
        } );

        Button cancelButton = new Button( "Cancel" );
        cancelButton.addActionListener( new ActionListener( )
        {
            public void actionPerformed( ActionEvent ae )
            {
                closeDialog( false );
            }
        } );
        cancelButton.addKeyListener( new KeyListener( )
        {
            public void keyPressed( KeyEvent ke )
            {
            	if( ke.getKeyCode( ) == KeyEvent.VK_ENTER )
            	{
                    closeDialog( false );
            	}
            }

            public void keyReleased( KeyEvent ke )
            {
            }

            public void keyTyped( KeyEvent ke )
            {
            }
        } );

        Panel buttonPanel = new Panel( new GridLayout( 1, 3 ) );
        buttonPanel.add( okButton );
        buttonPanel.add( new Label( ) );
        buttonPanel.add( cancelButton );

        Panel southPanel = new Panel( new BorderLayout( ) );
        southPanel.add( new Label( ), BorderLayout.WEST );
        southPanel.add( buttonPanel, BorderLayout.CENTER );
        southPanel.add( new Label( ), BorderLayout.EAST );

        setLayout( new BorderLayout( ) );
        add( northPanel, BorderLayout.NORTH );
        add( centerPanel, BorderLayout.CENTER );
        add( southPanel, BorderLayout.SOUTH );
    }

    private void closeDialog( boolean assume )
    {
        if( assume )
        {
            String fileName = ControlTerminal.TRACES_FOLDER + traceName.getText( );
            new File( ControlTerminal.TRACES_FOLDER + parent.logFileName +
                ControlTerminal.EXT_CSV ).renameTo( new File( fileName + ControlTerminal.EXT_CSV ) );
            
            if( saveConfig.getState( ) )
            {
            	parent.saveConfiguration( fileName + ControlTerminal.EXT_CFG );
            }
        }

        dispose( );
        Instance = null;
        parent.traceDialogOpen = false;
    }
}

// End of file.