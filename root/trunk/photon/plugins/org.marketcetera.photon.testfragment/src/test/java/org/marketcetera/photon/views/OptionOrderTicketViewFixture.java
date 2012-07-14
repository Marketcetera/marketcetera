package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/* $License$ */

/**
 * Helps test {@link OptionOrderTicketView}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: OptionOrderTicketViewFixture.java 10886 2009-11-17 19:31:49Z
 *          klim $
 * @since 2.0.0
 */
public class OptionOrderTicketViewFixture extends
        OrderTicketViewFixture<IOptionOrderTicket> {

    private final SWTBotText mExpiryText;
    private final SWTBotText mStrikeText;
    private final SWTBotButton mExpiryButton;
    private final SWTBotCombo mOptionTypeCombo;
    private final SWTBotCombo mOpenCloseCombo;
    private final SWTBotCombo mCapacityCombo;

    public OptionOrderTicketViewFixture() throws Exception {
        super(OptionOrderTicketView.ID);
        SWTBot bot = getView().bot();
        mExpiryText = bot.text(3);
        mExpiryButton = bot.button("Select...");
        mStrikeText = bot.text(4);
        mOptionTypeCombo = bot.comboBox(4);
        mOpenCloseCombo = bot.comboBox(5);
        mCapacityCombo = bot.comboBox(6);
    }

    protected SWTBotText findAccount(SWTBot bot) {
        return bot.text(5);
    }

    public SWTBotText getExpiryText() {
        return mExpiryText;
    }

    public SWTBotText getStrikeText() {
        return mStrikeText;
    }

    public SWTBotCombo getOptionTypeCombo() {
        return mOptionTypeCombo;
    }

    public SWTBotCombo getOpenCloseCombo() {
        return mOpenCloseCombo;
    }

    public SWTBotCombo getCapacityCombo() {
        return mCapacityCombo;
    }

    public void assertEnabled(boolean side, boolean quantity, boolean symbol,
            boolean type, boolean price, boolean broker, boolean tif,
            boolean expiry, boolean expiryButton, boolean strike,
            boolean optionType, boolean account, boolean openClose,
            boolean capacity) {
        super.assertEnabled(side, quantity, symbol, type, price, broker, tif,
                account);
        assertThat(mExpiryText.isEnabled(), is(expiry));
        assertThat(mExpiryButton.isEnabled(), is(expiryButton));
        assertThat(mStrikeText.isEnabled(), is(strike));
        assertThat(mOptionTypeCombo.isEnabled(), is(optionType));
        assertThat(mOpenCloseCombo.isEnabled(), is(openClose));
        assertThat(mCapacityCombo.isEnabled(), is(capacity));
    }
}
