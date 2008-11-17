package org.rubypeople.rdt.core;

import java.util.Map;

import org.rubypeople.rdt.core.compiler.IScanner;
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.internal.core.util.PublicScanner;
import org.rubypeople.rdt.internal.formatter.OldCodeFormatter;

public class ToolFactory {

    /**
     * Create an instance of the built-in code formatter.
     * 
     * @param options -
     *            the options map to use for formatting with the default code
     *            formatter. Recognized options are documented on
     *            <code>RubyCore#getDefaultOptions()</code>. If set to
     *            <code>null</code>, then use the current settings from
     *            <code>RubyCore#getOptions</code>.
     * @return an instance of the built-in code formatter
     * @see OldCodeFormatter
     * @see RubyCore#getOptions()
     * @since 0.8.0
     */
    public static CodeFormatter createCodeFormatter(Map options) {
        if (options == null) options = RubyCore.getOptions();
        return new OldCodeFormatter(options);
    }

	public static IScanner createScanner(boolean b, boolean c, boolean d, boolean e) {
		return new PublicScanner();
	}

}
