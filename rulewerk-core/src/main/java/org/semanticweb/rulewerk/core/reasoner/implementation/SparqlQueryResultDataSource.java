package org.semanticweb.rulewerk.core.reasoner.implementation;

/*-
 * #%L
 * Rulewerk Core Components
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

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

/**
 * A SparqlQueryResultDataSource provide the results of a SPARQL query on a
 * given web endpoint.
 *
 * @author Irina Dragoste
 *
 */
public class SparqlQueryResultDataSource implements ReasonerDataSource {

	/**
	 * The name of the predicate used for declarations of data sources of this type.
	 */
	public static final String declarationPredicateName = "sparql";

	private final URL endpoint;
	private final String queryVariables;
	private final String queryBody;

	/**
	 * Creates a data source from answers to a remote SPARQL query.
	 *
	 * @param endpoint       web location of the resource the query will be
	 *                       evaluated on
	 * @param queryVariables comma-separated list of SPARQL variable names (without
	 *                       leading ? or $)
	 * @param queryBody      content of the <i>WHERE</i> clause in the SPARQL query
	 */
	// TODO add examples to javadoc
	// TODO add illegal argument exceptions to javadoc
	public SparqlQueryResultDataSource(final URL endpoint, final String queryVariables, final String queryBody) {
		Validate.notNull(endpoint, "Endpoint cannot be null.");
		Validate.notNull(queryVariables, "Query variables string cannot be null.");
		Validate.notEmpty(queryVariables, "There must be at least one query variable.");
		Validate.notBlank(queryBody, "Query body cannot be null or blank [{}].", queryBody);
		// TODO validate query body syntax (for example, new line character)
		// TODO validate early that the arity coincides with the assigned predicate
		this.endpoint = endpoint;
		this.queryVariables = queryVariables.replace(" ", "");
		this.queryBody = queryBody.replace("\n", " ");
	}

	/**
	 * Creates a data source from answers to a remote SPARQL query.
	 *
	 * @param endpoint       the web location of the resource the query will be
	 *                       evaluated on.
	 * @param queryVariables the variables of the query, in the given order. The
	 *                       variable at each position in the ordered set will be
	 *                       mapped to its correspondent query answer term at the
	 *                       same position.
	 * @param queryBody      the content of the <i>WHERE</i> clause in the SPARQL
	 *                       query. Must not contain {@code newline} characters
	 *                       ({@code "\n")}.
	 */
	// TODO add examples to javadoc
	// TODO add illegal argument exceptions to javadoc
	public SparqlQueryResultDataSource(final URL endpoint, final LinkedHashSet<Variable> queryVariables,
			final String queryBody) {
		Validate.notNull(endpoint, "Endpoint cannot be null.");
		Validate.notNull(queryVariables, "Query variables ordered set cannot be null.");
		Validate.noNullElements(queryVariables, "Query variables cannot be null or contain null elements.");
		Validate.notEmpty(queryVariables, "There must be at least one query variable.");
		Validate.notBlank(queryBody, "Query body cannot be null or blank [{}].", queryBody);
		// TODO validate query body syntax (for example, new line character)
		// TODO validate early that the arity coincides with the assigned predicate
		this.endpoint = endpoint;
		this.queryVariables = getQueryVariablesList(queryVariables);
		this.queryBody = queryBody;
	}

	public URL getEndpoint() {
		return this.endpoint;
	}

	public String getQueryBody() {
		return this.queryBody;
	}

	public String getQueryVariables() {
		return this.queryVariables;
	}

	static String getQueryVariablesList(LinkedHashSet<Variable> queryVariables) {
		final StringBuilder sb = new StringBuilder();
		final Iterator<Variable> iterator = queryVariables.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next().getName());
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	@Override
	public Optional<Integer> getRequiredArity() {
		return Optional.of(this.queryVariables.split(",").length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.endpoint.hashCode();
		result = prime * result + this.queryBody.hashCode();
		result = prime * result + this.queryVariables.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SparqlQueryResultDataSource other = (SparqlQueryResultDataSource) obj;
		return this.endpoint.equals(other.getEndpoint()) && this.queryVariables.equals(other.getQueryVariables())
				&& this.queryBody.equals(other.getQueryBody());
	}

	@Override
	public String toString() {
		return "SparqlQueryResultDataSource [endpoint=" + this.endpoint + ", queryVariables=" + this.queryVariables
				+ ", queryBody=" + this.queryBody + "]";
	}

	@Override
	public void accept(DataSourceConfigurationVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Fact getDeclarationFact() {
		Predicate predicate = Expressions.makePredicate(declarationPredicateName, 3);
		Term endpointTerm = Expressions.makeAbstractConstant(getEndpoint().toString());
		Term variablesTerm = Expressions.makeDatatypeConstant(getQueryVariables(),
				PrefixDeclarationRegistry.XSD_STRING);
		Term patternTerm = Expressions.makeDatatypeConstant(getQueryBody(), PrefixDeclarationRegistry.XSD_STRING);
		return Expressions.makeFact(predicate, endpointTerm, variablesTerm, patternTerm);
	}

}
