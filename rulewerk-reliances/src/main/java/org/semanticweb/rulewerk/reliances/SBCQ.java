package org.semanticweb.rulewerk.reliances;

import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Literal;

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

			if (assignment.isValidForBCQ()) {
				MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(query, instance, assignment);

//				System.out.println("    Assignment: " + assignment);
//				System.out.println("    Unifier:    " + unifier);

				if (unifier.success) {
					return true;
				}
			}
		}
		return false;
	}

}
