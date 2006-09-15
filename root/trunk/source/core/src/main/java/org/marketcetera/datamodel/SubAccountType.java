package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "sub_account_types")
public class SubAccountType extends TableBase {
    @Column(name = "accounting_account_type")
    private char accountingAccountType;

    private String description;

    public char getAccountingAccountType() {
        return accountingAccountType;
    }

    public void setAccountingAccountType(char accountingAccountType) {
        this.accountingAccountType = accountingAccountType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
