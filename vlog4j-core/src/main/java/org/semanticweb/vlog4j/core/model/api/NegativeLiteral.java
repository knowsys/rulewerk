package org.semanticweb.vlog4j.core.model.api;

public interface NegativeLiteral extends Literal {

	@Override
	default boolean isNegated() {
		return true;
	}

}
