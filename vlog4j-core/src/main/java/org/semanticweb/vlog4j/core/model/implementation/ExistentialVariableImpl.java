package org.semanticweb.vlog4j.core.model.implementation;

import org.semanticweb.vlog4j.core.model.api.TermVisitor;
import org.semanticweb.vlog4j.core.model.api.ExistentialVariable;

/**
 * Simple implementation of {@link ExistentialVariable}.
 *
 * @author Markus Kroetzsch
 */
public class ExistentialVariableImpl extends AbstractTermImpl implements ExistentialVariable {

	/**
	 * Constructor.
	 *
	 * @param name cannot be a blank String (null, empty or whitespace).
	 */
	public ExistentialVariableImpl(final String name) {
		super(name);
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}

	@Override
	public String toString() {
		return "!" + this.getName();
	}
}
