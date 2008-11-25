package org.marketcetera.photon.preferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.marketcetera.core.MMapEntry;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;


/**
 * A collection of utility methods used in the map field editors.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class MapEditorUtil {

	private static final String UTF_8 = "UTF-8";  //$NON-NLS-1$


	//agl non-instantiable
	private MapEditorUtil() {
	}
	
	public static String encodeList(EventList<Map.Entry<String, String>> list) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (Map.Entry<String, String> anEntry : list) {
			try {
				buf.append(URLEncoder.encode(anEntry.getKey(), UTF_8));
				buf.append("="); //$NON-NLS-1$
				buf.append(URLEncoder.encode(anEntry.getValue(), UTF_8));
			} catch (UnsupportedEncodingException e) {
				buf.append(anEntry.getKey());
				buf.append("="); //$NON-NLS-1$
				buf.append(anEntry.getValue());
			}
			if (i < list.size() - 1){
				buf.append("&"); //$NON-NLS-1$
			}
			i++;
		}
		return buf.toString();
	}

	public static EventList<Entry<String, String>> parseString(String stringList) {
		String [] pieces = stringList.split("&"); //$NON-NLS-1$
		EventList<Map.Entry<String, String>> outList = new BasicEventList<Map.Entry<String, String>>();
		int i = 0;
		for (String aPiece : pieces) {
			if (aPiece.contains("=")){ //$NON-NLS-1$
				String[] keyValueArray = aPiece.split("="); //$NON-NLS-1$
				MMapEntry<String, String> outEntry;
				try {
					outEntry = new MMapEntry<String, String>(URLDecoder.decode(keyValueArray[0], UTF_8)
							, URLDecoder.decode(keyValueArray[1], UTF_8));
				} catch (UnsupportedEncodingException e) {
					outEntry = new MMapEntry<String, String>(keyValueArray[0]
							, keyValueArray[1]);
				}
				outList.add(outEntry);
				i++;
			}
		}
		return outList;
	}

}
