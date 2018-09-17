package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.semanticweb.vlog4j.core.reasoner.DataSource;

/**
 * A {@code FileDataSource} is an abstract implementation of a storage for fact
 * terms in a file of some format. The exact syntax of this storage is
 * determined by the individual extensions of this class.
 *
 * @author Christian Lewe
 * @author Irina Dragoste
 *
 */
public abstract class FileDataSource implements DataSource {

	private final static String DATASOURCE_TYPE_CONFIG_VALUE = "INMEMORY";

	private final File file;
	private final String extension;
	private final String dirCanonicalPath;
	private final String fileNameWithoutExtension;

	/**
	 * Constructor.
	 *
	 * @param file               a file that will serve as storage for fact terms.
	 * @param possibleExtensions a list of extensions that the files could have
	 * @throws IOException              if the path of the given {@code file} is
	 *                                  invalid.
	 * @throws IllegalArgumentException if the extension of the given {@code file}
	 *                                  does not occur in
	 *                                  {@code possibleExtensions}.
	 */
	public FileDataSource(@NonNull final File file, final Iterable<String> possibleExtensions) throws IOException {
		Validate.notNull(file, "Data source file cannot be null!");
		final String fileName = file.getName();

		final Stream<String> extensions = StreamSupport.stream(possibleExtensions.spliterator(), true);
		final Optional<String> maybeExtension = extensions.filter(ex -> fileName.endsWith(ex) == true).findFirst();

		if (!maybeExtension.isPresent()) {
			throw new IllegalArgumentException(
					"Expected one of the following extensions for the data source file " + file + ": "
							+ String.join(", ", possibleExtensions) + ".");
		}

		this.file = file;
		this.extension = maybeExtension.get();
		this.dirCanonicalPath = file.getAbsoluteFile().getParentFile().getCanonicalPath();
		this.fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf(this.extension));
	}

	@Override
	public final String toConfigString() {
		final String configStringPattern =

				DataSource.PREDICATE_NAME_CONFIG_LINE +

				DATASOURCE_TYPE_CONFIG_PARAM + "=" + DATASOURCE_TYPE_CONFIG_VALUE + "\n" +

						"EDB%1$d_param0=" + this.dirCanonicalPath + "\n" +

						"EDB%1$d_param1=" + this.fileNameWithoutExtension + "\n";

		return configStringPattern;
	}

	public File getFile() {
		return this.file;
	}

	public String getDirCanonicalPath() {
		return this.dirCanonicalPath;
	}

	public String getFileNameWithoutExtension() {
		return this.fileNameWithoutExtension;
	}

	@Override
	public int hashCode() {
		return this.file.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FileDataSource)) {
			return false;
		}
		final FileDataSource other = (FileDataSource) obj;
		return this.file.equals(other.getFile());
	}

}
