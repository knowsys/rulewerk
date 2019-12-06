package org.semanticweb.vlog4j.parser;

/*-
 * #%L
 * vlog4j-parser
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.parser.javacc.SubParserFactory;

/**
 * Class to keep parser configuration.
 *
 * @author Maximilian Marx
 */
public class ParserConfiguration {
	/**
	 * The registered data sources.
	 */
	private HashMap<String, DataSourceDeclarationHandler> dataSources = new HashMap<>();

	/**
	 * The registered datatypes.
	 */
	private HashMap<String, DatatypeConstantHandler> datatypes = new HashMap<>();

	/**
	 * Register a new (type of) Data Source.
	 *
	 * This registers a handler for some custom value of the {@code DATASOURCE}
	 * production of the rules grammar, corresponding to some {@link DataSource}
	 * type.
	 *
	 * @see <"https://github.com/knowsys/vlog4j/wiki/Rule-syntax-grammar"> for the
	 *      grammar.
	 *
	 * @param name    Name of the data source, as it appears in the declaring
	 *                directive.
	 * @param handler Handler for parsing a data source declaration.
	 *
	 * @throws IllegalArgumentException if the provided name is already registered.
	 * @return this
	 */
	public ParserConfiguration registerDataSource(String name, DataSourceDeclarationHandler handler)
			throws IllegalArgumentException {
		Validate.isTrue(!dataSources.containsKey(name), "The Data Source \"%s\" is already registered.", name);

		this.dataSources.put(name, handler);
		return this;
	}

	/**
	 * Parse the source-specific part of a Data Source declaration.
	 *
	 * This is called by the parser to construct a {@link DataSourceDeclaration}. It
	 * is responsible for instantiating an appropriate {@link DataSource} type.
	 *
	 * @param name             Name of the data source.
	 * @param args             arguments given in the data source declaration.
	 * @param subParserFactory a {@link SubParserFactory} instance that creates
	 *                         parser with the same context as the current parser.
	 *
	 * @throws ParsingException when the declaration is invalid, e.g., if the Data
	 *                          Source is not known.
	 *
	 * @return the Data Source instance.
	 */
	public DataSource parseDataSourceSpecificPartOfDataSourceDeclaration(String name, List<String> args,
			final SubParserFactory subParserFactory) throws ParsingException {
		DataSourceDeclarationHandler handler = dataSources.get(name);

		if (handler == null) {
			throw new ParsingException("Data source \"" + name + "\" is not known.");
		}

		return handler.handleDeclaration(args, subParserFactory);
	}

	/**
	 * Parse a constant with optional data type and language tag.
	 *
	 * @param lexicalForm the (unescaped) lexical form of the constant.
	 * @param languageTag the language tag, or null if not present.
	 * @param the         datatype, or null if not present.
	 * @note At most one of {@code languageTag} and {@code datatype} may be
	 *       non-null.
	 *
	 * @throws ParsingException         when the lexical form is invalid for the
	 *                                  given data type.
	 * @throws IllegalArgumentException when both {@code languageTag} and
	 *                                  {@code datatype} are non-null.
	 * @return the {@link Constant} corresponding to the given arguments.
	 */
	public Constant parseConstant(String lexicalForm, String languageTag, String datatype)
			throws ParsingException, IllegalArgumentException {
		Validate.isTrue(languageTag == null || datatype == null,
				"A constant with a language tag may not explicitly specify a data type.");

		if (languageTag != null) {
			return Expressions.makeLanguageStringConstant(lexicalForm, languageTag);
		} else {
			return parseDatatypeConstant(lexicalForm, datatype);
		}
	}

	private Constant parseDatatypeConstant(String lexicalForm, String datatype) throws ParsingException {
		String type = ((datatype != null) ? datatype : PrefixDeclarations.XSD_STRING);
		DatatypeConstantHandler handler = datatypes.get(type);

		if (handler != null) {
			return handler.createConstant(lexicalForm);
		}

		return Expressions.makeDatatypeConstant(lexicalForm, type);
	}

	/**
	 * Register a new data type.
	 *
	 * @param name    the IRI representing the data type.
	 * @param handler a {@link DatatypeConstantHandler} that parses a syntactic form
	 *                into a {@link Constant}.
	 *
	 * @throws IllegalArgumentException when the data type name has already been
	 *                                  registered.
	 *
	 * @return this
	 */
	public ParserConfiguration registerDatatype(String name, DatatypeConstantHandler handler)
			throws IllegalArgumentException {
		Validate.isTrue(!datatypes.containsKey(name), "The Data type \"%s\" is already registered.", name);

		this.datatypes.put(name, handler);
		return this;
	}
}
