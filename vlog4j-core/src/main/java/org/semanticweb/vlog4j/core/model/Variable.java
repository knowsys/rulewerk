package org.semanticweb.vlog4j.core.model;

public class Variable implements Term {

	private final String name;

	public Variable(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public TermType getType() {
		return TermType.VARIABLE;
	}

	public boolean isVariable() {
		return true;
	}

	public boolean isConstant() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
		Variable other = (Variable) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Variable [name=" + this.name + "]";
	}

}
