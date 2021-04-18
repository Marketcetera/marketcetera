package org.marketcetera.web.font;

import com.vaadin.server.FontIcon;

/* $License$ */

/**
 *
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
    Session_Status_Black(0xe90e),
    Session_Status_General(0xe90f),
    Session_Status_Green(0xe910),
    Session_Status_Grey(0xe911),
    Session_Status_Orange(0xe912),
    Session_Status_Red(0xe913),
    Session_Status_Yellow(0xe914),
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
     * @param inCodepoint
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
