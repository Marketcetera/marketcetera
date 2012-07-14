/*
 * AccountID.java
 *
 * Created on April 18, 2005, 11:28 AM
 */

package org.marketcetera.core;



/**
 * This simple subclass of InternalID is used to identify accounts. It
 * also has an optional account nickname that can be used for human readability
 * or to group associated accounts like long/short accounts.
 * @author gmiller
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class AccountID extends InternalID {
    private final String mAccountNickname;
    /**
     * Create a new account id with the specified ID and nickname
     * @param accountID the ID
     * @param accountNickname the nickname
     */
    public AccountID(String accountID, String accountNickname) {
        super(accountID);
        if (accountNickname == null)
            mAccountNickname = "";  //$NON-NLS-1$
        else
            mAccountNickname = accountNickname;
    }
    /**
     * Create a new account ID without a nickname
     * @param accountID the account id
     */
    public AccountID(String accountID) {
        super(accountID);
        mAccountNickname = "";  //$NON-NLS-1$
    }

    /**
     * Gets just the nickname part of the account ID
     * @return The nickname associated with the account
     */
    public String getAccountNickname() {
        return mAccountNickname;
    }

}
