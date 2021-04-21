package org.marketcetera.web;

/* $License$ */

/**
 * Tagging interface that indicates the implementor provides a widget and should be initialized on login.
 * 
 * <p>The purpose of this interface is to provide for widgets (permanent display artifacts that don't require user interaction to start
 * like the time display in the footer) that might need services not available in the core. Tag your implementation with this interface
 * and make it a Spring Component with Prototype scope and it will be instantiated via Spring upon successful login.</p>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface WidgetProvider
{
}
