package org.marketcetera.web.fonts;

import com.vaadin.server.FontIcon;

/* $License$ */

/**
 * Provides a glyph for a disconnected session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum MarketceteraSessionStatusDisconnectedFont
        implements FontIcon
{
    SESSION_STATUS_DISCONNECTED(0xe910);
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
        return "MetcSessionStatusDisconnected";
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
        return "<span class=\"v-icon MetcSessionStatusDisconnected\">&#x" + Integer.toHexString(codepoint) + ";</span>";
    }
    /**
     * Create a new MarketceteraSessionStatusDisconnectedFont instance.
     *
     * @param inCodepoint an <code>int</code> value
     */
    private MarketceteraSessionStatusDisconnectedFont(int inCodepoint)
    {
        codepoint = inCodepoint;
    }
    /**
     * codepoint within the font for the particular glyph
     */
    private final int codepoint;
}
