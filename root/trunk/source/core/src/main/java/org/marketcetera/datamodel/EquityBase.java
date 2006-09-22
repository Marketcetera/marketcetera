package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;

/**
 * Base class for modeling equity/derivatives that all point to an m_symbols table
 * basically, this exists for the foreign key code
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@MappedSuperclass
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
/*package */ abstract class EquityBase extends TableBase {
    @OneToOne
    @JoinColumn(name="m_symbol_id")
    private MSymbol mSymbol;

    protected EquityBase() {
    }

    protected EquityBase(MSymbol inSymbol) {
        this.mSymbol = inSymbol;
    }

    public MSymbol getMSymbol() {
        return mSymbol;
    }

    public void setMSymbol(MSymbol mSymbol) {
        this.mSymbol = mSymbol;
    }
}
