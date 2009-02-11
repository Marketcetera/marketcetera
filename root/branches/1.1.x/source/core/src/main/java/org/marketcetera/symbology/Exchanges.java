package org.marketcetera.symbology;

import org.marketcetera.core.ClassVersion;
import org.skife.csv.CSVReader;
import org.skife.csv.SimpleReader;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class Exchanges {

    private static Map<String, Exchange> micMap = new HashMap<String, Exchange>();
    private static EnumMap<SymbolScheme, ExchangeMap> schemeMap = new EnumMap<SymbolScheme, ExchangeMap>(SymbolScheme.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMM yyyy",Locale.US); //$NON-NLS-1$

    static {
        try {
            loadExchanges();
        } catch (Exception ex) {
            Messages.ERROR_EXCHANGES_INIT.error(Exchanges.class, ex);
        }
    }

    private static final String XASE_MIC = "XASE"; //$NON-NLS-1$
    private static final String XBOS_MIC = "XBOS"; //$NON-NLS-1$
    private static final String XCIS_MIC = "XCIS"; //$NON-NLS-1$
    private static final String XISX_MIC = "XISX"; //$NON-NLS-1$
    private static final String XCHI_MIC = "XCHI"; //$NON-NLS-1$
    private static final String XNYS_MIC = "XNYS"; //$NON-NLS-1$
    private static final String XARC_MIC = "XARC"; //$NON-NLS-1$
    private static final String XNAS_MIC = "XNAS"; //$NON-NLS-1$
    private static final String XPHL_MIC = "XPHL"; //$NON-NLS-1$

    public static Exchange AMEX = micMap.get(XASE_MIC);
    public static Exchange BOSTON = micMap.get(XBOS_MIC);
    public static Exchange CINCINNATI = micMap.get(XCIS_MIC);
    public static Exchange ISE = micMap.get(XISX_MIC);
    public static Exchange CHICAGO = micMap.get(XCHI_MIC);
    public static Exchange NYSE = micMap.get(XNYS_MIC);
    public static Exchange ARCA = micMap.get(XARC_MIC);
    public static Exchange NASDAQ = micMap.get(XNAS_MIC);
    public static Exchange PHILADELPHIA = micMap.get(XPHL_MIC);

    //COUNTRY,CC,MIC,INSTITUTION DESCRIPTION,ACCR.,CITY,WEB SITE,Date added
    private static final int COUNTRY_COLUMN = 0;
    private static final int COUNTRY_CODE_COLUMN = 1;
    private static final int MIC_COLUMN = 2;
    private static final int INSTITUTION_NAME_COLUMN = 3;
    private static final int OTHER_ACRONYM_COLUMN = 4;
    private static final int CITY_COLUMN = 5;
    private static final int WEBSITE_COLUMN = 6;
    private static final int DATE_ADDED_COLUMN = 7;

    public static final String HYPERFEED_SCHEME = "HYPERFEED"; //$NON-NLS-1$
    public static final String FIX_SCHEME = "FIX"; //$NON-NLS-1$


    private static void loadExchanges() throws IOException, ParseException {
        CSVReader reader = new SimpleReader();

        URL url = Exchanges.class.getClassLoader().getResource("iso-10383.csv"); //$NON-NLS-1$
        InputStream in = url.openStream();

        List items = reader.parse(in);
        for (int i = 1; i < items.size(); i++) {
            String [] row = (String[]) items.get(i);

            if (row.length == 8) {
                Date addedDate;
                String dateString = row[DATE_ADDED_COLUMN];
                addedDate = DATE_FORMAT.parse(dateString); //i18n_date

                Exchange anExchange = new Exchange(
                        row[COUNTRY_COLUMN],
                        row[COUNTRY_CODE_COLUMN],
                        row[MIC_COLUMN],
                        row[INSTITUTION_NAME_COLUMN],
                        row[OTHER_ACRONYM_COLUMN],
                        row[CITY_COLUMN],
                        row[WEBSITE_COLUMN],
                        addedDate);

                micMap.put(anExchange.getMarketIdentifierCode(), anExchange);
            }
        }
        PropertiesExchangeMap hfExchangeMap = new PropertiesExchangeMap("hyperfeed-exchanges.properties"); //$NON-NLS-1$
        PropertiesExchangeMap basicExchangeMap = new PropertiesExchangeMap("basic-exchanges.properties"); //$NON-NLS-1$
        schemeMap.put(SymbolScheme.HYPERFEED, hfExchangeMap);
        schemeMap.put(SymbolScheme.BASIC, basicExchangeMap);
    }

    public static Exchange getExchange(String marketIdentifierCode){
        return micMap.get(marketIdentifierCode);
    }

    public static Exchange getExchange(SymbolScheme scheme, String exchangeString){
        ExchangeMap exchangeMap = schemeMap.get(scheme);

        return exchangeMap==null ? null : exchangeMap.getExchange(exchangeString);

    }





}
