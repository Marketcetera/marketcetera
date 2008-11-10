package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class AbstractReadStrategy {
	protected XmlPullParser xpp ;

	public AbstractReadStrategy(XmlPullParser xpp) {
		this.xpp = xpp ;
	}

	public abstract void readElement(XmlStreamReader streamReader) throws XmlPullParserException, IOException, XmlStreamReaderException  ;
	
	public abstract void readElement(XmlStreamReader streamReader, long maxWaitTime) throws XmlPullParserException, IOException, XmlStreamReaderException  ;
	
	public abstract boolean isConnected() ;

}
