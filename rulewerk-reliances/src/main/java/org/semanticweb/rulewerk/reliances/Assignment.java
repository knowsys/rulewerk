package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Assignment {
	int[] representation;
	int assignedLength;
	int assigneeLength;

	public Assignment(int[] assignment, int assignedLength, int assigneeLength) {
		this.representation = Arrays.copyOf(assignment, assignment.length);
		this.assignedLength = assignedLength;
		this.assigneeLength = assigneeLength;
	}

	boolean isValid() {
		for (int i = 0; i < this.representation.length; i++) {
			if (this.representation[i] != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param firstContainerLenght is used in the method complement.
	 * @param assignment
	 * @return list of positions in the first container that are going to be used in
	 *         the unification process.
	 */
	List<Integer> indexesInAssignedListToBeUnified() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < this.representation.length; i++) {
			if (!result.contains(this.representation[i]) && this.representation[i] != -1) {
				result.add(this.representation[i]);
			}
		}
		return result;
	}

	List<Integer> indexesInAssignedListToBeIgnored() {
		return complement(this.assigneeLength, indexesInAssignedListToBeUnified());
	}

	List<Integer> indexesInAssigneeListToBeUnified() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < assignedLength; i++) {
			if (this.representation[i] != -1) {
				result.add(i);
			}
		}
		return result;
	}

	List<Integer> indexesInAssigneeListToBeIgnored() {
		return complement(this.assignedLength, indexesInAssigneeListToBeUnified());

	}

	@Override
	public String toString() {
		return Arrays.toString(representation) + ", " + assignedLength + ", " + assigneeLength;
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
