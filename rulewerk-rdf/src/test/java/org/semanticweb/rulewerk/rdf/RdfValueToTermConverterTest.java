package org.semanticweb.rulewerk.rdf;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;
import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.LanguageStringConstant;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.reasoner.implementation.Skolemization;

public class RdfValueToTermConverterTest {

	@Test
	public void convertUri_succeeds() {
		URI uri = new URIImpl("http://example.org");

		RdfValueToTermConverter converter = new RdfValueToTermConverter(true);
		Term term = converter.convertValue(uri);

		assertEquals(TermType.ABSTRACT_CONSTANT, term.getType());
		assertEquals("http://example.org", term.getName());
	}

	@Test
	public void convertLiteralDatatype_succeeds() {
		Literal literal = new LiteralImpl("42", new URIImpl("http://example.org/integer"));

		RdfValueToTermConverter converter = new RdfValueToTermConverter(true);
		Term term = converter.convertValue(literal);

		assertEquals(TermType.DATATYPE_CONSTANT, term.getType());
		DatatypeConstant datataypeConstant = (DatatypeConstant) term;
		assertEquals("http://example.org/integer", datataypeConstant.getDatatype());
		assertEquals("42", datataypeConstant.getLexicalValue());
	}

	@Test
	public void convertLiteralLanguage_succeeds() {
		Literal literal = new LiteralImpl("Test", "de");

		RdfValueToTermConverter converter = new RdfValueToTermConverter(true);
		Term term = converter.convertValue(literal);

		assertEquals(TermType.LANGSTRING_CONSTANT, term.getType());
		LanguageStringConstant langStringConstant = (LanguageStringConstant) term;
		assertEquals("Test", langStringConstant.getString());
		assertEquals("de", langStringConstant.getLanguageTag());
	}

	@Test
	public void convertLiteralString_succeeds() {
		Literal literal = new LiteralImpl("RDF 1.0 untyped");

		RdfValueToTermConverter converter = new RdfValueToTermConverter(true);
		Term term = converter.convertValue(literal);

		assertEquals(TermType.DATATYPE_CONSTANT, term.getType());
		DatatypeConstant datataypeConstant = (DatatypeConstant) term;
		assertEquals(PrefixDeclarationRegistry.XSD_STRING, datataypeConstant.getDatatype());
		assertEquals("RDF 1.0 untyped", datataypeConstant.getLexicalValue());
	}

	@Test
	public void convertBNodeSkolemize_succeeds() {
		BNode bnode = new BNodeImpl("myid");

		RdfValueToTermConverter converter = new RdfValueToTermConverter(true);
		Term term = converter.convertValue(bnode);

		assertEquals(TermType.ABSTRACT_CONSTANT, term.getType());
		assertTrue(term.getName().startsWith(Skolemization.SKOLEM_IRI_PREFIX));
	}

	@Test
	public void convertBNode_succeeds() {
		BNode bnode = new BNodeImpl("myid");

		RdfValueToTermConverter converter = new RdfValueToTermConverter(false);
		Term term = converter.convertValue(bnode);

		assertEquals(TermType.NAMED_NULL, term.getType());
		assertNotEquals("myid", term.getName());
	}

	@Test(expected=RulewerkRuntimeException.class)
	public void convertValueUnkownType_fails() {
		Value value = Mockito.mock(Value.class);

		RdfValueToTermConverter converter = new RdfValueToTermConverter(true);
		converter.convertValue(value);
	}

}
