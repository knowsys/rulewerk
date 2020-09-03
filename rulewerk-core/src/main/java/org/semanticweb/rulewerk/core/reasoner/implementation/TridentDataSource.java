package org.semanticweb.rulewerk.core.reasoner.implementation;

import java.io.File;

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

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

/**
 * Data source for loading data from a database created with the
 * <a href="https://github.com/karmaresearch/trident">Trident RDF indexing and
 * storage</a> utility. This is the recommended data source for large RDF
 * datasets in the VLog reasoner. Trident databases are generated from RDF input
 * files in a batch process using the Trident tool.
 *
 * @author Markus Kroetzsch
 *
 */
public class TridentDataSource implements ReasonerDataSource {

	/**
	 * The name of the predicate used for declarations of data sources of this type.
	 */
	public static final String declarationPredicateName = "trident";

	final String filePath;
	final String fileName;

	public TridentDataSource(final String filePath) {
		Validate.notBlank(filePath, "Path to Trident database cannot be blank!");
		this.filePath = filePath;  // unmodified file path, necessary for correct serialisation
		this.fileName = new File(filePath).getName();
	}

	public String getPath() {
		return this.filePath;
	}

	public String getName() {
		return this.fileName;
	}


	@Override
	public Fact getDeclarationFact() {
		Predicate predicate = Expressions.makePredicate(declarationPredicateName, 1);
		return Expressions.makeFact(predicate,
				Expressions.makeDatatypeConstant(filePath, PrefixDeclarationRegistry.XSD_STRING));
	}

	@Override
	public String toString() {
		return "[TridentDataSource [tridentFile=" + this.fileName + "]";
	}

	@Override
	public void accept(DataSourceConfigurationVisitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		return this.filePath.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TridentDataSource)) {
			return false;
		}
		final TridentDataSource other = (TridentDataSource) obj;
		return this.fileName.equals(other.getName());
	}

}
