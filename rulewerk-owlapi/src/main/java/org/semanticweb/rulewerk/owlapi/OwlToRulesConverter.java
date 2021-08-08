package org.semanticweb.rulewerk.owlapi;

import java.util.ArrayList;
import java.util.List;

/*-
 * #%L
 * Rulewerk OWL API Support
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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.owlapi.OwlFeatureNotSupportedException.FeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for converting OWL ontologies to rules. Note that OWL Axioms containing
 * Data features (data types and data properties) are ignored in the conversion.
 *
 * @author Markus Kroetzsch
 *
 */
public class OwlToRulesConverter {

	private static Logger LOGGER = LoggerFactory.getLogger(OwlToRulesConverter.class);

	final OwlAxiomToRulesConverter owlAxiomToRulesConverter = new OwlAxiomToRulesConverter();

	private final boolean failOnUnsupported;
	private final List<OWLAxiom> unsupportedAxioms = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param failOnUnsupported whether the converter should fail with an
	 *                          {@link OwlFeatureNotSupportedException} when
	 *                          encountering axioms that cannot be converted to
	 *                          rules or facts.
	 */
	public OwlToRulesConverter(final boolean failOnUnsupported) {
		this.failOnUnsupported = failOnUnsupported;
	}

	/**
	 * Constructs an object that fails with a
	 * {@link OwlFeatureNotSupportedException} when encountering axioms that cannot
	 * be converted to rules or facts.
	 */
	public OwlToRulesConverter() {
		this(true);
	}

	/**
	 * Converts the given OWL ontology to rules and facts, and adds the result to
	 * the internal buffer of rules and facts for later retrieval. Note that OWL
	 * Axioms containing Data features (data types and data properties) are ignored
	 * in the conversion.
	 *
	 * @param owlOntology the ontology
	 */
	public void addOntology(final OWLOntology owlOntology) {
		this.owlAxiomToRulesConverter.startNewBlankNodeContext();
		owlOntology.axioms().forEach(owlAxiom -> {
			try {
				owlAxiom.accept(this.owlAxiomToRulesConverter);
			} catch (final OwlFeatureNotSupportedException e) {
				if (e.getFeatureType().equals(FeatureType.DATA)) {
					LOGGER.warn("Ignoring axiom with data features: " + owlAxiom);
				} else {

					if (this.failOnUnsupported) {
						LOGGER.error(e.getMessage());
						throw e;
					} else {
						LOGGER.warn(e.getMessage());
//					if (unsupportedAxioms.size() < 10) {
						this.unsupportedAxioms.add(owlAxiom);
//					}
					}
				}
			}
		});
	}

	/**
	 * Returns the set of facts generated by transforming the given OWL ontology. No
	 * copy is created, so the set should not be modified if its owner is still to
	 * be used.
	 *
	 * @return set of facts
	 */
	public Set<Fact> getFacts() {
		return this.owlAxiomToRulesConverter.facts;
	}

	/**
	 * Returns the set of rules generated by transforming the given OWL ontology. No
	 * copy is created, so the set should not be modified if its owner is still to
	 * be used.
	 *
	 * @return set of rules
	 */
	public Set<Rule> getRules() {
		return this.owlAxiomToRulesConverter.rules;
	}

	/**
	 * Returns unsupported axioms encountered during the conversion.
	 * 
	 * @return list of unsupported axioms that were encountered
	 */
	public List<OWLAxiom> getUnsupportedAxioms() {
		return this.unsupportedAxioms;
	}

	/**
	 * Returns the first 10 unsupported axioms encountered during the conversion.
	 * 
	 * @return list of unsupported axioms that were encountered
	 */
	public List<OWLAxiom> getUnsupportedAxiomsSample() {
		if (this.unsupportedAxioms.size() < 10) {
			return this.unsupportedAxioms;
		}
		final ArrayList<OWLAxiom> sample = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			sample.add(this.unsupportedAxioms.get(0));
		}
		return sample;
	}

	public int getUnsupportedAxiomsCount() {
		return this.unsupportedAxioms.size();
	}

}
