/**
 * 
 */
package org.rubypeople.rdt.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.internal.core.LoadpathEntry;
import org.rubypeople.rdt.internal.core.RubyModelStatus;
import org.rubypeople.rdt.internal.core.util.Messages;

/**
 * @author Chris
 * 
 */
public class RubyConventions {

	/**
	 * Validate the given ruby script name. A ruby script name must obey the
	 * following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must match the exact filename or file extensions for the ruby content type
	 * <li> its prefix must be a valid identifier
	 * <li> it must not contain any characters or substrings that are not valid
	 * on the file system on which workspace root is located.
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            the name of a ruby script
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 *         name is valid as a ruby script name, otherwise a status object
	 *         indicating what is wrong with the name
	 */
	public static IStatus validateRubyScriptName(String name) {
		if (name == null) { return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_unit_nullName), null);
		}
		if (!org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(name)) { return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_unit_notRubyName), null);
		}
		IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
		if (!status.isOK()) { return status; }
		return RubyModelStatus.VERIFIED_OK;
	}

	/**
	 * Validate the given Ruby identifier. The identifier must not have the same
	 * spelling as a Ruby keyword, boolean literal (<code>"true"</code>,
	 * <code>"false"</code>), or null literal (<code>"null"</code>). 
	 * A valid identifier can act as a simple type name, method name or
	 * field name.
	 * 
	 * @param id
	 *            the Ruby identifier
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 *         identifier is a valid Ruby identifier, otherwise a status object
	 *         indicating what is wrong with the identifier
	 */
	public static IStatus validateIdentifier(String id) {
		// if (scannedIdentifier(id) != null) {
		// FIXME We should actually check the identifier
		return RubyModelStatus.VERIFIED_OK;
		// } else {
		// return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1,
		// Util.bind("convention.illegalIdentifier", id), null); //$NON-NLS-1$
		// }
	}

	public static IStatus validateRubyTypeName(String typeName) {
		if (typeName == null) {
			return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.convention_type_nullName, null); 
		}
		if (typeName.length() == 0) {
			return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_type_invalidName, typeName), null);
        }
        if (!isConstant(typeName)) {
        	return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, "Class name must be a constant. It must begin with a capital letter, and contain only letters, digits, or underscores.", null);
        }
        return RubyModelStatus.VERIFIED_OK;
    
	}
	
	public static IStatus validateConstant(String constantName) {
		if (constantName == null) {
			return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.convention_type_nullName, null); 
		}
		if (constantName.length() == 0) {
			return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Messages.bind(Messages.convention_type_invalidName, constantName), null);
        }
        if (!isConstant(constantName)) {
        	return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, "Name must be a constant. It must begin with a capital letter, and contain only letters, digits, or underscores.", null);
        }
        return RubyModelStatus.VERIFIED_OK;
    
	}
	
	private static boolean isConstant(String className) {
        if (className == null || className.length() == 0) return false;
        if (!Character.isLowerCase(className.charAt(0)) && !Character.isLetter(className.charAt(0)))
            return false;
        int namespaceDelimeterIndex = className.indexOf("::");
        if (namespaceDelimeterIndex != -1) {
        	return isConstant(className.substring(0, namespaceDelimeterIndex)) && isConstant(className.substring(namespaceDelimeterIndex+2));
        }
        for (int i = 0; i < className.length(); i++) {
            char c = className.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') return false;
        }
        return true;
    }

	public static IStatus validateSourceFolderName(String packName) {
		// TODO Actually do some validation
		return RubyModelStatus.VERIFIED_OK;
	}

	public static IRubyModelStatus validateLoadpath(IRubyProject rubyProject, ILoadpathEntry[] rawCLoadpath, IPath projectOutputLocation) {
		return LoadpathEntry.validateLoadpath(rubyProject, rawCLoadpath, projectOutputLocation);
	}

	public static IStatus validateMethodName(String methodName) {
		return validateIdentifier(methodName);
	}

	/**
	 * Retruns true if character can be part of an identifier (including symbol, instance, class and global variable beginnings.
	 * @param c
	 * @return
	 */
	public static boolean isRubyIdentifierPart(char c) {
		return isStrictRubyIdentifierPart(c) || c == ':' || c == '$' || c == '@' || c =='!' || c == '?';
	}
	
	/**
	 * Retruns true if the character is ok as part of the strictly idnetifier portion of a variable/method name (i.e. don't count $, @, ?, !)
	 * @param c
	 * @return
	 */
	private static boolean isStrictRubyIdentifierPart(char c) {
		return Character.isLetterOrDigit(c) || c == '_';
	}
}
