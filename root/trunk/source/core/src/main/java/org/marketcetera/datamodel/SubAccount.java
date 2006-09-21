package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "sub_accounts")
public class SubAccount extends TableBase {
    @ManyToOne(optional = false)
    @JoinColumn(name="account_id")
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(name="sub_account_type_id")
    private SubAccountType subAccountType;

    private String description;


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public SubAccountType getSubAccountType() {
        return subAccountType;
    }

    public void setSubAccountType(SubAccountType subAccountType) {
        this.subAccountType = subAccountType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
