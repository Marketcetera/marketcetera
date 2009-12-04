package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.INVALID_ASSET_CLASS;
import static org.marketcetera.marketdata.Messages.INVALID_CONTENT;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.core.Util;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Builds {@link MarketDataRequest} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public class MarketDataRequestBuilder
{
    /**
     * the key used to identify the asset class in the string representation of the market data request
     */
    public static final String ASSETCLASS_KEY = "assetclass"; //$NON-NLS-1$
    /**
     * the key used to identify the exchange in the string representation of the market data request
     */
    public static final String EXCHANGE_KEY = "exchange"; //$NON-NLS-1$
    /**
     * the key used to identify the content in the string representation of the market data request
     */
    public static final String CONTENT_KEY = "content"; //$NON-NLS-1$
    /**
     * the key used to identify the provider in the string representation of the market data request
     */
    public static final String PROVIDER_KEY = "provider"; //$NON-NLS-1$
    /**
     * the key used to identify the underlying symbols in the string representation of the market data request
     */
    public static final String UNDERLYINGSYMBOLS_KEY = "underlyingsymbols"; //$NON-NLS-1$
    /**
     * the key used to identify the symbols in the string representation of the market data request
     */
    public static final String SYMBOLS_KEY = "symbols"; //$NON-NLS-1$
    /**
     * the key used to identify the parameters in the string representation of the market data request
     */
    public static final String PARAMETERS_KEY = "parameters"; //$NON-NLS-1$
    /**
     * the delimiter used to distinguish between symbols in the string representation of the symbol collection
     */
    public static final String SYMBOL_DELIMITER = ","; //$NON-NLS-1$
    /**
     * Creates a <code>MarketDataRequest</code>.
     * 
     * <p>The <code>String</code> parameter should be a set of key/value pairs delimited
     * by {@link Util#KEY_VALUE_DELIMITER}.  The set of keys that this method understands
     * is as follows:
     * <ul>
     *   <li>{@link MarketDataRequestBuilder#SYMBOLS_KEY} - the symbols for which to request market data</li>
     *   <li>{@link MarketDataRequestBuilder#UNDERLYINGSYMBOLS_KEY} - the underlying symbols for which to request market data</li>
     *   <li>{@link MarketDataRequestBuilder#PROVIDER_KEY} - the provider from which to request market data</li>
     *   <li>{@link MarketDataRequestBuilder#CONTENT_KEY} - the content of the market data</li>
     *   <li>{@link MarketDataRequestBuilder#EXCHANGE_KEY} - the exchange for which to request market data</li>
     *   <li>{@link MarketDataRequestBuilder#ASSETCLASS_KEY} - the asset class for which to request market data</li>
     *   <li>{@link MarketDataRequestBuilder#PARAMETERS_KEY} - the parameters to add to the market data request</li>
     * </ul>
     * 
     * <p>Example:
     * <pre>
     * "symbols=GOOG,ORCL,MSFT:provider=marketcetera:content=TOP_OF_BOOK"
     * </pre>
     * 
     * <p>The key/value pairs are validated according to the rules established for each
     * component.  Extraneous key/value pairs, i.e., key/value pairs with a key that
     * does not match one of the above list are ignored.  Additional validation is performed
     * according to the rules defined at {@link Util#propertiesFromString(String)}.
     * 
     * <p>Validation is performed on each key/value pair as it is processed with respect to that
     * key/value pair only.  After all key/value pairs are processed and validated, a
     * second, comprehensive validation checks that all key/value pairs are valid with respect
     * to each other.
     *
     * @param inRequest a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the request cannot be constructed
     */
    public static MarketDataRequest newRequestFromString(String inRequest)
    {
        if(inRequest.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Properties props = Util.propertiesFromString(inRequest);
        Map<String,String> sanitizedProps = new HashMap<String,String>();
        for(Object key : props.keySet()) {
            sanitizedProps.put(((String)key).toLowerCase().trim(),
                               ((String)props.get(key)).trim());
        }
        MarketDataRequestBuilder builder = newRequest();
        if(sanitizedProps.containsKey(SYMBOLS_KEY)) {
            builder.withSymbols(sanitizedProps.get(SYMBOLS_KEY).split(SYMBOL_DELIMITER));
        }
        if(sanitizedProps.containsKey(UNDERLYINGSYMBOLS_KEY)) {
            builder.withUnderlyingSymbols(sanitizedProps.get(UNDERLYINGSYMBOLS_KEY).split(SYMBOL_DELIMITER));
        }
        if(sanitizedProps.containsKey(PROVIDER_KEY)) {
            builder.withProvider(sanitizedProps.get(PROVIDER_KEY));
        }
        if(sanitizedProps.containsKey(CONTENT_KEY)) {
            builder.withContent(sanitizedProps.get(CONTENT_KEY).split(SYMBOL_DELIMITER));
        }
        if(sanitizedProps.containsKey(EXCHANGE_KEY)) {
            builder.withExchange(sanitizedProps.get(EXCHANGE_KEY));
        }
        if(sanitizedProps.containsKey(ASSETCLASS_KEY)) {
            builder.withAssetClass(sanitizedProps.get(ASSETCLASS_KEY));
        }
        if(sanitizedProps.containsKey(PARAMETERS_KEY)) {
            Properties params = Util.propertiesFromString(sanitizedProps.get(PARAMETERS_KEY));
            for(Map.Entry<Object,Object> entry : params.entrySet()) {
                String key = ((String)entry.getKey()).trim();
                String value = ((String)entry.getValue()).trim();
                builder.withParameter(key,
                                      value);
            }
        }
        return builder.create();
    }
    /**
     * Creates a new <code>MarketDataRequestBuilder</code>.
     *
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public static MarketDataRequestBuilder newRequest()
    {
        return new MarketDataRequestBuilder();
    }
    /**
     * Creates a {@link MarketDataRequest} with the builder's current values.
     *
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the object cannot be created
     */
    public MarketDataRequest create()
    {
        return new MarketDataRequest(marketdata);
    }
    /**
     * Adds the given symbols to the market data request. 
     *
     * <p>Either symbols or underlying symbols ({@link #withUnderlyingSymbols(String)} or
     * {@link #withUnderlyingSymbols(String[])}) must be specified and no default is provided.
     * 
     * <p>The given symbols replace any existing symbols.  If the given symbols are <code>null</code> 
     * or empty, any existing symbols are removed.
     * 
     * @param inSymbols a <code>String[]</code> value containing symbols to add to the request or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withSymbols(String... inSymbols)
    {
        if(inSymbols == null ||
           inSymbols.length == 0) {
            marketdata.setSymbols(EMPTY_SYMBOLS);
            return this;
        }
        return withSymbols(Arrays.asList(inSymbols));
    }
    /**
     * Adds the given symbols to the market data request. 
     *
     * <p>Either symbols or underlying symbols ({@link #withUnderlyingSymbols(String)} or
     * {@link #withUnderlyingSymbols(String[])}) must be specified and no default is provided.
     * 
     * <p>The given symbols replace any existing symbols.  If the given symbols are <code>null</code> 
     * or empty, any existing symbols are removed.
     * 
     * @param inSymbols a <code>Collection&lt;String&gt;</code> value containing symbols to add to the request or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withSymbols(Collection<String> inSymbols)
    {
        if(inSymbols == null ||
           inSymbols.isEmpty()) {
            marketdata.setSymbols(EMPTY_SYMBOLS);
        } else {
            marketdata.setSymbols(inSymbols);
        }
        return this;
    }
    /**
     * Adds the given symbols to the market data request. 
     *
     * <p>The symbols may be a single symbol or a series of symbols delimited by 
     * {@link MarketDataRequestBuilder#SYMBOL_DELIMITER}.
     * 
     * <p>Either symbols or underlying symbols ({@link #withUnderlyingSymbols(String)} or
     * {@link #withUnderlyingSymbols(String[])}) must be specified and no default is provided.
     * 
     * <p>The given symbols replace any existing symbols.  If the given symbols are <code>null</code> 
     * or empty, any existing symbols are removed.
     * 
     * @param inSymbols a <code>String</code> value containing symbols separated by {@link MarketDataRequestBuilder#SYMBOL_DELIMITER}
     *  to add to the request or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withSymbols(String inSymbols)
    {
        if(inSymbols == null ||
           inSymbols.length() == 0) {
            marketdata.setSymbols(EMPTY_SYMBOLS);
            return this; 
        }
        return withSymbols(inSymbols.split(SYMBOL_DELIMITER));
    }
    /**
     * Adds the given underlying symbols to the market data request. 
     *
     * <p>Either symbols ({@link #withSymbols(String)} or {@link #withSymbols(String[])}) or
     * underlying symbols must be specified and no default is provided. 
     * 
     * <p>The given underlying symbols replace any existing underlying symbols.  If the given underlying symbols are <code>null</code> 
     * or empty, any existing underlying symbols are removed.
     * 
     * @param inUnderlyingSymbols a <code>String[]</code> value containing underlying symbols to add to the request or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withUnderlyingSymbols(String... inUnderlyingSymbols)
    {
        if(inUnderlyingSymbols == null ||
           inUnderlyingSymbols.length == 0) {
            marketdata.setUnderlyingSymbols(EMPTY_SYMBOLS);
        } else {
            Collection<String> symbols = new ArrayList<String>();
            for(String symbol : inUnderlyingSymbols) {
                symbols.add(symbol);
            }
            marketdata.setUnderlyingSymbols(symbols);
        }
        return this;
    }
    /**
     * Adds the given underlying symbols to the market data request. 
     *
     * <p>Either symbols or underlying symbols ({@link #withUnderlyingSymbols(String)} or
     * {@link #withUnderlyingSymbols(String[])}) must be specified and no default is provided.
     * 
     * <p>The given underlying symbols replace any existing underlying symbols.  If the given underlying symbols are <code>null</code> 
     * or empty, any existing underlying symbols are removed.
     * 
     * @param inUnderlyingSymbols a <code>Collection&lt;String&gt;</code> value containing underlying symbols to add to the 
     *  request or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withUnderlyingSymbols(Collection<String> inUnderlyingSymbols)
    {
        if(inUnderlyingSymbols == null ||
           inUnderlyingSymbols.isEmpty()) {
            marketdata.setUnderlyingSymbols(EMPTY_SYMBOLS);
        } else {
            marketdata.setUnderlyingSymbols(inUnderlyingSymbols);
        }
        return this;
    }
    /**
     * Adds the given underlying symbols to the market data request. 
     *
     * <p>The underlying symbols may be a single symbol
     * or a series of symbols delimited by {@link MarketDataRequestBuilder#SYMBOL_DELIMITER}.
     * 
     * <p>Either symbols ({@link #withSymbols(String)} or {@link #withSymbols(String[])}) or
     * underlying symbols must be specified and no default is provided. 
     * 
     * <p>The given underlying symbols replace any existing underlying symbols.  If the given underlying symbols are <code>null</code> 
     * or empty, any existing underlying symbols are removed.
     * 
     * @param inUnderlyingSymbols a <code>String</code> value containing underlying symbols separated by 
     *  {@link MarketDataRequestBuilder#SYMBOL_DELIMITER} to add to the request or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the specified symbols result in an invalid request 
     */
    public MarketDataRequestBuilder withUnderlyingSymbols(String inUnderlyingSymbols)
    {
        if(inUnderlyingSymbols == null ||
           inUnderlyingSymbols.length() == 0) {
            marketdata.setUnderlyingSymbols(EMPTY_SYMBOLS);
        } else {
            withUnderlyingSymbols(inUnderlyingSymbols.split(SYMBOL_DELIMITER));
        }
        return this;
    }
    /**
     * Adds the given provider to the market data request.
     *
     * <p>The provider is not validated because the set of valid providers is
     * resolved at run-time.
     * 
     * <p>This attribute is required and no default is provided except under circumstances
     * where the provider can be inferred by context. 
     * 
     * @param inProvider a <code>String</code> value containing the provider from which to request data or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the specified provider results in an invalid request 
     */
    public MarketDataRequestBuilder withProvider(String inProvider)
    {
        marketdata.setProvider(inProvider);
        return this;
    }
    /**
     * Adds the given exchange to the market data request.
     *
     * <p>The exchange is not validated as the set of valid exchanges is dependent on the
     * provider and the provisioning within the domain of the services provided therein.
     * 
     * <p>This attribute is optional and no default is provided. 
     *
     * @param inExchange a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withExchange(String inExchange)
    {
        marketdata.setExchange(inExchange);
        return this;
    }
    /**
     * Adds the given content to the market data request.
     *
     * <p>The given value must correspond to one or more valid {@link Content} values separated by 
     * {@link MarketDataRequestBuilder#SYMBOL_DELIMITER}.  Case is not considered.  If <code>null</code>
     * or empty, the current content is removed.
     * 
     * <p>This attribute is required.  If unspecified, the default value is {@link Content#TOP_OF_BOOK}. 
     *
     * @param inContent a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the specified content results in an invalid request 
     */
    public MarketDataRequestBuilder withContent(String inContent)
    {
        if(inContent == null ||
           inContent.length() == 0) {
            marketdata.setContent(EMPTY_CONTENT);
        } else {
            withContent(inContent.split(SYMBOL_DELIMITER));
        }
        return this;
    }
    /**
     * Adds the given content to the market data request.
     *
     * <p>The given content value must not be null.  This attribute is required and no
     * default is provided.
     *
     * @param inContent a <code>Content[]</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the specified content results in an invalid request 
     */
    public MarketDataRequestBuilder withContent(Content...inContent)
    {
        if(inContent == null ||
           inContent.length == 0) {
            marketdata.setContent(EMPTY_CONTENT);
        } else {
            Collection<Content> contents = new ArrayList<Content>();
            for(Content content : inContent) {
                contents.add(content);
            }
            marketdata.setContent(contents);
        }
        return this;
    }
    /**
     * Adds the given content to the market data request. 
     *
     * <p>Either symbols or underlying symbols ({@link #withUnderlyingSymbols(String)} or
     * {@link #withUnderlyingSymbols(String[])}) must be specified and no default is provided.
     * 
     * <p>The given content replaces the existing content.  If the given content collection is <code>null</code> 
     * or empty, any existing content is removed.
     * 
     * @param inContent a <code>Collection&lt;Content&gt;</code> value containing content to add to the request or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withContent(Collection<Content> inContent)
    {
        if(inContent == null ||
           inContent.isEmpty()) {
            marketdata.setContent(EMPTY_CONTENT);
        } else {
            marketdata.setContent(inContent);
        }
        return this;
    }
    /**
     * Adds the given content to the market data request.
     *
     * <p>The given content value must not be null.  This attribute is required and no
     * default is provided.
     *
     * @param inContent a <code>String[]</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the specified content results in an invalid request 
     */
    public MarketDataRequestBuilder withContent(String...inContent)
    {
        if(inContent == null ||
           inContent.length == 0) {
            marketdata.setContent(EMPTY_CONTENT);
        } else {
            List<Content> newContents = new ArrayList<Content>();
            for(String contentString : inContent) {
                if(contentString == null) {
                    throw new IllegalArgumentException(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                              contentString).getText());
                }
                try {
                    // normally, validation is not done at time of attribute-set, but this is a special
                    //  case because the given strings must be translated to valid Contents
                    newContents.add(Content.valueOf(contentString.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                              contentString).getText());
                }
            }
            withContent(newContents.toArray(new Content[newContents.size()]));
        }
        return this;
    }
    /**
     * Adds the given asset class to the market data request.
     *
     * <p>This attribute is required.  If unspecified, the default value is {@link AssetClass#EQUITY}.
     * If set to <code>null</code>, the current asset class is removed.
     *
     * @param inAssetClass an <code>AssetClass</code> value or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withAssetClass(AssetClass inAssetClass)
    {
        marketdata.setAssetClass(inAssetClass);
        return this;
    }
    /**
     * Adds the given asset class to the market data request.
     *
     * <p>This attribute is required.  If
     * unspecified, the default value is {@link AssetClass#EQUITY}. If the given asset class
     * is <code>null</code> or empty, the current asset class is removed.
     *
     * @param inAssetClass a <code>String</code> value containing a string representation of
     *  an {@link AssetClass} or <code>null</code>
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the given <code>String</code> is not a valid asset class
     */
    public MarketDataRequestBuilder withAssetClass(String inAssetClass)
    {
        if(inAssetClass == null ||
           inAssetClass.isEmpty()) {
            marketdata.setAssetClass(null);
            return this;
        }
        try {
            // normally, validation is not done at time of attribute-set, but this is a special
            //  case because the given string must be translated to a valid Asset
            marketdata.setAssetClass(AssetClass.valueOf(inAssetClass.toUpperCase().trim()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(INVALID_ASSET_CLASS,
                                                                      inAssetClass).getText());
        }
        return this;
    }
    /**
     * Adds the given parameter name and value to the market data request.
     * <p>
     * See marketdata providers documentation for supported parameters.
     * Parameters not supported by a marketdata provider will be ignored.
     * <p>
     * Both name and value must be non-null.  Name and value will have any
     * whitespace removed.
     *
     * @param inName the parameter name
     * @param inValue the parameter value
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequestBuilder withParameter(String inName,
                                                  String inValue)
    {
        if(inName == null ||
           inValue == null) {
            throw new NullPointerException();
        }
        marketdata.setParameter(inName.trim(),
                                inValue.trim());
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return marketdata.toString();
    }
    /**
     * Create a new MarketDataRequestBuilder instance.
     */
    private MarketDataRequestBuilder()
    {
        marketdata = new MarketDataRequestBean();
    }
    /**
     * contains the data to use to create the {@link MarketDataRequest}
     */
    private final MarketDataRequestBean marketdata;
    /**
     * empty collection used to indicate that existing symbols or underlying symbols should be removed
     */
    private static final Collection<String> EMPTY_SYMBOLS = Collections.emptyList();
    /**
     * empty collection used to indicate that existing content should be removed 
     */
    private static final Collection<Content> EMPTY_CONTENT = EnumSet.noneOf(Content.class);
}
