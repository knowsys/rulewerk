package org.semanticweb.vlog4j.parser.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.semanticweb.vlog4j.parser.implementation.javacc.JavaCCRuleParser;


public class RuleParser extends JavaCCRuleParser {

	public RuleParser(InputStream stream) {
		super(stream, "UTF-8");
	}
	
	public RuleParser(InputStream stream, String encoding) {
		super(stream, encoding);
	}

	
	public RuleParser(String rules) {
		super(new ByteArrayInputStream(rules.getBytes()), "UTF-8");
	}
}