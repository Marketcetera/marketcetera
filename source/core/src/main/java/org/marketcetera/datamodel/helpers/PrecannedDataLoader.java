package org.marketcetera.datamodel.helpers;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.datamodel.Currency;
import org.marketcetera.datamodel.SubAccountType;
import org.hibernate.Session;
import org.hibernate.Query;

/**
 * Loads a set of known pre-canned data into the database.
 * For example, load all the predefined currencies and sub-account types.
 *
 * This should only really be called at either app install time,
 * or during unit tests when the DB is cleaned.
 *
 * The assumption is that the session passed in hs already been initialized -
 * we don't do any transaction management in this method.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public final class PrecannedDataLoader {

    public static Currency USD;
    public static Currency GBP;
    public static Currency EUR;

    public static SubAccountType STI;
    public static SubAccountType CASH;
    public static SubAccountType DIV_REV;
    public static SubAccountType UNREALIZED_GAIN_LOSS;
    public static SubAccountType CHANGE_ON_CLOSE_OF_INV;
    public static SubAccountType COMMISIONS;
    public static SubAccountType INTEREST_REV;

    public static void loadAllCurrencies(Session inSession)
    {
        createOneCurrency(inSession, "AED","784","United Arab Emirates dirham", false);
        createOneCurrency(inSession, "AFN","971","Afghani", false);
        createOneCurrency(inSession, "ALL","008","Lek", false);
        createOneCurrency(inSession, "AMD","051","Armenian Dram", false);
        createOneCurrency(inSession, "ANG","532","Netherlands Antillian Guilder", false);
        createOneCurrency(inSession, "AOA","973","Kwanza", false);
        createOneCurrency(inSession, "ARS","032","Argentine Peso", false);
        createOneCurrency(inSession, "AUD","036","Australian Dollar", false);
        createOneCurrency(inSession, "AWG","533","Aruban Guilder", false);
        createOneCurrency(inSession, "AZN","944","Azerbaijanian Manat", false);
        createOneCurrency(inSession, "BAM","977","Convertible Marks", false);
        createOneCurrency(inSession, "BBD","052","Barbados Dollar", false);
        createOneCurrency(inSession, "BDT","050","Taka", false);
        createOneCurrency(inSession, "BGN","975","Bulgarian Lev", false);
        createOneCurrency(inSession, "BHD","048","Bahraini Dinar", false);
        createOneCurrency(inSession, "BIF","108","Burundian Franc", false);
        createOneCurrency(inSession, "BMD","060","Bermudian Dollar (customarily known as Bermuda Dollar)", false);
        createOneCurrency(inSession, "BND","096","Brunei Dollar", false);
        createOneCurrency(inSession, "BOB","068","Boliviano", false);
        createOneCurrency(inSession, "BOV","984","Bolivian Mvdol (Funds code)", false);
        createOneCurrency(inSession, "BRL","986","Brazilian Real", false);
        createOneCurrency(inSession, "BSD","044","Bahamian Dollar", false);
        createOneCurrency(inSession, "BTN","064","Ngultrum", false);
        createOneCurrency(inSession, "BWP","072","Pula", false);
        createOneCurrency(inSession, "BYR","974","Belarussian Ruble", false);
        createOneCurrency(inSession, "BZD","084","Belize Dollar", false);
        createOneCurrency(inSession, "CAD","124","Canadian Dollar", false);
        createOneCurrency(inSession, "CDF","976","Franc Congolais", false);
        createOneCurrency(inSession, "CHE","947","WIR Euro", false);
        createOneCurrency(inSession, "CHF","756","Swiss Franc", false);
        createOneCurrency(inSession, "CHW","948","WIR Franc", false);
        createOneCurrency(inSession, "CLF","990","Unidades de formento (Funds code)", false);
        createOneCurrency(inSession, "CLP","152","Chilean Peso", false);
        createOneCurrency(inSession, "CNY","156","Yuan Renminbi", false);
        createOneCurrency(inSession, "COP","170","Colombian Peso", false);
        createOneCurrency(inSession, "COU","970","Unidad de Valor Real", false);
        createOneCurrency(inSession, "CRC","188","Costa Rican Colon", false);
        createOneCurrency(inSession, "CSD","891","Serbian Dinar", false);
        createOneCurrency(inSession, "CUP","192","Cuban Peso", false);
        createOneCurrency(inSession, "CVE","132","Cape Verde Escudo", false);
        createOneCurrency(inSession, "CYP","196","Cyprus Pound", false);
        createOneCurrency(inSession, "CZK","203","Czech Koruna", false);
        createOneCurrency(inSession, "DJF","262","Djibouti Franc", false);
        createOneCurrency(inSession, "DKK","208","Danish Krone", false);
        createOneCurrency(inSession, "DOP","214","Dominican Peso", false);
        createOneCurrency(inSession, "DZD","012","Algerian Dinar", false);
        createOneCurrency(inSession, "EEK","233","Kroon", false);
        createOneCurrency(inSession, "EGP","818","Egyptian Pound", false);
        createOneCurrency(inSession, "ERN","232","Nakfa", false);
        createOneCurrency(inSession, "ETB","230","Ethiopian Birr", false);
        createOneCurrency(inSession, "EUR","978","Euro", false);
        createOneCurrency(inSession, "FJD","242","Fiji Dollar", false);
        createOneCurrency(inSession, "FKP","238","Falkland Islands Pound", false);
        createOneCurrency(inSession, "GBP","826","Pound Sterling", false);
        createOneCurrency(inSession, "GEL","981","Lari", false);
        createOneCurrency(inSession, "GHC","288","Cedi", false);
        createOneCurrency(inSession, "GIP","292","Gibraltar pound", false);
        createOneCurrency(inSession, "GMD","270","Dalasi", false);
        createOneCurrency(inSession, "GNF","324","Guinea Franc", false);
        createOneCurrency(inSession, "GTQ","320","Quetzal", false);
        createOneCurrency(inSession, "GYD","328","Guyana Dollar", false);
        createOneCurrency(inSession, "HKD","344","Hong Kong Dollar", false);
        createOneCurrency(inSession, "HNL","340","Lempira", false);
        createOneCurrency(inSession, "HRK","191","Croatian Kuna", false);
        createOneCurrency(inSession, "HTG","332","Haiti Gourde", false);
        createOneCurrency(inSession, "HUF","348","Forint", false);
        createOneCurrency(inSession, "IDR","360","Rupiah", false);
        createOneCurrency(inSession, "ILS","376","New Israeli Shekel", false);
        createOneCurrency(inSession, "INR","356","Indian Rupee", false);
        createOneCurrency(inSession, "IQD","368","Iraqi Dinar", false);
        createOneCurrency(inSession, "IRR","364","Iranian Rial", false);
        createOneCurrency(inSession, "ISK","352","Iceland Krona", false);
        createOneCurrency(inSession, "JMD","388","Jamaican Dollar", false);
        createOneCurrency(inSession, "JOD","400","Jordanian Dinar", false);
        createOneCurrency(inSession, "JPY","392","Japanese yen", false);
        createOneCurrency(inSession, "KES","404","Kenyan Shilling", false);
        createOneCurrency(inSession, "KGS","417","Som", false);
        createOneCurrency(inSession, "KHR","116","Riel", false);
        createOneCurrency(inSession, "KMF","174","Comoro Franc", false);
        createOneCurrency(inSession, "KPW","408","North Korean Won", false);
        createOneCurrency(inSession, "KRW","410","Won", false);
        createOneCurrency(inSession, "KWD","414","Kuwaiti Dinar", false);
        createOneCurrency(inSession, "KYD","136","Cayman Islands Dollar", false);
        createOneCurrency(inSession, "KZT","398","Tenge", false);
        createOneCurrency(inSession, "LAK","418","Kip", false);
        createOneCurrency(inSession, "LBP","422","Lebanese Pound", false);
        createOneCurrency(inSession, "LKR","144","Sri Lanka Rupee", false);
        createOneCurrency(inSession, "LRD","430","Liberian Dollar", false);
        createOneCurrency(inSession, "LSL","426","Loti", false);
        createOneCurrency(inSession, "LTL","440","Lithuanian Litas", false);
        createOneCurrency(inSession, "LVL","428","Latvian Lats", false);
        createOneCurrency(inSession, "LYD","434","Libyan Dinar", false);
        createOneCurrency(inSession, "MAD","504","Moroccan Dirham", false);
        createOneCurrency(inSession, "MDL","498","Moldovan Leu", false);
        createOneCurrency(inSession, "MGA","969","Malagasy Ariary", false);
        createOneCurrency(inSession, "MKD","807","Denar", false);
        createOneCurrency(inSession, "MMK","104","Kyat", false);
        createOneCurrency(inSession, "MNT","496","Tugrik", false);
        createOneCurrency(inSession, "MOP","446","Pataca", false);
        createOneCurrency(inSession, "MRO","478","Ouguiya", false);
        createOneCurrency(inSession, "MTL","470","Maltese Lira", false);
        createOneCurrency(inSession, "MUR","480","Mauritius Rupee", false);
        createOneCurrency(inSession, "MVR","462","Rufiyaa", false);
        createOneCurrency(inSession, "MWK","454","Kwacha", false);
        createOneCurrency(inSession, "MXN","484","Mexican Peso", false);
        createOneCurrency(inSession, "MXV","979","Mexican Unidad de Inversion (UDI) (Funds code) (Mexico)", false);
        createOneCurrency(inSession, "MYR","458","Malaysian Ringgit", false);
        createOneCurrency(inSession, "MZN","943","Metical", false);
        createOneCurrency(inSession, "NAD","516","Namibian Dollar", false);
        createOneCurrency(inSession, "NGN","566","Naira", false);
        createOneCurrency(inSession, "NIO","558","Cordoba Oro", false);
        createOneCurrency(inSession, "NOK","578","Norwegian Krone", false);
        createOneCurrency(inSession, "NPR","524","Nepalese Rupee", false);
        createOneCurrency(inSession, "NZD","554","New Zealand Dollar", false);
        createOneCurrency(inSession, "OMR","512","Rial Omani", false);
        createOneCurrency(inSession, "PAB","590","Balboa", false);
        createOneCurrency(inSession, "PEN","604","Nuevo Sol", false);
        createOneCurrency(inSession, "PGK","598","Kina", false);
        createOneCurrency(inSession, "PHP","608","Philippine Peso", false);
        createOneCurrency(inSession, "PKR","586","Pakistan Rupee", false);
        createOneCurrency(inSession, "PLN","985","Zloty", false);
        createOneCurrency(inSession, "PYG","600","Guarani", false);
        createOneCurrency(inSession, "QAR","634","Qatari Rial", false);
        createOneCurrency(inSession, "ROL","642","Old Romanian Leu", false);
        createOneCurrency(inSession, "RON","946","New Leu", false);
        createOneCurrency(inSession, "RUB","643","Russian Ruble", false);
        createOneCurrency(inSession, "RWF","646","Rwanda Franc", false);
        createOneCurrency(inSession, "SAR","682","Saudi Riyal", false);
        createOneCurrency(inSession, "SBD","090","Solomon Islands Dollar", false);
        createOneCurrency(inSession, "SCR","690","Seychelles Rupee", false);
        createOneCurrency(inSession, "SDD","736","Sudanese Dinar", false);
        createOneCurrency(inSession, "SEK","752","Swedish Krona", false);
        createOneCurrency(inSession, "SGD","702","Singapore Dollar", false);
        createOneCurrency(inSession, "SHP","654","Saint Helena Pound", false);
        createOneCurrency(inSession, "SIT","705","Tolar", false);
        createOneCurrency(inSession, "SKK","703","Slovak Koruna", false);
        createOneCurrency(inSession, "SLL","694","Leone", false);
        createOneCurrency(inSession, "SOS","706","Somali Shilling", false);
        createOneCurrency(inSession, "SRD","968","Surinam Dollar", false);
        createOneCurrency(inSession, "STD","678","Dobra", false);
        createOneCurrency(inSession, "SYP","760","Syrian Pound", false);
        createOneCurrency(inSession, "SZL","748","Lilangeni", false);
        createOneCurrency(inSession, "THB","764","Baht", false);
        createOneCurrency(inSession, "TJS","972","Somoni", false);
        createOneCurrency(inSession, "TMM","795","Manat", false);
        createOneCurrency(inSession, "TND","788","Tunisian Dinar", false);
        createOneCurrency(inSession, "TOP","776","Pa\"anga", false);
        createOneCurrency(inSession, "TRY","949","New Turkish Lira", false);
        createOneCurrency(inSession, "TTD","780","Trinidad and Tobago Dollar", false);
        createOneCurrency(inSession, "TWD","901","New Taiwan Dollar", false);
        createOneCurrency(inSession, "TZS","834","Tanzanian Shilling", false);
        createOneCurrency(inSession, "UAH","980","Hryvnia", false);
        createOneCurrency(inSession, "UGX","800","Uganda Shilling", false);
        createOneCurrency(inSession, "USD","840","US Dollar", false);
        createOneCurrency(inSession, "USN","997",null, false);
        createOneCurrency(inSession, "USS","998",null, false);
        createOneCurrency(inSession, "UYU","858","Peso Uruguayo", false);
        createOneCurrency(inSession, "UZS","860","Uzbekistan Som", false);
        createOneCurrency(inSession, "VEB","862","Venezuelan bolívar", false);
        createOneCurrency(inSession, "VND","704","Dong", false);
        createOneCurrency(inSession, "VUV","548","Vatu", false);
        createOneCurrency(inSession, "WST","882","Tala", false);
        createOneCurrency(inSession, "XAF","950","CFA Franc BEAC", false);
        createOneCurrency(inSession, "XAG","961","Silver (one Troy ounce)", false);
        createOneCurrency(inSession, "XAU","959","Gold (one Troy ounce)", false);
        createOneCurrency(inSession, "XBA","955","European Composite Unit (EURCO) (Bonds market unit)", false);
        createOneCurrency(inSession, "XBB","956","European Monetary Unit (E.M.U.-6) (Bonds market unit)", false);
        createOneCurrency(inSession, "XBC","957","European Unit of Account 9 (E.U.A.-9) (Bonds market unit)", false);
        createOneCurrency(inSession, "XBD","958","European Unit of Account 17 (E.U.A.-17) (Bonds market unit)", false);
        createOneCurrency(inSession, "XCD","951","East Caribbean Dollar", false);
        createOneCurrency(inSession, "XDR","960","Special Drawing Rights", false);
        createOneCurrency(inSession, "XFO",null,"Gold-Franc (Special settlement currency)", false);
        createOneCurrency(inSession, "XFU",null,"UIC Franc (Special settlement currency)", false);
        createOneCurrency(inSession, "XOF","952","CFA Franc BCEAO", false);
        createOneCurrency(inSession, "XPD","964","Palladium (one Troy ounce)", false);
        createOneCurrency(inSession, "XPF","953","CFP franc", false);
        createOneCurrency(inSession, "XPT","962","Platinum (one Troy ounce)", false);
        createOneCurrency(inSession, "XTS","963","Code reserved for testing purposes", false);
        createOneCurrency(inSession, "XXX","999","No currency", false);
        createOneCurrency(inSession, "YER","886","Yemeni Rial", false);
        createOneCurrency(inSession, "ZAR","710","Rand", false);
        createOneCurrency(inSession, "ZMK","894","Kwacha", false);
        createOneCurrency(inSession, "ZWD","942","Zimbabwe Dollar", false);
        createOneCurrency(inSession, "ADP","20","Andorran Peseta",true);
        createOneCurrency(inSession, "ATS","40","Austrian Schilling",true);
        createOneCurrency(inSession, "BEF","56","Belgian Franc",true);
        createOneCurrency(inSession, "DEM","276","Deutsche Mark",true);
        createOneCurrency(inSession, "ESP","724","Spanish Peseta",true);
        createOneCurrency(inSession, "FIM","246","Finnish Markka",true);
        createOneCurrency(inSession, "FRF","250","French Franc",true);
        createOneCurrency(inSession, "GRD","300","Greek Drachma",true);
        createOneCurrency(inSession, "IEP","372","Irish Pound",true);
        createOneCurrency(inSession, "ITL","380","Italian Lira",true);
        createOneCurrency(inSession, "LUF","442","Luxembourg Franc",true);
        createOneCurrency(inSession, "NLG","528","Netherlands Guilder",true);
        createOneCurrency(inSession, "PTE","620","Portuguese Escudo",true);
        createOneCurrency(inSession, "XEU","954","European Currency Unit",true);
        createOneCurrency(inSession, "ADF",null,"Andorran Franc",true);
        createOneCurrency(inSession, "AFA","4","Afghani",true);
        createOneCurrency(inSession, "ALK",null,"Albanian old lek",true);
        createOneCurrency(inSession, "AON","24","Angolan New Kwanza",true);
        createOneCurrency(inSession, "AOR","982","Angolan Kwanza Readjustado",true);
        createOneCurrency(inSession, "ARP",null,"Peso Argentino",true);
        createOneCurrency(inSession, "ARY",null,"Argentine peso",true);
        createOneCurrency(inSession, "AZM","31","Azerbaijani manat",true);
        createOneCurrency(inSession, "BEC","993","Belgian Franc (convertible)",true);
        createOneCurrency(inSession, "BEL","992","Belgian Franc (financial)",true);
        createOneCurrency(inSession, "BGJ",null,"Bulgarian lev A/52",true);
        createOneCurrency(inSession, "BGK",null,"Bulgarian lev A/62",true);
        createOneCurrency(inSession, "BGL","100","Bulgarian lev A/99",true);
        createOneCurrency(inSession, "BOP",null,"Bolivian peso",true);
        createOneCurrency(inSession, "BRB",null,"Brazilian cruzeiro",true);
        createOneCurrency(inSession, "BRC",null,"Brazilian cruzado",true);
        createOneCurrency(inSession, "CNX",null,"Chinese People\"s Bank dollar",true);
        createOneCurrency(inSession, "CSJ",null,"Czechoslovak koruna A/53",true);
        createOneCurrency(inSession, "CSK","200","Czechoslovak koruna",true);
        createOneCurrency(inSession, "DDM","278","mark der DDR (East Germany)",true);
        createOneCurrency(inSession, "ECS","218","Ecuador sucre",true);
        createOneCurrency(inSession, "ECV","983","Ecuador Unidad de Valor Constante (Funds code) (discontinued)",true);
        createOneCurrency(inSession, "EQE",null,"Equatorial Guinean ekwele",true);
        createOneCurrency(inSession, "ESA","996","Spanish peseta (account A)",true);
        createOneCurrency(inSession, "ESB","995","Spanish peseta (account B)",true);
        createOneCurrency(inSession, "GNE",null,"Guinean syli",true);
        createOneCurrency(inSession, "GWP","624","Guinea peso",true);
        createOneCurrency(inSession, "ILP",null,"Israeli pound",true);
        createOneCurrency(inSession, "ILR",null,"Israeli old shekel",true);
        createOneCurrency(inSession, "ISJ",null,"Icelandic old krona",true);
        createOneCurrency(inSession, "LAJ",null,"Lao kip - Pot Pol",true);
        createOneCurrency(inSession, "MAF",null,"Mali franc",true);
        createOneCurrency(inSession, "MGF","450","Malagasy franc",true);
        createOneCurrency(inSession, "MKN",null,"Macedonian denar A/93",true);
        createOneCurrency(inSession, "MVQ",null,"Maldive rupee",true);
        createOneCurrency(inSession, "MXP",null,"Mexican peso",true);
        createOneCurrency(inSession, "MZM","508","Metical",true);
        createOneCurrency(inSession, "PEH",null,"Peruvian sol",true);
        createOneCurrency(inSession, "PEI",null,"Peruvian inti",true);
        createOneCurrency(inSession, "PLZ","616","Polish zloty A/94",true);
        createOneCurrency(inSession, "ROK",null,"Romanian leu A/52",true);
        createOneCurrency(inSession, "RUR","810","Russian ruble",true);
        createOneCurrency(inSession, "SRG","740","Suriname guilder",true);
        createOneCurrency(inSession, "SUR",null,"Soviet Union ruble",true);
        createOneCurrency(inSession, "SVC","222","Salvadoran colón",true);
        createOneCurrency(inSession, "TJR","762","tjr",true);
        createOneCurrency(inSession, "TPE","626","Timor escudo",true);
        createOneCurrency(inSession, "TRL","792","Turkish lira A/05",true);
        createOneCurrency(inSession, "UAK","804","Ukrainian karbovanets",true);
        createOneCurrency(inSession, "UGW",null,"Ugandan old shilling",true);
        createOneCurrency(inSession, "UYN",null,"Uruguay old peso",true);
        createOneCurrency(inSession, "VNC",null,"Vietnamese old dong",true);
        createOneCurrency(inSession, "YDD","720","South Yemeni dinar",true);
        createOneCurrency(inSession, "YUD",null,"New Yugoslavian Dinar",true);
        createOneCurrency(inSession, "ZAL","991","South African financial rand (Funds code) (discontinued)",true);
        createOneCurrency(inSession, "ZRN","180","New Zaire",true);
        createOneCurrency(inSession, "ZRZ",null,"Zaire",true);
        createOneCurrency(inSession, "ZWC",null,"Zimbabwe Rhodesian dollar",true);
    }

    public static void loadSubAccountTypes(Session inSession)
    {
        createOneSubAccountType(inSession, SubAccountType.AccountingType.Asset, SubAccountType.SHORT_TERM_INV);
        createOneSubAccountType(inSession, SubAccountType.AccountingType.Asset, SubAccountType.CASH);
        createOneSubAccountType(inSession, SubAccountType.AccountingType.Revenue, SubAccountType.DIVIDENT_REVENUE);
        createOneSubAccountType(inSession, SubAccountType.AccountingType.Asset, SubAccountType.UNREALIZED_GAIN_LOSS);
        createOneSubAccountType(inSession, SubAccountType.AccountingType.Revenue, SubAccountType.CHANGE_ON_CLOSE_OF_INV);
        createOneSubAccountType(inSession, SubAccountType.AccountingType.Expense, SubAccountType.COMMISIONS);
        createOneSubAccountType(inSession, SubAccountType.AccountingType.Revenue, SubAccountType.INTEREST_REV);
    }

    private static SubAccountType createOneSubAccountType(Session inSession, SubAccountType.AccountingType inAcctingType, String inDesc)
    {
        SubAccountType type = new SubAccountType();
        type.setAccountingAccountType(inAcctingType);
        type.setDescription(inDesc);
        inSession.save(type);
        return type;
    }

    private static void createOneCurrency(Session inSession, String inAlphaCode, String inNumericCode, String inDesc,
                                          boolean inObsolete)
    {
        Currency cur = new Currency();
        cur.setAlphaCode(inAlphaCode);
        cur.setDescription(inDesc);
        cur.setNumericCode(inNumericCode);
        cur.setObsolete(inObsolete);
        inSession.save(cur);
    }

    /** Initializes the common-used currencies: {@link USD}, {@link #GBP}, {@link EUR} */
    public static void initializeCommon(Session inSession)
    {
        Query q = inSession.createQuery("from "+Currency.class.getSimpleName() +" c where c.alphaCode = :ac");
        USD = (Currency) q.setString("ac", "USD").uniqueResult();
        GBP = (Currency) q.setString("ac", "GBP").uniqueResult();
        EUR = (Currency) q.setString("ac", "EUR").uniqueResult();

        // load the SubAccount types
        q = inSession.createQuery("from " + SubAccountType.class.getSimpleName() + " s where s.description = :desc");
        STI = (SubAccountType) q.setString("desc", SubAccountType.SHORT_TERM_INV).uniqueResult();
        CASH = (SubAccountType) q.setString("desc", SubAccountType.CASH).uniqueResult();
        DIV_REV = (SubAccountType) q.setString("desc", SubAccountType.DIVIDENT_REVENUE).uniqueResult();
        UNREALIZED_GAIN_LOSS = (SubAccountType) q.setString("desc", SubAccountType.UNREALIZED_GAIN_LOSS).uniqueResult();
        CHANGE_ON_CLOSE_OF_INV = (SubAccountType) q.setString("desc", SubAccountType.CHANGE_ON_CLOSE_OF_INV).uniqueResult();
        COMMISIONS = (SubAccountType) q.setString("desc", SubAccountType.COMMISIONS).uniqueResult();
        INTEREST_REV = (SubAccountType) q.setString("desc", SubAccountType.INTEREST_REV).uniqueResult();
    }
}
