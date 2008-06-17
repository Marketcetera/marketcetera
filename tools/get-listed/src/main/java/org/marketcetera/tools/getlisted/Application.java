package org.marketcetera.tools.getlisted;

import org.marketcetera.core.ClassVersion;
import java.net.URL;
import java.net.URLConnection;

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

    private static final String URL=
        "http://www.nasdaq.com/asp/symbols.asp?start=0&exchange=";

    public static void main
        (String args[])
        throws Exception
    {
        /*
        URLConnection connection=(new URL(URL+NYSE)).openConnection();

        BufferedReader in = null;
        try {
          in = new BufferedReader(
            new InputStreamReader(
              connection.getInputStream()));

          String inputLine;
          while (
            (inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
          }
        */
    }
}
