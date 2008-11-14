package org.rubypeople.rdt.internal.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.internal.compiler.CompilerOptions;

public class RubyCorePreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        // Get options names set
        HashSet optionNames = RubyModelManager.getRubyModelManager().optionNames;
		
		// Compiler settings
		Map defaultOptionsMap = new CompilerOptions().getMap(); // compiler defaults
       
        // Override some compiler defaults
        defaultOptionsMap.put(RubyCore.COMPILER_TASK_TAGS, RubyCore.DEFAULT_TASK_TAGS);
        defaultOptionsMap.put(RubyCore.COMPILER_TASK_PRIORITIES, RubyCore.DEFAULT_TASK_PRIORITIES);
        defaultOptionsMap.put(RubyCore.COMPILER_TASK_CASE_SENSITIVE, RubyCore.ENABLED);

        // encoding setting comes from resource plug-in
        optionNames.add(RubyCore.CORE_ENCODING);

        // Formatter settings
        Map codeFormatterOptionsMap = DefaultCodeFormatterConstants.getEclipseDefaultSettings(); // code
                                                                                                    // formatter
                                                                                                    // defaults
        for (Iterator iter = codeFormatterOptionsMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String optionName = (String) entry.getKey();
            defaultOptionsMap.put(optionName, entry.getValue());
            optionNames.add(optionName);
        }

        // Store default values to default preferences
        IEclipsePreferences defaultPreferences = new DefaultScope().getNode(RubyCore.PLUGIN_ID);
        for (Iterator iter = defaultOptionsMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String optionName = (String) entry.getKey();
            defaultPreferences.put(optionName, (String) entry.getValue());
            optionNames.add(optionName);
        }

    }

}
