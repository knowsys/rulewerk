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
 * A class to represent a partial mapping between two lists. The partial mapping
 * is stored in an array of int's s.t. the position {@value i} represents the
 * index of an element in the first list, and its value {@value mapping[i]}
 * represent the index of an element in the second list. When the value
 * {@value mapping[i]} is equals to {@value -1}, then the element {@value i} in
 * the first list is not considered in the partial mapping.
 * 
 * @note we do not store the domineSize because it is equal to the
 *       {@code mapping} size.
 * 
 * @author Larry González
 */
public class PartialMapping {
	int[] mapping;
	int codomineSize;

	// TODO where is this called?
	public PartialMapping(int[] mapping, int codomineSize) {
		this.mapping = mapping.clone();
		this.codomineSize = codomineSize;
	}

// TODO where is this called? is this necessary?
//	private PartialMapping(List<Pair<Integer, Integer>> mappings, int domineSize, int codomineSize) {
//		this.mappings = new ArrayList<>(mappings);
//
//		this.domineSize = domineSize;
//		this.codomineSize = codomineSize;
//	}

	static public PartialMapping compose(PartialMapping f, PartialMapping g) {
		int[] newMapping = new int[f.size()];
		
		for (int i=0; i<f.size(); i++) {
			if (0 <= f.getImage(i) &&  f.getImage(i) < g.size()) {
				newMapping[i] = g.getImage(f.getImage(i));
			} else {
				newMapping[i] = -1;
			}
		}
		return new PartialMapping(newMapping, g.getCodomineSize());
	}

// TODO should this be a composition of mappings. 19.01.2021 I don't think so. 23.02.2021 I think so.
//	public PartialMappingIdx(PartialMapping old, List<Integer> previousIndexes, int oldDomineSize) {
//		mappings = new ArrayList<>();
//		for (Pair<Integer, Integer> oldMatch : old.getImages()) {
//			mappings.add(new Pair<Integer, Integer>(previousIndexes.get(oldMatch.getX()), oldMatch.getY()));
//		}
//		this.domineSize = oldDomineSize;
//		this.codomineSize = old.codomineSize;
//	}

	/**
	 * @return the number of images in the partial mapping.
	 */
	public int size() {
		return mapping.length;
	}
	
	/**
	 * @return the number of images in the partial mapping.
	 */
	public int getDomineSize() {
		return activeDomain().size();
	}
	
	/**
	 * @return the number of images in the partial mapping.
	 */
	public int getCodomineSize() {
		return codomineSize;
	}

	/**
	 * @return the list of elements in the domain that appear in the partial mapping
	 */
	public List<Integer> activeDomain() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < mapping.length; i++) {
			if (mapping[i] != -1) {
				result.add(i);
			}
		}
		return result;
	}

	/**
	 * @return the list of elements in the domain that do not appear in the partial
	 *         mapping
	 */
	public List<Integer> inactiveDomain() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < mapping.length; i++) {
			if (mapping[i] == -1) {
				result.add(i);
			}
		}
		return result;
	}

	/**
	 * @return the non-unique list of elements in the codomain that appear in the
	 *         partial mapping as value.
	 */
	public List<Integer> range() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < mapping.length; i++) {
			if (mapping[i] != -1) {
				result.add(mapping[i]);
			}
		}
		return result;
	}

	/**
	 * @return the list of elements in the codomain that do not appear in the
	 *         partial mapping as value.
	 */
	public List<Integer> corange() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < codomineSize; i++) {
			if (mapping[i] == -1) {
				result.add(mapping[i]);
			}
		}
		return result;
	}

	/**
	 * @return the image index of the domain element index x or -1, if not present
	 */
	public int getImage(int x) {
		return mapping[x];
	}

	/**
	 * Getter of images
	 *
	 * @return list of Images
	 */
	public List<Pair<Integer, Integer>> getImages() {
		List<Pair<Integer, Integer>> result = new ArrayList<>();
		for (int i = 0; i < mapping.length; i++) {
			if (mapping[i] != -1) {
				result.add(new Pair<>(i, mapping[i]));
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return Arrays.toString(mapping) + ", codomine size = " + codomineSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codomineSize;
		result = prime * result + Arrays.hashCode(mapping);
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
		PartialMapping other = (PartialMapping) obj;
		if (codomineSize != other.codomineSize)
			return false;
		if (!Arrays.equals(mapping, other.mapping))
			return false;
		return true;
	}

}
