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

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.DataSource;
import org.semanticweb.rulewerk.core.model.api.DataSourceDeclaration;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.StatementVisitor;

/**
 * Basic implementation for {@link DataSourceDeclaration}.
 * 
 * @author Markus Kroetzsch
 *
 */
public class DataSourceDeclarationImpl implements DataSourceDeclaration {

	final Predicate predicate;
	final DataSource dataSource;

	public DataSourceDeclarationImpl(Predicate predicate, DataSource dataSource) {
		Validate.notNull(predicate, "Predicate cannot be null.");
		Validate.notNull(dataSource, "Data source cannot be null.");
		this.predicate = predicate;
		this.dataSource = dataSource;
	}

	@Override
	public Predicate getPredicate() {
		return this.predicate;
	}

	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.predicate.hashCode();
		result = prime * result + this.dataSource.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataSourceDeclaration)) {
			return false;
		}
		final DataSourceDeclaration other = (DataSourceDeclaration) obj;

		return (this.predicate.equals(other.getPredicate())) && this.dataSource.equals(other.getDataSource());
	}

	@Override
	public <T> T accept(StatementVisitor<T> statementVisitor) {
		return statementVisitor.visit(this);
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeDataSourceDeclaration(this));
	}

}
