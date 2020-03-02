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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class SparqlQueryResultDataSourceTest {

	final URL endpoint = new URL("http://query.wikidata.org/sparql");

	public SparqlQueryResultDataSourceTest() throws MalformedURLException {
	}

	@Test
	public void testToStringSimpleConstructor() throws MalformedURLException {
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, "b,a",
				"?a wdt:P22 ?b");
		final String expectedStringConfig = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=SPARQL\n"
				+ "EDB%1$d_param0=http://query.wikidata.org/sparql\n" + "EDB%1$d_param1=b,a\n"
				+ "EDB%1$d_param2=?a wdt:P22 ?b\n";
		assertEquals(expectedStringConfig, dataSource.toConfigString());
	}

	@Test
	public void testToStringList() throws MalformedURLException {
		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("b"), Expressions.makeUniversalVariable("a")));
		final SparqlQueryResultDataSource dataSource = new SparqlQueryResultDataSource(endpoint, queryVariables,
				"?a wdt:P22 ?b");
		final String expectedStringConfig = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=SPARQL\n"
				+ "EDB%1$d_param0=http://query.wikidata.org/sparql\n" + "EDB%1$d_param1=b,a\n"
				+ "EDB%1$d_param2=?a wdt:P22 ?b\n";
		assertEquals(expectedStringConfig, dataSource.toConfigString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyQueryBodyList() throws IOException {

		final LinkedHashSet<Variable> queryVariables = new LinkedHashSet<>(
				Arrays.asList(Expressions.makeUniversalVariable("a")));
		new SparqlQueryResultDataSource(endpoint, queryVariables, StringUtils.SPACE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyQueryBody() throws IOException {
		new SparqlQueryResultDataSource(endpoint, "a", StringUtils.SPACE);
	}

}
