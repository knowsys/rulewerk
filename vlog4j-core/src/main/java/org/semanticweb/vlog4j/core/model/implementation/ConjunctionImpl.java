/**
 * 
 */
package org.semanticweb.vlog4j.core.model.implementation;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * Simple implementation of {@link Conjunction}.
 * 
 * @author Markus Krötzsch
 */
public class ConjunctionImpl implements Conjunction {

	final List<Atom> atoms;

	/**
	 * Constructor.
	 * 
	 * @param atoms
	 *            a non-null list of atoms, that cannot contain null elements.
	 */
	public ConjunctionImpl(@NonNull List<Atom> atoms) {
		Validate.noNullElements(atoms);
		this.atoms = atoms;
	}

	@Override
	public List<Atom> getAtoms() {
		return Collections.unmodifiableList(this.atoms);
	}

	/**
	 * Returns a term filter object that has visited all terms in this conjunction
	 * for the given type.
	 * 
	 * @param termType
	 *            specifies the type of term to look for
	 * @return term filter
	 */
	TermFilter getTermFilter(TermType termType) {
		final TermFilter termFilter = new TermFilter(termType);
		for (final Atom atom : this.atoms) {
			for (final Term term : atom.getTerms()) {
				term.accept(termFilter);
			}
		}
		return termFilter;
	}

	@Override
	public Set<Term> getTerms() {
		return getTermFilter(null).getTerms();
	}

	@Override
	public Set<Term> getTerms(TermType termType) {
		return getTermFilter(termType).getTerms();
	}

	@Override
	public Set<Variable> getVariables() {
		return getTermFilter(TermType.VARIABLE).getVariables();
	}

	@Override
	public int hashCode() {
		return this.atoms.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Conjunction)) {
			return false;
		}
		final Conjunction other = (Conjunction) obj;
		return this.atoms.equals(other.getAtoms());
	}

	@Override
	public Iterator<Atom> iterator() {
		return this.atoms.iterator();
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for (final Atom atom : this.atoms) {
			if (first) {
				first = false;
			} else {
				stringBuilder.append(", ");
			}
			stringBuilder.append(atom.toString());
		}
		return stringBuilder.toString();
	}

}
