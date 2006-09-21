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
@Table(name = "equity_option_underlyings")
public class EquityOptionUnderlying extends TableBase {
    @Column(columnDefinition = "DECIMAL(20,5)")
    private BigDecimal quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name="equity_option_series_id")
    private EquityOptionSeries equityOptionSeries;

    @ManyToOne(optional = false)
    @JoinColumn(name="underlying_m_symbol_id")
    private MSymbol underlyingMSymbol;

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public EquityOptionSeries getEquityOptionSeries() {
        return equityOptionSeries;
    }

    public void setEquityOptionSeries(EquityOptionSeries equityOptionSeries) {
        this.equityOptionSeries = equityOptionSeries;
    }

    public MSymbol getUnderlyingMSymbol() {
        return underlyingMSymbol;
    }

    public void setUnderlyingMSymbol(MSymbol underlyingMSymbol) {
        this.underlyingMSymbol = underlyingMSymbol;
    }
}
