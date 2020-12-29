package org.marketcetera.eventbus.server;

import com.espertech.esper.runtime.client.EPStatement;

/* $License$ */

/**
 * Contains information related to a submitted Esper query.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EsperQueryMetaData
{
    /**
     * Create a new EsperQueryMetaData instance.
     *
     * @param inStatementId a <code>String</code> value
     * @param inStatement an <code>EPStatement</code> value
     */
    EsperQueryMetaData(String inStatementId,
                       EPStatement inStatement)
    {
        statementId = inStatementId;
        esperStatement = inStatement;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("EsperQueryMetaData [queryId=").append(statementId).append("]");
        return builder.toString();
    }
    /**
     * Get the statementId value.
     *
     * @return a <code>String</code> value
     */
    public String getStatementId()
    {
        return statementId;
    }
    /**
     * Sets the statementId value.
     *
     * @param inStatementId a <code>String</code> value
     */
    public void setStatementId(String inStatementId)
    {
        statementId = inStatementId;
    }
    /**
     * Get the esperStatement value.
     *
     * @return an <code>EPStatement</code> value
     */
    public EPStatement getEsperStatement()
    {
        return esperStatement;
    }
    /**
     * Sets the esperStatement value.
     *
     * @param inEsperStatement an <code>EPStatement</code> value
     */
    public void setEsperStatement(EPStatement inEsperStatement)
    {
        esperStatement = inEsperStatement;
    }
    /**
     * statement id value
     */
    private String statementId;
    /**
     * Esper statement valu
     */
    private EPStatement esperStatement;
}
