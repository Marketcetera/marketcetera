package org.marketcetera.photon.internal.positions.ui;

import java.util.EnumMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.util.misc.ClassVersion;

import edu.emory.mathcs.backport.java.util.Arrays;

/* $License$ */

/**
 * Menu contribution that provides radio buttons to control the tree grouping of
 * {@link PositionsView}.
 * 
 * @see PositionsView#showHierarchicalPage(Grouping[])
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class GroupingContributionItem extends CompoundContributionItem {

	private static final EnumMap<Grouping, String> labels;

	static {
		// labels must be updated if grouping options change
		assert Grouping.values().length == 3;
		labels = new EnumMap<Grouping, String>(Grouping.class);
		labels.put(Grouping.Account, Messages.GROUPING_CONTRIBUTION_ITEM_ACCOUNT_LABEL.getText());
		labels.put(Grouping.Symbol, Messages.GROUPING_CONTRIBUTION_ITEM_SYMBOL_LABEL.getText());
		labels.put(Grouping.Trader, Messages.GROUPING_CONTRIBUTION_ITEM_TRADER_LABEL.getText());
	}

	private final static class GroupingOption {
		final Grouping[] grouping;
		final String label;

		GroupingOption(Grouping... grouping) {
			Assert.isLegal(grouping != null && grouping.length > 0);
			this.grouping = grouping;
			this.label = createLabel();
		}

		String createLabel() {
			StringBuilder buf = new StringBuilder();
			int last = grouping.length - 1;
			for (int i = 0; i < last; i++) {
				buf.append(labels.get(grouping[i]));
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append(labels.get(grouping[last]));
			return buf.toString();
		}

		IContributionItem createContributionItem() {
			return new ContributionItem() {

				@Override
				public void fill(Menu menu, int index) {
					MenuItem item = new MenuItem(menu, SWT.RADIO);
					item.setText(label);
					PositionsView view = PositionsView.getView();
					item.addListener(SWT.Selection, getMenuItemListener(view));

					if (view != null) {
						item.setSelection(Arrays.equals(grouping, view.getGrouping()));
					}
				}

				private Listener getMenuItemListener(final PositionsView view) {
					return new Listener() {
						@Override
						public void handleEvent(Event event) {
							if (Activator.getDefault().getPositionEngine().getValue() == null) {
								event.doit = false;
								return;
							}
							MenuItem item = (MenuItem) event.widget;
							if (item.getSelection() && view != null) {
								view.showHierarchicalPage(grouping);
							}
						}
					};
				}
			};
		}
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		GroupingOption[] groupings = getGroupingOptions();
		IContributionItem[] items = new IContributionItem[groupings.length];
		int i = 0;
		for (GroupingOption grouping : groupings) {
			items[i++] = grouping.createContributionItem();
		}
		return items;
	}

	private GroupingOption[] getGroupingOptions() {
		// Maybe customize at some point, e.g. don't include trader unless
		// current user is superuser.
		return new GroupingOption[] { new GroupingOption(Grouping.Symbol, Grouping.Account),
				new GroupingOption(Grouping.Symbol, Grouping.Trader),
				new GroupingOption(Grouping.Account, Grouping.Symbol),
				new GroupingOption(Grouping.Account, Grouping.Trader),
				new GroupingOption(Grouping.Trader, Grouping.Symbol),
				new GroupingOption(Grouping.Trader, Grouping.Account) };
	}

}
