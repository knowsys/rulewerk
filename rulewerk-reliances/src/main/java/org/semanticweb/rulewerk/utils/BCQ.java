package org.semanticweb.rulewerk.utils;

/*-
 * #%L
 * Rulewerk Reliances
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

import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.logic.MartelliMontanariUnifier;
import org.semanticweb.rulewerk.math.mapping.PartialMapping;
import org.semanticweb.rulewerk.math.mapping.PartialMappingIterable;

/**
 * A class to implement a (very simple) boolean conjunctive query.
 * 
 * @author Larry González
 * @TODO:
 * * create a more efficient unifier that assumes no variables in instance.
 */
public class BCQ {

	static boolean query(List<Literal> instance, List<Literal> query) {

		for (PartialMapping assignment : new PartialMappingIterable(instance.size(), query.size())) {

			if (assignment.size() == query.size()) {
				MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(query, instance, assignment);

				if (unifier.getSuccess()) {
					return true;
				}
			}
		}
		return false;
	}

}
