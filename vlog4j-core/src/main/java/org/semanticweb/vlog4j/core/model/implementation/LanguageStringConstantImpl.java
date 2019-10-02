package org.semanticweb.vlog4j.core.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.LanguageStringConstant;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;

/**
 * Simple implementation of {@link LanguageStringConstant}.
 * 
 * @author Markus Kroetzsch
 *
 */
public class LanguageStringConstantImpl implements LanguageStringConstant {

	final String string;
	final String lang;

	public LanguageStringConstantImpl(String string, String languageTag) {
		Validate.notNull(string);
		Validate.notBlank(languageTag, "Language tags cannot be blank strings.");
		this.string = string;
		this.lang = languageTag;
	}

	@Override
	public String getName() {
		return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"") + "\"@" + lang;
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}

	@Override
	public String getString() {
		return this.string;
	}

	@Override
	public String getLanguageTag() {
		return this.lang;
	}

}
