package org.marketcetera.photon.internal.marketdata;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.marketcetera.marketdata.Content;
import org.marketcetera.trade.Instrument;
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
public class DepthOfBookKey
        extends Key
{

	public static final Set<Content> VALID_PRODUCTS = Sets.immutableEnumSet(Content.LEVEL_2,Content.TOTAL_VIEW,Content.OPEN_BOOK,Content.BBO10);
	private final Content mProduct;

	/**
	 * Constructor.
	 * 
	 * @param instrument
	 *            the instrument
	 * @param product
	 *            the depth of book product, one of {@link Content#LEVEL_2},
	 *            {@link Content#TOTAL_VIEW}, {@link Content#OPEN_BOOK}
	 * @throws IllegalArgumentException
	 *             if the product is not one of the available products listed above
	 */
	public DepthOfBookKey(final Instrument instrument, final Content product) {
		super(instrument);
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
}