package org.semanticweb.vlog4j.owlapi.reasoner;

/*-
 * #%L
 * VLog4j OWL API Support
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

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.owlapi.OwlFeatureNotSupportedException;

public class TermsToOWLIndividualsConversionHelper implements TermVisitor<OWLIndividual> {

	@Override
	public OWLIndividual visit(Constant term) {
		// TODO OWLNamedIndividual
		return null;
	}

	@Override
	public OWLIndividual visit(Variable term) {
		throw new OwlFeatureNotSupportedException(
				"Could not convert VLog Variable '" + term + "' to an OWLIndividual.");
	}

	@Override
	public OWLIndividual visit(Blank term) {
		// TODO OWLAnonymousIndividual
		return null;
	}

}
