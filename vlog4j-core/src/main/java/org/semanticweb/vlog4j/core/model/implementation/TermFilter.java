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
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;
import org.semanticweb.vlog4j.core.model.api.Variable;

/**
 * A visitor that builds a set of terms of a specific type. It can be used to
 * visit many terms and will only retain the ones that match the given type.
 * 
 * @author Markus Krötzsch
 *
 */
public class TermFilter implements TermVisitor<Void> {

	final TermType termType;
	final Set<Term> terms = new HashSet<>();

	/**
	 * Creates a new term filter.
	 * 
	 * @param termType
	 *            type of the term to restrict to, or null if all terms should be
	 *            kept
	 */
	public TermFilter(TermType termType) {
		this.termType = termType;
	}

	/**
	 * Returns the set of terms collected so far.
	 * 
	 * @return set of terms
	 */
	public Set<Term> getTerms() {
		return Collections.unmodifiableSet(this.terms);
	}

	/**
	 * Returns the set of variables collected so far, which might be empty if the
	 * terms collected are not variables. The method will also return an empty set
	 * if anything other than variables are being collected.
	 * 
	 * @return set of variables (see {@link Variable}).
	 */
	@SuppressWarnings("unchecked")
	public Set<Variable> getVariables() {
		if (this.termType.equals(TermType.VARIABLE)) {
			return (Set<Variable>) (Set<? extends Term>) Collections.unmodifiableSet(this.terms);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Returns the set of constants collected so far, which might be empty if the
	 * terms collected are not constants. The method will also return an empty set
	 * if anything other than constants are being collected.
	 * 
	 * @return set of constants (see {@link Constant}).
	 */
	@SuppressWarnings("unchecked")
	public Set<Constant> getConstants() {
		if (this.termType.equals(TermType.CONSTANT)) {
			return (Set<Constant>) (Set<? extends Term>) Collections.unmodifiableSet(this.terms);
		} else {
			return Collections.emptySet();
		}
	}
	
	/**
	 * Returns the set of blanks collected so far, which might be empty if the
	 * terms collected are not blanks. The method will also return an empty set
	 * if anything other than blanks are being collected.
	 * 
	 * @return set of blanks (see {@link Blank}).
	 */
	@SuppressWarnings("unchecked")
	public Set<Blank> getBlanks(){
		if (this.termType.equals(TermType.BLANK)) {
			return (Set<Blank>) (Set<? extends Term>) Collections.unmodifiableSet(this.terms);
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public Void visit(Constant term) {
		if (this.termType == null || this.termType.equals(TermType.CONSTANT)) {
			this.terms.add(term);
		}
		return null;
	}

	@Override
	public Void visit(Variable term) {
		if (this.termType == null || this.termType.equals(TermType.VARIABLE)) {
			this.terms.add(term);
		}
		return null;
	}

	@Override
	public Void visit(Blank term) {
		if (this.termType == null || this.termType.equals(TermType.BLANK)) {
			this.terms.add(term);
		}
		return null;
	}

}
