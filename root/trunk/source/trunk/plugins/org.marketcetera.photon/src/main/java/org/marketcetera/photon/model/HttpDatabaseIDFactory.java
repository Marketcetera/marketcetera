package org.marketcetera.photon.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.marketcetera.core.ExternalIDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.Application;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HttpDatabaseIDFactory extends ExternalIDFactory {

	URL url;
	private DocumentBuilder parser;
	
	
	public HttpDatabaseIDFactory(URL url) {
		this.url = url;
        try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Application.getMainConsoleLogger().error("Could not initialize parser for HttpDatabaseIDFactory",e);
		}
	}

	public void grabIDs() throws NoMoreIDsException {
		if (parser == null){
			throw new NoMoreIDsException("Missing XML parser");
		}
		Reader inputReader = null;
		try {
	        // Connect to the remote host and read in the data
			
	        inputReader = getInputReader();
			Document document = parser.parse(new InputSource(inputReader));
	        Node nextIDNode = document.getElementsByTagName("next").item(0);
	        int nextID = Integer.parseInt(nextIDNode.getTextContent());
	        Node numAllowedNode = document.getElementsByTagName("num").item(0);
	        int numAllowed = Integer.parseInt(numAllowedNode.getTextContent());
	        setNextID(nextID);
	        setMaxAllowedID(nextID + numAllowed);

	    } catch (IOException ioe) {
	    	throw new NoMoreIDsException(ioe);
	    } catch (SAXException e) {
	    	throw new NoMoreIDsException(e);
	    } catch (Throwable t){
	    	throw new NoMoreIDsException(t);
	    } finally {
			if (inputReader != null){
				try {
					inputReader.close();
				} catch (IOException e) {}
			}
		}
	}

	protected Reader getInputReader() throws IOException {
		InputStreamReader inputReader;
		URLConnection connection = url.openConnection();

		inputReader = new InputStreamReader(connection.getInputStream());
		return inputReader;
	}
	

}
