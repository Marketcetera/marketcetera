import java.io.*;

import org.xmlpull.v1.*;
import org.kxml2.kdom.*;
import org.kxml2.io.*;


/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */

public class KDomRoundtrip {

	/**
	 * Constructor for KDomRoundtrip.
	 */
	

	public static void main(String[] args) throws IOException, XmlPullParserException {
		if (args.length == 0) throw new RuntimeException ("input url expected");
		for (int i = 0; i < args.length; i++) {
			System.out.println ("generating KDom from "+args[i]);
			
			KXmlParser parser = new KXmlParser ();
			parser.setInput (new FileReader (args [i]));
			parser.setFeature (XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			
			Document doc = new Document ();
			doc.parse (parser);

			KXmlSerializer serializer = new KXmlSerializer ();
			serializer.setOutput (System.out, null);
			
			doc.write (serializer);
			serializer.flush ();
		}
	}
}
