package org.rubypeople.rdt.internal.debug.core.parsing;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.xmlpull.v1.XmlPullParser;

public class BreakpointModificationReader extends XmlStreamReader {

	private String no;

	public BreakpointModificationReader(XmlPullParser xpp) {
		super(xpp);
	}

	public BreakpointModificationReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	public int readBreakpointNo() throws NumberFormatException {
		try {
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex);
			return -1;
		}
		return Integer.parseInt(no);
	}

	@Override
	protected boolean processStartElement(XmlPullParser xpp) throws XmlStreamReaderException {
		boolean result = false;
		if (xpp.getName().equals("breakpointAdded")) {
			no = xpp.getAttributeValue("", "no");
			result = true;
		} else if (xpp.getName().equals("breakpointDeleted")) {
			no = xpp.getAttributeValue("", "no");
			result = true;
		} else if (xpp.getName().equals("error")) {
			no = "-1";
			result = true;
		}
		return result;
	}

	@Override
	public void processContent(String text) {}

	@Override
	protected boolean processEndElement(XmlPullParser xpp) {
		return xpp.getName().equals("breakpointAdded") || xpp.getName().equals("breakpointDeleted") || xpp.getName().equals("error");
	}

}
