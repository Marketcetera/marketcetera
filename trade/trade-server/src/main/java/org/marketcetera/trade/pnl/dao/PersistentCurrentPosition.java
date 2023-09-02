//
// this file is automatically generated
//
package org.marketcetera.trade.pnl.dao;

/* $License$ */

/**
 * Describes the current position of a given instrument owned by a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@javax.persistence.Entity(name="CurrentPosition")
@javax.persistence.Table(name="metc_pnl_current_positions")
public class PersistentCurrentPosition
        extends org.marketcetera.persist.EntityBase
        implements org.marketcetera.trade.pnl.CurrentPosition,org.marketcetera.trade.HasInstrument,org.marketcetera.admin.HasUser
{
    /**
     * Create a new PersistentCurrentPosition instance.
     */
    public PersistentCurrentPosition() {}
    /**
     * Create a new PersistentCurrentPosition instance.
     *
     * @param inCurrentPosition a <code>CurrentPosition</code> value
     */
    public PersistentCurrentPosition(org.marketcetera.trade.pnl.CurrentPosition inCurrentPosition)
    {
        setInstrument(inCurrentPosition.getInstrument());
        setUser(inCurrentPosition.getUser());
        setPosition(inCurrentPosition.getPosition());
        setWeightedAverageCost(inCurrentPosition.getWeightedAverageCost());
        setRealizedGain(inCurrentPosition.getRealizedGain());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public org.marketcetera.trade.Instrument getInstrument()
    {
        initInstrument();
        return instrument;
    }
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    @Override
    public void setInstrument(org.marketcetera.trade.Instrument inInstrument)
    {
        instrument = inInstrument;
        if(inInstrument == null) {
            securityType = null;
            symbol = null;
            strikePrice = null;
            optionType = null;
            expiry = null;
        } else {
            securityType = inInstrument.getSecurityType();
            symbol = inInstrument.getFullSymbol();
            if(inInstrument.getSecurityType().equals(org.marketcetera.trade.SecurityType.Option)) {
                strikePrice = ((org.marketcetera.trade.Option)inInstrument).getStrikePrice();
                optionType = ((org.marketcetera.trade.Option)inInstrument).getType();
                expiry = ((org.marketcetera.trade.Option)inInstrument).getExpiry();
            }
        }
    }
    /**
     * Get the user value.
     *
     * @return an <code>org.marketcetera.admin.User</code> value
     */
    @Override
    public org.marketcetera.admin.User getUser()
    {
        return user;
    }
    /**
     * Set the user value.
     *
     * @param inUser an <code>org.marketcetera.admin.User</code> value
     */
    @Override
    public void setUser(org.marketcetera.admin.User inUser)
    {
        user = (org.marketcetera.admin.user.PersistentUser)inUser;
    }
    /**
     * Get the position value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    @Override
    public java.math.BigDecimal getPosition()
    {
        return position;
    }
    /**
     * Set the position value.
     *
     * @param inPosition a <code>java.math.BigDecimal</code> value
     */
    @Override
    public void setPosition(java.math.BigDecimal inPosition)
    {
        position = inPosition == null ? java.math.BigDecimal.ZERO : inPosition;
    }
    /**
     * Get the weightedAverageCost value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    @Override
    public java.math.BigDecimal getWeightedAverageCost()
    {
        return weightedAverageCost;
    }
    /**
     * Set the weightedAverageCost value.
     *
     * @param inWeightedAverageCost a <code>java.math.BigDecimal</code> value
     */
    @Override
    public void setWeightedAverageCost(java.math.BigDecimal inWeightedAverageCost)
    {
        weightedAverageCost = inWeightedAverageCost == null ? java.math.BigDecimal.ZERO : inWeightedAverageCost;
    }
    /**
     * Get the realizedGain value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    @Override
    public java.math.BigDecimal getRealizedGain()
    {
        return realizedGain;
    }
    /**
     * Set the realizedGain value.
     *
     * @param inRealizedGain a <code>java.math.BigDecimal</code> value
     */
    @Override
    public void setRealizedGain(java.math.BigDecimal inRealizedGain)
    {
        realizedGain = inRealizedGain == null ? java.math.BigDecimal.ZERO : inRealizedGain;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("CurrentPosition [")
            .append("instrument=").append(instrument)
            .append(", user=").append(user)
            .append(", position=").append(org.marketcetera.core.BigDecimalUtil.render(position))
            .append(", weightedAverageCost=").append(org.marketcetera.core.BigDecimalUtil.renderCurrency(weightedAverageCost))
            .append(", realizedGain=").append(org.marketcetera.core.BigDecimalUtil.renderCurrency(realizedGain)).append("]");
        return builder.toString();
    }
    /**
     * Sets the value of the instrument, if necessary.
     */
    private void initInstrument()
    {
        if(instrument == null) {
            instrument = org.marketcetera.core.PlatformServices.getInstrument(symbol);
        }
    }
    /**
     * instrument value
     */
    private transient org.marketcetera.trade.Instrument instrument;
    /**
     * symbol value
     */
    @javax.persistence.Column(name="symbol",nullable=false)
    private String symbol;
    /**
     * strike price value, <code>null</code> for non-option types
     */
    @javax.persistence.Column(name="strike_price",nullable=false,precision=org.marketcetera.core.PlatformServices.DECIMAL_PRECISION,scale=org.marketcetera.core.PlatformServices.DECIMAL_SCALE)
    private java.math.BigDecimal strikePrice;
    /**
    * security type value
     */
    @javax.persistence.Column(name="security_type",nullable=false)
    private org.marketcetera.trade.SecurityType securityType;
    /**
     * expiry value, <code>null</code> for non-option types
     */
    @javax.persistence.Column(name="expiry",nullable=true)
    private String expiry;
    /**
    * option type value, <code>null</code> for non-option types
     */
    @javax.persistence.Column(name="option_type",nullable=true)
    private org.marketcetera.trade.OptionType optionType;
    /**
     * user which owns lot
     */
    @javax.persistence.ManyToOne(fetch=javax.persistence.FetchType.EAGER,optional=false)
    @javax.persistence.JoinColumn(name="user_id",nullable=false)
    private org.marketcetera.admin.user.PersistentUser user;
    /**
     * position value
     */
    @javax.persistence.Column(name="position",precision=org.marketcetera.core.PlatformServices.DECIMAL_PRECISION,scale=org.marketcetera.core.PlatformServices.DECIMAL_SCALE,nullable=false,unique=false)
    private java.math.BigDecimal position = java.math.BigDecimal.ZERO;
    /**
     * weighted average cost to attain this position
     */
    @javax.persistence.Column(name="weighted_average_cost",precision=org.marketcetera.core.PlatformServices.DECIMAL_PRECISION,scale=org.marketcetera.core.PlatformServices.DECIMAL_SCALE,nullable=false,unique=false)
    private java.math.BigDecimal weightedAverageCost = java.math.BigDecimal.ZERO;
    /**
     * realized gain value
     */
    @javax.persistence.Column(name="realized_gain",precision=org.marketcetera.core.PlatformServices.DECIMAL_PRECISION,scale=org.marketcetera.core.PlatformServices.DECIMAL_SCALE,nullable=false,unique=false)
    private java.math.BigDecimal realizedGain = java.math.BigDecimal.ZERO;
    private static final long serialVersionUID = -1363005408L;
}
