package com.swtworkbench.community.xswt.dataparser;

import java.util.ArrayList;
import java.util.List;

import com.swtworkbench.community.xswt.XSWTException;

public class CompositeDataParser implements IDataParser {

	private List dataParsers;
	
	public CompositeDataParser() {
	}
	public CompositeDataParser(IDataParser dataParser) {
		addDataParser(dataParser);
	}
	
	public void addDataParser(IDataParser dataParser) {
		if (dataParsers == null) {
			dataParsers = new ArrayList();
		}
		dataParsers.add(dataParser);
	}

	public void removeDataParser(IDataParser dataParser) {
		if (dataParsers != null) {
			dataParsers.remove(dataParser);
		}
	}

	public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
		for (int i = 0; i < dataParsers.size(); i++) {
			IDataParser	dataParser = (IDataParser)dataParsers.get(i);
			Object o = null;
			try {
				o = dataParser.parse(source, klass, context);
			} catch (XSWTException e) {
			}
			if (o != null) {
				return o;
			}
		}		
		return null;
	}

	public boolean isResourceDisposeRequired() {
		for (int i = 0; i < dataParsers.size(); i++) {
			IDataParser	dataParser = (IDataParser)dataParsers.get(i);
			if (dataParser.isResourceDisposeRequired()) {
				return true;
			}
		}
		return false;
	}
}
