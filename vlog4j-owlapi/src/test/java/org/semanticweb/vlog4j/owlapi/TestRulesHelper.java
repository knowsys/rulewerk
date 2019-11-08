package org.semanticweb.vlog4j.owlapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.AbstractConstant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.UniversalVariable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class TestRulesHelper {

	@Test
	public void testReplaceTerm() {
		AbstractConstant c1 = Expressions.makeAbstractConstant("c1");
		UniversalVariable v1 = Expressions.makeUniversalVariable("v1");
		UniversalVariable v2 = Expressions.makeUniversalVariable("v2");

		PositiveLiteral positiveLiteral = Expressions.makePositiveLiteral("a", v1, v1, v2, c1);

		PositiveLiteral expectedLiteral = Expressions.makePositiveLiteral("a", c1, c1, v2, c1);
		assertEquals(expectedLiteral, RulesHelper.replaceTerm(positiveLiteral, v1, c1));
	}

}
