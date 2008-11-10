package org.rubypeople.rdt.internal.ui.text.hyperlinks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.text.hyperlinks.IHyperlinkProvider;

public class RubyHyperLinkDetector implements IHyperlinkDetector {

	public static final String RDT_UI_NAMESPACE = "org.rubypeople.rdt.ui";
	public static final String RDT_UI_HYPERLINKPROVIDER = "hyperlinkProvider";

	private List fExtensions;
	private final IEditorInput fEditorInput;

	public RubyHyperLinkDetector(IEditorInput editorInput) {
		this.fEditorInput = editorInput;
	}

	private IExtensionPoint[] getExtensionPoints() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		return reg.getExtensionPoints(RDT_UI_NAMESPACE);
	}

	private List initExtensions() {
		if (fExtensions != null)
			return fExtensions;
		fExtensions = new ArrayList();
		IExtensionPoint[] points = getExtensionPoints();
		// TODO: Look for textProvider!
		IExtensionPoint point = getExtensionPoint(points);
		if (point != null) {
			IExtension[] exts = point.getExtensions();
			for (int i = 0; i < exts.length; i++) {
				IConfigurationElement[] elem = exts[i].getConfigurationElements();
				String attrs[] = elem[0].getAttributeNames();
				try {
					Object tempProv = elem[0].createExecutableExtension("class");
					if (tempProv instanceof IHyperlinkProvider) {
						IHyperlinkProvider prov = (IHyperlinkProvider) tempProv;
						fExtensions.add(prov);
					}
				} catch (Exception e) {
					RubyPlugin.log(e);
				}
			}
		}
		return fExtensions;
	}

	private IExtensionPoint getExtensionPoint(IExtensionPoint[] points) {
		for (int i = 0; i < points.length; i++) {
			IExtensionPoint currentPoint = points[i];
			String uniqueIdentifier = currentPoint.getUniqueIdentifier();
			if (uniqueIdentifier.endsWith(RDT_UI_HYPERLINKPROVIDER)) {
				return currentPoint;
			}
		}
		return null;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		List extensions = initExtensions();
		if (extensions.isEmpty()) return null;
		// FIXME We cheat and pass a null Node down to providers, because so far we only have one, and it doesn't use it. So this allows us to speed things up by not parsing the file...
		for (int i = 0; i < extensions.size(); i++) {
			IHyperlinkProvider currentProvider = (IHyperlinkProvider) extensions.get(i);
			IHyperlink link = currentProvider.getHyperlink(fEditorInput, textViewer, null, region, canShowMultipleHyperlinks);
			// TODO: either do that or query all HyperlinkProviders and
			// return a list of hyperlinks?
			if (link != null) {
				return new IHyperlink[] { link };
			}
		}
		return null;
	}

}