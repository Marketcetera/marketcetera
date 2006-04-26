package org.marketcetera.photon.parser;

@SuppressWarnings("serial")
public class ParserException extends Exception {
    int position;

    Enum[] completions;

    public ParserException(String message, int position, Enum[] completions) {
        super(message);
        this.position = position;
        this.completions = completions;
    }

    /*
       * (non-Javadoc)
       *
       * @see java.lang.Throwable#getMessage()
       */
    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return super.getMessage() + " (Near character " + position + ")";
    }

    /**
     * @return Returns the position.
     */
    public int getPosition() {
        return position;
    }

    public Enum[] getCompletions() {
        return completions;
    }

}
