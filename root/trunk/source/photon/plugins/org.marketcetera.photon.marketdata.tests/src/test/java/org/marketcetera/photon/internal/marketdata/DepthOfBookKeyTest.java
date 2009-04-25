package org.marketcetera.photon.internal.marketdata;

import org.junit.Test;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ExpectedFailure;



/* $License$ */

/**
 * Test {@link DepthOfBookKey}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class DepthOfBookKeyTest extends KeyTestBase {

	@Override
	Object createKey1() {
		return new DepthOfBookKey("IBM", Content.LEVEL_2);
	}

	@Override
	Object createKey2() {
		return new DepthOfBookKey("IBM", Content.TOTAL_VIEW);
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new DepthOfBookKey("IBM", Content.LEVEL_2) {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new DepthOfBookKey(null, Content.OPEN_BOOK);
	}
	
	@Test
	public void testInvalidContent() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new DepthOfBookKey("IBM", null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new DepthOfBookKey("IBM", Content.LATEST_TICK);
			}
		};
	}

}
