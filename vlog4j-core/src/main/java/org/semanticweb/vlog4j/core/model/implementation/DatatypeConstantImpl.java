package org.semanticweb.vlog4j.core.model.implementation;

import org.semanticweb.vlog4j.core.model.api.DatatypeConstant;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;

/**
 * Simple implementation of {@link DatatypeConstant}.
 * 
 * @author Markus Kroetzsch
 *
 */
public class DatatypeConstantImpl extends AbstractTermImpl implements DatatypeConstant {

	final String datatype;
	final String lexicalValue;

	public DatatypeConstantImpl(String lexicalValue, String datatype) {
		super("\"" + lexicalValue + "\"^^<" + datatype + ">");
		this.lexicalValue = lexicalValue;
		this.datatype = datatype;
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}

	@Override
	public String getDatatype() {
		return this.datatype;
	}

	@Override
	public String getLexicalValue() {
		return this.lexicalValue;
	}

}
