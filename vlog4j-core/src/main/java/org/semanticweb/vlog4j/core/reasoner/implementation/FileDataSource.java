package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
	/**
	 * The canonical path to the parent directory where the file resides.
	 */
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

		this.file = file;
		this.dirCanonicalPath = Paths.get(file.getCanonicalPath()).getParent().toString();
		this.extension = getValidExtension(file, possibleExtensions);
		this.fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf(this.extension));
	}

	private String getValidExtension(final File file, final Iterable<String> possibleExtensions) {
		final String fileName = file.getName();
		final Stream<String> extensionsStream = StreamSupport.stream(possibleExtensions.spliterator(), true);
		final Optional<String> potentialExtension = extensionsStream.filter(ex -> fileName.endsWith(ex)).findFirst();

		if (!potentialExtension.isPresent()) {
			throw new IllegalArgumentException(
					"Expected one of the following extensions for the data source file " + file + ": "
							+ String.join(", ", possibleExtensions) + ".");
		}

		return potentialExtension.get();
	}

	@Override
	public final String toConfigString() {
		final String configStringPattern =

				PREDICATE_NAME_CONFIG_LINE +

				DATASOURCE_TYPE_CONFIG_PARAM + "=" + DATASOURCE_TYPE_CONFIG_VALUE + "\n" +

						"EDB%1$d_param0=" + this.dirCanonicalPath + "\n" +

						"EDB%1$d_param1=" + this.fileNameWithoutExtension + "\n";

		return configStringPattern;
	}

	public File getFile() {
		return this.file;
	}

	/**
	 * 
	 * @return The canonical path to the parent directory where the file resides.
	 */
	String getDirCanonicalPath() {
		return this.dirCanonicalPath;
	}

	String getFileNameWithoutExtension() {
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
