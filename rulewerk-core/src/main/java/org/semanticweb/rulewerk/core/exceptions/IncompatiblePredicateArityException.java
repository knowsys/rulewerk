package org.semanticweb.rulewerk.core.exceptions;

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

import java.text.MessageFormat;

import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.Predicate;

/**
 * Expression thrown when attempting to load facts for a {@link Predicate} from
 * a {@link DataSource} that does not contain data of the specified arity.
 *
 * @author Irina Dragoste
 *
 */
public class IncompatiblePredicateArityException extends RulewerkRuntimeException {
	private static final long serialVersionUID = -5081219042292721026L;

	private static final String messagePattern = "Predicate arity [{0}] of predicate [{1}] incompatible with arity [{2}] of the data source [{3}]!";

	public IncompatiblePredicateArityException(Predicate predicate, int dataSourceArity, DataSource dataSource) {
		super(MessageFormat.format(messagePattern, predicate.getArity(), predicate, dataSourceArity, dataSource));
	}

}
