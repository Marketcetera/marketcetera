package org.marketcetera.trade;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Implementation for Single Order Suggestions.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com>Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="OrderSingleSuggestion")
@ClassVersion("$Id$")
public class OrderSingleSuggestionImpl
        implements OrderSingleSuggestion,HasSuggestionAction
{
    @Override
    public String getIdentifier() {
        return mIdentifier;
    }

    @Override
    public void setIdentifier(String inIdentifier) {
        mIdentifier = inIdentifier;
    }

    @Override
    public BigDecimal getScore() {
        return mScore;
    }

    @Override
    public void setScore(BigDecimal inScore) {
        mScore = inScore;
    }

    @Override
    public OrderSingle getOrder() {
        return mOrder == null
                ? null
                : mOrder.clone();
    }

    @Override
    public void setOrder(OrderSingle inOrder) {
        mOrder = inOrder == null
                ? null
                : inOrder.clone();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestionAction#getSuggestionAction()
     */
    @Override
    public SuggestionAction getSuggestionAction()
    {
        return suggestionAction;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestionAction#setSuggestionAction(org.marketcetera.trade.SuggestionAction)
     */
    @Override
    public void setSuggestionAction(SuggestionAction inAction)
    {
        suggestionAction = inAction;
    }
    @Override
    public String toString() {
        return Messages.ORDER_SINGLE_SUGGESTION_TO_STRING.getText(String.valueOf(getSuggestionAction()),
                                                                  String.valueOf(getIdentifier()),
                                                                  String.valueOf(getScore()),
                                                                  String.valueOf(getOrder()));
    }
    /**
     * identifier value
     */
    @XmlAttribute(name="identifier")
    private String mIdentifier;
    /**
     * score value
     */
    @XmlAttribute(name="score")
    private BigDecimal mScore;
    /**
     * order value
     */
    @XmlTransient
    private OrderSingle mOrder;
    /**
     * action value
     */
    @XmlAttribute(name="action")
    private SuggestionAction suggestionAction = SuggestionAction.ADD;
    private static final long serialVersionUID = 6304005195360225079L;
}
