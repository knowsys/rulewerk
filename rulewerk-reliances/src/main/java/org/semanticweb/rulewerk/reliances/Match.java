package org.semanticweb.rulewerk.reliances;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
