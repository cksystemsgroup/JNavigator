/*
 * @(#) LocationDaemonTestCase.java
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
package at.uni_salzburg.cs.ckgroup.location;

import java.io.IOException;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.io.IConnection;

public class LocationDaemonTestCase extends TestCase {

	private static final String[] testCaseMessages = {
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:56.603807303,0.0512721,1,0.593868,3.32336,0.950172,0.753014,0,0,-0.658005*69\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:56.711898772,0.00569327,1,0.636995,3.17831,1.00643,1,0,0,0*52\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:56.819990364,0.0306836,1,0.573064,3.3266,0.957076,1,0,0,0*68\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:56.819990364,0.0306836,1,0.573064,3.3266,0.957076,0.726676,0,0,-0.68698*61\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:56.928081861,0.00525637,1,0.629932,3.15824,0.98971,1,0,0,0*5C\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:57.036173352,0.0188863,1,0.550342,3.36598,0.935115,1,0,0,0*5E\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:57.036173352,0.0188863,1,0.550342,3.36598,0.935115,0.638003,0,0,-0.770034*63\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:57.144264822,0.00595462,1,0.626243,3.17265,1.00489,1,0,0,0*5D\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:57.252356293,0.0335712,1,0.571241,3.34013,0.898792,1,0,0,0*50\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:57.252356293,0.0335712,1,0.571241,3.34013,0.898792,0.659925,0,0,-0.751332*61\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:57.360447784,0.0520538,1,0.827681,3.29461,0.912295,1,0,0,0*57\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:57.468539254,0.0426316,1,0.571875,3.33716,0.901,1,0,0,0*6B\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:57.468539254,0.0426316,1,0.571875,3.33716,0.901,0.6607,0,0,-0.75065*69\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:57.576630677,0.00440948,1,0.659905,3.17372,0.999729,1,0,0,0*68\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:57.684722147,0.0543525,1,0.571325,3.33334,0.905395,1,0,0,0*57\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:57.684722147,0.0543525,1,0.571325,3.33334,0.905395,0.660654,0,0,-0.75069*59\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:57.792813617,0.00543701,1,0.653025,3.1641,1.01901,1,0,0,0*66\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:57.900905099,0.0454515,1,0.57987,3.33372,0.909671,1,0,0,0*6B\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:57.900905099,0.0454515,1,0.57987,3.33372,0.909671,0.67177,0,0,-0.740759*67\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:58.008996569,0.00461433,1,0.672409,3.18023,1.01472,1,0,0,0*52\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:58.117088039,0.0423999,1,0.571305,3.33581,0.909869,1,0,0,0*58\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:58.117088039,0.0423999,1,0.571305,3.33581,0.909869,0.660515,0,0,-0.750812*64\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:58.225179521,0.00452388,1,0.684812,3.20831,1.00492,1,0,0,0*5D\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:58.333270978,0.0476324,1,0.558187,3.34459,0.907654,1,0,0,0*57\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:58.333270978,0.0476324,1,0.558187,3.34459,0.907654,0.636078,0,0,-0.771624*6E\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:58.441362449,0.00506887,1,0.632531,3.18312,0.965283,1,0,0,0*67\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-30 12:44:58.549453923,0.0463968,1,0.541526,3.33877,0.913918,1,0,0,0*51\r\n",
		"$LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-30 12:44:58.549453923,0.0463968,1,0.541526,3.33877,0.913918,0.621772,0,0,-0.783198*6E\r\n",
		"$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021176,2008-06-30 12:44:58.657545394,0.0444777,1,0.79312,3.27395,0.925265,1,0,0,0*60\r\n"
	};
	
	public int receivedMessages = 0;
	
	public void testCase01 () {
		String[] msgs = new String[testCaseMessages.length];
		for (int k=0; k < testCaseMessages.length; k++)
			msgs[k] = testCaseMessages[k];
		
		msgs[0] = "00021176,2008-06-30 12:44:58.657545394,0.0444777,1,0.79312,3.27395,0.925265,1,0,0,0*60\r\n";
		
		IConnection rcv = new LocationReceiverMock (msgs, 100);

		try
		{
			receivedMessages = 0;
			ILocationMessageListener listener = new MyMsgListener ();
			LocationDaemon locd = new LocationDaemon (rcv);
			locd.addLocationMessageListener (listener);
			locd.start ();
			int counter = 100;
			while (receivedMessages < 8 && counter-- > 0)
				try	{ Thread.sleep (100); } catch (Exception e) { }
			locd.terminate ();
			
			int wrecked = locd.getNumberOfMalformedMessages();
			int ok = locd.getNumberOfWellFormedMessages();
			System.out.println ("wrecked=" + wrecked + ", ok=" + ok + ", receivedMessages=" + receivedMessages);
			
			assertEquals (2, wrecked);
			assertEquals (8, ok);
		} catch (IOException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	

	public void testCase02 () {
		IConnection rcv = new LocationReceiverMock (testCaseMessages, 100);

		try
		{
			receivedMessages = 0;
			ILocationMessageListener listener = new MyMsgListener ();
			LocationDaemon locd = new LocationDaemon (rcv);
			locd.addLocationMessageListener (listener);
			locd.removeLocationMessageListener (listener);
			locd.start ();
			try	{ Thread.sleep (1000); } catch (Exception e) { }
			locd.terminate ();

			assertEquals (0, receivedMessages);
		} catch (IOException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	public void testCase03 () {
		LocationReceiverMock rcv = new LocationReceiverMock (testCaseMessages, 100);

		try
		{
			receivedMessages = 0;
			ILocationMessageListener listener = new MyMsgListener ();
			LocationDaemon locd = new LocationDaemon (rcv);
			locd.addLocationMessageListener (listener);
			locd.start ();

			try { Thread.sleep (1000); } catch (Exception e) {}
			
			rcv.pretendInputStreamIOException ();
			
			int counter = 20;
			while (locd.isRunning () && counter-- > 0) {
				try { Thread.sleep (100); } catch (Exception e) {}
			}

			assertFalse (locd.isRunning ());

		} catch (IOException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	
	
	
	
	
	/**
	 * This class implements a simple receiver for location messages.
	 */
	private class MyMsgListener implements ILocationMessageListener
	{
		public void receive (LocationMessage message) {
			++receivedMessages;
			System.out.print ("LocationDaemonTestCase.receive: ");
			try
			{
				System.out.write (message.getBytes ());
			} catch (IOException e)
			{
			}
			System.out.flush ();
		}

	}
}
