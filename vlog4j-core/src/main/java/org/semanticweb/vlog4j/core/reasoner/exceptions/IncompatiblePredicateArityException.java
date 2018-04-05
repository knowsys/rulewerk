package org.semanticweb.vlog4j.core.reasoner.exceptions;

/*-
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

import java.text.MessageFormat;

import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;

/**
 * Expression thrown when attempting to load the reasoner with a knowledge base
 * that contains facts from a {@link DataSource} (added with
 * {@link Reasoner#addFactsFromDataSource(Predicate, DataSource)}), whose arity
 * does not correspond to the arity of the {@link Predicate} the data source was
 * added for.
 * 
 * @author Irina Dragoste
 *
 */
public class IncompatiblePredicateArityException extends VLog4jException {
	private static final long serialVersionUID = -5081219042292721026L;

	private static final String messagePattern = "Predicate arity [{0}] of predicate [{1}] incompatible with arity [{2}] of the data source [{3}]!";

	public IncompatiblePredicateArityException(Predicate predicate, int dataSourceArity, DataSource dataSource) {
		super(MessageFormat.format(messagePattern, predicate.getArity(), predicate, dataSourceArity, dataSource));
	}

}
