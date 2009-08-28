package org.marketcetera.photon.test;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;

/* $License$ */

/**
 * Factory for SWT Bot {@link ICondition}s for testing UI. Unlike an assert,
 * conditions are used to wait for something to happen.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SWTBotConditions {

    public static ICondition isClosed(SWTBotShell shell) {
        return new IsClosed(shell);
    }

    public static ICondition widgetTextIs(AbstractSWTBot<?> widget,
            String expected) {
        return new WidgetTextIs(widget, expected);
    }

    public static ICondition treeItemForegroundIs(SWTBotTreeItem widget,
            Color expected) {
        return new TreeItemForegroundIs(widget, expected);
    }

    public static ICondition itemImageIs(AbstractSWTBot<? extends Item> widget,
            ImageDescriptor expected) {
        return new ItemImageIs(widget, expected);
    }

    public static ICondition itemImageIsReplaced(
            AbstractSWTBot<? extends Item> widget, final ImageDescriptor base,
            ImageDescriptor decoration) throws Throwable {
        Image baseImage = AbstractUIRunner.syncCall(new Callable<Image>() {
            @Override
            public Image call() throws Exception {
                return JFaceResources.getResources().createImage(base);
            }
        });
        try {
            return itemImageIsReplaced(widget, baseImage, decoration);
        } finally {
            AbstractUIRunner.syncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    JFaceResources.getResources().destroyImage(base);
                }
            });
        }
    }

    public static ICondition itemImageIsReplaced(
            AbstractSWTBot<? extends Item> widget, Image base,
            ImageDescriptor decoration) {
        return itemImageIs(widget, new DecorationOverlayIcon(base,
                new ImageDescriptor[] { null, null, null, null, null,
                        decoration }));
    }

    private static class IsClosed extends DefaultCondition {

        private final SWTBotShell mShell;

        private IsClosed(SWTBotShell shell) {
            mShell = shell;
        }

        @Override
        public boolean test() throws Exception {
            return !mShell.isOpen();
        }

        @Override
        public String getFailureMessage() {
            return MessageFormat.format("waiting for shell [{0}] to close",
                    mShell.getText());
        }
    }

    private static class WidgetTextIs extends DefaultCondition {

        private final AbstractSWTBot<?> mWidget;
        private final String mExpected;
        private String mLastActual;

        private WidgetTextIs(AbstractSWTBot<?> widget, String expected) {
            mWidget = widget;
            mExpected = expected;
        }

        @Override
        public boolean test() throws Exception {
            mLastActual = mWidget.getText();
            return mLastActual.equals(mExpected);
        }

        @Override
        public String getFailureMessage() {
            return MessageFormat
                    .format(
                            "waiting for text [{0}] on widget [{1}], most recent value was [{2}]",
                            mExpected, mWidget, mLastActual);
        }
    }

    private static class ItemImageIs extends DefaultCondition {

        private final AbstractSWTBot<? extends Item> mWidget;
        private final ImageDescriptor mExpected;

        private ItemImageIs(AbstractSWTBot<? extends Item> widget,
                ImageDescriptor expected) {
            mWidget = widget;
            mExpected = expected;
        }

        @Override
        public boolean test() throws Exception {
            return AbstractUIRunner.syncCall(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Image expectedImage = JFaceAsserts
                            .createImageAndAssertNotMissing(mExpected);
                    try {
                        return mWidget.widget.getImage().equals(expectedImage);
                    } finally {
                        JFaceResources.getResources().destroyImage(mExpected);
                    }
                }
            });
        }

        @Override
        public String getFailureMessage() {
            // don't have an easy way to display the last tested value - ok
            // since the image reference number is not that useful for debugging
            return MessageFormat.format(
                    "waiting for image [{0}] on widget [{1}]", mExpected,
                    mWidget);
        }
    }

    private static class TreeItemForegroundIs extends DefaultCondition {

        private final SWTBotTreeItem mWidget;
        private final Color mExpected;
        private volatile Color mLastActual;

        private TreeItemForegroundIs(SWTBotTreeItem widget, Color expected) {
            mWidget = widget;
            mExpected = expected;
        }

        @Override
        public boolean test() throws Exception {
            return AbstractUIRunner.syncCall(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    final Color mLastActual = mWidget.widget.getForeground(0);
                    return mLastActual.equals(mExpected);
                }
            });
        }

        @Override
        public String getFailureMessage() {
            return MessageFormat
                    .format(
                            "waiting for foreground color [{0}] on widget [{1}], most recent value was [{2}]",
                            mExpected, mWidget, mLastActual);
        }
    }

    private SWTBotConditions() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
