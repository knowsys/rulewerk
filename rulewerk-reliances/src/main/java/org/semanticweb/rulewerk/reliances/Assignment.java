package org.semanticweb.rulewerk.reliances;

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

	boolean isValid() {
		return matches.size() > 0;
	}

	/**
	 * @param firstContainerLenght is used in the method complement.
	 * @param assignment
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
