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

import java.util.Iterator;

public class AssignmentIterable implements Iterable<Assignment> {

	AssignmentIterator assignmentIterator;

	private class AssignmentIterator implements Iterator<Assignment> {
		int assignedLength;
		int assigneeLength;
		NumbersInBaseAndLengthFromMinusOne numbers; // base to count

		public AssignmentIterator(int assignedLength, int assigneeLength) {
			this.assignedLength = assignedLength;
			this.assigneeLength = assigneeLength;
			numbers = new NumbersInBaseAndLengthFromMinusOne(assigneeLength, assignedLength);
		}

		@Override
		public boolean hasNext() {
			return !numbers.stop;
		}

		/**
		 * Returns an Assignment of the positions in the second container to the
		 * positions in the first container. The position in the array ([i]) represents
		 * the location in the second container (what is being mapped). The value in the
		 * array at a given position (array[i]) represents the location in the first
		 * container (what is mapped to).
		 */
		@Override
		public Assignment next() {
			Assignment assignment = new Assignment(numbers.next(), assignedLength, assigneeLength);
			while (!assignment.isValid()) {
				assignment = new Assignment(numbers.next(), assignedLength, assigneeLength);
			}
			return assignment;
		}

	}

	/**
	 * Given two int's that represent the number of elements in an assigned and
	 * assignee lists, an Assignment is an array of int's s.t. the position in the
	 * array indicates the position of the element in the assigned list, and the
	 * value indicates the position in the assignee list.
	 * 
	 * @param assignedLenght number of assigned objects
	 * @param assigneeLenght number of assignee objects
	 */
	public AssignmentIterable(int assignedLength, int assigneeLength) {
		assignmentIterator = new AssignmentIterator(assignedLength, assigneeLength);
	}

	@Override
	public Iterator<Assignment> iterator() {
		return assignmentIterator;
	}

}
