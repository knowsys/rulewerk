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

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
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
	// TODO null, blank
	// TODO perhaps this should be a List of Variables, whose arity coincides with
	// the assigned predicate
	private final String queryParamsList;
	// TODO null, blank
	private final String queryBody;

	/**
	 * Creates a data source from answers to a remote SPARQL query.
	 * 
	 * @param endpoint
	 *            the web location of the resource the query will be evaluated on.
	 * @param queryParamsList
	 *            a comma-separated list of query variable names. The variable at
	 *            each position in the list will be mapped to its correspondent
	 *            query answer term at the same position.
	 * @param queryBody
	 *            the content of the <i>WHERE</i> clause in the SPARQL query. Must
	 *            not contain {@code newline} characters ({@code "\n")}. SPARQL body
	 *            lines are separated by space.
	 */
	// TODO add examples to javadoc
	public SparqlQueryResultDataSource(@NonNull final URL endpoint, @NonNull final String queryParamsList,
			@NonNull final String queryBody) {
		Validate.notNull(endpoint, "endpoint cannot be null.");
		Validate.notNull(queryParamsList, "query parameters list cannot be null.");
		Validate.notBlank(queryParamsList, "query parameters list must contain at least one query parameter.");
		Validate.notNull(queryBody, "query body cannot be null.");
		this.endpoint = endpoint;
		this.queryParamsList = queryParamsList;
		this.queryBody = queryBody;
	}

	public URL getEndpoint() {
		return endpoint;
	}

	public String getQueryParamsList() {
		return queryParamsList;
	}

	public String getQueryBody() {
		return queryBody;
	}

	@Override
	public final String toConfigString() {
		final String configStringPattern =

				DataSource.PREDICATE_NAME_CONFIG_LINE +

						DATASOURCE_TYPE_CONFIG_PARAM + "=" + DATASOURCE_TYPE_CONFIG_VALUE + "\n" +

						"EDB%1$d_param0=" + endpoint + "\n" + "EDB%1$d_param1=" + queryParamsList + "\n" +

						"EDB%1$d_param2=" + queryBody + "\n";

		return configStringPattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
		result = prime * result + ((queryBody == null) ? 0 : queryBody.hashCode());
		result = prime * result + ((queryParamsList == null) ? 0 : queryParamsList.hashCode());
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
		return this.endpoint.equals(other.getEndpoint()) && this.queryParamsList.equals(other.getQueryParamsList())
				&& this.queryBody.equals(other.getQueryBody());
	}

	@Override
	public String toString() {
		return "SparqlQueryResultDataSource [endpoint=" + endpoint + ", queryParamsList=" + queryParamsList
				+ ", queryBody=" + queryBody + "]";
	}

}
