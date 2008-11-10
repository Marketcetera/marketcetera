import java.io.*;

import org.kxml2.io.*;
import org.xmlpull.v1.*;

public class EventList {

	public static void main(String[] args)
		throws IOException, XmlPullParserException {

		for (int i = 0; i < 2; i++) {

			XmlPullParser xr = new KXmlParser();
			xr.setInput(new FileReader(args[0]));

			System.out.println("");
			System.out.println(
				"*** next" + (i == 0 ? "Token" : "") + " () event list ***");
			System.out.println("");

			do {
				if (i == 0)
					xr.nextToken();
				else
					xr.next();

				System.out.println(xr.getPositionDescription());


			} while (xr.getEventType() != XmlPullParser.END_DOCUMENT);
		}
	}

}