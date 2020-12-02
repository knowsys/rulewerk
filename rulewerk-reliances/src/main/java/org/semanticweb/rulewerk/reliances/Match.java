package org.semanticweb.rulewerk.reliances;

public class Match {
	int origin;
	int destination;

	public Match(int origin, int destination) {
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "[" + origin + ", " + destination + "]";
	}

	public int getOrigin() {
		return origin;
	}

	public int getDestination() {
		return destination;
	}

}
