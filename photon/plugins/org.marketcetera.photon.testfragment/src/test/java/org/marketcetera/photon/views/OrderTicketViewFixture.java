package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;

/* $License$ */

/**
 * Helps test order ticket views.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public abstract class OrderTicketViewFixture<T extends IOrderTicket> {

    private final SWTWorkbenchBot mBot = new SWTWorkbenchBot();
    private final SWTBotView mView;
    private volatile OrderTicketView<?, T> mRealView;
    private final SWTBotCombo mSideCombo;
    private final SWTBotText mQuantityText;
    private final SWTBotText mSymbolText;
    private final SWTBotCombo mOrderTypeCombo;
    private final SWTBotText mPriceText;
    private final SWTBotCombo mBrokerCombo;
    private final SWTBotCombo mTimeInForceCombo;
    private final SWTBotText mAccountText;
    private final SWTBotButton mSendButton;
    private final SWTBotButton mClearButton;

    protected OrderTicketViewFixture(final String id) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            @SuppressWarnings("unchecked")
            public void run() throws Exception {
                mRealView = (OrderTicketView<?, T>) PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage()
                        .showView(id);
            }
        });
        mView = mBot.viewById(id);
        SWTBot bot = mView.bot();
        mSideCombo = findSide(bot);
        mQuantityText = findQuantity(bot);
        SWTBotText findSymbol = bot.text(1);
        mSymbolText = findSymbol;
        mOrderTypeCombo = findOrderType(bot);
        mPriceText = findPrice(bot);
        mBrokerCombo = findBroker(bot);
        mTimeInForceCombo = findTimeInForce(bot);
        mAccountText = findAccount(bot);
        mSendButton = bot.button("Send");
        mClearButton = bot.button("Clear");
    }

    protected SWTBotCombo findSide(SWTBot bot) {
        return bot.comboBox(0);
    }

    protected SWTBotText findQuantity(SWTBot bot) {
        return bot.text(0);
    }

    protected SWTBotCombo findOrderType(SWTBot bot) {
        return bot.comboBox(1);
    }

    protected SWTBotText findPrice(SWTBot bot) {
        return bot.text(2);
    }

    protected SWTBotCombo findBroker(SWTBot bot) {
        return bot.comboBox(2);
    }

    protected SWTBotCombo findTimeInForce(SWTBot bot) {
        return bot.comboBox(3);
    }

    protected SWTBotText findAccount(SWTBot bot) {
        return bot.text(3);
    }

    public SWTBotView getView() {
        return mView;
    }

    /**
     * Warning: returned view must be accessed from UI thread.
     */
    public OrderTicketView<?, T> getRealView() {
        return mRealView;
    }

    public void assertTicket(String side, String quantity, String symbol,
            String orderType, String price, String broker, String timeInForce,
            String account) {
        assertThat(mSideCombo.getText(), is(side));
        assertThat(mQuantityText.getText(), is(quantity));
        assertThat(mSymbolText.getText(), is(symbol));
        assertThat(mOrderTypeCombo.getText(), is(orderType));
        assertThat(mPriceText.getText(), is(price));
        assertThat(mBrokerCombo.getText(), is(broker));
        assertThat(mTimeInForceCombo.getText(), is(timeInForce));
        assertThat(mAccountText.getText(), is(account));
    }

    public void assertEnabled(boolean side, boolean quantity, boolean symbol,
            boolean type, boolean price, boolean broker, boolean tif,
            boolean account) {
        assertThat(mSideCombo.isEnabled(), is(side));
        assertThat(mQuantityText.isEnabled(), is(quantity));
        assertThat(mSymbolText.isEnabled(), is(symbol));
        assertThat(mOrderTypeCombo.isEnabled(), is(type));
        assertThat(mPriceText.isEnabled(), is(price));
        assertThat(mBrokerCombo.isEnabled(), is(broker));
        assertThat(mTimeInForceCombo.isEnabled(), is(tif));
        assertThat(mAccountText.isEnabled(), is(account));
    }

    public SWTBotButton getSendButton() {
        return mSendButton;
    }

    public SWTBotButton getClearButton() {
        return mClearButton;
    }

    public SWTBotCombo getSideCombo() {
        return mSideCombo;
    }

    public SWTBotText getQuantityText() {
        return mQuantityText;
    }

    public SWTBotText getSymbolText() {
        return mSymbolText;
    }

    public SWTBotCombo getOrderTypeCombo() {
        return mOrderTypeCombo;
    }

    public SWTBotText getPriceText() {
        return mPriceText;
    }

    public SWTBotCombo getBrokerCombo() {
        return mBrokerCombo;
    }

    public SWTBotCombo getTimeInForceCombo() {
        return mTimeInForceCombo;
    }

    public SWTBotText getAccountText() {
        return mAccountText;
    }

    public void assertError(String errorText) {
        mView.bot().label(errorText);
    }
}
