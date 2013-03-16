package org.marketcetera.core.trade;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

/* $License$ */
/**
 * A uniqueID for every report received by the system. The report IDs
 * increase monotonically with every received report
 *
 * @version $Id$
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportID implements Serializable, Comparable<ReportID> {
    @Override
    public int compareTo(ReportID inReportID) {
        if(inReportID == null) {
            throw new NullPointerException();
        }
        return mValue == inReportID.mValue
                ? 0
                : mValue < inReportID.mValue
                ? -1
                :1;
    }
    @Override
    public String toString() {
        return String.valueOf(mValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportID reportID = (ReportID) o;

        return mValue == reportID.mValue;
    }

    @Override
    public int hashCode() {
        return (int) (mValue ^ (mValue >>> 32));
    }

    /**
     * Returns the value underlying this key.
     *
     * @return the value underlying this key.
     */
    public long longValue() {
        return mValue;
    }
    /**
     * Creates an instance.
     *
     * @param inValue the reportID value.
     */
    public ReportID(long inValue) {
        mValue = inValue;
    }

    /**
     * No arg constructor for JAXB
     */
    ReportID() {
        mValue = -1;
    }

    @XmlValue
    private final long mValue;
    private static final long serialVersionUID = 1L;
}
