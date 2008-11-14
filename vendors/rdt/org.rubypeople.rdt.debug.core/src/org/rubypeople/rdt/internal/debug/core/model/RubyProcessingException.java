
package org.rubypeople.rdt.internal.debug.core.model;


public class RubyProcessingException extends Exception {
	private static final long serialVersionUID = -1651883905005341856L;
	private String rubyExceptionType ;
	
	public RubyProcessingException(String message) {
		super(message) ;
	}
	
	public RubyProcessingException(String type, String message) {
		super(message) ;	
		this.rubyExceptionType = type ;
	}
	

    public String getRubyExceptionType() {
        return rubyExceptionType;
    }

}
