package org.marketcetera.tools.getlisted;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
/*
import org.marketcetera.market.generated.ObjectFactory;
import org.marketcetera.market.generated.Securities;
import org.marketcetera.market.generated.Security;
*/
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.util.file.ReaderWrapper;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Downloads and converts the 
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Application
{
    public static final String NYSE="N";
    public static final String NASDAQ="Q";
    public static final String AMEX="A";

    public static final String EXT=".csv";

    private static final String URL=
        "http://www.nasdaq.com/asp/symbols.asp?start=0&exchange=";


    private static boolean isDelimiter
        (char c)
    {
        return (c=='"');
    }

    private static boolean isSeparator
        (char c)
    {
        return ((c=='\r') || (c=='\n') || (c==','));
    }

    private static boolean isEscape
        (char c)
    {
        return ((c=='\\') || (c=='"'));
    }

    private static boolean isContent
        (char c)
    {
        return (!isSeparator(c) && !isEscape(c));
    }


    public static void run
        (String args[])
        throws Exception
    {

        //        URLConnection connection=(new URL(URL+NYSE)).openConnection();
        //        CopyUtils.copyBytes(connection.getInputStream(),false,NYSE+EXT);

        char[] csvRaw=CopyCharsUtils.copy(NYSE+EXT);
        StringBuffer csvOut=new StringBuffer();
        char prev3=' ';
        char prev2='"';
        char prev1=',';
        for (int i=0;i<csvRaw.length;i++) {
            char c=csvRaw[i];
            char next1=((i+1<csvRaw.length)?csvRaw[i+1]:',');
            char next2=((i+2<csvRaw.length)?csvRaw[i+2]:'"');
            char next3=((i+3<csvRaw.length)?csvRaw[i+3]:' ');
            csvOut.append(c);
            /* "Extra quotes [ " ] need escaping inside a field"
               "Previous field [ ","" ] Field starts with extra quote"
               "Field ends with extra quote [ ""," ] Next field" */
            if ((isDelimiter(c)) &&
                ((isContent(prev1) && isContent(next1)) ||
                 (isContent(prev3) && isDelimiter(prev2) &&
                  isSeparator(prev1) && isDelimiter(next1) &&
                  isContent(next2)) ||
                 (isContent(prev2) && isDelimiter(prev1) &&
                  isSeparator(next1) && isDelimiter(next2) &&
                  isContent(next3)))) {
                csvOut.append('"');
            }
            prev3=prev2;
            prev2=prev1;
            prev1=c;
        }
        ReaderWrapper in=new ReaderWrapper
            (new StringReader(csvOut.toString()));

        try {
            CSVParser parser=new CSVParser(in.getReader());
            String[] fields;
            while (true) {
                fields=parser.getLine();
                if ((fields==null) ||
                    ("Name".equals(fields[0]) &&
                     "Symbol".equals(fields[1]) &&
                     "Market Value (millions)".equals(fields[2]) &&
                     "Description (as filed with the SEC)".equals(fields[3]))) {
                    break;
                }
            }
            /*
            ObjectFactory factory=new ObjectFactory();
            Securities securities=factory.createSecurities();
            List<Security> list=securities.getSecurity();
            while (true) {
                fields=parser.getLine();
                if ((fields==null) ||
                    fields[0].contains
                    ("The NASDAQ Stock Market, Inc. All Rights Reserved.")) {
                    break;
                }
                Security security=factory.createSecurity();
                security.setName(fields[0]);
                security.setSymbol(fields[1]);
                if ("N/A".equals(fields[2])) {
                    security.setMarketValue(new BigDecimal("-1"));
                } else {
                    DecimalFormat format=new DecimalFormat("$#,##0.##");
                    format.setParseBigDecimal(true);
                    security.setMarketValue
                        ((BigDecimal)format.parse(fields[2]));
                }
                security.setDescription(fields[3]);
                list.add(security);
            }
            Marshaller m = JAXBContext.newInstance(ObjectFactory.class).
                createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                          Boolean.TRUE);
            m.marshal(factory.createSecurities(securities),System.err);
            */
        } finally {
            in.close();
        }
    }

    public static void main
        (String args[])
    {
        try {
            run(args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
