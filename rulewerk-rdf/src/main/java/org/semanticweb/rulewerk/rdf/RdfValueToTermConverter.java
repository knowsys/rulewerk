package org.semanticweb.rulewerk.rdf;

/*-
 * #%L
 * Rulewerk RDF Support
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

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.rio.ntriples.NTriplesUtil;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.AbstractConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.DatatypeConstantImpl;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;

final class RdfValueToTermConverter {

	private RdfValueToTermConverter() {
	}

	static Term rdfValueToTerm(final Value value) {
		if (value instanceof BNode) {
			return rdfBlankNodeToBlank((BNode) value);
		} else if (value instanceof Literal) {
			return rdfLiteralToConstant((Literal) value);
		} else if (value instanceof URI) {
			return rdfUriToConstant((URI) value);
		} else {
			throw new RulewerkRuntimeException("Unknown value type: " + value.getClass());
		}
	}

	static Term rdfBlankNodeToBlank(final BNode bNode) {
		// IDs are generated to be unique in every Model.
		return new NamedNullImpl(bNode.getID());
	}

	static Term rdfUriToConstant(final URI uri) {
		final String escapedURIString = NTriplesUtil.escapeString(uri.toString());
		return new AbstractConstantImpl(escapedURIString);
	}

	static Term rdfLiteralToConstant(final Literal literal) {
		final URI datatype = literal.getDatatype();
		if (datatype != null) {
			return new DatatypeConstantImpl(XMLDatatypeUtil.normalize(literal.getLabel(), datatype),
					datatype.toString());
		} else if (literal.getLanguage() != null) {
			return new LanguageStringConstantImpl(literal.getLabel(), literal.getLanguage());
		} else {
			return new DatatypeConstantImpl(literal.getLabel(), PrefixDeclarationRegistry.XSD_STRING);
		}
	}

}
