package org.semanticweb.rulewerk.reasoner.vlog;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.LanguageStringConstant;

/*-
 * #%L
 * Rulewerk VLog Reasoner Support
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
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

import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.TermVisitor;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.reasoner.implementation.Skolemization;

/**
 * A visitor that converts {@link Term}s of different types to corresponding
 * internal VLog model {@link karmaresearch.vlog.Term}s.
 *
 * @author Irina Dragoste
 *
 */
class TermToVLogConverter implements TermVisitor<karmaresearch.vlog.Term> {

	static final Skolemization skolemization = new Skolemization();

	/**
	 * Transforms an abstract constant to a {@link karmaresearch.vlog.Term} with the
	 * same name and type {@link karmaresearch.vlog.Term.TermType#CONSTANT}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(final AbstractConstant term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, getVLogNameForConstant(term));
	}

	/**
	 * Transforms a datatype constant to a {@link karmaresearch.vlog.Term} with the
	 * same name and type {@link karmaresearch.vlog.Term.TermType#CONSTANT}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(final DatatypeConstant term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, term.getName());
	}

	/**
	 * Transforms a language-tagged string constant to a
	 * {@link karmaresearch.vlog.Term} with the same name and type
	 * {@link karmaresearch.vlog.Term.TermType#CONSTANT}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(final LanguageStringConstant term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, term.getName());
	}

	/**
	 * Converts the given constant to the name of a constant in VLog.
	 *
	 * @param constant
	 * @return VLog constant string
	 */
	public static String getVLogNameForConstant(final Constant constant) {
		final String constantName = constant.getName();
		if (constant.getType() == TermType.ABSTRACT_CONSTANT) {
			return getVLogNameForIRI(constantName);
		} else { // datatype literal
			return constantName;
		}
	}

	/**
	 * Converts the given named null to the name of a (skolem) constant in VLog.
	 *
	 * @param named null
	 * @return VLog constant string
	 */
	public static String getVLogNameForNamedNull(final NamedNull namedNull) {
		return skolemization.getSkolemConstantName(namedNull);
	}

	/**
	 * Converts the string representation of a constant in Rulewerk directly to the
	 * name of a constant in VLog, without parsing it into a {@link Constant} first.
	 *
	 * @param rulewerkConstantName
	 * @return VLog constant string
	 */
	public static String getVLogNameForConstantName(final String rulewerkConstantName) {
		if (rulewerkConstantName.startsWith("\"")) { // keep datatype literal strings unchanged
			return rulewerkConstantName;
		} else
			return getVLogNameForIRI(rulewerkConstantName);
	}

	private static String getVLogNameForIRI(final String rulewerkConstantName) {
		if (rulewerkConstantName.contains(":")) { // enclose absolute IRIs with < >
			return "<" + rulewerkConstantName + ">";
		} else { // keep relative IRIs unchanged
			return rulewerkConstantName;
		}
	}

	/**
	 * Transforms a universal variable to a {@link karmaresearch.vlog.Term} with the
	 * same name and type {@link karmaresearch.vlog.Term.TermType#VARIABLE}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(final UniversalVariable term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, term.getName());
	}

	/**
	 * Transforms an existential variable to a {@link karmaresearch.vlog.Term} with
	 * the same name and type {@link karmaresearch.vlog.Term.TermType#VARIABLE}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(final ExistentialVariable term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, "!" + term.getName());
	}

	/**
	 * Transforms a named null to a {@link karmaresearch.vlog.Term} with the same
	 * name and type {@link karmaresearch.vlog.Term.TermType#BLANK}.
	 */
	@Override
	public karmaresearch.vlog.Term visit(final NamedNull term) {
		return new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.BLANK, term.getName());
	}

}
