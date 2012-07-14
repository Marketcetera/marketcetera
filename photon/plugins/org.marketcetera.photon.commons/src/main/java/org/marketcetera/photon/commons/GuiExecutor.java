package org.marketcetera.photon.commons;

import java.util.concurrent.ExecutorService;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Marker interface for an executor service that runs tasks in the UI thread.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface GuiExecutor extends ExecutorService {
}
