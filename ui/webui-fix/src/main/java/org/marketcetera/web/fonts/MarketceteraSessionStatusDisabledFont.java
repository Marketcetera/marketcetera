package org.marketcetera.web.fonts;

import com.vaadin.server.FontIcon;

/* $License$ */

/**
 * Provides a glyph for a disabled session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum MarketceteraSessionStatusDisabledFont
        implements FontIcon
{
    SESSION_STATUS_DISABLED(0xe910);
    /* (non-Javadoc)
     * @see com.vaadin.server.Resource#getMIMEType()
     */
    @Override
    public String getMIMEType()
    {
        throw new UnsupportedOperationException("Not supported for FontIcon");
    }
    /* (non-Javadoc)
     * @see com.vaadin.server.FontIcon#getFontFamily()
     */
    @Override
    public String getFontFamily()
    {
        return "MetcSessionStatusDisabled";
    }
    /* (non-Javadoc)
     * @see com.vaadin.server.FontIcon#getCodepoint()
     */
    @Override
    public int getCodepoint()
    {
        return codepoint;
    }
    /* (non-Javadoc)
     * @see com.vaadin.server.FontIcon#getHtml()
     */
    @Override
    public String getHtml()
    {
        return "<span class=\"v-icon MetcSessionStatusDisabled\">&#x" + Integer.toHexString(codepoint) + ";</span>";
    }
    /**
     * Create a new MarketceteraSessionStatusDisabledFont instance.
     *
     * @param inCodepoint an <code>int</code> value
     */
    private MarketceteraSessionStatusDisabledFont(int inCodepoint)
    {
        codepoint = inCodepoint;
    }
    /**
     * codepoint within the font for the particular glyph
     */
    private final int codepoint;
}
