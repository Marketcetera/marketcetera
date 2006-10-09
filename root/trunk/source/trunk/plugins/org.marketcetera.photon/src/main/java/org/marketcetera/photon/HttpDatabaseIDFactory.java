package org.marketcetera.photon;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.marketcetera.core.ExternalIDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class HttpDatabaseIDFactory extends ExternalIDFactory {

	URL url;
	
	
	public HttpDatabaseIDFactory(URL url) {
		this.url = url;
	}

	public int grabIDs() throws NoMoreIDsException {
		try {
	        // Connect to the remote host and read in the data
	        URLConnection connection = url.openConnection();

	        // Parse an XML document into a DOM tree.
	        DocumentBuilder parser =
	            DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document document = parser.parse(connection.getInputStream());
	        Element nextIDNode = document.getElementById("nextID");
	        int nextID = Integer.parseInt(nextIDNode.getTextContent());
	        Element numAllowedNode = document.getElementById("numAllowed");
	        int numAllowed = Integer.parseInt(numAllowedNode.getTextContent());
            setMaxAllowedID(nextID + numAllowed);

	    	return nextID;
	    } catch (IOException ioe) {
	    	throw new NoMoreIDsException(ioe);
	    } catch (ParserConfigurationException e) {
	    	throw new NoMoreIDsException(e);
		} catch (SAXException e) {
	    	throw new NoMoreIDsException(e);
		}
	}


	

}
