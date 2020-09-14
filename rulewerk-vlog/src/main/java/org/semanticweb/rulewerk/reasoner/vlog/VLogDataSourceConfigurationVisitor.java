package org.semanticweb.rulewerk.reasoner.vlog;

/*
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

import java.io.IOException;
import java.nio.file.Paths;

import org.semanticweb.rulewerk.core.reasoner.implementation.DataSourceConfigurationVisitor;
import org.semanticweb.rulewerk.core.reasoner.implementation.FileDataSource;

import org.semanticweb.rulewerk.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.TridentDataSource;
import org.semanticweb.rulewerk.core.reasoner.implementation.InMemoryDataSource;

public class VLogDataSourceConfigurationVisitor implements DataSourceConfigurationVisitor {
	private String configString = null;

	private static final String PREDICATE_NAME_CONFIG_LINE = "EDB%1$d_predname=%2$s\n";
	private static final String DATASOURCE_TYPE_CONFIG_PARAM = "EDB%1$d_type";
	private final static String FILE_DATASOURCE_TYPE_CONFIG_VALUE = "INMEMORY";
	private final static String TRIDENT_DATASOURCE_TYPE_CONFIG_VALUE = "Trident";
	private static final String SPARQL_DATASOURCE_TYPE_CONFIG_VALUE = "SPARQL";

	public String getConfigString() {
		return configString;
	}

	protected void setFileConfigString(FileDataSource dataSource) throws IOException {
		this.configString = PREDICATE_NAME_CONFIG_LINE + DATASOURCE_TYPE_CONFIG_PARAM + "="
				+ FILE_DATASOURCE_TYPE_CONFIG_VALUE + "\n" + "EDB%1$d_param0=" + getDirCanonicalPath(dataSource) + "\n"
				+ "EDB%1$d_param1=" + getFileNameWithoutExtension(dataSource) + "\n";
	}

	String getDirCanonicalPath(FileDataSource dataSource) throws IOException {
		return Paths.get(dataSource.getFile().getCanonicalPath()).getParent().toString();
	}

	String getFileNameWithoutExtension(FileDataSource dataSource) {
		final String fileName = dataSource.getName();
		return fileName.substring(0, fileName.lastIndexOf(dataSource.getExtension()));
	}

	@Override
	public void visit(CsvFileDataSource dataSource) throws IOException {
		setFileConfigString(dataSource);
	}

	@Override
	public void visit(RdfFileDataSource dataSource) throws IOException {
		setFileConfigString(dataSource);
	}

	@Override
	public void visit(SparqlQueryResultDataSource dataSource) {
		this.configString = PREDICATE_NAME_CONFIG_LINE + DATASOURCE_TYPE_CONFIG_PARAM + "="
				+ SPARQL_DATASOURCE_TYPE_CONFIG_VALUE + "\n" + "EDB%1$d_param0=" + dataSource.getEndpoint() + "\n"
				+ "EDB%1$d_param1=" + dataSource.getQueryVariables() + "\n" + "EDB%1$d_param2="
				+ dataSource.getQueryBody() + "\n";
	}

	@Override
	public void visit(TridentDataSource dataSource) {
		this.configString = PREDICATE_NAME_CONFIG_LINE + DATASOURCE_TYPE_CONFIG_PARAM + "=" //
				+ TRIDENT_DATASOURCE_TYPE_CONFIG_VALUE + "\n" //
				+ "EDB%1$d_param0=" + dataSource.getName() + "\n";
	}

	@Override
	public void visit(InMemoryDataSource dataSource) {
		this.configString = null;
	}
}
