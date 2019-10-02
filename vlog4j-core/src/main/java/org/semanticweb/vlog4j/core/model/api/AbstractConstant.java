package org.semanticweb.vlog4j.core.model.api;

/**
 * Interface for abstract constants, i.e. for constants that represent an
 * abstract domain element (in contrast to a specific value of a concrete
 * datatype). Such terms are of type {@link TermType#ABSTRACT_CONSTANT}.
 * 
 * @author Markus Kroetzsch
 */
public interface AbstractConstant extends Constant {

	@Override
	default TermType getType() {
		return TermType.ABSTRACT_CONSTANT;
	}

}
