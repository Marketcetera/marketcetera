package org.rubypeople.rdt.internal.debug.core.parsing;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.xmlpull.v1.XmlPullParser;

public class EvalReader extends XmlStreamReader {

	private String exceptionType;
	private String exceptionMessage;
	private String name;
	private String value;

	public EvalReader(XmlPullParser xpp) {
		super(xpp);
	}

	public EvalReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	@Override
	protected boolean processStartElement(XmlPullParser xpp) throws XmlStreamReaderException {
		boolean result = false;
		if (xpp.getName().equals("processingException")) {
			exceptionType = xpp.getAttributeValue("", "type");
			exceptionMessage = xpp.getAttributeValue("", "message");
			result = true;
		} else if (xpp.getName().equals("eval")) {
			name = xpp.getAttributeValue("", "name");
			value = xpp.getAttributeValue("", "value");
			result = true;
		}
		return result;
	}

	public String readEvalResult() throws RubyProcessingException {

		try {
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex);
			return null;
		}
		if (exceptionType != null) {
			throw new RubyProcessingException(exceptionType, exceptionMessage);
		}
		return value;
	}

	@Override
	public void processContent(String text) {}

	@Override
	protected boolean processEndElement(XmlPullParser xpp) {
		return xpp.getName().equals("processingException") || xpp.getName().equals("eval");
	}

}
