/**
 * 
 */
package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.ClassVersion;

/**
 * A holder for information about a user-entered command. This is the primary
 * type returned by the Parser.
 * 
 * @author gmiller
 * 
 */
@ClassVersion("$Id$")
public class ParsedCommand {
	public ParsedCommand() {
	}

	/**
	 * Create a new ParsedCommand with the specified command type and the
	 * specified results. The type of the objects in the results list depends on
	 * the value of commandType. The commandType argument should be one of the
	 * string constants found in quickfix.field.MsgType, and results should be
	 * QuickFIX Messages of that type.
	 * 
	 * @see quickfix.field.MsgType
	 * @param commandType
	 * @param results
	 */
	public ParsedCommand(String commandType, List results) {
		mCommandType = commandType;
		mResults = results;
	}

	/**
	 * Create a new ParsedCommand with the specified command type and the
	 * specified results. The type of the aResult argument depends on the value
	 * of commandType. The commandType argument should be one of the string
	 * constants found in quickfix.field.MsgType, and results should be QuickFIX
	 * Messages of that type.
	 * 
	 * @param commandType
	 * @param aResult
	 */
	@SuppressWarnings("unchecked")
	public ParsedCommand(String commandType, Object aResult) {
		mCommandType = commandType;
		mResults = new ArrayList();
		mResults.add(aResult);
	}

	public String mCommandType;

	public List mResults;
}