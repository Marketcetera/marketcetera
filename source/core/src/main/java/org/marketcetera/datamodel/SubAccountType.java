package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

/**
 * Possible Values for accounting_account_type
 * <ol>
 * <li>A: asset		DEBIT=increase	CREDIT=decrease</li>
 * <li>L: liability		DEBIT=decrease	CREDIT=increase</li>
 * <li>E: expense		DEBIT=increase	CREDIT=decrease</li>
 * <li>R: revenue		DEBIT=decrease	CREDIT=increase</li>
 * </ol>
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "sub_account_types")
public class SubAccountType extends TableBase {

    /** Series of named constants for pre-canned account types */
    public static final String SHORT_TERM_INV = "Short Term Investment";
    public static final String CASH = "Cash";
    public static final String DIVIDENT_REVENUE = "Dividend Revenue";
    public static final String UNREALIZED_GAIN_LOSS = "Unrealized Gain/Loss";
    public static final String CHANGE_ON_CLOSE_OF_INV = "Change on Close of Investment";
    public static final String COMMISIONS = "Commissions";
    public static final String INTEREST_REV = "Interest Revenue";

    @Column(name = "accounting_account_type")
    private char accountingAccountType;

    public static enum AccountingType { Asset('A'), Liability('L'), Expense('E'), Revenue('R');
        private char type;
        AccountingType(char inType) { type = inType; }
        private char getType() { return type; }
    }

    private String description;

    public SubAccountType() {
    }

    public SubAccountType(AccountingType accountingType, String description) {
        this.accountingAccountType = accountingType.getType();
        this.description = description;
    }

    public char getAccountingAccountType() {
        return accountingAccountType;
    }

    public void setAccountingAccountType(AccountingType accountingType) {
        this.accountingAccountType = accountingType.getType();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
