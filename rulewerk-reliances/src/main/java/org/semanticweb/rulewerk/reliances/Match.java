package org.semanticweb.rulewerk.reliances;

/**
 * A class to represent a match from an origin into a destination. Origin and
 * destination should be indexes (list or arrays)
 * 
 * @author Larry Gonzalez
 *
 */
public class Match {
	int origin;
	int destination;

	/**
	 * Constructor of a Match.
	 * 
	 * @param origin      index in the origin container
	 * @param destination index in the destination container
	 */
	public Match(int origin, int destination) {
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "[" + origin + ", " + destination + "]";
	}

	/**
	 * Getter of origin
	 * 
	 * @return int origin
	 */
	public int getOrigin() {
		return origin;
	}

	/**
	 * Getter of destination
	 * 
	 * @return int destination
	 */
	public int getDestination() {
		return destination;
	}

}
