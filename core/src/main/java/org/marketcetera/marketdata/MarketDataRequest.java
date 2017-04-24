package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.AssetClass.EQUITY;
import static org.marketcetera.marketdata.Content.DIVIDEND;
import static org.marketcetera.marketdata.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Messages.BOTH_SYMBOLS_AND_UNDERLYING_SYMBOLS_SPECIFIED;
import static org.marketcetera.marketdata.Messages.DIVIDEND_REQUIRES_SYMBOLS;
import static org.marketcetera.marketdata.Messages.INVALID_CONTENT;
import static org.marketcetera.marketdata.Messages.INVALID_SYMBOLS;
import static org.marketcetera.marketdata.Messages.INVALID_UNDERLYING_SYMBOLS;
import static org.marketcetera.marketdata.Messages.MISSING_ASSET_CLASS;
import static org.marketcetera.marketdata.Messages.MISSING_CONTENT;
import static org.marketcetera.marketdata.Messages.NEITHER_SYMBOLS_NOR_UNDERLYING_SYMBOLS_SPECIFIED;
import static org.marketcetera.marketdata.Messages.VALID_UNDERLYING_ASSET_CLASS_REQUIRED;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a market data request.
 * 
 * <p>Use a {@link MarketDataRequestBuilder builder} to create a <code>MarketDataRequest</code> object.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@Immutable
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="marketDataRequest")
@ClassVersion("$Id$")
public class MarketDataRequest
        implements Serializable
{
    /**
     * Get the symbols value.
     * 
     * @return a <code>String[]</code> value
     */
    public Set<String> getSymbols()
    {
        return request.getSymbols();
    }
    /**
     * Get the underlying symbols value.
     * 
     * @return a <code>String[]</code> value
     */
    public Set<String> getUnderlyingSymbols()
    {
        return request.getUnderlyingSymbols();
    }
    /**
     * Get the provider value.
     *
     * @return a <code>String</code> value
     */
    public String getProvider()
    {
        return request.getProvider();
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return request.getExchange();
    }
    /**
     * Get the content value.
     * 
     * @return a <code>Set&lt;Content&gt;</code> value
     */
    public Set<Content> getContent()
    {
        return request.getContent();
    }
    /**
     * Get the map of parameter names and values.
     *
     * @return an umodifiable map of parameter names and values. 
     */
    public Map<String,String> getParameters()
    {
        return request.getParameters();
    }
    /**
     * Get the asset class value.
     *
     * @return an <code>AssetClass</code> value
     */
    public AssetClass getAssetClass()
    {
        return request.getAssetClass();
    }
    /**
     * Determines if the request is valid apropos the given capabilities.
     *
     * @param inCapabilities a <code>Content[]</code> value containing the capabilities against which to verify the request
     * @return a <code>boolean</code> value indicating whether the request if valid according to the given capabilities
     */
    public boolean validateWithCapabilities(Content...inCapabilities)
    {
        Set<Content> results = new HashSet<Content>(request.getContent());
        results.removeAll(Arrays.asList(inCapabilities));
        return results.isEmpty();
    }
    /**
     * Get the request id value.
     *
     * @return a <code>long</code> value
     */
    public long getRequestId()
    {
        return request.getId();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(13,
                                   31).append(request).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null) { 
            return false;
        }
        if(obj == this) { 
            return true; 
        }
        if(!(obj instanceof MarketDataRequest)) {
          return false;
        }
        MarketDataRequest rhs = (MarketDataRequest)obj;
        return new EqualsBuilder().append(request,
                                          rhs.request).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return request.toString();
    }
    /**
     * Create a new MarketDataRequest instance.
     *
     * @param inMarketDataRequestBean
     */
    MarketDataRequest(MarketDataRequestBean inMarketDataRequestBean)
    {
        try {
            request = inMarketDataRequestBean.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
        setDefaults();
        validate();
    }
    /**
     * Create a new MarketDataRequest instance.
     */
    @SuppressWarnings("unused")
    private MarketDataRequest()
    {
        request = new MarketDataRequestBean();
    }
    /**
     * Validates the <code>MarketDataRequest</code>.
     * 
     * <p>This method is intended to validate a request when it is believed to be complete
     * and ready to be submitted.  Some validation is performed that is relevant only to
     * a completed request.
     *
     * @throws IllegalArgumentException if the request is invalid
     */
    private void validate()
    {
        validateAssetClass();
        validateContent();
        validateExchange();
        validateParameters();
        validateProvider();
        doCommonSymbolValidation();
        validateSymbols();
        validateUnderlyingSymbols();
    }
    /**
     * Sets any unset values to their default if a default exists. 
     */
    private void setDefaults()
    {
        if(request.getContent().isEmpty()) {
            request.setContent(EnumSet.of(TOP_OF_BOOK));
        }
        if(request.getAssetClass() == null) {
            request.setAssetClass(EQUITY);
        }
    }
    /**
     * Verifies that the symbols are valid.
     * 
     * @throws IllegalArgumentException if the symbols are not valid
     */
    private void validateSymbols()
    {
        Set<String> symbols = getSymbols();
        // symbols is non-empty, underlying symbols is empty
        for(String symbol : symbols) {
            if(symbol == null ||
                    symbol.trim().isEmpty()) {
                throw new IllegalArgumentException(new I18NBoundMessage1P(INVALID_SYMBOLS,
                                                                          String.valueOf(symbols)).getText());
            }
        }
    }
    /**
     * Executes validation common to symbols and underlying symbols. 
     *
     * @throws IllegalArgumentException if the validation fails
     */
    private void doCommonSymbolValidation()
    {
        Set<String> symbols = getSymbols();
        Set<String> underlyingSymbols = getUnderlyingSymbols();
        // symbols xor underlying symbols must be specified
        if(symbols.isEmpty() &&
           underlyingSymbols.isEmpty()) {
            throw new IllegalArgumentException(NEITHER_SYMBOLS_NOR_UNDERLYING_SYMBOLS_SPECIFIED.getText(this)); 
        }
        if(!symbols.isEmpty() &&
           !underlyingSymbols.isEmpty()) {
            throw new IllegalArgumentException(BOTH_SYMBOLS_AND_UNDERLYING_SYMBOLS_SPECIFIED.getText(this)); 
        }
    }
    /**
     * Verifies that the underlying symbols are valid.
     * 
     * @throws IllegalArgumentException if the underlying symbols are not valid
     */
    private void validateUnderlyingSymbols()
    {
        Set<String> underlyingSymbols = getUnderlyingSymbols();
        // underlying symbols is non-empty, symbols is empty
        for(String underlyingSymbol : underlyingSymbols) {
            if(underlyingSymbol == null ||
                    underlyingSymbol.trim().isEmpty()) {
                throw new IllegalArgumentException(new I18NBoundMessage1P(INVALID_UNDERLYING_SYMBOLS,
                                                                          String.valueOf(underlyingSymbols)).getText());
            }
        }
    }
    /**
     * Verifies that the <code>Exchange</code> is valid.
     */
    private void validateExchange()
    {
        // nothing to do
    }
    /**
     * Verifies that the parameters are valid.
     */
    private void validateParameters()
    {
        // nothing to do
    }
    /**
     * Verifies that the provider is valid.
     */
    private void validateProvider()
    {
        // provider is optional when an MDR is passed directly to a Market Data Module
    }
    /**
     * Verifies that the <code>AssetClass</code> is valid.
     *
     * @throws IllegalArgumentException if the <code>AssetClass</code> is not valid
     */
    private void validateAssetClass()
    {
        if(getAssetClass() == null) {
            throw new IllegalArgumentException(MISSING_ASSET_CLASS.getText());
        }
        // if underlying symbols are specified then OPTION or FUTURE is required
        if(!getUnderlyingSymbols().isEmpty() &&
           !getAssetClass().isValidForUnderlyingSymbols()) {
            throw new IllegalArgumentException(VALID_UNDERLYING_ASSET_CLASS_REQUIRED.getText(this,
                                                                                             getAssetClass()));
        }
    }
    /**
     * Verifies that the <code>Content</code> is valid.
     *
     * @throws IllegalArgumentException if the <code>Content</code> is not valid
     */
    private void validateContent()
    {
        Set<Content> content = getContent();
        // content cannot be empty
        if(content.isEmpty()) {
            throw new IllegalArgumentException(MISSING_CONTENT.getText());
        }
        // check to make sure the list of content is valid
        if(!isValidEnumList(content)) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                      String.valueOf(content)).getText());
        }
        // content dividend requires symbols
        if(content.contains(DIVIDEND) &&
           getSymbols().isEmpty()) {
            throw new IllegalArgumentException(DIVIDEND_REQUIRES_SYMBOLS.getText(this));
        }
    }
    /**
     * Checks to see if the given enum collection value is valid.
     *
     * @param inEnums a <code>Collection&lt;E&gt;</code> value
     * @return a <code>boolean</code> value
     */
    private static <E extends Enum<E>> boolean isValidEnumList(Collection<E> inEnums)
    {
        if(inEnums == null ||
           inEnums.isEmpty()) {
            return false;
        }
        for(Enum<E> e : inEnums) {
            if(e == null) {
                return false;
            }
        }
        return true;
    }
    /**
     * the request data 
     */
    @XmlElement(name="requestBody")
    private final MarketDataRequestBean request;
    private static final long serialVersionUID = -1541092871609463604L;
}
