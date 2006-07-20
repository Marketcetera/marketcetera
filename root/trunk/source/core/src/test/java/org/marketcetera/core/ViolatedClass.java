package org.marketcetera.core;

/**
 * @author Graham Miller
 * @version $Id$
 */
class ViolatedClass{


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
