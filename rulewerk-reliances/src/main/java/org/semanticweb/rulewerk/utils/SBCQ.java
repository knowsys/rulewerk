package org.semanticweb.rulewerk.utils;

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

import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.reliances.Assignment;
import org.semanticweb.rulewerk.reliances.AssignmentIterable;
import org.semanticweb.rulewerk.reliances.MartelliMontanariUnifier;

/**
 * A class to implement a simple boolean conjunctive query.
 * 
 * @author Larry Gonzalez
 *
 */
public class SBCQ {

	static boolean query(List<Literal> instance, List<Literal> query) {

		AssignmentIterable assignmentIterable = new AssignmentIterable(query.size(), instance.size());
		for (Assignment assignment : assignmentIterable) {

			if (assignment.size() == query.size()) {
				MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(query, instance, assignment);

//				System.out.println("    Assignment: " + assignment);
//				System.out.println("    Unifier:    " + unifier);

				if (unifier.getSuccess()) {
					return true;
				}
			}
		}
		return false;
	}

}
