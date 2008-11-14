package org.rubypeople.rdt.internal.debug.core.parsing;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.xmlpull.v1.XmlPullParser;

public class ErrorReader extends XmlStreamReader {

	public ErrorReader(XmlPullParser xpp) {
		super(xpp);
	}

	public ErrorReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	@Override
	protected boolean processStartElement(XmlPullParser xpp)
			throws XmlStreamReaderException {
		return xpp.getName().equals("error") || xpp.getName().equals("message") ;
	}

	@Override
	public void processContent(String text) {
		RdtDebugCorePlugin.log(text,null) ;
	}
	@Override
	protected boolean processEndElement(XmlPullParser xpp) {
		return xpp.getName().equals("error")|| xpp.getName().equals("message") ;
	}
	
	

}
