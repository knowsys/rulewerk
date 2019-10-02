package org.semanticweb.vlog4j.core.model.implementation;

import org.semanticweb.vlog4j.core.model.api.DatatypeConstant;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;

/**
 * Simple implementation of {@link DatatypeConstant}.
 * 
 * @author Markus Kroetzsch
 *
 */
public class DatatypeConstantImpl implements DatatypeConstant {

	final String datatype;
	final String lexicalValue;

	public DatatypeConstantImpl(String lexicalValue, String datatype) {
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

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public String getName() {
		return "\"" + lexicalValue.replace("\\", "\\\\").replace("\"", "\\\"") + "\"^^<" + datatype + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = datatype.hashCode();
		result = prime * result + lexicalValue.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatatypeConstantImpl other = (DatatypeConstantImpl) obj;

		return this.lexicalValue.equals(other.getLexicalValue()) && this.datatype.equals(other.getDatatype());
	}

}
