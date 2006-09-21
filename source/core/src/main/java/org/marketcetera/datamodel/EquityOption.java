package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "equity_options")
public class EquityOption extends EquityBase {
    @ManyToOne(optional = false)
    @JoinColumn(name="equity_option_series_id")
    private EquityOptionSeries equityOptionSeries;

    @Column(name = "expiration_date", columnDefinition = "DATE")
    private Date expirationDate;

    @Column(name = "strike_price", columnDefinition = "DECIMAL(20,5)")
    private BigDecimal strikePrice;

    @ManyToOne(optional = false)
    @JoinColumn(name="strike_price_currency_id")
    private Currency strikePriceCurrency;

    private char callPut;

    @Column(name = "exercise_type")
    private char exerciseType;

    public EquityOptionSeries getEquityOptionSeries() {
        return equityOptionSeries;
    }

    public void setEquityOptionSeries(EquityOptionSeries equityOptionSeries) {
        this.equityOptionSeries = equityOptionSeries;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(BigDecimal strikePrice) {
        this.strikePrice = strikePrice;
    }

    public Currency getStrikePriceCurrency() {
        return strikePriceCurrency;
    }

    public void setStrikePriceCurrency(Currency strikePriceCurrency) {
        this.strikePriceCurrency = strikePriceCurrency;
    }

    public char getCallPut() {
        return callPut;
    }

    public void setCallPut(char callPut) {
        this.callPut = callPut;
    }

    public char getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(char exerciseType) {
        this.exerciseType = exerciseType;
    }
}
