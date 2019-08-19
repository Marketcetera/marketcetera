package org.marketcetera.ors.filters;

import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;

/* $License$ */

/**
 * Modifies <code>Logon</code> FIX messages to support username and/or password.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LogonMessageModifier
        implements MessageModifier
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
     */
    @Override
    public boolean modify(ServerFixSession inServerFixSession,
                          Message inMessage)
    {
        if(FIXMessageUtil.isLogon(inMessage)) {
            if(username != null) {
                inMessage.setField(new quickfix.field.Username(username));
            }
            if(password != null) {
                inMessage.setField(new quickfix.field.Password(password));
            }
            if(encryptMethod != null) {
                inMessage.setField(new quickfix.field.EncryptMethod(encryptMethod));
            }
            if(testMessageIndicator != null) {
                inMessage.setField(new quickfix.field.TestMessageIndicator(testMessageIndicator));
            }
        }
        return true;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /**
     * Get the encryptMethod value.
     *
     * @return an <code>Integer</code> value
     */
    public Integer getEncryptMethod()
    {
        return encryptMethod;
    }
    /**
     * Sets the encryptMethod value.
     *
     * @param inEncryptMethod an <code>int</code> value
     */
    public void setEncryptMethod(int inEncryptMethod)
    {
        encryptMethod = inEncryptMethod;
    }
    /**
     * Get the testMessageIndicator value.
     *
     * @return a <code>Boolean</code> value
     */
    public Boolean getTestMessageIndicator()
    {
        return testMessageIndicator;
    }
    /**
     * Sets the testMessageIndicator value.
     *
     * @param inTestMessageIndicator a <code>Boolean</code> value
     */
    public void setTestMessageIndicator(Boolean inTestMessageIndicator)
    {
        testMessageIndicator = inTestMessageIndicator;
    }
    /**
     * username value or <code>null</code>
     */
    private String username;
    /**
     * password value or <code>null</code>
     */
    private String password;
    /**
     * encrypt method value or <code>null</code> to omit
     */
    private Integer encryptMethod = null;
    /**
     * indicates whether the session is test or production or <code>null</code> to omit
     */
    private Boolean testMessageIndicator = null;
}
