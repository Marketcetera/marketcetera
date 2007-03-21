package org.marketcetera.core;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
class ViolatedClass{

    public static final String HIDDEN_VALUE = "hiddenValue";
    private String hidden = HIDDEN_VALUE;
    private int YOU_CANT_READ_ME = 7;
    private String youCantCallMe(String arg){
        return ("You don't know if I know "+arg);
    }
    private String youCantGetMyException() throws Exception{
        throw new Exception();
    }

    public void dontCallMe() throws Exception {
        youCantCallMe("");
        youCantGetMyException();
    }
}
