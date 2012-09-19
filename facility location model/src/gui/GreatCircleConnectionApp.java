package gui;

import processing.core.PApplet;
import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.MapUtils;

/**
 * Shows a great circle connection between two locations. A great circle connection is an approximation of the shortest
 * route between two places on earth. Displayed as curves in a Mercator projection (as used in Unfolding).
 * 
 * Mouse move and SHIFT or CTRL to move the locations.
 */
public class GreatCircleConnectionApp extends PApplet {

	final Location singaporeLocation = new Location(1, 100);
	final Location berlinLocation = new Location(54, 13.5f);
	final Location dubaiLocation = new Location (24,54);

	Map map;

	Location sourceLocation = berlinLocation;
	Location targetLocation = singaporeLocation;
	Location intermediaryLocation = dubaiLocation;

	public void setup() {
		size(800, 600, GLConstants.GLGRAPHICS);
		map = new Map(this);
		MapUtils.createDefaultEventDispatcher(this, map);
	}

	public void draw() {
		background(0);
		map.draw();

		fill(0, 0, 255);
		float[] sourcePos = map.getScreenPositionFromLocation(targetLocation);
		ellipse(sourcePos[0], sourcePos[1], 10, 10);

		fill(255, 0, 0);
		float[] targetPos = map.getScreenPositionFromLocation(sourceLocation);
		ellipse(targetPos[0], targetPos[1], 10, 10);
		
		fill(0, 0, 255);
		float[] intermediaryPos=map.getScreenPositionFromLocation(intermediaryLocation);
		ellipse(intermediaryPos[0], intermediaryPos[1], 10, 10);

		double bearing1 = GeoUtils.getAngleBetween(targetLocation, intermediaryLocation);
		double bearing2 = GeoUtils.getAngleBetween(intermediaryLocation, sourceLocation);
		double dist1 = GeoUtils.getDistance(targetLocation, intermediaryLocation);
		double dist2 = GeoUtils.getDistance(intermediaryLocation, sourceLocation);
		
		noFill();
		strokeWeight(2);
		stroke(0, 100);
		beginShape();
		for (float d = 0; d < dist1; d += 100) {
			Location tweenLocation = GeoUtils.getDestinationLocation(targetLocation, degrees((float) bearing1),
					(float) d);
			float[] tweenPos = map.getScreenPositionFromLocation(tweenLocation);
			vertex(tweenPos[0], tweenPos[1]);
		}
		for (float d = 0; d < dist2; d += 100) {
			Location tweenLocation = GeoUtils.getDestinationLocation(intermediaryLocation, degrees((float) bearing2),
					(float) d);
			float[] tweenPos = map.getScreenPositionFromLocation(tweenLocation);
			vertex(tweenPos[0], tweenPos[1]);
		}
		endShape();
	}

	public void mouseMoved() {
		if (keyPressed && key == CODED) {
			if (keyCode == SHIFT) {
				targetLocation = map.getLocationFromScreenPosition(mouseX, mouseY);
			}
			if (keyCode == CONTROL) {
				sourceLocation = map.getLocationFromScreenPosition(mouseX, mouseY);
			}
		}
	}

}
