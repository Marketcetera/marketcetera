package org.marketcetera.photon.test;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.hamcrest.Matcher;

/* $License$ */

/**
 * Utility to work around SWTBot bugs. From SWTBot newsgroup <a href=
 * "http://dev.eclipse.org/mhonarc/newsLists/news.eclipse.swtbot/msg00712.html"
 * >http
 * ://dev.eclipse.org/mhonarc/newsLists/news.eclipse.swtbot/msg00712.html</a>
 * 
 * @author Stefan Seelmann
 * @version $Id$
 * @since 2.0.0
 */
public class ContextMenuHelper {

    /**
     * Clicks the context menu matching the text.
     * 
     * @param text
     *            the text on the context menu.
     * @throws WidgetNotFoundException
     *             if the widget is not found.
     */
    public static void clickContextMenu(final AbstractSWTBot<?> bot,
            final String... texts) {

        // show
        final MenuItem menuItem = UIThreadRunnable
                .syncExec(new WidgetResult<MenuItem>() {
                    @SuppressWarnings("unchecked")
                    public MenuItem run() {
                        MenuItem menuItem = null;
                        Control control = (Control) bot.widget;
                        Menu menu = control.getMenu();
                        for (String text : texts) {
                            Matcher<?> matcher = allOf(
                                    instanceOf(MenuItem.class),
                                    withMnemonic(text));
                            menuItem = show(menu, matcher);
                            if (menuItem != null) {
                                menu = menuItem.getMenu();
                            } else {
                                hide(menu);
                                break;
                            }
                        }

                        return menuItem;
                    }
                });
        if (menuItem == null) {
            throw new WidgetNotFoundException("Could not find menu: "
                    + Arrays.asList(texts));
        }

        // click
        click(menuItem);

        // hide
        UIThreadRunnable.syncExec(new VoidResult() {
            public void run() {
                hide(menuItem.getParent());
            }
        });
    }

    private static MenuItem show(final Menu menu, final Matcher<?> matcher) {
        if (menu != null) {
            menu.notifyListeners(SWT.Show, new Event());
            MenuItem[] items = menu.getItems();
            for (final MenuItem menuItem : items) {
                if (matcher.matches(menuItem)) {
                    return menuItem;
                }
            }
            menu.notifyListeners(SWT.Hide, new Event());
        }
        return null;
    }

    private static void click(final MenuItem menuItem) {
        final Event event = new Event();
        event.time = (int) System.currentTimeMillis();
        event.widget = menuItem;
        event.display = menuItem.getDisplay();
        event.type = SWT.Selection;

        UIThreadRunnable.asyncExec(menuItem.getDisplay(), new VoidResult() {
            public void run() {
                menuItem.notifyListeners(SWT.Selection, event);
            }
        });
    }

    private static void hide(final Menu menu) {
        menu.notifyListeners(SWT.Hide, new Event());
        if (menu.getParentMenu() != null) {
            hide(menu.getParentMenu());
        }
    }
}