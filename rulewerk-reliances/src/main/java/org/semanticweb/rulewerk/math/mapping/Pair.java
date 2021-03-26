package org.semanticweb.rulewerk.math.mapping;

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
 * A class to store a pair of objects.
 * 
 * @author Larry González
 *
 */
public class Pair<T1, T2> {
	private T1 x;
	private T2 y;

	/**
	 * Constructor of an Pair.
	 * 
	 * @param x first element of the pair
	 * @param y second element of the pair
	 */
	public Pair(T1 x, T2 y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	/**
	 * Setter of x
	 * 
	 * @param x the x to set
	 */
	public void setX(T1 x) {
		this.x = x;
	}

	/**
	 * Setter of y
	 * 
	 * @param y the y to set
	 */
	public void setY(T2 y) {
		this.y = y;
	}

	/**
	 * Getter of x
	 * 
	 * @return x
	 */
	public T1 getX() {
		return x;
	}

	/**
	 * Getter of y
	 * 
	 * @return y
	 */
	public T2 getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
}
