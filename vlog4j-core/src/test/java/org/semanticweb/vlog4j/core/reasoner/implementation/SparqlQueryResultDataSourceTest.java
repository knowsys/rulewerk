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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class SparqlQueryResultDataSourceTest {

	@Test
	public void testToStringSimpleSparqlQueryResultDataSource() throws MalformedURLException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("b"), Expressions.makeVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				"?a p:P22 ?b");
		final String configString = dataSource.toConfigString();
		final String expectedStringConfig = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=SPARQL\n"
				+ "EDB%1$d_param0=http://query.wikidata.org/sparql\n" + "EDB%1$d_param1=b,a\n"
				+ "EDB%1$d_param2=?a p:P22 ?b\n";
		assertEquals(expectedStringConfig, configString);
	}

	@Test
	public void testUniqueVariableNamesQuery()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("b"), Expressions.makeVariable("b")));

		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				"?a p:P22 ?b");
		assertEquals(1, dataSource.getQueryVariables().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyQueryBody()
			throws ReasonerStateException, EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		final URL endpoint = new URL("http://query.wikidata.org/sparql");
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeVariable("a")));
		new SparqlQueryResultDataSource(endpoint, queryVariables, StringUtils.SPACE);

	}

}
