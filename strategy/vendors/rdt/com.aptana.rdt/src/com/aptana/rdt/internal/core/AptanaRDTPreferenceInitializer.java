package com.aptana.rdt.internal.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.internal.parser.warnings.LintOptions;

public class AptanaRDTPreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {		
    	HashSet optionNames = AptanaRDTPlugin.getDefault().optionNames;
		// Lint visitor settings
		Map defaultOptionsMap = new LintOptions().getMap(); // compiler defaults

        // Store default values to default preferences
        IEclipsePreferences defaultPreferences = new DefaultScope().getNode(AptanaRDTPlugin.PLUGIN_ID);
        for (Iterator iter = defaultOptionsMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String optionName = (String) entry.getKey();
            defaultPreferences.put(optionName, (String) entry.getValue());
            optionNames.add(optionName);
        }
        AptanaRDTPlugin.getDefault().optionsCache = null;
    }

}
