package org.rubypeople.rdt.internal.debug.core.parsing;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.xmlpull.v1.XmlPullParser;

public class LoadResultReader extends XmlStreamReader {

	
	private LoadResult loadResult ;
	
	public LoadResultReader(XmlPullParser xpp) {
		super(xpp);
	}

	public LoadResultReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}
		
	public LoadResult readLoadResult() throws RubyProcessingException {
		this.loadResult = new LoadResult() ;
		try {			
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex) ;
		}
		return loadResult ;		
	}

	protected boolean processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();
		if (name.equals("loadResult")) {
			this.loadResult.setFileName(xpp.getAttributeValue("", "fileName"));
			this.loadResult.setExceptionType(xpp.getAttributeValue("", "exceptionType"));
			this.loadResult.setExceptionMessage(xpp.getAttributeValue("", "exceptionMessage"));			
			return true ;
		}
		return false ;
	}
	
	public class LoadResult {
		private String fileName ;
		private String exceptionMessage ;
		private String exceptionType ;
		
		public String getExceptionMessage() {
			return exceptionMessage;
		}

		public void setExceptionMessage(String exceptionMessage) {
			this.exceptionMessage = exceptionMessage;
		}

		public String getExceptionType() {
			return exceptionType;
		}

		public void setExceptionType(String exceptionType) {
			this.exceptionType = exceptionType;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public boolean isOk() {
			return exceptionType == null ;
		}
	}


}
