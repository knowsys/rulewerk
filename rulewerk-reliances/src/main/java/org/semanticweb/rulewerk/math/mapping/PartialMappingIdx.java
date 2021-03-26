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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class to store a list of mappings (or images) indexes represented by
 * {@code Pair}s of integers. The integers represent the position, or index, of
 * the objects in a list or array.
 * 
 * @author Larry González
 * @TODO is a set better here?
 */
public class PartialMappingIdx {
	List<Pair<Integer, Integer>> mappings;
	int domineSize;
	int codomineSize;

	// TODO where is this called?
	public PartialMappingIdx(int[] assignment, int domineSize, int codomineSize) {
		mappings = new ArrayList<>();
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] != -1) {
				mappings.add(new Pair<Integer, Integer>(i, assignment[i]));
			}
		}

		this.domineSize = domineSize;
		this.codomineSize = codomineSize;
	}

	// TODO where is this called? is this necesary?
	private PartialMappingIdx(List<Pair<Integer, Integer>> mappings, int domineSize, int codomineSize) {
		this.mappings = new ArrayList<>(mappings);

		this.domineSize = domineSize;
		this.codomineSize = codomineSize;
	}

	// TODO where is this called? is this necessary? is this name correct?
	static public PartialMappingIdx composition(PartialMappingIdx f, PartialMappingIdx g) {
		List<Pair<Integer, Integer>> newMappings = new ArrayList<>();

		for (Pair<Integer, Integer> image : f.getImages()) {
			if (g.getImage(image.getY()) >= 0) {
				newMappings.add(new Pair<Integer, Integer>(image.getX(), g.getImage(image.getY())));
			}
		}
		return new PartialMappingIdx(newMappings, f.domineSize, g.codomineSize);
	}

	// TODO this should be a composition of mappings. 19.01.2021 I dno't think so.
	public PartialMappingIdx(PartialMappingIdx old, List<Integer> previousIndexes, int oldDomineSize) {
		mappings = new ArrayList<>();
		for (Pair<Integer, Integer> oldMatch : old.getImages()) {
			mappings.add(new Pair<Integer, Integer>(previousIndexes.get(oldMatch.getX()), oldMatch.getY()));
		}
		this.domineSize = oldDomineSize;
		this.codomineSize = old.codomineSize;
	}

	/**
	 *
	 * @return the number of images in the partial mapping.
	 */
	public int size() {
		return mappings.size();
	}

	/**
	 *
	 * @return the list of elements in the domain that appear in the partial mapping
	 */
	public List<Integer> activeDomain() {
		List<Integer> result = new ArrayList<>();
		mappings.forEach(image -> result.add(image.getX()));
		return result;
	}

	/**
	 * 
	 * @return the list of elements in the domain that do not appear in the partial
	 *         mapping
	 */
	public List<Integer> inactiveDomain() {
		List<Integer> activeDomain = activeDomain();
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < domineSize; i++) {
			if (!activeDomain.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}

	/**
	 *
	 * @return the list of elements in the codomain that appear in the partial
	 *         mapping as value.
	 */
	public List<Integer> range() {
		List<Integer> result = new ArrayList<>();
		mappings.forEach(image -> result.add(image.getY()));
		return result;
	}

	/**
	 *
	 * @return the list of elements in the codomain that do not appear in the
	 *         partial mapping as value.
	 */
	List<Integer> rangeComplement() {
		List<Integer> range = range();
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < codomineSize; i++) {
			if (!range.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}

	/**
	 *
	 * @return the image index of the domain element index x or -1, if not present
	 */
	public int getImage(int x) {
		for (Pair<Integer, Integer> image : mappings) {
			if (image.getX() == x) {
				return image.getY();
			}
		}
		return -1;
	}

	/**
	 * Getter of images
	 *
	 * @return list of Images
	 */
	public List<Pair<Integer, Integer>> getImages() {
		return mappings;
	}

	@Override
	public String toString() {
		return Arrays.toString(mappings.toArray()) + ", " + domineSize + ", " + codomineSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + domineSize;
		result = prime * result + codomineSize;
		result = prime * result + ((mappings == null) ? 0 : mappings.hashCode());
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
		PartialMappingIdx other = (PartialMappingIdx) obj;
		if (domineSize != other.domineSize)
			return false;
		if (codomineSize != other.codomineSize)
			return false;
		if (mappings == null) {
			if (other.mappings != null)
				return false;
		} else if (!mappings.equals(other.mappings))
			return false;
		return true;
	}

}
