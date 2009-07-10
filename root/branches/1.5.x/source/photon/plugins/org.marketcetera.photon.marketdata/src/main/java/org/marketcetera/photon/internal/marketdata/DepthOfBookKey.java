package org.marketcetera.photon.internal.marketdata;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Key for depth of book data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class DepthOfBookKey extends Key<MDDepthOfBook> {

	public static final Set<Content> VALID_PRODUCTS = Sets.immutableEnumSet(Content.LEVEL_2,
			Content.TOTAL_VIEW, Content.OPEN_BOOK);
	private final Content mProduct;

	/**
	 * Constructor.
	 * 
	 * @param symbol
	 *            the symbol
	 * @param product
	 *            the depth of book product, one of {@link Content#LEVEL_2},
	 *            {@link Content#TOTAL_VIEW}, {@link Content#OPEN_BOOK}
	 * @throws IllegalArgumentException
	 *             if the product is not one of the available products listed above
	 */
	public DepthOfBookKey(String symbol, Content product) {
		super(symbol);
		Validate.notNull(product);
		Validate.isTrue(VALID_PRODUCTS.contains(product));
		mProduct = product;
	}

	/**
	 * Return the depth of book product.
	 * 
	 * @return the depth of book product
	 */
	public Content getProduct() {
		return mProduct;
	}

	@Override
	protected void enhanceHashCode(HashCodeBuilder builder) {
		builder.append(mProduct);
	}

	@Override
	protected void refineEquals(EqualsBuilder builder, Key<?> otherKey) {
		// super class guarantees this so we can safely cast to DepthOfBookKey below
		assert getClass() == otherKey.getClass();
		builder.append(mProduct, ((DepthOfBookKey) otherKey).mProduct);
	}

}