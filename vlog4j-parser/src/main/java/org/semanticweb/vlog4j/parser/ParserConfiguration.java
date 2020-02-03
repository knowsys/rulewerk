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
import org.semanticweb.vlog4j.core.model.api.DataSourceDeclaration;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarations;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.parser.javacc.JavaCCParserBase.ConfigurableLiteralDelimiter;
import org.semanticweb.vlog4j.parser.javacc.SubParserFactory;

/**
 * Class to keep parser configuration.
 *
 * @author Maximilian Marx
 */
public class ParserConfiguration {
	/**
	 * Whether to allow parsing Named Nulls.
	 */
	private boolean allowNamedNulls = false;

	/**
	 * The registered data sources.
	 */
	private final HashMap<String, DataSourceDeclarationHandler> dataSources = new HashMap<>();

	/**
	 * The registered datatypes.
	 */
	private final HashMap<String, DatatypeConstantHandler> datatypes = new HashMap<>();

	/**
	 * The registered configurable literals.
	 */
	private HashMap<ConfigurableLiteralDelimiter, ConfigurableLiteralHandler> literals = new HashMap<>();

	/**
	 * Register a new (type of) Data Source.
	 *
	 * This registers a handler for some custom value of the {@code DATASOURCE}
	 * production of the rules grammar, corresponding to some {@link DataSource}
	 * type.
	 *
	 * @see <a href="https://github.com/knowsys/vlog4j/wiki/Rule-syntax-grammar">
	 *      the grammar</a>.
	 *
	 * @param name    Name of the data source, as it appears in the declaring
	 *                directive.
	 * @param handler Handler for parsing a data source declaration.
	 *
	 * @throws IllegalArgumentException if the provided name is already registered.
	 * @return this
	 */
	public ParserConfiguration registerDataSource(final String name, final DataSourceDeclarationHandler handler)
			throws IllegalArgumentException {
		Validate.isTrue(!this.dataSources.containsKey(name), "The Data Source \"%s\" is already registered.", name);

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
	public DataSource parseDataSourceSpecificPartOfDataSourceDeclaration(final String name,
			final List<DirectiveArgument> args, final SubParserFactory subParserFactory) throws ParsingException {
		final DataSourceDeclarationHandler handler = this.dataSources.get(name);

		if (handler == null) {
			throw new ParsingException("Data source \"" + name + "\" is not known.");
		}

		return handler.handleDirective(args, subParserFactory);
	}

	/**
	 * Parse a constant with optional data type.
	 *
	 * @param lexicalForm the (unescaped) lexical form of the constant.
	 * @param languageTag the language tag, or null if not present.
	 * @param the         datatype, or null if not present.
	 *
	 * @throws ParsingException when the lexical form is invalid for the given data
	 *                          type.
	 * @return the {@link Constant} corresponding to the given arguments.
	 */
	public Constant parseDatatypeConstant(final String lexicalForm, final String datatype) throws ParsingException {
		final String type = ((datatype != null) ? datatype : PrefixDeclarations.XSD_STRING);
		final DatatypeConstantHandler handler = this.datatypes.get(type);

		if (handler != null) {
			return handler.createConstant(lexicalForm);
		}

		return Expressions.makeDatatypeConstant(lexicalForm, type);
	}

	/**
	 * Check if a handler for this
	 * {@link org.semanticweb.vlog4j.parser.javacc.JavaCCParserBase.ConfigurableLiteralDelimiter}
	 * is registered
	 *
	 * @param delimiter delimiter to check.
	 * @return true if a handler for the given delimiter is registered.
	 */
	public boolean isConfigurableLiteralRegistered(ConfigurableLiteralDelimiter delimiter) {
		return literals.containsKey(delimiter);
	}

	/**
	 * Parse a configurable literal.
	 *
	 * @param delimiter        delimiter given for the syntactic form.
	 * @param syntacticForm    syntantic form of the literal to parse.
	 * @param subParserFactory a {@link SubParserFactory} instance that creates
	 *                         parser with the same context as the current parser.
	 *
	 * @throws ParsingException when no handler for the literal is registered, or
	 *                          the given syntactic form is invalid.
	 * @return an appropriate {@link Constant} instance.
	 */
	public Term parseConfigurableLiteral(ConfigurableLiteralDelimiter delimiter, String syntacticForm,
			final SubParserFactory subParserFactory) throws ParsingException {
		if (!isConfigurableLiteralRegistered(delimiter)) {
			throw new ParsingException(
					"No handler for configurable literal delimiter \"" + delimiter + "\" registered.");
		}

		ConfigurableLiteralHandler handler = literals.get(delimiter);
		return handler.parseLiteral(syntacticForm, subParserFactory);
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
	public ParserConfiguration registerDatatype(final String name, final DatatypeConstantHandler handler)
			throws IllegalArgumentException {
		Validate.isTrue(!this.datatypes.containsKey(name), "The Data type \"%s\" is already registered.", name);

		this.datatypes.put(name, handler);
		return this;
	}

	/**
	 * Register a custom literal handler.
	 *
	 * @argument delimiter the delimiter to handle.
	 * @argument handler the handler for this literal type.
	 *
	 * @throws IllegalArgumentException when the literal delimiter has
	 *                                  already been registered.
	 *
	 * @return this
	 */
	public ParserConfiguration registerLiteral(ConfigurableLiteralDelimiter delimiter,
			ConfigurableLiteralHandler handler) throws IllegalArgumentException {
		if (literals.containsKey(delimiter)) {
			throw new IllegalArgumentException("Literal delimiter \"" + delimiter + "\" is already registered.");
		}

		this.literals.put(delimiter, handler);
		return this;
	}

	/**
	 * Set whether to allow parsing of {@link org.semanticweb.vlog4j.core.model.api.NamedNull}.
	 *
	 * @argument allow true allows parsing of named nulls.
	 *
	 * @return this
	 */
	public ParserConfiguration setNamedNulls(boolean allow) {
		this.allowNamedNulls = allow;
		return this;
	}

	/**
	 * Allow parsing of {@link org.semanticweb.vlog4j.core.model.api.NamedNull}.
	 *
	 * @return this
	 */
	public ParserConfiguration allowNamedNulls() {
		return this.setNamedNulls(true);
	}

	/**
	 * Disallow parsing of {@link org.semanticweb.vlog4j.core.model.api.NamedNull}.
	 *
	 * @return this
	 */
	public ParserConfiguration disallowNamedNulls() {
		return this.setNamedNulls(false);
	}

	/**
	 * Whether parsing of {@link org.semanticweb.vlog4j.core.model.api.NamedNull} is allowed.
	 *
	 * @return this
	 */
	public boolean isParsingOfNamedNullsAllowed() {
		return this.allowNamedNulls;
	}
}
