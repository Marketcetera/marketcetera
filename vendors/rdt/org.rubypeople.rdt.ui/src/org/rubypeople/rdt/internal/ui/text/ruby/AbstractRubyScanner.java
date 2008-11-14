package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.IAbstractManagedScanner;
import org.rubypeople.rdt.ui.text.IColorManager;
import org.rubypeople.rdt.ui.text.IColorManagerExtension;

public abstract class AbstractRubyScanner extends BufferedRuleBasedScanner implements IAbstractManagedScanner {

	private String[] fPropertyNamesColor;
	private String[] fPropertyNamesBgColor;
	private String[] fPropertyNamesBgEnabled;
	private String[] fPropertyNamesBold;
	private String[] fPropertyNamesItalic;
	private String[] fPropertyNamesStrikethrough;
	private String[] fPropertyNamesUnderline;
	private IColorManager fColorManager;
	private IPreferenceStore fPreferenceStore;
	private boolean fNeedsLazyColorLoading;
	private Map fTokenMap = new HashMap();

	/**
	 * Creates an abstract Ruby scanner.
	 */
	public AbstractRubyScanner(IColorManager manager, IPreferenceStore store) {
		super();
		fColorManager = manager;
		fPreferenceStore = store;
	}

	/**
	 * Returns the preference store.
	 * 
	 * @return the preference store.
	 * 
	 * @since 3.0
	 */
	protected IPreferenceStore getPreferenceStore() {
		return fPreferenceStore;
	}

	private void initializeRules() {
		List rules = createRules();
		if (rules != null) {
			IRule[] result = new IRule[rules.size()];
			rules.toArray(result);
			setRules(result);
		}
	}
	
	public IToken nextToken() {
		if (fNeedsLazyColorLoading)
			resolveProxyAttributes();
		return super.nextToken();
	}

	/**
	 * Returns an array of preference keys which define the tokens used in the
	 * rules of this scanner.
	 * <p>
	 * The preference key is used access the color in the preference store and
	 * in the color manager.
	 * </p>
	 * <p>
	 * Preference key +{@link PreferenceConstants#EDITOR_BOLD_SUFFIX}is used
	 * to retrieve whether the token is rendered in bold.
	 * </p>
	 * <p>
	 * Preference key +{@link PreferenceConstants#EDITOR_ITALIC_SUFFIX}is used
	 * to retrieve whether the token is rendered in italic.
	 * </p>
	 */
	abstract protected String[] getTokenProperties();

	protected abstract List createRules();

	public final void initialize() {
		fPropertyNamesColor = getTokenProperties();
		int length = fPropertyNamesColor.length;
		fPropertyNamesBgEnabled = new String[length];
		fPropertyNamesBgColor = new String[length];
		fPropertyNamesBold = new String[length];
		fPropertyNamesItalic = new String[length];
		fPropertyNamesStrikethrough = new String[length];
		fPropertyNamesUnderline = new String[length];

		for (int i= 0; i < length; i++) {
			fPropertyNamesBgColor[i]= getBGKey(fPropertyNamesColor[i]);
			fPropertyNamesBgEnabled[i] = getBGEnabledKey(fPropertyNamesColor[i]);
			fPropertyNamesBold[i]= getBoldKey(fPropertyNamesColor[i]);
			fPropertyNamesItalic[i]= getItalicKey(fPropertyNamesColor[i]);
			fPropertyNamesStrikethrough[i]= getStrikethroughKey(fPropertyNamesColor[i]);
			fPropertyNamesUnderline[i]= getUnderlineKey(fPropertyNamesColor[i]);
		}
		
		fNeedsLazyColorLoading = Display.getCurrent() == null;
		for (int i = 0; i < length; i++) {
			if (fNeedsLazyColorLoading)
				addTokenWithProxyAttribute(fPropertyNamesColor[i], fPropertyNamesBgColor[i], fPropertyNamesBgEnabled[i], fPropertyNamesBold[i], fPropertyNamesItalic[i], fPropertyNamesStrikethrough[i], fPropertyNamesUnderline[i]);
			else
				addToken(fPropertyNamesColor[i], fPropertyNamesBgColor[i], fPropertyNamesBold[i], fPropertyNamesBgEnabled[i], fPropertyNamesItalic[i], fPropertyNamesStrikethrough[i], fPropertyNamesUnderline[i]);
		}

		initializeRules();
	}
	
	protected String getBoldKey(String colorKey) {
		return colorKey + PreferenceConstants.EDITOR_BOLD_SUFFIX;
	}
	
	protected String getBGEnabledKey(String colorKey) {
		return colorKey + PreferenceConstants.EDITOR_BG_ENABLED_SUFFIX;
	}
	
	protected String getBGKey(String colorKey) {
		return colorKey + PreferenceConstants.EDITOR_BG_SUFFIX;
	}

	protected String getItalicKey(String colorKey) {
		return colorKey + PreferenceConstants.EDITOR_ITALIC_SUFFIX;
	}
	
	protected String getStrikethroughKey(String colorKey) {
		return colorKey + PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX;
	}
	
	protected String getUnderlineKey(String colorKey) {
		return colorKey + PreferenceConstants.EDITOR_UNDERLINE_SUFFIX;
	}

	private void addTokenWithProxyAttribute(String colorKey, String bgColorKey, String bgEnabledKey, String boldKey, String italicKey, String strikethroughKey, String underlineKey) {
		fTokenMap.put(colorKey, new Token(createTextAttribute(null, null, bgEnabledKey, boldKey, italicKey, strikethroughKey, underlineKey)));
	}

	private void resolveProxyAttributes() {
		if (fNeedsLazyColorLoading && Display.getCurrent() != null) {
			for (int i = 0; i < fPropertyNamesColor.length; i++) {
				addToken(fPropertyNamesColor[i], fPropertyNamesBgColor[i], fPropertyNamesBgEnabled[i], fPropertyNamesBold[i], fPropertyNamesItalic[i], fPropertyNamesStrikethrough[i], fPropertyNamesUnderline[i]);
			}
			fNeedsLazyColorLoading = false;
		}
	}

	private void addToken(String colorKey, String bgColorKey, String bgEnabledKey, String boldKey, String italicKey, String strikethroughKey, String underlineKey) {
		bindColor(colorKey);
		bindColor(bgColorKey);

		if (!fNeedsLazyColorLoading)
			fTokenMap.put(colorKey, new Token(createTextAttribute(colorKey, bgColorKey, bgEnabledKey, boldKey, italicKey, strikethroughKey, underlineKey)));
		else {
			Token token = ((Token) fTokenMap.get(colorKey));
			if (token != null) token.setData(createTextAttribute(colorKey, bgColorKey, bgEnabledKey, boldKey, italicKey, strikethroughKey, underlineKey));
		}
	}

	private void bindColor(String colorKey) {
		if (fColorManager != null && colorKey != null && fColorManager.getColor(colorKey) == null) {
			RGB rgb = PreferenceConverter.getColor(fPreferenceStore, colorKey);
			if (rgb == PreferenceConverter.COLOR_DEFAULT_DEFAULT && !colorKey.endsWith(PreferenceConstants.EDITOR_BG_SUFFIX)) return;
			
			if (fColorManager instanceof IColorManagerExtension) {
				IColorManagerExtension ext = (IColorManagerExtension) fColorManager;
				ext.unbindColor(colorKey);
				ext.bindColor(colorKey, rgb);
			}
		}
	}

	protected Token getToken(String key) {
		if (fNeedsLazyColorLoading) resolveProxyAttributes();
		return (Token) fTokenMap.get(key);
	}

	/**
	 * Create a text attribute based on the given color, bold and italic
	 * preference keys.
	 * 
	 * @param colorKey
	 *            the color preference key
	 * @param colorKey
	 *            the color preference key
	 * @param boldKey
	 *            the bold preference key
	 * @param italicKey
	 *            the italic preference key
	 * @param strikethroughKey
	 *            the strikethrough preference key
	 * @param underlineKey
	 *            the underline preference key
	 * @return the created text attribute
	 * @since 0.9.0
	 */
	private TextAttribute createTextAttribute(String colorKey, String bgColorKey, String bgEnabledKey, String boldKey, String italicKey, String strikethroughKey, String underlineKey) {	
		Color color= null;
		if (colorKey != null)
			color= fColorManager.getColor(colorKey);
		
		boolean useBG = fPreferenceStore.getBoolean(boldKey);
		Color bgColor= null;
		if (bgColorKey != null && useBG)
			bgColor= fColorManager.getColor(bgColorKey);

		int style= fPreferenceStore.getBoolean(boldKey) ? SWT.BOLD : SWT.NORMAL;
		if (fPreferenceStore.getBoolean(italicKey))
			style |= SWT.ITALIC;

		if (fPreferenceStore.getBoolean(strikethroughKey))
			style |= TextAttribute.STRIKETHROUGH;

		if (fPreferenceStore.getBoolean(underlineKey))
			style |= TextAttribute.UNDERLINE;

		return new TextAttribute(color, bgColor, style);
	}

	public boolean affectsBehavior(PropertyChangeEvent event) {
		return indexOf(event.getProperty()) >= 0;
	}

	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		// New
		String p = event.getProperty();
		int index = indexOf(p);
		Token token = getToken(fPropertyNamesColor[index]);
		if (fPropertyNamesColor[index].equals(p))
			adaptToColorChange(token, event);
		if (fPropertyNamesBgColor[index].equals(p) || fPropertyNamesBgEnabled[index].equals(p))
			adaptToBgColorChange(token, event);
		else if (fPropertyNamesBold[index].equals(p))
			adaptToStyleChange(token, event, SWT.BOLD);
		else if (fPropertyNamesItalic[index].equals(p)) 
			adaptToStyleChange(token, event, SWT.ITALIC);
		else if (fPropertyNamesStrikethrough[index].equals(p)) 
			adaptToStyleChange(token, event, TextAttribute.STRIKETHROUGH);
		else if (fPropertyNamesUnderline[index].equals(p)) 
			adaptToStyleChange(token, event, TextAttribute.UNDERLINE);
	}

	private void adaptToStyleChange(Token token, PropertyChangeEvent event, int styleAttribute) {
		boolean eventValue = false;
		Object value = event.getNewValue();
		if (value instanceof Boolean)
			eventValue = ((Boolean) value).booleanValue();
		else if (IPreferenceStore.TRUE.equals(value)) eventValue = true;

		Object data = token.getData();
		if (data instanceof TextAttribute) {
			TextAttribute oldAttr = (TextAttribute) data;
			boolean activeValue = (oldAttr.getStyle() & styleAttribute) == styleAttribute;
			if (activeValue != eventValue) token.setData(new TextAttribute(oldAttr.getForeground(), oldAttr.getBackground(), eventValue ? oldAttr.getStyle() | styleAttribute : oldAttr.getStyle() & ~styleAttribute));
		}
	}

	private void adaptToColorChange(Token token, PropertyChangeEvent event) {
		adaptToSomeColorChange(token, event, true);
	}
	
	private void adaptToBgColorChange(Token token, PropertyChangeEvent event) {
		adaptToSomeColorChange(token, event, false);
	}

	private void adaptToSomeColorChange(Token token, PropertyChangeEvent event, boolean isForeground) {
		if (event.getProperty().endsWith(PreferenceConstants.EDITOR_BG_ENABLED_SUFFIX)) { // Handle toggling of background enablement!
			Object value = event.getNewValue();		
			boolean enabling = false;
			if (value instanceof Boolean) {
				enabling = ((Boolean) value).booleanValue();
			} else if (value instanceof String) {
				enabling = Boolean.parseBoolean((String) value);
			}
			Object data = token.getData();
			if (data instanceof TextAttribute) {
				TextAttribute oldAttr = (TextAttribute) data;
				Color foreGround = oldAttr.getForeground();
				if (enabling) { // we're enabling, so we need to grab the background color somehow
					String property = getBGKey(event.getProperty().substring(0, event.getProperty().length() - PreferenceConstants.EDITOR_BG_ENABLED_SUFFIX.length()));
					RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), property);					
					Color color = fColorManager.getColor(property);
					if ((color == null || !rgb.equals(color.getRGB())) && fColorManager instanceof IColorManagerExtension) {
						IColorManagerExtension ext = (IColorManagerExtension) fColorManager;

						ext.unbindColor(property);
						ext.bindColor(property, rgb);

						color = fColorManager.getColor(property);
					}					
					token.setData(new TextAttribute(foreGround, color, oldAttr.getStyle()));
				} else {
					token.setData(new TextAttribute(foreGround, null, oldAttr.getStyle()));
				}	
				return;
			}
		}
		
		RGB rgb = null;
		
		Object value = event.getNewValue();
		if (value instanceof RGB)
			rgb = (RGB) value;
		else if (value instanceof String) rgb = StringConverter.asRGB((String) value);

		if (rgb != null) {

			String property = event.getProperty();
			Color color = fColorManager.getColor(property);

			if ((color == null || !rgb.equals(color.getRGB())) && fColorManager instanceof IColorManagerExtension) {
				IColorManagerExtension ext = (IColorManagerExtension) fColorManager;

				ext.unbindColor(property);
				ext.bindColor(property, rgb);

				color = fColorManager.getColor(property);
			}

			Object data = token.getData();
			if (data instanceof TextAttribute) {
				TextAttribute oldAttr = (TextAttribute) data;
				Color foreGround;
				Color backGround;
				if (!isForeground) {
					foreGround = oldAttr.getForeground();
					backGround = color;
				} else {
					foreGround = color;
					backGround = oldAttr.getBackground();
				}
				token.setData(new TextAttribute(foreGround, backGround, oldAttr.getStyle()));
			}
		}
	}

	private int indexOf(String property) {
		if (property != null) {
			int length = fPropertyNamesColor.length;
			for (int i = 0; i < length; i++) {
				if (property.equals(fPropertyNamesColor[i]) || property.equals(fPropertyNamesBgColor[i]) || property.equals(fPropertyNamesBgEnabled[i]) || property.equals(fPropertyNamesBold[i]) || property.equals(fPropertyNamesItalic[i]) || property.equals(fPropertyNamesStrikethrough[i]) || property.equals(fPropertyNamesUnderline[i])) return i;
			}
		}
		return -1;
	}
}
