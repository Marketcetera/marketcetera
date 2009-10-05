package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupportTest;
import org.marketcetera.photon.commons.ui.databinding.SWTBotControlDecoration;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link NewPropertyInputDialog}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class NewPropertyInputDialogTest {

    private final SWTBot mBot = new SWTBot();
    private NewPropertyInputDialog mDialog;

    @Before
    @UI
    public void before() {
        mDialog = new NewPropertyInputDialog(null);
        mDialog.setBlockOnOpen(false);
        mDialog.open();
    }

    @After
    @UI
    public void after() {
        mDialog.close();
    }

    @Test
    public void testKeyValuePair() throws Exception {
        mBot.shell("Add new property");
        SWTBotText key = mBot.textWithLabel("New property key:");
        SWTBotButton okButton = mBot.button("OK");
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(key);
        String message = RequiredFieldSupportTest
                .getRequiredValueMessage("Key");
        decoration.assertRequired(message);
        assertThat(okButton.isEnabled(), is(false));
        assertThat(key.getText(), is(""));
        key.setText("asdf");
        decoration.assertHidden();
        assertThat(okButton.isEnabled(), is(true));
        key.setText("");
        decoration.assertRequired(message);
        assertThat(okButton.isEnabled(), is(false));
        key.setText("hello");
        SWTBotText value = mBot.textWithLabel("New property value:");
        value.setText("world");
        okButton.click();
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mDialog.getPropertyKey(), is("hello"));
                assertThat(mDialog.getPropertyValue(), is("world"));
            }
        });
    }

    @Test
    public void testDefaultEmptyValue() throws Exception {
        new NewPropertyInputDialogTestFixture("asdf", null).inputData();
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mDialog.getPropertyKey(), is("asdf"));
                assertThat(mDialog.getPropertyValue(), is(""));
            }
        });
    }

    /**
     * Re-usable fixture.
     */
    static class NewPropertyInputDialogTestFixture {
        private final String mKey;
        private final String mValue;

        public NewPropertyInputDialogTestFixture(String key, String value) {
            Validate.notNull(key, "key");
            mKey = key;
            mValue = value;
        }

        public void inputData() {
            SWTBot bot = new SWTBot();
            bot.shell("Add new property");
            bot.textWithLabel("New property key:").setText(mKey);
            if (mValue != null) {
                bot.textWithLabel("New property value:").setText(mValue);
            }
            bot.button("OK").click();
        }

    }

}
