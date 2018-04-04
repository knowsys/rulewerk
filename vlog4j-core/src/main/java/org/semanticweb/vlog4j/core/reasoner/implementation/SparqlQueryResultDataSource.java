package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.reasoner.DataSource;

/**
 * A SparqlQueryResultDataSource provide the results of a SPARQL query on a
 * given web endpoint.
 * 
 * @author Irina Dragoste
 *
 */
public class SparqlQueryResultDataSource implements DataSource {

	private static final String DATASOURCE_TYPE_CONFIG_VALUE = "SPARQL";

	private final URL endpoint;
	private final LinkedHashSet<Variable> queryVariables;
	private final String queryBody;

	/**
	 * Creates a data source from answers to a remote SPARQL query.
	 * 
	 * @param endpoint
	 *            the web location of the resource the query will be evaluated on.
	 * @param queryVariables
	 *            the variables of the query, in the given order. The variable at
	 *            each position in the ordered set will be mapped to its
	 *            correspondent query answer term at the same position.
	 * @param queryBody
	 *            the content of the <i>WHERE</i> clause in the SPARQL query. Must
	 *            not contain {@code newline} characters ({@code "\n")}.
	 */
	// TODO add examples to javadoc
	// TODO add illegal argument exceptions to javadoc
	public SparqlQueryResultDataSource(@NonNull final URL endpoint,
			@NonNull final LinkedHashSet<Variable> queryVariables, @NonNull final String queryBody) {
		Validate.notNull(endpoint, "Endpoint cannot be null.");
		Validate.notNull(queryVariables, "Query variables ordered set cannot be null.");
		Validate.noNullElements(queryVariables, "Query variables cannot be null or contain null elements.");
		Validate.notEmpty(queryVariables, "There must be at least one query variable.");
		Validate.notBlank(queryBody, "Query body cannot be null or blank [{}].", queryBody);
		// TODO validate query body syntax (for example, new line character)
		// TODO validate early that the arity coincides with
		// the assigned predicate
		this.endpoint = endpoint;
		this.queryVariables = queryVariables;
		this.queryBody = queryBody;
	}

	public URL getEndpoint() {
		return endpoint;
	}

	public String getQueryBody() {
		return queryBody;
	}

	public LinkedHashSet<Variable> getQueryVariables() {
		return queryVariables;
	}

	@Override
	public final String toConfigString() {
		final String configStringPattern =

				DataSource.PREDICATE_NAME_CONFIG_LINE +

						DATASOURCE_TYPE_CONFIG_PARAM + "=" + DATASOURCE_TYPE_CONFIG_VALUE + "\n" +

						"EDB%1$d_param0=" + endpoint + "\n" + "EDB%1$d_param1=" + getQueryVariablesList(queryVariables)
						+ "\n" +

						"EDB%1$d_param2=" + queryBody + "\n";

		return configStringPattern;
	}

	private String getQueryVariablesList(LinkedHashSet<Variable> queryVariables) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endpoint.hashCode();
		result = prime * result + queryBody.hashCode();
		result = prime * result + queryVariables.hashCode();
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
		final SparqlQueryResultDataSource other = (SparqlQueryResultDataSource) obj;
		return this.endpoint.equals(other.getEndpoint()) && this.queryVariables.equals(other.getQueryVariables())
				&& this.queryBody.equals(other.getQueryBody());
	}

	@Override
	public String toString() {
		return "SparqlQueryResultDataSource [endpoint=" + endpoint + ", queryVariables=" + queryVariables
				+ ", queryBody=" + queryBody + "]";
	}

}
