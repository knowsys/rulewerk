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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to store a list of matches. An assignment represent a mapping between
 * the elements of two arrays/lists.
 * 
 * @note that the assignment does not need to be complete.
 * 
 * @author Larry Gonzalez
 *
 */
public class Assignment {
	List<Match> matches;
	int assignedLength;
	int assigneeLength;

	public Assignment(int[] assignment, int assignedLength, int assigneeLength) {
		matches = new ArrayList<>();
		for (int i = 0; i < assignment.length; i++) {
			if (assignment[i] != -1) {
				matches.add(new Match(i, assignment[i]));
			}
		}

		this.assignedLength = assignedLength;
		this.assigneeLength = assigneeLength;
	}

	public Assignment(Assignment old, List<Integer> previousIndexes, int previousAssignedLengnth) {
		matches = new ArrayList<>();
		for (Match oldMatch : old.getMatches()) {
			matches.add(new Match(previousIndexes.get(oldMatch.origin), oldMatch.destination));
		}
		this.assignedLength = previousAssignedLengnth;
		this.assigneeLength = old.assigneeLength;
	}

	public int size() {
		return matches.size();
	}

	List<Integer> indexesInAssigneeListToBeUnified() {
		Set<Integer> result = new HashSet<>();
		for (Match match : matches) {
			result.add(match.getOrigin());
		}
		return new ArrayList<>(result);
	}

	List<Integer> indexesInAssigneeListToBeIgnored() {
		return complement(this.assignedLength, indexesInAssigneeListToBeUnified());

	}

	/**
	 * Getter of matches
	 * 
	 * @return list of Matches
	 */
	public List<Match> getMatches() {
		return matches;
	}

	@Override
	public String toString() {
		return Arrays.toString(matches.toArray()) + ", " + assignedLength + ", " + assigneeLength;
	}

	private List<Integer> complement(int size, List<Integer> of) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			if (!of.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + assignedLength;
		result = prime * result + assigneeLength;
		result = prime * result + ((matches == null) ? 0 : matches.hashCode());
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
		Assignment other = (Assignment) obj;
		if (assignedLength != other.assignedLength)
			return false;
		if (assigneeLength != other.assigneeLength)
			return false;
		if (matches == null) {
			if (other.matches != null)
				return false;
		} else if (!matches.equals(other.matches))
			return false;
		return true;
	}

}
