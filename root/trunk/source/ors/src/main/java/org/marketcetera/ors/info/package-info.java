/**
 * <p>Information stores.</p>
 *
 * <p>Information stores manage the storage and retrieval of system,
 * session, and request information.</p>
 *
 * <p>All information stores are organized in a map-oriented
 * fashion. Null values are allowed but null keys are not; if a null
 * key is supplied to any store method, an {@link
 * org.marketcetera.ors.info.InfoRuntimeException} is thrown.</p>
 *
 * <p>Stores are nested: a request store {@link
 * org.marketcetera.ors.info.RequestInfo} is contained in a session
 * store {@link org.marketcetera.ors.info.SessionInfo}, and that in
 * turn is contained in the (usually singleton) system store {@link
 * org.marketcetera.ors.info.SystemInfo}.</p>
 *
 * <p>Stores have names and paths. A unique name for a store is
 * typically generated when the store is created, and includes a
 * monotonically inceasing integer specific to each store class. The
 * path of a store includes its own name and those of the stores that
 * contain it. This naming scheme makes it easy to understand how
 * requests and sessions relate to each other.</p>
 *
 * <p>Stores emit log information (at debug level, with the category
 * being the name of the store's class) when their contents are
 * queried or modified. These messages include the store's path,
 * thereby providing the necessary context to correlate simultaneous
 * actions.</p>
 *
 * <p>There is minimal provision for multi-threaded access to a
 * store. While the store's information will not get corrupted under
 * multi-threaded access, it is nevertheless possible for certain
 * operations to behave in an unexpected manner: for example, if
 * {@link
 * org.marketcetera.ors.info.ReadWriteInfo#setValueIfUnset(String,Object)}
 * is called by two threads concurrently upon the same nonexistent
 * key, then both calls may succeed; with external synchronization,
 * should it be provided by the stores' user, only the first call
 * would succeed.</p>
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

package org.marketcetera.ors.info;
