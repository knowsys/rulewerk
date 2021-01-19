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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class to store a list of {@code Image}s x → y of a partial mapping.
 * 
 * @author Larry González
 *
 */
public class PartialMapping {
	List<Image> images;
	int domineSize;
	int codomineSize;

	public PartialMapping(int[] assignment, int domineSize, int codomineSize) {
		images = new ArrayList<>();
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] != -1) {
				images.add(new Image(i, assignment[i]));
			}
		}

		this.domineSize = domineSize;
		this.codomineSize = codomineSize;
	}

	private PartialMapping(List<Image> images, int domineSize, int codomineSize) {
		this.images = new ArrayList<>(images);

		this.domineSize = domineSize;
		this.codomineSize = codomineSize;
	}

	static public PartialMapping composition(PartialMapping f, PartialMapping g) {
		List<Image> newImages = new ArrayList<>();

		for (Image image : f.getImages()) {
			if (g.getImage(image.getY()) >= 0) {
				newImages.add(new Image(image.getX(), g.getImage(image.getY())));
			}
		}
		return new PartialMapping(newImages, f.domineSize, g.codomineSize);
	}

	// TODO this should be a composition of mappings
	public PartialMapping(PartialMapping old, List<Integer> previousIndexes, int oldDomineSize) {
		images = new ArrayList<>();
		for (Image oldMatch : old.getImages()) {
			images.add(new Image(previousIndexes.get(oldMatch.x), oldMatch.y));
		}
		this.domineSize = oldDomineSize;
		this.codomineSize = old.codomineSize;
	}

	/**
	 *
	 * @return the number of images in the partial mapping.
	 */
	public int size() {
		return images.size();
	}

	/**
	 *
	 * @return the list of elements in the domain that appear in the partial mapping
	 */
	public List<Integer> activeDomain() {
		List<Integer> result = new ArrayList<>();
		images.forEach(image -> result.add(image.getX()));
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
		images.forEach(image -> result.add(image.getY()));
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
		for (Image image : images) {
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
	public List<Image> getImages() {
		return images;
	}

	@Override
	public String toString() {
		return Arrays.toString(images.toArray()) + ", " + domineSize + ", " + codomineSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + domineSize;
		result = prime * result + codomineSize;
		result = prime * result + ((images == null) ? 0 : images.hashCode());
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
		if (domineSize != other.domineSize)
			return false;
		if (codomineSize != other.codomineSize)
			return false;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		return true;
	}

}
