package org.marketcetera.web.font;

import com.vaadin.server.FontIcon;

/* $License$ */

/**
 * Provides icon glyphs for Marketcetera Icons.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * @see https://vaadin.com/docs/v8/framework/articles/UsingFontIcons
 */
public enum MarketceteraFont
        implements FontIcon
{
    Average_Price(0xe900),
    Backward(0xea1f),
    Cascade_Windows(0xe901),
    Close_All_Windows(0xe902),
    Cluster_Data(0xe903),
    Fills(0xe904),
    First(0xea21),
    FIX_Messages(0xe905),
    FIX_Sessions(0xe906),
    Forward(0xea20),
    Last(0xea22),
    Logout(0xe907),
    Market_Data(0xe908),
    Metrics(0xe909),
    Open_Orders(0xe90a),
    Order_Ticket(0xe90b),
    Permissions(0xe90c),
    Plus(0xea0a),
    Roles(0xe90d),
    Session_Status_General(0xe90f),
    Supervisor_Permissions(0xe915),
    Tile_Windows(0xe916),
    Users(0xe917),
    Workspace(0xe918);
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
        return "Metc";
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
        return "<span class=\"v-icon Metc\">&#x" + Integer.toHexString(codepoint) + ";</span>";
    }
    /**
     * Create a new MarketceteraFont instance.
     *
     * @param inCodepoint an <code>int</code> value
     */
    private MarketceteraFont(int inCodepoint)
    {
        codepoint = inCodepoint;
    }
    /**
     * codepoint within the font for the particular glyph
     */
    private final int codepoint;
}
