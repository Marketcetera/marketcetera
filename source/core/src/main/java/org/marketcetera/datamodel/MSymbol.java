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
@Table(name = "m_symbols")
public class MSymbol extends TableBase {
    @Column(columnDefinition = "varchar(30)")
    private String root;
    @Column(columnDefinition = "varchar(30)")
    private String bloomberg;
    @Column(columnDefinition = "char(12)")
    private String isin;
    @Column(columnDefinition = "varchar(30)")
    private String reuters;

    public MSymbol() {
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getBloomberg() {
        return bloomberg;
    }

    public void setBloomberg(String bloomberg) {
        this.bloomberg = bloomberg;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getReuters() {
        return reuters;
    }

    public void setReuters(String reuters) {
        this.reuters = reuters;
    }
}
