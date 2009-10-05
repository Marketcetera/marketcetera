package org.marketcetera.photon.internal.strategy;

import static org.ops4j.peaberry.Peaberry.service;

import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.AbstractModule;

/* $License$ */

/**
 * Guice configuration for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(IStrategyEngines.class).toProvider(
                service(IStrategyEngines.class).single().direct());
    }

}
