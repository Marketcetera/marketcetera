package org.marketcetera.photon.commons.ui;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Tests {@link LocalizedLabelTest}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class LocalizedLabelTest extends PhotonTestBase {

    @Test
    public void testExtension() throws Exception {
        final I18NMessageProvider mockProvider = mock(I18NMessageProvider.class);
        final I18NLoggerProxy mockLogger = new I18NLoggerProxy(mockProvider);
        when(mockProvider.getText((I18NMessage) anyObject())).thenReturn("abc")
                .thenReturn("xyz");
        LocalizedLabel l = LocalizedLabel.initReflectiveMessages("ABCD",
                mockLogger);
        assertThat(l.getRawLabel(), is("abc"));
        assertThat(l.getTooltip(), is("xyz"));
        ReflectiveMessages.init(Messages.class);
        // instead of providing an actual message file, we just verify that the
        // error text was set - this proves the LocalizedLabel#init method was
        // successfully called
        assertThat(Messages.MYLABEL.getRawLabel(),
                containsString("id 'mylabel'; entry 'label'"));
        assertThat(Messages.MYLABEL.getTooltip(),
                containsString("id 'mylabel'; entry 'tooltip'"));
    }

    @Test
    public void testFormattedLabel() {
        I18NMessage0P mockLabelMessage = mock(I18NMessage0P.class);
        when(mockLabelMessage.getText()).thenReturn("label");
        I18NMessage0P mockTooltipMessage = mock(I18NMessage0P.class);
        assertThat(new LocalizedLabel(mockLabelMessage, mockTooltipMessage)
                .getFormattedLabel(), is("label:"));
        assertThat(LocalizedLabel.formatLabel("widget"), is("widget:"));
    }

    @Test
    @UI
    public void testInitializeLabel() {
        final Shell shell = new Shell();
        Label label = new Label(shell, SWT.NONE);
        I18NMessage0P mockLabelMessage = mock(I18NMessage0P.class);
        when(mockLabelMessage.getText()).thenReturn("l");
        I18NMessage0P mockTooltipMessage = mock(I18NMessage0P.class);
        when(mockTooltipMessage.getText()).thenReturn("t");
        new LocalizedLabel(mockLabelMessage, mockTooltipMessage)
                .initializeLabel(label);
        assertThat(label.getText(), is("l:"));
        assertThat(label.getToolTipText(), is("t"));
        shell.dispose();
    }

    @Test
    @UI
    public void testCreateLabel() {
        final Shell shell = new Shell();
        I18NMessage0P mockLabelMessage = mock(I18NMessage0P.class);
        when(mockLabelMessage.getText()).thenReturn("l");
        I18NMessage0P mockTooltipMessage = mock(I18NMessage0P.class);
        when(mockTooltipMessage.getText()).thenReturn("t");
        Label label = new LocalizedLabel(mockLabelMessage, mockTooltipMessage)
                .createLabel(shell);
        assertThat(label.getText(), is("l:"));
        assertThat(label.getToolTipText(), is("t"));
        shell.dispose();
    }

    public static class Messages {
        static LocalizedLabel MYLABEL;
    }

}
