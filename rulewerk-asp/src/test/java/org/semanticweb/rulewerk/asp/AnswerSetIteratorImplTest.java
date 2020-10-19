package org.semanticweb.rulewerk.asp;

import org.junit.Test;
import org.semanticweb.rulewerk.asp.implementation.AnswerSetIteratorImpl;
import org.semanticweb.rulewerk.asp.model.AnswerSet;
import org.semanticweb.rulewerk.asp.model.AnswerSetIterator;
import org.semanticweb.rulewerk.asp.model.AspReasoningState;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AnswerSetIteratorImplTest {

	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");

	final PositiveLiteral atom1 = Expressions.makePositiveLiteral("p", d, c);
	final PositiveLiteral atom2 = Expressions.makePositiveLiteral("q", d);
	final PositiveLiteral atom3 = Expressions.makePositiveLiteral("q", c);

	Set<Literal> set12 = new HashSet<>(Arrays.asList(atom1, atom2));
	Set<Literal> set13 = new HashSet<>(Arrays.asList(atom1, atom3));

	@Test(expected = NullPointerException.class)
	public void readerNotNull() throws IOException {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		new AnswerSetIteratorImpl(null, integerLiteralMap);
	}

	@Test(expected = NullPointerException.class)
	public void mapNotNull() throws IOException {
		new AnswerSetIteratorImpl(new BufferedReader(new StringReader("")), null);
	}

	@Test
	public void unsatisfiableAspProgramTest() throws IOException {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);
		StringReader reader = new StringReader("Introduction\n" +
			"\n" +
			"UNSATISFIABLE\n" +
			"Final remarks");
		AnswerSetIterator answerSetIterator = new AnswerSetIteratorImpl(new BufferedReader(reader), integerLiteralMap);
		assertFalse(answerSetIterator.hasNext());
		assertEquals(AspReasoningState.UNSATISFIABLE, answerSetIterator.getReasoningState());
	}

	@Test
	public void satisfiableAspProgramTest() throws IOException {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);

		StringReader reader = new StringReader("Introduction\n" +
			"\n" +
			"SATISFIABLE\n" +
			"Answer: 1\n" +
			"1 2\n" +
			"Answer: 2\n" +
			"1 3\n" +
			"Final remarks");
		AnswerSetIterator answerSetIterator = new AnswerSetIteratorImpl(new BufferedReader(reader), integerLiteralMap);
		AnswerSet answerSet1 = answerSetIterator.next();
		AnswerSet answerSet2 = answerSetIterator.next();
		assertFalse(answerSetIterator.hasNext());
		assertEquals(answerSet1.getLiterals(), set12);
		assertEquals(answerSet2.getLiterals(), set13);
		assertEquals(AspReasoningState.SATISFIABLE, answerSetIterator.getReasoningState());
	}

	@Test
	public void interruptedAspProgramTest() throws IOException {
		Map<Integer, Literal> integerLiteralMap = new HashMap<>();
		integerLiteralMap.put(1, atom1);
		integerLiteralMap.put(2, atom2);
		integerLiteralMap.put(3, atom3);

		StringReader reader = new StringReader("Introduction\n" +
			"\n" +
			"INTERRUPTED\n" +
			"Answer: 1\n" +
			"1 2\n");
		AnswerSetIterator answerSetIterator = new AnswerSetIteratorImpl(new BufferedReader(reader), integerLiteralMap);
		AnswerSet answerSet1 = answerSetIterator.next();
		assertFalse(answerSetIterator.hasNext());
		assertEquals(answerSet1.getLiterals(), set12);
		assertEquals(AspReasoningState.INTERRUPTED, answerSetIterator.getReasoningState());
	}
}
