package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "dividends")
public class Dividend extends TableBase {
    @ManyToOne(optional = false)
    @JoinColumn(name="equity_id", unique = false)
    private Equity equity;

    @Column(columnDefinition = "DECIMAL(20,5)")
    private BigDecimal amount;

    @ManyToOne(optional = false)
    @JoinColumn(name="currency_id", unique = false)
    private Currency currency;

    @Column(name = "announce_date")
    private Date announceDate;

    @Column(name = "ex_date")
    private Date exDate;

    @Column(name = "payable_date")
    private Date payableDate;

    @Column(nullable = true)
    private char status;
    private String description;

    public Equity getEquity() {
        return equity;
    }

    public void setEquity(Equity equity) {
        this.equity = equity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getAnnounceDate() {
        return announceDate;
    }

    public void setAnnounceDate(Date announceDate) {
        this.announceDate = announceDate;
    }

    public Date getExDate() {
        return exDate;
    }

    public void setExDate(Date exDate) {
        this.exDate = exDate;
    }

    public Date getPayableDate() {
        return payableDate;
    }

    public void setPayableDate(Date payableDate) {
        this.payableDate = payableDate;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
