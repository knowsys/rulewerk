package org.semanticweb.vlog4j.core.model.api;

/**
 * Interface for existentially quantified variables, i.e., variables that appear
 * in the scope of an (implicit) existential quantifier in a rule.
 *
 * @author Markus Krötzsch
 */
public interface ExistentialVariable extends Variable {

	@Override
	default TermType getType() {
		return TermType.EXISTENTIAL_VARIABLE;
	}
	
}
