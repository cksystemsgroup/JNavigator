/*
 * @(#) FlightPlanOne.java
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
package at.uni_salzburg.cs.ckgroup.pilot;

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;

/**
 * This class implements the flight plan type I.
 * 
 * Flight plan I is a general purpose flight plan requesting no preconditions.
 * The <code>ProactivePilot</code> will employ this flight plan if the others
 * are not applicable. However, the estimation of a flight plan might result in
 * a command sequence that needs more than the specified plan time. In this case
 * the estimation will extend the schedule as needed.
 * 
 * The command sequence for this flight plan is as follows:
 * 
 * <li>Accelerate or slow down to an intermediate speed.
 * <li>Travel at an intermediate speed along the distance vector to the next
 * set course fix point.
 * <li>Slow down or accelerate duly to the specified speed at the next
 * scheduled set course fix point to arrive there in time.
 * 
 * @author Clemens Krainer
 */
public class FlightPlanOne implements IFlightPlan
{
	/**
	 * The provider of flight parameters.
	 */
	private IFlightParameterProvider flightParameterProvider;
	
	/**
	 * The estimated acceleration vector.
	 */
	private CartesianCoordinate acceleration[] = null;

	/**
	 * The estimated duration of the acceleration.
	 */
	private long commandTime[];
	
	/**
	 * Construct a <code>FlightPlanOne</code> object.
	 * 
	 * @param props the <code>Properties</code> to be used.
	 */
	public FlightPlanOne (Properties props)
	{
		// Intentionally empty
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightPlan#estimateFlightPlan(at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate, at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate, at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate, long)
	 */
	public boolean estimateFlightPlan (CartesianCoordinate distance, CartesianCoordinate currentVelocity, CartesianCoordinate nextVelocity, long pTime)
	{
		acceleration = null;
		commandTime = null;

		double dist = distance.norm();
		double messageDelay = 0.001*flightParameterProvider.getPositionMessageDelay();
		double deadTime = 0.001*flightParameterProvider.getCommandDeadTime();
		double responseTime = 0.001*flightParameterProvider.getCommandResponseTime();
		double minCommandTime = 0.001*flightParameterProvider.getMinimumCommandTime();
		double planTime = pTime < 0 ? 0.0 : 0.001*pTime;
		double maxAcceleration = flightParameterProvider.getMaximumAcceleration();
		
		double currVel = currentVelocity.norm();
		double nextVel = nextVelocity.norm();

		if (dist < 0.5*(currVel + nextVel)*planTime)
			maxAcceleration = -maxAcceleration;
		
		double p = maxAcceleration*(deadTime-planTime) - currVel - nextVel;
		double q = maxAcceleration*(dist - currVel*(messageDelay + deadTime)) + 0.5*(currVel*currVel + nextVel*nextVel);
		double r = p * p / 4 - q;
		double newPlanTime;
		
		if (r < 0) {
			newPlanTime = deadTime - (-Math.sqrt(4*q) + currVel + nextVel)/maxAcceleration;
			System.out.println ("FlightPlanOne.estimateFlightPlan: planTime changed from " + planTime + "s to " + newPlanTime + "s");
			planTime = newPlanTime;
			p = maxAcceleration*(deadTime-planTime) - currVel - nextVel;
			r = p * p / 4 - q;
			r = 0;
		}
		
		double medVel = -p / 2 - Math.sqrt(r);
		if (medVel < 0)
			return false;

		double time1 = (medVel - currVel)/maxAcceleration;
		double time2 = (medVel - nextVel)/maxAcceleration;

		double commandTime1 = time1 - responseTime + deadTime;
		double commandTime2 = time2 - responseTime + deadTime;
		
		boolean recalc = false;
		if (commandTime1 < minCommandTime) {
			recalc = true;
			commandTime1 = minCommandTime;
			time1 = commandTime1 + responseTime - deadTime;
		}
		
		if (commandTime2 < minCommandTime) {
			recalc = true;
			commandTime2 = minCommandTime;
			time2 = commandTime2 + responseTime - deadTime;
		}
		
		newPlanTime = 2*deadTime + time1 + time2;
		if (planTime < newPlanTime) {
			System.out.println ("FlightPlanOne.estimateFlightPlan: planTime changed from " + planTime + "s to " + newPlanTime + "s");
			recalc = true;
			planTime = newPlanTime;
		}
		
		if (recalc) {
			medVel = (2*dist - currVel*(2*(messageDelay + deadTime) + time1) - nextVel*time2) / (2*(planTime - deadTime) - time1 - time2);
		}
		
		double acceleration1 = (medVel - currVel)/time1;
		double acceleration2 = (nextVel - medVel)/time2;
		
		CartesianCoordinate accelerationVector1 = distance.multiply(acceleration1/distance.norm());
		CartesianCoordinate accelerationVector2 = distance.multiply(acceleration2/distance.norm());
		CartesianCoordinate noAccelationVector = new CartesianCoordinate (0,0,0);
		
		acceleration = new CartesianCoordinate[] {
				accelerationVector1,
				noAccelationVector,
				accelerationVector2,
				noAccelationVector
			};
		
		commandTime = new long[] {
				(long)(1000*commandTime1),
				(long)(1000*(planTime - commandTime1 - commandTime2 - responseTime)),
				(long)(1000*commandTime2),
				(long)(1000*responseTime)
			};
		
		double d2 = currVel*(messageDelay + deadTime) + medVel*(planTime - deadTime) - 0.5*(medVel - currVel)*time1 + 0.5*(nextVel - medVel)*time2; 
		System.out.println ("FlightPlanOne.estimateFlightPlan: distance=" + distance + ", d2=" + d2 + ", currentVelocity=" + currentVelocity + ", nextVelocity=" + nextVelocity + ", planTime=" + planTime);
		System.out.println ("FlightPlanOne.estimateFlightPlan: distance=" + distance.norm() + ", currentVelocity=" + currentVelocity.norm() + ", nextVelocity=" + nextVelocity.norm());
		for (int k=0; k < acceleration.length; k++)
			System.out.println ("FlightPlanOne.estimateFlightPlan: commandTime["+k+"]=" + commandTime[k] + ", acceleration["+k+"]=" + acceleration[k]);

		return true;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightPlan#getAcceleration()
	 */
	public CartesianCoordinate[] getAccelerations()
	{
		return acceleration;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightPlan#getCommandTime()
	 */
	public long[] getCommandTimes()
	{
		return commandTime;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightPlan#reworkSchedule()
	 */
	public boolean scheduleNeedsReworking ()
	{
		return acceleration == null;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightPlan#setFlightParameterProvider(at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider)
	 */
	public void setFlightParameterProvider(IFlightParameterProvider flightParameterProvider) {
		this.flightParameterProvider = flightParameterProvider;
	}
}
