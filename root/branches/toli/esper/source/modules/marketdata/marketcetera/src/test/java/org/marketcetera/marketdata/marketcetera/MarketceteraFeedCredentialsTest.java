package org.marketcetera.marketdata.marketcetera;

import static org.marketcetera.marketdata.marketcetera.Messages.TARGET_COMP_ID_REQUIRED;
import static org.marketcetera.marketdata.marketcetera.Messages.URL_REQUIRED;

import org.junit.Test;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link MarketceteteraFeedCredentials}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketceteraFeedCredentialsTest
{
    @Test
    public void badCredentialsValues()
        throws Exception
    {
        new ExpectedFailure<FeedException>(TARGET_COMP_ID_REQUIRED) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketceteraFeedCredentials.getInstance("url",
                                                        "senderCompID",
                                                        null);
            }
        };
        new ExpectedFailure<FeedException>(TARGET_COMP_ID_REQUIRED) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketceteraFeedCredentials.getInstance("url",
                                                        "senderCompID",
                                                        "");
            }
        };
        new ExpectedFailure<FeedException>(URL_REQUIRED) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketceteraFeedCredentials.getInstance(null,
                                                        "senderCompID",
                                                        "targetCompID");
            }
        };
        new ExpectedFailure<FeedException>(URL_REQUIRED) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketceteraFeedCredentials.getInstance("",
                                                        "senderCompID",
                                                        "targetCompID");
            }
        };
    }
}
