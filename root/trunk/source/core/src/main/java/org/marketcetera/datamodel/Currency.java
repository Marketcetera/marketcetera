package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
@Entity
@Table(name = "currencies")
public class Currency extends TableBase {

    @Column(name = "alpha_code", columnDefinition = "varchar(3)")
    private String alphaCode;
    @Column(name = "numeric_code")
    private String numericCode;
    private String description;
    private boolean obsolete;

    public Currency() {
    }

    public String getAlphaCode() {
        return alphaCode;
    }

    public void setAlphaCode(String alphaCode) {
        this.alphaCode = alphaCode;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getObsolete() {
        return obsolete;
    }

    public void setObsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }
}
