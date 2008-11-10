/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.core;


/**
 * @author cawilliams
 */
public interface ISourceReference {

    /**
     * @return
     * @throws RubyModelException 
     */
    ISourceRange getSourceRange() throws RubyModelException;

    /**
     * @return
     * @throws RubyModelException 
     */
    String getSource() throws RubyModelException;

}
