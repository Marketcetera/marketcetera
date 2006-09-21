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
@Table(name = "trades")
public class Trade extends TableBase {

    @ManyToOne(optional = false)
    @JoinColumn(name = "journal_id")
    private Journal journal;

    @Column(name = "asset_type", columnDefinition = "CHAR(2)")
    private char assetType;

    @Column(name = "asset_id", columnDefinition = "INT(11)")
    // todo: is this a FK somewhere?
    private int assetID;

    @Column(columnDefinition = "DECIMAL(20,5)")
    private BigDecimal quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "trade_type")
    private char tradeType;

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public char getAssetType() {
        return assetType;
    }

    public void setAssetType(char assetType) {
        this.assetType = assetType;
    }

    public int getAssetID() {
        return assetID;
    }

    public void setAssetID(int assetID) {
        this.assetID = assetID;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public char getTradeType() {
        return tradeType;
    }

    public void setTradeType(char tradeType) {
        this.tradeType = tradeType;
    }
}
