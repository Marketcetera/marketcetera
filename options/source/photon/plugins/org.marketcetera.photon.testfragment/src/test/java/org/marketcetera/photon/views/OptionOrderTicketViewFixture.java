package org.marketcetera.photon.views;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/* $License$ */

/**
 * Helps test {@link OptionOrderTicketView}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class OptionOrderTicketViewFixture extends
        OrderTicketViewFixture<IOptionOrderTicket> {

    private final SWTBotText mExpiryText;
    private final SWTBotText mStrikeText;
    private final SWTBotCombo mOptionTypeCombo;
    private final SWTBotCombo mOpenCloseCombo;
    private final SWTBotCombo mCapacityCombo;

    public OptionOrderTicketViewFixture() throws Exception {
        super(OptionOrderTicketView.ID);
        SWTBot bot = getView().bot();
        mExpiryText = bot.text(3);
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
}
