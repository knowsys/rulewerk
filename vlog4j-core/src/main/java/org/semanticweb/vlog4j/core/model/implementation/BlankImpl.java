package org.semanticweb.vlog4j.core.model.implementation;

/*
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;

/**
 * Implements {@link TermType#BLANK} terms. A blank is an entity used to
 * represent anonymous domain elements introduced during the reasoning process
 * to satisfy existential restrictions.
 *
 * @author david.carral@tu-dresden.de
 */
public class BlankImpl extends AbstractTermImpl implements Blank {

	/**
	 * Instantiates a <b>{@code BlankImpl}</b> object with the name
	 * <b>{@code name}</b>.
	 *
	 * @param name
	 *            cannot be a blank String (null, empty or whitespace).
	 */
	public BlankImpl(final String name) {
		super(name);
	}

	@Override
	public TermType getType() {
		return TermType.BLANK;
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}
	
	@Override
	public String toString() {
		return "_" + this.getName();
	}
}
