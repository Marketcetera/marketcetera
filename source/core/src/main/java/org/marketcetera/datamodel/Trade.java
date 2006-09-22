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
    public static enum AssetType { Equity('E'), EquityOption('O');
        private char type;
        AssetType(char inType)
        {
            type = inType;
        }
        public char getType() {return type; }
    }

    @Column(name = "asset_id", columnDefinition = "INT(11)")
    // todo: is this a FK somewhere?
    private long assetID;

    @Column(columnDefinition = "DECIMAL(20,5)")
    private BigDecimal quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "trade_type")
    private char tradeType;

    public Trade() {
    }

    public static enum TradeType { BasicTrade('T'), CorpAction('C'), ExerciseOrExpire('E'), Reconciliation('R');
        private char type;
        TradeType(char inType) {
            type = inType;
        }
        private char getType() { return type; }
    }


    public Trade(Journal journal, AssetType assetType, long assetID, BigDecimal quantity, Account account, TradeType tradeType) {
        this.journal = journal;
        this.assetType = assetType.getType();
        this.assetID = assetID;
        this.quantity = quantity;
        this.account = account;
        this.tradeType = tradeType.getType();
    }

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

    public long getAssetID() {
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
