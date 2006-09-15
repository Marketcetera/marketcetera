package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "equity_option_series")
public class EquityOptionSeries extends EquityBase {
    @Column(name = "cash_deliverable", columnDefinition = "DECIMAL(20,5)")
    private BigDecimal cashDeliverable;

    @OneToOne(optional = false)
    @JoinColumn(name="cash_deliverable_currency_id")
    private Currency cashDeliverableCurrencyID;

    public BigDecimal getCashDeliverable() {
        return cashDeliverable;
    }

    public void setCashDeliverable(BigDecimal cashDeliverable) {
        this.cashDeliverable = cashDeliverable;
    }

    public Currency getCashDeliverableCurrencyID() {
        return cashDeliverableCurrencyID;
    }

    public void setCashDeliverableCurrencyID(Currency cashDeliverableCurrencyID) {
        this.cashDeliverableCurrencyID = cashDeliverableCurrencyID;
    }
}
