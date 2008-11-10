package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class XmlStreamReader {
	private AbstractReadStrategy readStrategy ;
	private boolean isWaitTimeExpired ;
	
	public XmlStreamReader(XmlPullParser xpp) {
		this(new SingleReaderStrategy(xpp)) ;	
	}
	
	public XmlStreamReader(AbstractReadStrategy readStrategy) {
		this.readStrategy = readStrategy ;
		this.isWaitTimeExpired = false ;
	}
	
	public void read()  throws XmlPullParserException, IOException, XmlStreamReaderException{
		this.readStrategy.readElement(this) ;	
	}
	
	public void read(long maxWaitTime)  throws XmlPullParserException, IOException, XmlStreamReaderException{
		this.readStrategy.readElement(this, maxWaitTime) ;	
	}

	protected abstract boolean processStartElement(XmlPullParser xpp)  throws XmlStreamReaderException ;
	protected boolean processEndElement(XmlPullParser xpp) {
		// returns true if processing is finished, false if there are further elements expected
		String name = xpp.getName();
		RdtDebugCorePlugin.debug("Reader " + this.getClass().getName() + " received End element: " + name) ;
		return true ;
	}

	public void processContent(String text) {
	}

	public boolean isWaitTimeExpired() {
		return isWaitTimeExpired;
	}

	protected void setWaitTimeExpired(boolean isWaitTimeExpired) {
		this.isWaitTimeExpired = isWaitTimeExpired;
	}

}
