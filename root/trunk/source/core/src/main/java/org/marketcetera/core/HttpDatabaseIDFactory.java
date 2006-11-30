package org.marketcetera.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * ID Factory that knows how to connect to an oustside URL and grab a block of IDs from it
 * If connecting to an outside provider fails we default to the in-memory id factory.
 * The expected output is to be formatted like this:
 * <pre>
 *  <id>
 *      <next>1</next>
 *      <num>1000</num>
 *  </id>
 * </pre>
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class HttpDatabaseIDFactory extends ExternalIDFactory {
	private URL url;
	private DocumentBuilder parser;
	private InMemoryIDFactory inMemoryFactory;
	
	
	public HttpDatabaseIDFactory(URL url) {
		this.url = url;
        try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LoggerAdapter.error(MessageKey.ERROR_DBFACTORY_HTTP_PARSER_INIT.getLocalizedMessage(),e, this);
		}
	}

	public void grabIDs() throws NoMoreIDsException {
		if (inMemoryFactory != null){
			return;
		}
		if (parser == null){
			throw new NoMoreIDsException(MessageKey.ERROR_DBFACTORY_MISSING_PARSER.getLocalizedMessage());
		}
		Reader inputReader = null;
		boolean succeeded = false;
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
	        succeeded = true;
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
				} catch (IOException e) {
                    // ignored
                }
			}
			if (!succeeded){
				try {
					inMemoryFactory = new InMemoryIDFactory(System.currentTimeMillis(),"-"+InetAddress.getLocalHost().toString());
				} catch (UnknownHostException e) {
					inMemoryFactory = new InMemoryIDFactory(System.currentTimeMillis());
				}
			}
		}
	}

	protected Reader getInputReader() throws IOException {
		InputStreamReader inputReader;
		URLConnection connection = url.openConnection();

		inputReader = new InputStreamReader(connection.getInputStream());
		return inputReader;
	}

	@Override
	public String getNext() throws NoMoreIDsException {
		if (inMemoryFactory == null){
			return super.getNext();
		} else {
			return inMemoryFactory.getNext();
		}
	}
}
