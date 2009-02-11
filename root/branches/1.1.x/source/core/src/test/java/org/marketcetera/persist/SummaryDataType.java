package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

/* $License$ */
/**
 * Summary view for the summary data type. Includes
 * all attributes except blob and clob. 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface SummaryDataType extends SummaryNDEntityBase {

    int getNumInt();

    float getNumFloat();

    double getNumDouble();

    BigInteger getBigInteger();

    BigDecimal getBigDecimal();

    java.sql.Date getDate();

    Time getTime();

    Timestamp getTimestamp();

    byte[] getBytes();

    DataTypes.TestEnum getTestEnum();

    DataTypes.SerializedTester getSerialTester();

}
