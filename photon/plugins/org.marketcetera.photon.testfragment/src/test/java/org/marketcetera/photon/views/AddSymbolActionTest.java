package org.marketcetera.photon.views;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Tests {@link AddSymbolAction}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class AddSymbolActionTest {
	
	private AddSymbolAction mFixture;
	private IMSymbolListener mMockListener;
	private TextContributionItem mMockText;

	@Before
	public void before() {
		mMockListener = mock(IMSymbolListener.class);
		mMockText = mock(TextContributionItem.class);
		mFixture = new AddSymbolAction(mMockText, mMockListener);
	}

	@Test
	public void singleSymbol() {
		when(mMockText.getText()).thenReturn("ABC");
		mFixture.run();
		verify(mMockListener).onAssertSymbol(new Equity("ABC"));
	}

	@Test
	public void multipleSymbols() {
		when(mMockText.getText()).thenReturn("ABC, IBM,GOOG , MSFT      ");
		mFixture.run();
		verify(mMockListener).onAssertSymbol(new Equity("ABC"));
		verify(mMockListener).onAssertSymbol(new Equity("IBM"));
		verify(mMockListener).onAssertSymbol(new Equity("GOOG"));
		verify(mMockListener).onAssertSymbol(new Equity("MSFT"));
	}

}
