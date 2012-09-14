/*
 * @(#) DistributedGisLegendRenderable.java Dec 14, 2004
 * 
 * 
 * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * 
 * See for project information <a href="http://www.simulation.tudelft.nl/">
 * www.simulation.tudelft.nl </a>.
 * 
 * The source code and binary code of this software is proprietary information
 * of Delft University of Technology.
 */
package com.accenture.simulation.supplychain.animation.D2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.rmi.RemoteException;

import javax.media.j3d.Bounds;
import javax.vecmath.Point2d;

import nl.tudelft.simulation.dsol.animation.LocatableInterface;
import nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface;
import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.gui.animation2D.AnimationPanel;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.language.d3.DirectedPoint;
import nl.tudelft.simulation.logger.Logger;


/**
 * Displays the legend on an animation panel.
 * <p>
 * 
 * Copyright (c) 2003-2006 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved.
 * 
 * See for project information <a href="http://www.simulation.tudelft.nl/">
 * www.simulation.tudelft.nl </a>.
 * 
 * The source code and binary code of this software is proprietary information
 * of Delft University of Technology.
 * 
 * @author <a
 *         href="http://www.tbm.tudelft.nl/webstaf/stijnh/index.htm">Stijn-Pieter
 *         van Houten </a>
 * @version $Revision: 1.6 $ $Date: 2007/01/04 13:30:58 $
 * @since 1.0.8
 */
public class GisLegendRenderable extends SingleImageRenderable

{
	/** the serial version uid */
	private static final long serialVersionUID = 12L;

	/** the scaling factor */
	public static double scalingFactor = 1.0;

	private LocatableInterface locatable = null;

	/**
	 * constructs a new GisLegendRenderable
	 * 
	 * @param locatable
	 *            the locatable
	 * @param simulator
	 *            the simulator
	 * @param imageURL
	 *            the image url
	 */
	public GisLegendRenderable(final LocatableInterface locatable,
			final SimulatorInterface simulator, final URL imageURL) {
		super(locatable, simulator, imageURL);
		this.locatable = locatable;
	}

	/**
	 * constructs a new GISContentAnimation
	 * 
	 * @param staticLocation
	 *            the static location
	 * @param size
	 *            the size
	 * @param simulator
	 *            the simulator
	 * @param image
	 *            the image
	 */
	public GisLegendRenderable(final Point2D staticLocation,
			final Dimension size, final SimulatorInterface simulator,
			final URL image) {
		super(staticLocation, size, simulator, image);
		this.locatable = new Location(staticLocation);
	}

	/**
	 * constructs a new GISContentAnimation
	 * 
	 * @param staticLocation
	 *            the static location
	 * @param size
	 *            the size of the image
	 * @param simulator
	 *            the simulator
	 * @param image
	 *            the image
	 */
	public GisLegendRenderable(final DirectedPoint staticLocation,
			final Dimension size, final SimulatorInterface simulator,
			final URL image) {
		super(staticLocation, size, simulator, image);
		this.locatable = new Location(staticLocation);
	}

	/**
	 * @see nl.tudelft.simulation.dsol.animation.D2.Renderable2DInterface#paint(java.awt.Graphics2D,
	 *      java.awt.geom.Rectangle2D, java.awt.Dimension,
	 *      java.awt.image.ImageObserver)
	 */
	@Override
	public synchronized void paint(final Graphics2D graphics,
			final Rectangle2D extent, final Dimension screen,
			final ImageObserver observer) {
		AnimationPanel animationPanel = (AnimationPanel) observer;
		double _scale = Renderable2DInterface.Util.getScale(animationPanel
				.getExtent(), animationPanel.getSize());

		Point2d screenLocation = null;
		try {
			screenLocation = new Point2d(this.locatable.getLocation().x,
					this.locatable.getLocation().y);
		} catch (RemoteException remoteException) {
			Logger.severe(this, "paint", remoteException);
		}

		// we only draw our images if their is a certain zoom level
		if (_scale > 0.5) {

			// supplier
			graphics.setColor(Color.YELLOW);
			graphics.fillOval((int) screenLocation.x, (int) screenLocation.y,
					10, 10);
			graphics.setColor(Color.BLACK);
			graphics.drawString("Supplier", (int) screenLocation.x + 25,
					(int) screenLocation.y + 10);

			// manufacturer
			graphics.setColor(Color.BLUE);
			graphics.fillOval((int) screenLocation.x,
					(int) screenLocation.y + 15, 10, 10);
			graphics.setColor(Color.BLACK);
			graphics.drawString("Manufacturer", (int) screenLocation.x + 25,
					(int) screenLocation.y + 25);

			// distributor
			graphics.setColor(Color.RED);
			graphics.fillOval((int) screenLocation.x,
					(int) screenLocation.y + 30, 10, 10);
			graphics.setColor(Color.BLACK);
			graphics.drawString("Distributor", (int) screenLocation.x + 25,
					(int) screenLocation.y + 40);

			// market
			graphics.setColor(Color.GREEN);
			graphics.fillOval((int) screenLocation.x,
					(int) screenLocation.y + 45, 10, 10);
			graphics.setColor(Color.BLACK);
			graphics.drawString("Market", (int) screenLocation.x + 25,
					(int) screenLocation.y + 55);
		}
	}

	/**
	 * @author s.van.houten
	 */
	private class Location implements LocatableInterface {

		private DirectedPoint location = null;

		/**
		 * @param location
		 */
		public Location(final Point2D location) {
			super();
			this.location = new DirectedPoint(location);
		}

		/**
		 * @param location
		 */
		public Location(final DirectedPoint location) {
			super();
			this.location = new DirectedPoint(location);
		}

		/**
		 * 
		 */
		public Bounds getBounds() throws RemoteException {
			return null;
		}

		/**
		 * 
		 */
		public DirectedPoint getLocation() throws RemoteException {
			return this.location;
		}

	}
}