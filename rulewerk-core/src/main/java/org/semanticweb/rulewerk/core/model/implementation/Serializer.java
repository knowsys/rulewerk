package org.semanticweb.rulewerk.core.model.implementation;

import java.io.IOException;
import java.io.StringWriter;

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

import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.LanguageStringConstant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.NamedNull;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.StatementVisitor;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermVisitor;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;

/**
 * Objects of this class are used to create string representations of syntactic
 * objects.
 * 
 * @see <a href=
 *      "https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar">RuleWerk
 *      rule syntax</a>
 * 
 * @author Markus Kroetzsch
 *
 */
public class Serializer {

	public static final String STATEMENT_END = " .";

	/**
	 * Default IRI serializer that can be used if no abbreviations (prefixes, base,
	 * etc.) are used.
	 */
	public static final Function<String, String> identityIriSerializer = new Function<String, String>() {
		@Override
		public String apply(String iri) {
			if (iri.contains(":") || !iri.matches(AbstractPrefixDeclarationRegistry.REGEXP_LOCNAME)) {
				return "<" + iri + ">";
			} else {
				return iri;
			}
		}
	};

	/**
	 * Interface for a method that writes something to a writer.
	 */
	@FunctionalInterface
	public interface SerializationWriter {
		void write(final Serializer serializer) throws IOException;
	}

	final Writer writer;
	final Function<String, String> iriTransformer;
	final SerializerTermVisitor serializerTermVisitor = new SerializerTermVisitor();
	final SerializerStatementVisitor serializerStatementVisitor = new SerializerStatementVisitor();

	/**
	 * Runtime exception used to report errors that occurred in visitors that do not
	 * declare checked exceptions.
	 * 
	 * @author Markus Kroetzsch
	 *
	 */
	private class RuntimeIoException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		final IOException cause;

		public RuntimeIoException(IOException cause) {
			super(cause);
			this.cause = cause;
		}

		public IOException getIoException() {
			return cause;
		}
	}

	/**
	 * Auxiliary class to visit {@link Term} objects for writing.
	 * 
	 * @author Markus Kroetzsch
	 *
	 */
	private class SerializerTermVisitor implements TermVisitor<Void> {

		@Override
		public Void visit(AbstractConstant term) {
			try {
				Serializer.this.writeAbstractConstant(term);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

		@Override
		public Void visit(DatatypeConstant term) {
			try {
				Serializer.this.writeDatatypeConstant(term);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

		@Override
		public Void visit(LanguageStringConstant term) {
			try {
				Serializer.this.writeLanguageStringConstant(term);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

		@Override
		public Void visit(UniversalVariable term) {
			try {
				Serializer.this.writeUniversalVariable(term);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

		@Override
		public Void visit(ExistentialVariable term) {
			try {
				Serializer.this.writeExistentialVariable(term);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

		@Override
		public Void visit(NamedNull term) {
			try {
				Serializer.this.writeNamedNull(term);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

	}

	/**
	 * Auxiliary class to visit {@link Statement} objects for writing.
	 * 
	 * @author Markus Kroetzsch
	 *
	 */
	private class SerializerStatementVisitor implements StatementVisitor<Void> {

		@Override
		public Void visit(Fact statement) {
			try {
				Serializer.this.writeFact(statement);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

		@Override
		public Void visit(Rule statement) {
			try {
				Serializer.this.writeRule(statement);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

		@Override
		public Void visit(DataSourceDeclaration statement) {
			try {
				Serializer.this.writeDataSourceDeclaration(statement);
			} catch (IOException e) {
				throw new RuntimeIoException(e);
			}
			return null;
		}

	}

	/**
	 * Construct a serializer that uses a specific function to serialize IRIs.
	 * 
	 * @param writer         the object used to write serializations
	 * @param iriTransformer a function used to abbreviate IRIs, e.g., if namespace
	 *                       prefixes were declared
	 */
	public Serializer(final Writer writer, final Function<String, String> iriTransformer) {
		this.writer = writer;
		this.iriTransformer = iriTransformer;
	}

	/**
	 * Construct a serializer that serializes IRIs without any form of
	 * transformation or abbreviation.
	 * 
	 * @param writer the object used to write serializations
	 */
	public Serializer(final Writer writer) {
		this(writer, identityIriSerializer);
	}

	/**
	 * Construct a serializer that uses the given {@link PrefixDeclarationRegistry}
	 * to abbreviate IRIs.
	 * 
	 * @param writer                    the object used to write serializations
	 * @param prefixDeclarationRegistry the object used to abbreviate IRIs
	 */
	public Serializer(final Writer writer, PrefixDeclarationRegistry prefixDeclarationRegistry) {
		this(writer, (string) -> {
			return prefixDeclarationRegistry.unresolveAbsoluteIri(string, true);
		});
	}

	/**
	 * Writes a serialization of the given {@link Statement}.
	 *
	 * @param term a {@link Statement}
	 * @throws IOException
	 */
	public void writeStatement(Statement statement) throws IOException {
		try {
			statement.accept(this.serializerStatementVisitor);
		} catch (Serializer.RuntimeIoException e) {
			throw e.getIoException();
		}
	}

	/**
	 * Writes a serialization of the given {@link Fact}.
	 *
	 * @param fact a {@link Fact}
	 * @throws IOException
	 */
	public void writeFact(Fact fact) throws IOException {
		writeLiteral(fact);
		writer.write(STATEMENT_END);
	}

	/**
	 * Writes a serialization of the given {@link Rule}.
	 *
	 * @param rule a {@link Rule}
	 * @throws IOException
	 */
	public void writeRule(Rule rule) throws IOException {
		writeRuleNoStatment(rule);
		writer.write(STATEMENT_END);
	}

	/**
	 * Writes a serialization of the given {@link Rule} without the final dot.
	 *
	 * @param rule a {@link Rule}
	 * @throws IOException
	 */
	private void writeRuleNoStatment(Rule rule) throws IOException {
		writeLiteralConjunction(rule.getHead());
		writer.write(" :- ");
		writeLiteralConjunction(rule.getBody());
	}

	/**
	 * Writes a serialization of the given {@link DataSourceDeclaration}.
	 *
	 * @param dataSourceDeclaration a {@link DataSourceDeclaration}
	 * @throws IOException
	 */
	public void writeDataSourceDeclaration(DataSourceDeclaration dataSourceDeclaration) throws IOException {
		writer.write("@source ");
		writePredicate(dataSourceDeclaration.getPredicate());
		writer.write(": ");
		writeLiteral(dataSourceDeclaration.getDataSource().getDeclarationFact());
		writer.write(STATEMENT_END);
	}

	/**
	 * Writes a serialization of the given {@link Literal}.
	 *
	 * @param literal a {@link Literal}
	 * @throws IOException
	 */
	public void writeLiteral(Literal literal) throws IOException {
		if (literal.isNegated()) {
			writer.write("~");
		}
		writePositiveLiteral(literal.getPredicate(), literal.getArguments());
	}

	/**
	 * Serialize the given predicate and list of terms like a
	 * {@link PositiveLiteral}.
	 *
	 * @param predicate a {@link Predicate}
	 * @param arguments a list of {@link Term} arguments
	 * @throws IOException
	 */
	public void writePositiveLiteral(Predicate predicate, List<Term> arguments) throws IOException {
		writer.write(getIri(predicate.getName()));
		writer.write("(");

		boolean first = true;
		for (final Term term : arguments) {
			if (first) {
				first = false;
			} else {
				writer.write(", ");
			}
			writeTerm(term);
		}

		writer.write(")");
	}

	/**
	 * Writes a serialization of the given {@link Conjunction} of {@link Literal}
	 * objects.
	 *
	 * @param literals a {@link Conjunction}
	 * @throws IOException
	 */
	public void writeLiteralConjunction(final Conjunction<? extends Literal> literals) throws IOException {
		boolean first = true;
		for (final Literal literal : literals.getLiterals()) {
			if (first) {
				first = false;
			} else {
				writer.write(", ");
			}
			writeLiteral(literal);
		}
	}

	/**
	 * Writes a serialization of the given {@link Predicate}. This serialization
	 * specifies the name and arity of the predicate.
	 *
	 * @param predicate a {@link Predicate}
	 * @throws IOException
	 */
	public void writePredicate(Predicate predicate) throws IOException {
		writer.write(getIri(predicate.getName()));
		writer.write("[");
		writer.write(String.valueOf(predicate.getArity()));
		writer.write("]");
	}

	/**
	 * Writes a serialization of the given {@link Term}.
	 *
	 * @param term a {@link Term}
	 * @throws IOException
	 */
	public void writeTerm(Term term) throws IOException {
		try {
			term.accept(this.serializerTermVisitor);
		} catch (Serializer.RuntimeIoException e) {
			throw e.getIoException();
		}
	}

	/**
	 * Writes a serialization of the given {@link AbstractConstant}.
	 *
	 * @param abstractConstant a {@link AbstractConstant}
	 * @throws IOException
	 */
	public void writeAbstractConstant(AbstractConstant abstractConstant) throws IOException {
		writer.write(getIri(abstractConstant.getName()));
	}

	/**
	 * Writes a serialization of the given {@link DatatypeConstant}.
	 *
	 * @param datatypeConstant a {@link DatatypeConstant}
	 * @throws IOException
	 */
	public void writeDatatypeConstant(DatatypeConstant datatypeConstant) throws IOException {
		if (PrefixDeclarationRegistry.XSD_STRING.equals(datatypeConstant.getDatatype())) {
			writer.write(getQuotedString(datatypeConstant.getLexicalValue()));
		} else if (PrefixDeclarationRegistry.XSD_INTEGER.equals(datatypeConstant.getDatatype())) {
			writer.write(datatypeConstant.getLexicalValue());
		} else {
			writer.write(getQuotedString(datatypeConstant.getLexicalValue()));
			writer.write("^^");
			writer.write(getIri(datatypeConstant.getDatatype()));
		}
	}

	/**
	 * Writes a serialization of the given {@link UniversalVariable}.
	 *
	 * @param universalVariable a {@link UniversalVariable}
	 * @throws IOException
	 */
	public void writeUniversalVariable(UniversalVariable universalVariable) throws IOException {
		writer.write("?");
		writer.write(universalVariable.getName());
	}

	/**
	 * Writes a serialization of the given {@link ExistentialVariable}.
	 *
	 * @param existentialVariable a {@link ExistentialVariable}
	 * @throws IOException
	 */
	public void writeExistentialVariable(ExistentialVariable existentialVariable) throws IOException {
		writer.write("!");
		writer.write(existentialVariable.getName());
	}

	/**
	 * Writes a serialization of the given {@link NamedNull}.
	 *
	 * @param namedNull a {@link NamedNull}
	 * @throws IOException
	 */
	public void writeNamedNull(NamedNull namedNull) throws IOException {
		writer.write("_:");
		writer.write(namedNull.getName());
	}

	/**
	 * Writes a serialization of the given {@link PrefixDeclarationRegistry}, and
	 * returns true if anything has been written.
	 *
	 * @param prefixDeclarationRegistry a {@link PrefixDeclarationRegistry}
	 * @throws IOException
	 * @return true if anything has been written
	 */
	public boolean writePrefixDeclarationRegistry(PrefixDeclarationRegistry prefixDeclarationRegistry)
			throws IOException {
		boolean result = false;
		final String baseIri = prefixDeclarationRegistry.getBaseIri();
		if (!PrefixDeclarationRegistry.EMPTY_BASE.contentEquals(baseIri)) {
			writer.write("@base <");
			writer.write(baseIri);
			writer.write(">");
			writer.write(STATEMENT_END);
			writer.write("\n");
			result = true;
		}

		Iterator<Entry<String, String>> prefixIterator = prefixDeclarationRegistry.iterator();
		while (prefixIterator.hasNext()) {
			Entry<String, String> entry = prefixIterator.next();
			writer.write("@prefix ");
			writer.write(entry.getKey());
			writer.write(" <");
			writer.write(entry.getValue());
			writer.write(">");
			writer.write(STATEMENT_END);
			writer.write("\n");
			result = true;
		}
		return result;
	}

	/**
	 * Writes a serialization of the given {@link LanguageStringConstant}.
	 *
	 * @param languageStringConstant a {@link LanguageStringConstant}
	 * @throws IOException
	 */
	public void writeLanguageStringConstant(LanguageStringConstant languageStringConstant) throws IOException {
		writer.write(getQuotedString(languageStringConstant.getString()));
		writer.write("@");
		writer.write(languageStringConstant.getLanguageTag());
	}

	/**
	 * Writes a serialization of the given {@link Command}.
	 *
	 * @param command a {@link Command}
	 * @throws IOException
	 */
	public void writeCommand(Command command) throws IOException {
		writer.write("@");
		writer.write(command.getName());

		for (Argument argument : command.getArguments()) {
			writer.write(" ");
			if (argument.fromRule().isPresent()) {
				writeRuleNoStatment(argument.fromRule().get());
			} else if (argument.fromPositiveLiteral().isPresent()) {
				writeLiteral(argument.fromPositiveLiteral().get());
			} else {
				writeTerm(argument.fromTerm().get());
			}
		}
		writer.write(STATEMENT_END);
	}

	/**
	 * Convenience method for obtaining serializations as Java strings.
	 * 
	 * @param writeAction a function that accepts a {@link Serializer} and produces
	 *                    a string
	 * @return serialization string
	 */
	public static String getSerialization(SerializationWriter writeAction) {
		final StringWriter stringWriter = new StringWriter();
		final Serializer serializer = new Serializer(stringWriter);
		try {
			writeAction.write(serializer);
		} catch (IOException e) {
			throw new RuntimeException("StringWriter should never throw an IOException.");
		}
		return stringWriter.toString();
	}

	/**
	 * Escapes (with {@code \}) special character occurrences in given
	 * {@code string}. The special characters are:
	 * <ul>
	 * <li>{@code \}</li>
	 * <li>{@code "}</li>
	 * <li>{@code \t}</li>
	 * <li>{@code \b}</li>
	 * <li>{@code \n}</li>
	 * <li>{@code \r}</li>
	 * <li>{@code \f}</li>
	 * </ul>
	 *
	 * @param string
	 * @return an escaped string
	 */
	private String getQuotedString(final String string) {
		return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\b", "\\b")
				.replace("\n", "\\n").replace("\r", "\\r").replace("\f", "\\f") + "\"";
	}

	private String getIri(final String string) {
		return iriTransformer.apply(string);
	}
}
