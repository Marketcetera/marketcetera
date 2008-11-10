import org.xmlpull.v1.*;

import java.util.*;
import java.io.*;
import java.net.*;

/** 
 * A simple example illustrationg some differences of the XmlPull API 
 * and SAX. For the corresponding SAX based implementation, please refer to 
 * http://www.cafeconleche.org/slides/sd2001east/xmlandjava/81.html ff. */

public class Weblogs {

    static List listChannels()
        throws IOException, XmlPullParserException {
        return listChannels("http://static.userland.com/weblogMonitor/logs.xml");
    }

    static List listChannels(String uri)
        throws IOException, XmlPullParserException {

        Vector result = new Vector();

        InputStream is = new URL(uri).openStream();
        XmlPullParser parser =
            XmlPullParserFactory.newInstance().newPullParser();

        parser.setInput(is, null);

        parser.nextTag();
        parser.require(parser.START_TAG, "", "weblogs");

        while (parser.nextTag() == parser.START_TAG) {
            String url = readSingle(parser);
            if (url != null)
                result.addElement(url);
        }
        parser.require(parser.END_TAG, "", "weblogs");

        parser.next();
        parser.require(parser.END_DOCUMENT, null, null);

		is.close ();
		parser.setInput (null);

        return result;
    }

    public static String readSingle(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        String url = null;
        parser.require(parser.START_TAG, "", "log");

        while (parser.nextTag() == parser.START_TAG) {
            String name = parser.getName();
            String content = parser.nextText();
            if (name.equals("url"))
                url = content;
            parser.require(parser.END_TAG, "", name);
        }
        parser.require(parser.END_TAG, "", "log");
        return url;
    }

    public static void main(String[] args)
        throws IOException, XmlPullParserException {

        List urls =
            args.length > 0
                ? listChannels(args[0])
                : listChannels();

        for (Iterator i = urls.iterator(); i.hasNext();)
            System.out.println(i.next());
    }
}
