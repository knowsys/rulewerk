package org.semanticweb.vlog4j.core.model.api;

/**
 * A visitor for the various types of {@link Statement}s in the data model.
 * Should be used to avoid any type casting or {@code instanceof} checks when
 * processing statements.
 * 
 * @author Markus Krötzsch
 */
public interface StatementVisitor<T> {

	/**
	 * Visits a {@link Fact} and returns a result.
	 * 
	 * @param statement the statement to visit
	 * @return some result
	 */
	T visit(Fact statement);

	/**
	 * Visits a {@link Rule} and returns a result.
	 * 
	 * @param statement the statement to visit
	 * @return some result
	 */
	T visit(Rule statement);

	/**
	 * Visits a {@link DataSourceDeclaration} and returns a result.
	 * 
	 * @param statement the statement to visit
	 * @return some result
	 */
	T visit(DataSourceDeclaration statement);

}
