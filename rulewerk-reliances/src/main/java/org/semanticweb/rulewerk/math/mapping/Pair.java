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
 * A class to store the image index {@code y} of a value index {@code x} under a
 * partial mapping. Both {@code x} and {@code y} must be indexes in two arrays
 * or lists that represent the domain and codomain of the partial mapping
 * respectively.
 * 
 * @note that {@code Image} stores the indexes of {@code x} and {@code y}, not
 *       the actual values.
 * 
 * @author Larry González
 *
 */
public class Image {
	int x;
	int y;

	/**
	 * Constructor of an Image.
	 * 
	 * @param x index in the domain array/list
	 * @param y index in the codomain array/list
	 */
	public Image(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	/**
	 * Getter of x
	 * 
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Getter of y
	 * 
	 * @return y
	 */
	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + y;
		result = prime * result + x;
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
		Image other = (Image) obj;
		if (y != other.y)
			return false;
		if (x != other.x)
			return false;
		return true;
	}

}
