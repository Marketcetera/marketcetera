package org.rubypeople.rdt.internal.codeassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.codeassist.CodeResolver;
import org.rubypeople.rdt.core.codeassist.ResolveContext;

public class SelectionEngine {

	private static final String EXTENSION_POINT = "codeResolver";
	private ArrayList<CodeResolver> fResolvers;

	/**
	 * Combines all the resolved elements from all resolvers. FIXME Need to make it so any resolver can modify the existing list (add or remove)!
	 * @param script
	 * @param start
	 * @param end
	 * @return
	 * @throws RubyModelException
	 */
	public IRubyElement[] select(IRubyScript script, int start, int end) throws RubyModelException {
		ResolveContext context = new ResolveContext(script, start, end);
		List<CodeResolver> resolvers = getResolvers();
		for (CodeResolver resolver : resolvers) {
			resolver.select(context);
		}
		return context.getResolved();
	}
	
	private List<CodeResolver> getResolvers() {
		if (fResolvers == null) {
			fResolvers = new ArrayList<CodeResolver>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<IConfigurationElement> elements = new ArrayList<IConfigurationElement>(
					Arrays.asList(registry.getConfigurationElementsFor(RubyCore
							.PLUGIN_ID, EXTENSION_POINT)));
			sortParticipants(elements);			
			for (IConfigurationElement configurationElement : elements) {
				try {
					CodeResolver resolver = (CodeResolver) configurationElement.createExecutableExtension("class");
					fResolvers.add(resolver);
				} catch (CoreException e) {
					RubyCore.log(e);
				}
			}
		}
		return fResolvers;
	}
	
	private void sortParticipants(List<IConfigurationElement> group) {
		Collections.sort(group, new Comparator<IConfigurationElement>() {
			public int compare(IConfigurationElement a, IConfigurationElement b) {
				if (a == b) return 0;
				String id = a.getAttribute("id"); //$NON-NLS-1$
				if (id == null) return -1;
				IConfigurationElement[] requiredElements = b.getChildren("requires"); //$NON-NLS-1$
				for (int i = 0, length = requiredElements.length; i < length; i++) {
					IConfigurationElement required = requiredElements[i];
					if (id.equals(required.getAttribute("id"))) //$NON-NLS-1$
						return 1;
				}
				return -1;
			}
		});
	}

}
