package org.marketcetera.spring;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Route("")
@PWA(name="Marketcetera Automated Trading Platform",shortName = "MATP")
public class DesktopView
        extends AbstractView
{
    private static final long serialVersionUID = -5825205970334600116L;
}
