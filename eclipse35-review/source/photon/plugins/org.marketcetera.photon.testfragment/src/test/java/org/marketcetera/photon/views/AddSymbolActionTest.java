package org.marketcetera.photon.views;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Tests {@link AddSymbolAction}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
		stub(mMockText.getText()).toReturn("ABC");
		mFixture.run();
		verify(mMockListener).onAssertSymbol(new MSymbol("ABC"));
	}

	@Test
	public void multipleSymbols() {
		stub(mMockText.getText()).toReturn("ABC, IBM,GOOG , MSFT      ");
		mFixture.run();
		verify(mMockListener).onAssertSymbol(new MSymbol("ABC"));
		verify(mMockListener).onAssertSymbol(new MSymbol("IBM"));
		verify(mMockListener).onAssertSymbol(new MSymbol("GOOG"));
		verify(mMockListener).onAssertSymbol(new MSymbol("MSFT"));
	}

}
