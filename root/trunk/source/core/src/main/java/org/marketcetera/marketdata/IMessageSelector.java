/*
 * Created by IntelliJ IDEA.
 * User: gmiller
 * Date: May 30, 2006
 * Time: 1:29:02 PM
 */
package org.marketcetera.marketdata;

import quickfix.Message;

public interface IMessageSelector {
	boolean select(Message aMessage);
}