package org.marketcetera.webui.ui.exceptions;

public class AccessDeniedException
        extends RuntimeException
{
    public AccessDeniedException() {}
    public AccessDeniedException(String message)
    {
        super(message);
    }
    private static final long serialVersionUID = -8357514750508613970L;
}
