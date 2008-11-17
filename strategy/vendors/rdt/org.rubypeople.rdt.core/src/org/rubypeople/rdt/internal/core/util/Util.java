/*
 * Created on Jan 29, 2005
 *
 */
package org.rubypeople.rdt.internal.core.util;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UTFDataFormatException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyElement;

/**
 * @author Chris
 * 
 */
public class Util {

	private static boolean ENABLE_RUBY_LIKE_EXTENSIONS = true;
	private static char[][] RUBY_LIKE_EXTENSIONS;
	private static char[][] RUBY_LIKE_FILENAMES;
	private static final String NAMESPACE_DELIMETER = "::";

	private Util() {
		// cannot be instantiated
	}

	public interface Comparer {
		/**
		 * Returns 0 if a and b are equal, >0 if a is greater than b, or <0 if a
		 * is less than b.
		 */
		int compare(Object a, Object b);
	}

	/**
	 * Sorts an array of objects in place. The given comparer compares pairs of
	 * items.
	 */
	public static void sort(Object[] objects, Comparer comparer) {
		if (objects.length > 1)
			quickSort(objects, 0, objects.length - 1, comparer);
	}

	/**
	 * Sort the objects in the given collection using the given comparer.
	 */
	private static void quickSort(Object[] sortedCollection, int left,
			int right, Comparer comparer) {
		int original_left = left;
		int original_right = right;
		Object mid = sortedCollection[(left + right) / 2];
		do {
			while (comparer.compare(sortedCollection[left], mid) < 0) {
				left++;
			}
			while (comparer.compare(mid, sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				Object tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right, comparer);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right, comparer);
		}
	}

	/*
	 * Returns whether the given resource path matches one of the
	 * inclusion/exclusion patterns. NOTE: should not be asked directly using
	 * pkg root pathes
	 * 
	 * @see IClasspathEntry#getInclusionPatterns
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IPath resourcePath,
			char[][] inclusionPatterns, char[][] exclusionPatterns,
			boolean isFolderPath) {
		if (inclusionPatterns == null && exclusionPatterns == null)
			return false;
		return isExcluded(resourcePath.toString().toCharArray(),
				inclusionPatterns, exclusionPatterns, isFolderPath);
	}

	/*
	 * Returns whether the given resource matches one of the exclusion patterns.
	 * NOTE: should not be asked directly using pkg root pathes
	 * 
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IResource resource,
			char[][] inclusionPatterns, char[][] exclusionPatterns) {
		IPath path = resource.getFullPath();
		// ensure that folders are only excluded if all of their children are
		// excluded
		return isExcluded(path, inclusionPatterns, exclusionPatterns, resource
				.getType() == IResource.FOLDER);
	}

	/*
	 * TODO (philippe) should consider promoting it to CharOperation Returns
	 * whether the given resource path matches one of the inclusion/exclusion
	 * patterns. NOTE: should not be asked directly using pkg root pathes
	 * 
	 * @see IClasspathEntry#getInclusionPatterns
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(char[] path,
			char[][] inclusionPatterns, char[][] exclusionPatterns,
			boolean isFolderPath) {
		if (inclusionPatterns == null && exclusionPatterns == null)
			return false;

		inclusionCheck: if (inclusionPatterns != null) {
			for (int i = 0, length = inclusionPatterns.length; i < length; i++) {
				char[] pattern = inclusionPatterns[i];
				char[] folderPattern = pattern;
				if (isFolderPath) {
					int lastSlash = CharOperation.lastIndexOf('/', pattern);
					if (lastSlash != -1 && lastSlash != pattern.length - 1) { // trailing
						// slash
						// ->
						// adds
						// '**'
						// for
						// free
						// (see
						// http://ant.apache.org/manual/dirtasks.html)
						int star = CharOperation.indexOf('*', pattern,
								lastSlash);
						if ((star == -1 || star >= pattern.length - 1 || pattern[star + 1] != '*')) {
							folderPattern = CharOperation.subarray(pattern, 0,
									lastSlash);
						}
					}
				}
				if (CharOperation.pathMatch(folderPattern, path, true, '/')) {
					break inclusionCheck;
				}
			}
			return true; // never included
		}
		if (isFolderPath) {
			path = CharOperation.concat(path, new char[] { '*' }, '/');
		}
		exclusionCheck: if (exclusionPatterns != null) {
			for (int i = 0, length = exclusionPatterns.length; i < length; i++) {
				if (CharOperation.pathMatch(exclusionPatterns[i], path, true,
						'/')) {
					return true;
				}
			}
		}
		return false;
	}

	public static void verbose(String log) {
		verbose(log, System.out);
	}

	public static synchronized void verbose(String log, PrintStream printStream) {
		int start = 0;
		do {
			int end = log.indexOf('\n', start);
			printStream.print(Thread.currentThread());
			printStream.print(" "); //$NON-NLS-1$
			printStream.print(log.substring(start, end == -1 ? log.length()
					: end + 1));
			start = end + 1;
		} while (start != 0);
		printStream.println();
	}

	/**
	 * Returns true if the given name ends with one of the known ruby like
	 * extension. (implementation is not creating extra strings)
	 */
	public final static boolean isRubyLikeFileName(String name) {
		if (name == null)
			return false;
		char[][] rubyFileNames = getRubyLikeFilenames();
		for (int i = 0; i < rubyFileNames.length; i++) {
			char[] filename = rubyFileNames[i];
			if (name.equals(new String(filename)))
				return true;
		}
		return indexOfRubyLikeExtension(name) != -1;
	}

	public final static boolean isRubyOrERBLikeFileName(String name) {
		return isRubyLikeFileName(name) || isERBLikeFileName(name);
	}

	/**
	 * Validate the given compilation unit name. A compilation unit name must
	 * obey the following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must include the <code>".rb"</code> or <code>".rbw"</code>
	 * suffix
	 * <li> its prefix must be a valid identifier
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            the name of a compilation unit
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 *         name is valid as a compilation unit name, otherwise a status
	 *         object indicating what is wrong with the name
	 */
	public static boolean isValidRubyScriptName(String name) {
		return RubyConventions.validateRubyScriptName(name).getSeverity() != IStatus.ERROR;
	}

	/**
	 * Compares two arrays using equals() on the elements. Either or both arrays
	 * may be null. Returns true if both are null. Returns false if only one is
	 * null. If both are arrays, returns true iff they have the same length and
	 * all elements compare true with equals.
	 */
	public static boolean equalArraysOrNull(Object[] a, Object[] b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;

		int len = a.length;
		if (len != b.length)
			return false;
		for (int i = 0; i < len; ++i) {
			if (a[i] == null) {
				if (b[i] != null)
					return false;
			} else {
				if (!a[i].equals(b[i]))
					return false;
			}
		}
		return true;
	}

	/*
	 * Add a log entry
	 */
	public static void log(Throwable e, String message) {
		Throwable nestedException;
		if (e instanceof RubyModelException
				&& (nestedException = ((RubyModelException) e).getException()) != null) {
			e = nestedException;
		}
		IStatus status = new Status(IStatus.ERROR, RubyCore.PLUGIN_ID,
				IStatus.ERROR, message, e);
		RubyCore.getPlugin().getLog().log(status);
	}

	/**
	 * Combines two hash codes to make a new one.
	 */
	public static int combineHashCodes(int hashCode1, int hashCode2) {
		return hashCode1 * 17 + hashCode2;
	}

	/*
	 * Returns whether the given ruby element is exluded from its root's
	 * classpath. It doesn't check whether the root itself is on the classpath
	 * or not
	 */
	public static final boolean isExcluded(IRubyElement element) {
		int elementType = element.getElementType();
		switch (elementType) {
		case IRubyElement.RUBY_MODEL:
		case IRubyElement.RUBY_PROJECT:
			return false;
		case IRubyElement.SCRIPT:
			IResource resource = element.getResource();
			if (resource == null)
				return false;
			// if (isExcluded(resource, root.fullInclusionPatternChars(),
			// root.fullExclusionPatternChars()))
			// return true;
			return isExcluded(element.getParent());

		default:
			IRubyElement cu = element.getAncestor(IRubyElement.SCRIPT);
			return cu != null && isExcluded(cu);
		}
	}

	/**
	 * Returns the substring of the given file name, ending at the start of a
	 * Ruby like extension. The entire file name is returned if it doesn't end
	 * with a Ruby like extension.
	 */
	public static String getNameWithoutRubyLikeExtension(String fileName) {
		int index = indexOfRubyLikeExtension(fileName);
		if (index == -1)
			return fileName;
		return fileName.substring(0, index);
	}

	/*
	 * Returns the index of the Ruby like extension of the given file name or -1
	 * if it doesn't end with a known Ruby like extension. Note this is the
	 * index of the '.' even if it is not considered part of the extension.
	 */
	public static int indexOfRubyLikeExtension(String fileName) {
		int fileNameLength = fileName.length();
		char[][] rubyLikeExtensions = getRubyLikeExtensions();
		extensions: for (int i = 0, length = rubyLikeExtensions.length; i < length; i++) {
			char[] extension = rubyLikeExtensions[i];
			int extensionLength = extension.length;
			int extensionStart = fileNameLength - extensionLength;
			int dotIndex = extensionStart - 1;
			if (dotIndex < 0)
				continue;
			if (fileName.charAt(dotIndex) != '.')
				continue;
			for (int j = 0; j < extensionLength; j++) {
				if (fileName.charAt(extensionStart + j) != extension[j])
					continue extensions;
			}
			return dotIndex;
		}
		return -1;
	}

	/**
	 * Returns the registered Ruby like extensions.
	 */
	public static char[][] getRubyLikeExtensions() {
		if (RUBY_LIKE_EXTENSIONS == null) {
			// TODO (jerome) reenable once RDT UI supports other file extensions
			// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=71460)
			if (!ENABLE_RUBY_LIKE_EXTENSIONS)
				RUBY_LIKE_EXTENSIONS = new char[][] { "rb".toCharArray(),
						"rbw".toCharArray(), "rjs".toCharArray(),
						"rxml".toCharArray(), "rake".toCharArray() };
			else {
				IContentType rubyContentType = Platform.getContentTypeManager()
						.getContentType(RubyCore.RUBY_SOURCE_CONTENT_TYPE);
				String[] fileExtensions = rubyContentType == null ? null
						: rubyContentType
								.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				// note that file extensions contains "ruby" as it is defined in
				// RDT Core's plugin.xml
				int length = fileExtensions == null ? 0 : fileExtensions.length;
				char[][] extensions = new char[length][];
				SimpleWordSet knownExtensions = new SimpleWordSet(length); // used
																			// to
																			// ensure
																			// no
																			// duplicate
																			// extensions
				extensions[0] = "rb".toCharArray(); // ensure that "rb" is first
				knownExtensions.add(extensions[0]);
				int index = 1;
				for (int i = 0; i < length; i++) {
					String fileExtension = fileExtensions[i];
					char[] extension = fileExtension.toCharArray();
					if (!knownExtensions.includes(extension)) {
						extensions[index++] = extension;
						knownExtensions.add(extension);
					}
				}
				if (index != length)
					System.arraycopy(extensions, 0,
							extensions = new char[index][], 0, index);
				RUBY_LIKE_EXTENSIONS = extensions;
			}
		}
		return RUBY_LIKE_EXTENSIONS;
	}

	/**
	 * Returns the registered Ruby like filenames.
	 */
	public static char[][] getRubyLikeFilenames() {
		if (RUBY_LIKE_FILENAMES == null) {
			IContentType rubyContentType = Platform.getContentTypeManager()
					.getContentType(RubyCore.RUBY_SOURCE_CONTENT_TYPE);
			String[] filenames = rubyContentType == null ? null
					: rubyContentType.getFileSpecs(IContentType.FILE_NAME_SPEC);
			int length = filenames == null ? 0 : filenames.length;
			names = new char[length][];
			SimpleWordSet knownExtensions = new SimpleWordSet(length); // used
			// to
			// ensure
			// no
			// duplicate
			// names
			names[0] = "Rakefile".toCharArray(); // ensure that "Rakefile" is
													// first
			knownExtensions.add(names[0]);
			int index = 1;
			for (int i = 0; i < length; i++) {
				String fileExtension = filenames[i];
				char[] extension = fileExtension.toCharArray();
				if (!knownExtensions.includes(extension)) {
					names[index++] = extension;
					knownExtensions.add(extension);
				}
			}
			if (index != length)
				System.arraycopy(names, 0, names = new char[index][], 0, index);
			RUBY_LIKE_FILENAMES = names;
		}
		return RUBY_LIKE_FILENAMES;
	}

	private static final int DEFAULT_READING_SIZE = 8192;
	private static char[][] names;

	private static final String ARGUMENTS_DELIMITER = "#"; //$NON-NLS-1$
	private static final String EMPTY_ARGUMENT = "   "; //$NON-NLS-1$

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static char[] getResourceContentsAsCharArray(IFile file)
			throws RubyModelException {
		// Get encoding from file
		String encoding = null;
		try {
			encoding = file.getCharset();
		} catch (CoreException ce) {
			// do not use any encoding
		}
		return getResourceContentsAsCharArray(file, encoding);
	}

	public static char[] getResourceContentsAsCharArray(IFile file,
			String encoding) throws RubyModelException {
		// Get resource contents
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(file.getContents(true));
		} catch (CoreException e) {
			throw new RubyModelException(e,
					IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
		}
		try {
			return Util.getInputStreamAsCharArray(stream, -1, encoding);
		} catch (IOException e) {
			throw new RubyModelException(e,
					IRubyModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Returns the given input stream's contents as a character array. If a
	 * length is specified (ie. if length != -1), only length chars are
	 * returned. Otherwise all chars in the stream are returned. Note this
	 * doesn't close the stream.
	 * 
	 * @throws IOException
	 *             if a problem occured reading the stream.
	 */
	public static char[] getInputStreamAsCharArray(InputStream stream,
			int length, String encoding) throws IOException {
		InputStreamReader reader = null;
		reader = encoding == null ? new InputStreamReader(stream)
				: new InputStreamReader(stream, encoding);
		char[] contents;
		if (length == -1) {
			contents = new char[0];
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(),
						DEFAULT_READING_SIZE); // read
				// at
				// least
				// 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(contents, 0,
							contents = new char[contentsLength
									+ amountRequested], 0, contentsLength);
				}

				// read as many chars as possible
				amountRead = reader.read(contents, contentsLength,
						amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// Do not keep first character for UTF-8 BOM encoding
			int start = 0;
			if (contentsLength > 0 && "UTF-8".equals(encoding)) { //$NON-NLS-1$
				if (contents[0] == 0xFEFF) { // if BOM char then skip
					contentsLength--;
					start = 1;
				}
			}
			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(contents, start,
						contents = new char[contentsLength], 0, contentsLength);
			}
		} else {
			contents = new char[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual
				// read size.
				len += readSize;
				readSize = reader.read(contents, len, length - len);
			}
			// Do not keep first character for UTF-8 BOM encoding
			int start = 0;
			if (length > 0 && "UTF-8".equals(encoding)) { //$NON-NLS-1$
				if (contents[0] == 0xFEFF) { // if BOM char then skip
					len--;
					start = 1;
				}
			}
			// See PR 1FMS89U
			// Now we need to resize in case the default encoding used more than
			// one byte for each
			// character
			if (len != length)
				System.arraycopy(contents, start, (contents = new char[len]),
						0, len);
		}

		return contents;
	}

	public static void resetRubyLikeExtensions() {
		RUBY_LIKE_EXTENSIONS = null;
		RUBY_LIKE_FILENAMES = null;
	}

	/**
	 * Returns a new array adding the second array at the end of first array. It
	 * answers null if the first and second are null. If the first array is null
	 * or if it is empty, then a new array is created with second. If the second
	 * array is null, then the first array is returned. <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *    first = null
	 *    second = &quot;a&quot;
	 *    =&gt; result = {&quot;a&quot;}
	 * </pre>
	 * 
	 * <li>
	 * 
	 * <pre>
	 *    first = {&quot;a&quot;}
	 *    second = null
	 *    =&gt; result = {&quot;a&quot;}
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *    first = {&quot;a&quot;}
	 *    second = {&quot;b&quot;}
	 *    =&gt; result = {&quot;a&quot;, &quot;b&quot;}
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param first
	 *            the first array to concatenate
	 * @param second
	 *            the array to add at the end of the first array
	 * @return a new array adding the second array at the end of first array, or
	 *         null if the two arrays are null.
	 */
	public static final String[] arrayConcat(String[] first, String second) {
		if (second == null)
			return first;
		if (first == null)
			return new String[] { second };

		int length = first.length;
		if (first.length == 0) {
			return new String[] { second };
		}

		String[] result = new String[length + 1];
		System.arraycopy(first, 0, result, 0, length);
		result[length] = second;
		return result;
	}

	public static String[] getTrimmedSimpleNames(String packageName) {
		if (packageName.length() == 0)
			return new String[0];
		return packageName.split("\\" + File.separator);
	}

	public static String concatWith(String[] array, char separator) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = array.length; i < length; i++) {
			buffer.append(array[i]);
			if (i < length - 1)
				buffer.append(separator);
		}
		return buffer.toString();
	}

	public static boolean isValidSourceFolderName(String name) {
		// TODO Actually make sure there's no special characters.
		return true;
	}

	/**
	 * Returns the given file's contents as a byte array.
	 */
	public static byte[] getResourceContentsAsByteArray(IFile file)
			throws RubyModelException {
		InputStream stream = null;
		try {
			stream = file.getContents(true);
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
		try {
			return org.rubypeople.rdt.core.util.Util.getInputStreamAsByteArray(
					stream, -1);
		} catch (IOException e) {
			throw new RubyModelException(e,
					IRubyModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/*
	 * Converts the given URI to a local file. Use the existing file if the uri
	 * is on the local file system. Otherwise fetch it. Returns null if unable
	 * to fetch it.
	 */
	public static File toLocalFile(URI uri, IProgressMonitor monitor)
			throws CoreException {
		IFileStore fileStore = EFS.getStore(uri);
		File localFile = fileStore.toLocalFile(EFS.NONE, monitor);
		if (localFile == null)
			// non local file system
			localFile = fileStore.toLocalFile(EFS.CACHE, monitor);
		return localFile;
	}

	/**
	 * Put all the arguments in one String.
	 */
	public static String getProblemArgumentsForMarker(String[] arguments) {
		StringBuffer args = new StringBuffer(10);

		args.append(arguments.length);
		args.append(':');

		for (int j = 0; j < arguments.length; j++) {
			if (j != 0)
				args.append(ARGUMENTS_DELIMITER);

			if (arguments[j].length() == 0) {
				args.append(EMPTY_ARGUMENT);
			} else {
				args.append(arguments[j]);
			}
		}

		return args.toString();
	}

	/**
	 * Returns the line separator found in the given text. If it is null, or not
	 * found return the line delimitor for the given project. If the project is
	 * null, returns the line separator for the workspace. If still null, return
	 * the system line separator.
	 */
	public static String getLineSeparator(String text, IRubyProject project) {
		String lineSeparator = null;

		// line delimiter in given text
		if (text != null && text.length() != 0) {
			lineSeparator = findLineSeparator(text.toCharArray());
			if (lineSeparator != null)
				return lineSeparator;
		}

		// line delimiter in project preference
		IScopeContext[] scopeContext;
		if (project != null) {
			scopeContext = new IScopeContext[] { new ProjectScope(project
					.getProject()) };
			lineSeparator = Platform.getPreferencesService().getString(
					Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null,
					scopeContext);
			if (lineSeparator != null)
				return lineSeparator;
		}

		// line delimiter in workspace preference
		scopeContext = new IScopeContext[] { new InstanceScope() };
		lineSeparator = Platform.getPreferencesService().getString(
				Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null,
				scopeContext);
		if (lineSeparator != null)
			return lineSeparator;

		// system line delimiter
		return org.rubypeople.rdt.core.util.Util.LINE_SEPARATOR;
	}

	/**
	 * Finds the first line separator used by the given text.
	 * 
	 * @return </code>"\n"</code> or </code>"\r"</code> or </code>"\r\n"</code>,
	 *         or <code>null</code> if none found
	 */
	public static String findLineSeparator(char[] text) {
		// find the first line separator
		int length = text.length;
		if (length > 0) {
			char nextChar = text[0];
			for (int i = 0; i < length; i++) {
				char currentChar = nextChar;
				nextChar = i < length - 1 ? text[i + 1] : ' ';
				switch (currentChar) {
				case '\n':
					return "\n"; //$NON-NLS-1$
				case '\r':
					return nextChar == '\n' ? "\r\n" : "\r"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		// not found
		return null;
	}

	/**
	 * Sorts an array of strings in place using quicksort.
	 */
	public static void sort(String[] strings) {
		if (strings.length > 1)
			quickSort(strings, 0, strings.length - 1);
	}

	/**
	 * Sort the strings in the given collection.
	 */
	private static void quickSort(String[] sortedCollection, int left, int right) {
		int original_left = left;
		int original_right = right;
		String mid = sortedCollection[(left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) < 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				String tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right);
		}
	}

	/**
	 * Compares two arrays using equals() on the elements. Neither can be null.
	 * Only the first len elements are compared. Return false if either array is
	 * shorter than len.
	 */
	public static boolean equalArrays(Object[] a, Object[] b, int len) {
		if (a == b)
			return true;
		if (a.length < len || b.length < len)
			return false;
		for (int i = 0; i < len; ++i) {
			if (a[i] == null) {
				if (b[i] != null)
					return false;
			} else {
				if (!a[i].equals(b[i]))
					return false;
			}
		}
		return true;
	}

	public static String getSimpleName(String fullyQualifiedName) {
		if (fullyQualifiedName == null)
			return null;
		String[] names = getTypeNameParts(fullyQualifiedName);
		if (names.length == 0)
			return null;
		return names[names.length - 1];
	}

	public static boolean parentsMatch(IType type, String fullyQualifiedName) {
		String[] names = getTypeNameParts(fullyQualifiedName);
		for (int i = names.length - 2; i >= 0; i--) { // Start at second last
														// name piece, go all
														// the way to first
			IType parent = type.getDeclaringType();
			if (parent == null || !names[i].equals(parent.getElementName())) {
				return false;
			}
			type = parent;
		}
		return true;
	}

	public static String[] getTypeNameParts(String fullyQualifiedName) {
		if (fullyQualifiedName == null)
			return new String[0];
		return fullyQualifiedName.split(NAMESPACE_DELIMETER);
	}

	/**
	 * Writes a string to the given output stream using UTF-8 encoding in a
	 * machine-independent manner.
	 * <p>
	 * First, two bytes are written to the output stream as if by the
	 * <code>writeShort</code> method giving the number of bytes to follow.
	 * This value is the number of bytes actually written out, not the length of
	 * the string. Following the length, each character of the string is output,
	 * in sequence, using the UTF-8 encoding for the character.
	 * 
	 * @param str
	 *            a string to be written.
	 * @return the number of bytes written to the stream.
	 * @exception IOException
	 *                if an I/O error occurs.
	 * @since JDK1.0
	 */
	public static int writeUTF(OutputStream out, char[] str) throws IOException {
		int strlen = str.length;
		int utflen = 0;
		for (int i = 0; i < strlen; i++) {
			int c = str[i];
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}
		if (utflen > 65535)
			throw new UTFDataFormatException();
		out.write((utflen >>> 8) & 0xFF);
		out.write((utflen >>> 0) & 0xFF);
		if (strlen == utflen) {
			for (int i = 0; i < strlen; i++)
				out.write(str[i]);
		} else {
			for (int i = 0; i < strlen; i++) {
				int c = str[i];
				if ((c >= 0x0001) && (c <= 0x007F)) {
					out.write(c);
				} else if (c > 0x07FF) {
					out.write(0xE0 | ((c >> 12) & 0x0F));
					out.write(0x80 | ((c >> 6) & 0x3F));
					out.write(0x80 | ((c >> 0) & 0x3F));
				} else {
					out.write(0xC0 | ((c >> 6) & 0x1F));
					out.write(0x80 | ((c >> 0) & 0x3F));
				}
			}
		}
		return utflen + 2; // the number of bytes written to the stream
	}

	public static void sort(int[] list) {
		if (list.length > 1)
			quickSort(list, 0, list.length - 1);
	}

	private static void quickSort(int[] list, int left, int right) {
		int original_left = left;
		int original_right = right;
		int mid = list[(left + right) / 2];
		do {
			while (list[left] < mid) {
				left++;
			}
			while (mid < list[right]) {
				right--;
			}
			if (left <= right) {
				int tmp = list[left];
				list[left] = list[right];
				list[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(list, original_left, right);
		}
		if (left < original_right) {
			quickSort(list, left, original_right);
		}
	}

	/**
	 * Reads in a string from the specified data input stream. The string has
	 * been encoded using a modified UTF-8 format.
	 * <p>
	 * The first two bytes are read as if by <code>readUnsignedShort</code>.
	 * This value gives the number of following bytes that are in the encoded
	 * string, not the length of the resulting string. The following bytes are
	 * then interpreted as bytes encoding characters in the UTF-8 format and are
	 * converted into characters.
	 * <p>
	 * This method blocks until all the bytes are read, the end of the stream is
	 * detected, or an exception is thrown.
	 * 
	 * @param in
	 *            a data input stream.
	 * @return a Unicode string.
	 * @exception EOFException
	 *                if the input stream reaches the end before all the bytes.
	 * @exception IOException
	 *                if an I/O error occurs.
	 * @exception UTFDataFormatException
	 *                if the bytes do not represent a valid UTF-8 encoding of a
	 *                Unicode string.
	 * @see java.io.DataInputStream#readUnsignedShort()
	 */
	public final static char[] readUTF(DataInput in) throws IOException {
		int utflen = in.readUnsignedShort();
		char str[] = new char[utflen];
		int count = 0;
		int strlen = 0;
		while (count < utflen) {
			int c = in.readUnsignedByte();
			int char2, char3;
			switch (c >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				// 0xxxxxxx
				count++;
				str[strlen++] = (char) c;
				break;
			case 12:
			case 13:
				// 110x xxxx 10xx xxxx
				count += 2;
				if (count > utflen)
					throw new UTFDataFormatException();
				char2 = in.readUnsignedByte();
				if ((char2 & 0xC0) != 0x80)
					throw new UTFDataFormatException();
				str[strlen++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
				break;
			case 14:
				// 1110 xxxx 10xx xxxx 10xx xxxx
				count += 3;
				if (count > utflen)
					throw new UTFDataFormatException();
				char2 = in.readUnsignedByte();
				char3 = in.readUnsignedByte();
				if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
					throw new UTFDataFormatException();
				str[strlen++] = (char) (((c & 0x0F) << 12)
						| ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
				break;
			default:
				// 10xx xxxx, 1111 xxxx
				throw new UTFDataFormatException();
			}
		}
		if (strlen < utflen) {
			System.arraycopy(str, 0, str = new char[strlen], 0, strlen);
		}
		return str;
	}

	/**
	 * Returns the toString() of the given full path minus the first given
	 * number of segments. The returned string is always a relative path (it has
	 * no leading slash)
	 */
	public static String relativePath(IPath fullPath, int skipSegmentCount) {
		boolean hasTrailingSeparator = fullPath.hasTrailingSeparator();
		String[] segments = fullPath.segments();

		// compute length
		int length = 0;
		int max = segments.length;
		if (max > skipSegmentCount) {
			for (int i1 = skipSegmentCount; i1 < max; i1++) {
				length += segments[i1].length();
			}
			// add the separator lengths
			length += max - skipSegmentCount - 1;
		}
		if (hasTrailingSeparator)
			length++;

		char[] result = new char[length];
		int offset = 0;
		int len = segments.length - 1;
		if (len >= skipSegmentCount) {
			// append all but the last segment, with separators
			for (int i = skipSegmentCount; i < len; i++) {
				int size = segments[i].length();
				segments[i].getChars(0, size, result, offset);
				offset += size;
				result[offset++] = '/';
			}
			// append the last segment
			int size = segments[len].length();
			segments[len].getChars(0, size, result, offset);
			offset += size;
		}
		if (hasTrailingSeparator)
			result[offset++] = '/';
		return new String(result);
	}

	/**
	 * Sorts an array of Ruby elements based on their toStringWithAncestors(),
	 * returning a new array with the sorted items. The original array is left
	 * untouched.
	 */
	public static IRubyElement[] sortCopy(IRubyElement[] elements) {
		int len = elements.length;
		IRubyElement[] copy = new IRubyElement[len];
		System.arraycopy(elements, 0, copy, 0, len);
		sort(copy, new Comparer() {
			public int compare(Object a, Object b) {
				return ((RubyElement) a).toStringWithAncestors().compareTo(
						((RubyElement) b).toStringWithAncestors());
			}
		});
		return copy;
	}

	public static String identifierToConstant(String typeName) {
		StringBuffer buffer = new StringBuffer();
		boolean doNextAsUpper = true;
		for (int i = 0; i < typeName.length(); i++) {
			char c = typeName.charAt(i);
			if (doNextAsUpper) {
				buffer.append(Character.toUpperCase(c));
				doNextAsUpper = false;
			} else if (c == '_') {
				doNextAsUpper = true;
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * Sorts an array of strings in place using quicksort in reverse
	 * alphabetical order.
	 */
	public static void sortReverseOrder(String[] strings) {
		if (strings.length > 1)
			quickSortReverse(strings, 0, strings.length - 1);
	}

	/**
	 * Sort the strings in the given collection in reverse alphabetical order.
	 */
	private static void quickSortReverse(String[] sortedCollection, int left,
			int right) {
		int original_left = left;
		int original_right = right;
		String mid = sortedCollection[(left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) > 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) > 0) {
				right--;
			}
			if (left <= right) {
				String tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSortReverse(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSortReverse(sortedCollection, left, original_right);
		}
	}

	/**
	 * Returns the concatenation of the given array parts using the given
	 * separator between each part and appending the given name at the end. <br>
	 * <br>
	 * For example:<br>
	 * <ol>
	 * <li>
	 * 
	 * <pre>
	 *    name = &quot;c&quot;
	 *    array = { &quot;a&quot;, &quot;b&quot; }
	 *    separator = '.'
	 *    =&gt; result = &quot;a.b.c&quot;
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *    name = null
	 *    array = { &quot;a&quot;, &quot;b&quot; }
	 *    separator = '.'
	 *    =&gt; result = &quot;a.b&quot;
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * 
	 * <pre>
	 *    name = &quot; c&quot;
	 *    array = null
	 *    separator = '.'
	 *    =&gt; result = &quot;c&quot;
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param array
	 *            the given array
	 * @param name
	 *            the given name
	 * @param separator
	 *            the given separator
	 * @return the concatenation of the given array parts using the given
	 *         separator between each part and appending the given name at the
	 *         end
	 */
	public static final String concatWith(String[] array, String name,
			char separator) {

		if (array == null || array.length == 0)
			return name;
		if (name == null || name.length() == 0)
			return concatWith(array, separator);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = array.length; i < length; i++) {
			buffer.append(array[i]);
			buffer.append(separator);
		}
		buffer.append(name);
		return buffer.toString();

	}

	/**
	 * Whether or not to ignore the warning from JRuby
	 * 
	 * @param message
	 * @return
	 */
	public static boolean ignore(String message) {
		return message.startsWith("Useless") || message.startsWith("Found '='");
	}

	public static boolean isERBLikeFileName(String name) {
		return name.endsWith(".erb") || name.endsWith(".rhtml");
	}

	public static char[] replaceNonRubyCodeWithWhitespace(String source) {
		List<String> code = getRubyCodeChunks(source);
		StringBuffer buffer = new StringBuffer();
		if (code == null || code.size() == 0) {
			for (int j = 0; j < source.length(); j++) {
				char chr = source.charAt(j);
				if (Character.isWhitespace(chr)) {
					buffer.append(chr);
				} else {
					buffer.append(' ');
				}
			}
			return buffer.toString().toCharArray();
		}

		int endOfLastFragment = 0;
		for (String codeFragment : code) {
			int beginningOfCurrentFragment = source.indexOf(codeFragment,
					endOfLastFragment); // find index of current piece of code,
										// start looking after last piece of
										// code
			// replace from end of last code piece to beginning of next with
			// spaces for any non-whitespace characters in between
			String portion = source.substring(endOfLastFragment,
					beginningOfCurrentFragment);
			for (int j = 0; j < portion.length(); j++) {
				char chr = portion.charAt(j);
				if (Character.isWhitespace(chr)) {
					buffer.append(chr);
				} else {
					if (j != 0 && chr == '>' && portion.charAt(j - 1) == '%') {
						buffer.append(';');
					} else {
						buffer.append(' ');
					}
				}
			}
			buffer.append(codeFragment); // now add in code piece
			endOfLastFragment = beginningOfCurrentFragment
					+ codeFragment.length(); // now search from end of
												// current fragment
		}
		return buffer.toString().toCharArray();
	}

	private static List<String> getRubyCodeChunks(String stringContents) {
		List<String> code = new ArrayList<String>();
		String[] pieces = stringContents
				.split("(<%%)|(%%>)|(<%=)|(<%#)|(<%)|(\\-?%>)");
		for (int i = 0; i < pieces.length; i++) {
			if ((i % 2) == 1) {
				code.add(pieces[i]);
			}
		}
		return code;
	}

	public static boolean isValidRubyOrERBScriptName(String name) {
		if (RubyConventions.validateRubyScriptName(name).getSeverity() != IStatus.ERROR)
			return true;
		return isERBLikeFileName(name);
	}
}
