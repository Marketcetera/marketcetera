package org.marketcetera.metrics.dao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.marketcetera.persist.EntityBase;

/* $License$ */

/**
 * Provides a persistent metric implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity(name="Metric")
@Table(name="metrics")
public class PersistentMetric
        extends EntityBase
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the timestamp value.
     *
     * @return a <code>Date</code> value
     */
    public Date getTimestamp()
    {
        return timestamp;
    }
    /**
     * Sets the timestamp value.
     *
     * @param a <code>Date</code> value
     */
    public void setTimestamp(Date inTimestamp)
    {
        timestamp = inTimestamp;
    }
    /**
     * Get the type value.
     *
     * @return a <code>MetricType</code> value
     */
    public MetricType getType()
    {
        return type;
    }
    /**
     * Sets the type value.
     *
     * @param a <code>MetricType</code> value
     */
    public void setType(MetricType inType)
    {
        type = inType;
    }
    /**
     * Get the count value.
     *
     * @return an <code>int</code> value
     */
    public int getCount()
    {
        return count;
    }
    /**
     * Sets the count value.
     *
     * @param an <code>int</code> value
     */
    public void setCount(int inCount)
    {
        count = inCount;
    }
    /**
     * Get the mean value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMean()
    {
        return mean;
    }
    /**
     * Sets the mean value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setMean(BigDecimal inMean)
    {
        mean = inMean;
    }
    /**
     * Get the m1 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getM1()
    {
        return m1;
    }
    /**
     * Sets the m1 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setM1(BigDecimal inM1)
    {
        m1 = inM1;
    }
    /**
     * Get the m5 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getM5()
    {
        return m5;
    }
    /**
     * Sets the m5 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setM5(BigDecimal inM5)
    {
        m5 = inM5;
    }
    /**
     * Get the m15 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getM15()
    {
        return m15;
    }
    /**
     * Sets the m15 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setM15(BigDecimal inM15)
    {
        m15 = inM15;
    }
    /**
     * Get the min value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMin()
    {
        return min;
    }
    /**
     * Sets the min value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setMin(BigDecimal inMin)
    {
        min = inMin;
    }
    /**
     * Get the max value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMax()
    {
        return max;
    }
    /**
     * Sets the max value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setMax(BigDecimal inMax)
    {
        max = inMax;
    }
    /**
     * Get the stdDev value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getStdDev()
    {
        return stdDev;
    }
    /**
     * Sets the stdDev value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setStdDev(BigDecimal inStdDev)
    {
        stdDev = inStdDev;
    }
    /**
     * Get the median value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMedian()
    {
        return median;
    }
    /**
     * Sets the median value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setMedian(BigDecimal inMedian)
    {
        median = inMedian;
    }
    /**
     * Get the p75 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getP75()
    {
        return p75;
    }
    /**
     * Sets the p75 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setP75(BigDecimal inP75)
    {
        p75 = inP75;
    }
    /**
     * Get the p95 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getP95()
    {
        return p95;
    }
    /**
     * Sets the p95 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setP95(BigDecimal inP95)
    {
        p95 = inP95;
    }
    /**
     * Get the p98 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getP98()
    {
        return p98;
    }
    /**
     * Sets the p98 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setP98(BigDecimal inP98)
    {
        p98 = inP98;
    }
    /**
     * Get the p99 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getP99()
    {
        return p99;
    }
    /**
     * Sets the p99 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setP99(BigDecimal inP99)
    {
        p99 = inP99;
    }
    /**
     * Get the p999 value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getP999()
    {
        return p999;
    }
    /**
     * Sets the p999 value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setP999(BigDecimal inP999)
    {
        p999 = inP999;
    }
    /**
     * Get the meanRate value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMeanRate()
    {
        return meanRate;
    }
    /**
     * Sets the meanRate value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setMeanRate(BigDecimal inMeanRate)
    {
        meanRate = inMeanRate;
    }
    /**
     * Get the rateUnit value.
     *
     * @return a <code>String</code> value
     */
    public String getRateUnit()
    {
        return rateUnit;
    }
    /**
     * Sets the rateUnit value.
     *
     * @param a <code>String</code> value
     */
    public void setRateUnit(String inRateUnit)
    {
        rateUnit = inRateUnit;
    }
    /**
     * Get the durationUnit value.
     *
     * @return a <code>String</code> value
     */
    public String getDurationUnit()
    {
        return durationUnit;
    }
    /**
     * Sets the durationUnit value.
     *
     * @param a <code>String</code> value
     */
    public void setDurationUnit(String inDurationUnit)
    {
        durationUnit = inDurationUnit;
    }
    /**
     * Get the hour value.
     *
     * @return an <code>int</code> value
     */
    public int getHour()
    {
        return hour;
    }
    /**
     * Sets the hour value.
     *
     * @param an <code>int</code> value
     */
    public void setHour(int inHour)
    {
        hour = inHour;
    }
    /**
     * Get the minute value.
     *
     * @return an <code>int</code> value
     */
    public int getMinute()
    {
        return minute;
    }
    /**
     * Sets the minute value.
     *
     * @param an <code>int</code> value
     */
    public void setMinute(int inMinute)
    {
        minute = inMinute;
    }
    /**
     * Get the second value.
     *
     * @return an <code>int</code> value
     */
    public int getSecond()
    {
        return second;
    }
    /**
     * Sets the second value.
     *
     * @param an <code>int</code> value
     */
    public void setSecond(int inSecond)
    {
        second = inSecond;
    }
    /**
     * Get the millis value.
     *
     * @return an <code>int</code> value
     */
    public int getMillis()
    {
        return millis;
    }
    /**
     * Sets the millis value.
     *
     * @param an <code>int</code> value
     */
    public void setMillis(int inMillis)
    {
        millis = inMillis;
    }
    /**
     * metric name value
     */
    @Column(name="name",nullable=false)
    private String name;
    /**
     * metric timestamp value
     */
    @Column(name="metric_timestamp",nullable=false)
    private Date timestamp;
    /**
     * metric type value
     */
    @Enumerated(EnumType.STRING)
    @Column(name="type",nullable=false)
    private MetricType type;
    /**
     * metric count value
     */
    @Column(name="count",nullable=true)
    private int count;
    /**
     * metric mean value
     */
    @Column(name="mean",nullable=true)
    private BigDecimal mean;
    /**
     * metric m1 value
     */
    @Column(name="m1",nullable=true)
    private BigDecimal m1;
    /**
     * metric m5 value
     */
    @Column(name="m5",nullable=true)
    private BigDecimal m5;
    /**
     * metric m15 value
     */
    @Column(name="m15",nullable=true)
    private BigDecimal m15;
    /**
     * metric min value
     */
    @Column(name="min",nullable=true)
    private BigDecimal min;
    /**
     * metric max value
     */
    @Column(name="max",nullable=true)
    private BigDecimal max;
    /**
     * metric standard deviation value
     */
    @Column(name="std_dev",nullable=true)
    private BigDecimal stdDev;
    /**
     * metric median value
     */
    @Column(name="median",nullable=true)
    private BigDecimal median;
    /**
     * metric p75 value
     */
    @Column(name="p75",nullable=true)
    private BigDecimal p75;
    /**
     * metric p95 value
     */
    @Column(name="p95",nullable=true)
    private BigDecimal p95;
    /**
     * metric p98 value
     */
    @Column(name="p98",nullable=true)
    private BigDecimal p98;
    /**
     * metric p99 value
     */
    @Column(name="p99",nullable=true)
    private BigDecimal p99;
    /**
     * metric p999 value
     */
    @Column(name="p999",nullable=true)
    private BigDecimal p999;
    /**
     * metric mean rate value
     */
    @Column(name="mean_rate",nullable=true)
    private BigDecimal meanRate;
    /**
     * metric rate unit value
     */
    @Column(name="rate_unit",nullable=true)
    private String rateUnit;
    /**
     * metric duration unit value
     */
    @Column(name="duration_unit",nullable=true)
    private String durationUnit;
    /**
     * metric hour value
     */
    @Column(name="hour",nullable=false)
    private int hour;
    /**
     * metric minute value
     */
    @Column(name="minute",nullable=false)
    private int minute;
    /**
     * metric second value
     */
    @Column(name="second",nullable=false)
    private int second;
    /**
     * metric millis value
     */
    @Column(name="millis",nullable=false)
    private int millis;
    private static final long serialVersionUID = 4678276112801771280L;
}
