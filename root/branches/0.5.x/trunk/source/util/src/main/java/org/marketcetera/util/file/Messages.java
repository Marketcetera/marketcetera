package org.marketcetera.util.file;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_file");

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P CLOSING_FAILED=
        new I18NMessage0P(LOGGER,"closing_failed");
    static final I18NMessage1P CANNOT_GET_TYPE=
        new I18NMessage1P(LOGGER,"cannot_get_type");
    static final I18NMessage1P CANNOT_DELETE=
        new I18NMessage1P(LOGGER,"cannot_delete");
    static final I18NMessage2P CANNOT_COPY_FILES=
        new I18NMessage2P(LOGGER,"cannot_copy_files");
    static final I18NMessage1P CANNOT_COPY_ISTREAM=
        new I18NMessage1P(LOGGER,"cannot_copy_istream");
    static final I18NMessage1P CANNOT_COPY_READER=
        new I18NMessage1P(LOGGER,"cannot_copy_reader");
    static final I18NMessage1P CANNOT_COPY_OSTREAM=
        new I18NMessage1P(LOGGER,"cannot_copy_ostream");
    static final I18NMessage1P CANNOT_COPY_WRITER=
        new I18NMessage1P(LOGGER,"cannot_copy_writer");
    static final I18NMessage1P CANNOT_COPY_MEMORY_SRC=
        new I18NMessage1P(LOGGER,"cannot_copy_memory_src");
    static final I18NMessage1P CANNOT_COPY_MEMORY_DST=
        new I18NMessage1P(LOGGER,"cannot_copy_memory_dst");
}
