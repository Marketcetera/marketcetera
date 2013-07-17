package org.marketcetera.photon.internal.marketdata.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.photon.marketdata.MarketDataConstants;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Top level market data preference page, allows selection of the active feed.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public class MarketDataPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private final IMarketDataManager mMarketDataManager = Activator.getMarketDataManager();

	private ComboFieldEditor mActiveFeedField;

	/**
	 * Constructor.
	 */
	public MarketDataPreferencePage() {
		setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(),
				"org.marketcetera.photon.marketdata")); //$NON-NLS-1$
		setDescription(Messages.MARKET_DATA_FEEDS_PREFERENCE_PAGE_DESCRIPTION
				.getText());
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Collection<? extends IMarketDataFeed> providers = mMarketDataManager
				.getProviders();
		List<String[]> namesValues = new ArrayList<String[]>();
		// blank one to represent no selection
		namesValues.add(new String[] { "", "" }); //$NON-NLS-1$ //$NON-NLS-2$
		for (IMarketDataFeed provider : providers) {
			String name = provider.getName();
			if (name == null) {
				name = provider.getId();
			}
			namesValues.add(new String[] { name, provider.getId() });
		}
		Collections.sort(namesValues, new Comparator<String[]>() {

			@Override
			public int compare(String[] o1, String[] o2) {
				return o1[0].compareTo(o2[0]);
			}

		});
		mActiveFeedField = new ComboFieldEditor(
				MarketDataConstants.DEFAULT_ACTIVE_MARKETDATA_PROVIDER,
				Messages.ACTIVE_MARKET_DATA_FEED_LABEL.getText(), namesValues
						.toArray(new String[namesValues.size()][]),
				getFieldEditorParent());
		addField(mActiveFeedField);
	}

}
