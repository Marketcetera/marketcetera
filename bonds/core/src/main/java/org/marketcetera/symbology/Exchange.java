package org.marketcetera.symbology;

import org.marketcetera.core.ClassVersion;

import java.util.Date;

/**
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class Exchange
{
    String country;
    String countryCode;
    String marketIdentifierCode;
    String institutionName;
    String otherAcronym;
    String city;
    String website;
    Date dateAdded;


    public Exchange(String country, String countryCode, String marketIdentifierCode, String institutionName, String otherAcronym, String city, String website, Date dateAdded) {
        this.country = country;
        this.countryCode = countryCode;
        this.marketIdentifierCode = marketIdentifierCode;
        this.institutionName = institutionName;
        this.otherAcronym = otherAcronym;
        this.city = city;
        this.website = website;
        this.dateAdded = dateAdded;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getMarketIdentifierCode() {
        return marketIdentifierCode;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public String getOtherAcronym() {
        return otherAcronym;
    }

    public String getCity() {
        return city;
    }

    public String getWebsite() {
        return website;
    }

    public Date getDateAdded() {
        return dateAdded;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof String){
            return obj.equals(getMarketIdentifierCode());
        } else if (obj instanceof Exchange){
            return ((Exchange)obj).getMarketIdentifierCode().equals(getMarketIdentifierCode());
        }
        return false;
    }
}
