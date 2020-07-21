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
import java.util.Iterator;
import java.util.List;

public class Assignment implements Iterable<int[]> {

	AssignmentIterator assignmentIterator;

	private class AssignmentIterator implements Iterator<int[]> {
		NumbersInBaseAndLengthFromMinusOne numbers; // base to count

		public AssignmentIterator(int assign, int assignTo) {
			numbers = new NumbersInBaseAndLengthFromMinusOne(assignTo, assign);
		}

		@Override
		public boolean hasNext() {
			return !numbers.stop;
		}

		@Override
		public int[] next() {
			int[] helper = numbers.next();
			while (!valid(helper)) {
				helper = numbers.next();
			}
			return helper;
		}

		private boolean valid(int[] representation) {
			for (int i=0; i<representation.length; i++) {
				if (representation[i] != -1) {
					return true;
				}
			}
			return false;
		}
	}

	public Assignment(int assign, int assignTo) {
		assignmentIterator = new AssignmentIterator(assign, assignTo);
	}

	@Override
	public Iterator<int[]> iterator() {
		return assignmentIterator;
	}

	private static List<Integer> complement(int size, List<Integer> of) {
		List<Integer> result = new ArrayList<>();
		for (int i=0; i<size; i++) {
			if (!of.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}
	
	public static List<Integer> head11Idx(int headSize, int[] match) {
		List<Integer> result = new ArrayList<>();
		for (int i=0; i<match.length; i++) {
			if (!result.contains(match[i]) && match[i] != -1) {
				result.add(match[i]);
			}
		}
		return result;
	}

	public static List<Integer> head12Idx(int headSize, int[] match) {
		return complement(headSize, head11Idx(headSize, match));
	}

	public static List<Integer> body21Idx(int bodySize, int[] match) {
		List<Integer> result = new ArrayList<>();

		for (int i=0; i<bodySize; i++) {
			if (match[i] != -1) {
				result.add(i);
			}
		}
		
		return result;
	}

	public static List<Integer> body22Idx(int bodySize, int[] match) {
		return complement(bodySize, body21Idx(bodySize, match));
	}

}
