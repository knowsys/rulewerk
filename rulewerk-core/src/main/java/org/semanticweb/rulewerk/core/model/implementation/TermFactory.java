package org.semanticweb.rulewerk.core.model.implementation;

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

import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.DatatypeConstant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.LanguageStringConstant;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;

/**
 * Class for creating various kinds of terms. Instances of this class maintain
 * an internal cache that allows them to re-use the generated objects, which is
 * useful to safe memory since the same term is often needed in multiple places.
 * 
 * @author Markus Kroetzsch
 *
 */
public class TermFactory {

	/**
	 * Use of Java's LinkedHashMap for implementing a simple LRU cache that is used
	 * here for mapping VLog ids to terms.
	 * 
	 * @author Markus Kroetzsch
	 *
	 * @param <K>
	 * @param <V>
	 */
	static class SimpleLruMap<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = 7151535464938775359L;
		private int maxCapacity;

		public SimpleLruMap(int initialCapacity, int maxCapacity) {
			super(initialCapacity, 0.75f, true);
			this.maxCapacity = maxCapacity;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() >= this.maxCapacity;
		}
	}

	final private SimpleLruMap<String, AbstractConstant> abstractConstants;
	final private SimpleLruMap<String, ExistentialVariable> existentialVariables;
	final private SimpleLruMap<String, UniversalVariable> universalVariables;
	final private SimpleLruMap<String, Predicate> predicates;

	public TermFactory() {
		this(65536);
	}

	public TermFactory(int cacheSize) {
		abstractConstants = new SimpleLruMap<>(256, cacheSize);
		existentialVariables = new SimpleLruMap<>(64, 1024);
		universalVariables = new SimpleLruMap<>(64, 1024);
		predicates = new SimpleLruMap<>(256, 4096);
	}

	/**
	 * Creates a {@link UniversalVariable}.
	 *
	 * @param name name of the variable
	 * @return a {@link UniversalVariable} corresponding to the input.
	 */
	public UniversalVariable makeUniversalVariable(String name) {
		if (universalVariables.containsKey(name)) {
			return universalVariables.get(name);
		} else {
			UniversalVariable result = new UniversalVariableImpl(name);
			universalVariables.put(name, result);
			return result;
		}
	}

	/**
	 * Creates an {@link ExistentialVariable}.
	 *
	 * @param name name of the variable
	 * @return a {@link ExistentialVariable} corresponding to the input.
	 */
	public ExistentialVariable makeExistentialVariable(String name) {
		if (existentialVariables.containsKey(name)) {
			return existentialVariables.get(name);
		} else {
			ExistentialVariable result = new ExistentialVariableImpl(name);
			existentialVariables.put(name, result);
			return result;
		}
	}

	/**
	 * Creates an {@link AbstractConstant}.
	 *
	 * @param name name of the constant
	 * @return an {@link AbstractConstant} corresponding to the input.
	 */
	public AbstractConstant makeAbstractConstant(String name) {
		if (abstractConstants.containsKey(name)) {
			return abstractConstants.get(name);
		} else {
			AbstractConstant result = new AbstractConstantImpl(name);
			abstractConstants.put(name, result);
			return result;
		}
	}

	/**
	 * Creates a {@link DatatypeConstant} from the given input.
	 *
	 * @param lexicalValue the lexical representation of the data value
	 * @param datatypeIri  the full absolute IRI of the datatype of this literal
	 * @return a {@link DatatypeConstant} corresponding to the input.
	 */
	public DatatypeConstant makeDatatypeConstant(String lexicalValue, String datatypeIri) {
		return new DatatypeConstantImpl(lexicalValue, datatypeIri);
	}

	/**
	 * Creates a {@link LanguageStringConstant} from the given input.
	 *
	 * @param string      the string value of the constant
	 * @param languageTag the BCP 47 language tag of the constant; should be in
	 *                    lower case
	 * @return a {@link LanguageStringConstant} corresponding to the input.
	 */
	public LanguageStringConstant makeLanguageStringConstant(String string, String languageTag) {
		return new LanguageStringConstantImpl(string, languageTag);
	}

	/**
	 * Creates a {@link Predicate}.
	 *
	 * @param name  non-blank predicate name
	 * @param arity predicate arity, strictly greater than 0
	 * @return a {@link Predicate} corresponding to the input.
	 */
	public Predicate makePredicate(String name, int arity) {
		String key = name + "#" + String.valueOf(arity);
		if (predicates.containsKey(key)) {
			return predicates.get(key);
		} else {
			Predicate result = new PredicateImpl(name, arity);
			predicates.put(key, result);
			return result;
		}
	}

}
