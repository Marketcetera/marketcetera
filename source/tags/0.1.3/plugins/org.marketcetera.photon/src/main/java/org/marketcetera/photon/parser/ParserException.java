package org.marketcetera.photon.parser;

import org.marketcetera.core.ClassVersion;

/**
 * Exception that contains information about a Parser error, including
 * the location of the error in the input text.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
@SuppressWarnings("serial")
public class ParserException extends Exception {
    int position;

    Enum[] completions;

    /**
     * Create a new ParserException with the specified human-readable message,
     * the position in the input text, and optionally a collection of completions 
     * for that position in the parse.
     * 
     * @param message the human-readable message
     * @param position the position of the error in the input text
     * @param completions the possible values for this position in the parse, or null if it is a free-form input
     */
    public ParserException(String message, int position, Enum[] completions) {
        super(message);
        this.position = position;
        this.completions = completions;
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return super.getMessage() + " (Near character " + position + ")";
    }

    /**
     * @return Returns the position of the error in the input text
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets the array of possible completions for this place in the
     * parse.  This will only contain values if the parse is expecting
     * one of an enumerated set of tokens at this place.  Otherwise this
     * will return null.
     * 
     * @return the array of possible completions
     */
    public Enum[] getCompletions() {
        return completions;
    }

}
