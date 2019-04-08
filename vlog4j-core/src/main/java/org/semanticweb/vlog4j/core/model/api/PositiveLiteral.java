package org.semanticweb.vlog4j.core.model.api;

public interface PositiveLiteral extends Literal {

	@Override
	default boolean isNegated() {
		return false;
	}
}
