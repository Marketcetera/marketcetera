package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.*;

/**
 * Class representing the "Account" table in the Marketcetera data model
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "accounts")
public class Account extends TableBase {

    private String nickname;
    private String description;
    @Column(name = "institution_identifier")
    private String institutionIdentifier;   // aka bank account # at that institution

    public Account() {
    }

    public Account(String institutionIdentifier, String nickname, String description) {
        this.nickname = nickname;
        this.description = description;
        this.institutionIdentifier = institutionIdentifier;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstitutionIdentifier() {
        return institutionIdentifier;
    }

    public void setInstitutionIdentifier(String institutionIdentifier) {
        this.institutionIdentifier = institutionIdentifier;
    }

}
