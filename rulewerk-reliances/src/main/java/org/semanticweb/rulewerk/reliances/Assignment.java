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

	boolean isValid() {
		return matches.size() > 0;
	}

	boolean isValidForBCQ() {
		return matches.size() == assignedLength;
	}

	/**
	 * @return list of positions in the first container that are going to be used in
	 *         the unification process.
	 */
	List<Integer> indexesInAssignedListToBeUnified() {
		Set<Integer> result = new HashSet<>();
		for (Match match : matches) {
			result.add(match.getDestination());
		}
		return new ArrayList<>(result);
	}

	List<Integer> indexesInAssignedListToBeIgnored() {
		return complement(this.assigneeLength, indexesInAssignedListToBeUnified());
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

}
