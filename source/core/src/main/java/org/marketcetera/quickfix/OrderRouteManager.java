package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigData;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.PropertiesConfigData;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 * $Id$
 * @author gmiller
 */
@ClassVersion("$Id$")
public class OrderRouteManager implements OrderModifier {


    public static final String FIELD_57_METHOD = "field:57";
    public static final String FIELD_100_METHOD = "field:100";

    public static final String ORDER_ROUTE_LIST = "org.marketcetera.omsclient.preferences.orderroutes";
    public static final String ORDER_ROUTE_TYPE = "order.route.type";
    public static final String ROUTES_NODE_KEY = "routes";
    public static final String METHOD_KEY = "method";
    public static final String SEPARATE_SUFFIX_KEY = "separate.suffix";

    private Map<String, String> mRoutes;
    private String mMethod;
    private boolean mSeparateSuffix;

    /** Creates a new instance of OrderRouteManager */
    public OrderRouteManager() {
    }

    public void init(ConfigData data) throws BackingStoreException {
        mSeparateSuffix = data.getBoolean(SEPARATE_SUFFIX_KEY, false);
        mRoutes = new HashMap<String, String>();
        // assume it's all stored in one field...
        mMethod = data.get(ORDER_ROUTE_TYPE, null);

        if (mMethod != null &&
            (!(FIELD_57_METHOD.equals(mMethod) || FIELD_100_METHOD.equals(mMethod)))) {
                throw new IllegalArgumentException(
                        "Could not recognize route method " + mMethod);
        }

        if (data instanceof PropertiesConfigData) {
            String[] propNames = data.keys();
            for(String oneName : propNames) {
                if(oneName.contains(ROUTES_NODE_KEY)) {
                    String theRoute = data.get(oneName, "");
                    addOneRoute(theRoute, mRoutes);
                }
            }
        } else {
            String [] results = parseStringHelper(data.get(ORDER_ROUTE_LIST, ""));
            if (results != null)
            {
                for (String aRoute : results) {
                    addOneRoute(aRoute, mRoutes);
                }
            }
        }

    }

    /** Takes the incoming route string and parses it out to get the mapping from it.
     * The name/value pairs in the route mappings are separated by spaces
     * ex: N M
     *     S SIGMA
     * @param aRoute Incoming string of "A B" format
     * @param inMap containing the new route or same map if no route was parsed out
     */
    protected void addOneRoute(String aRoute, Map<String, String> inMap)
    {
        int spacePosition = aRoute.indexOf(' ');
        if(spacePosition!= -1) {
            String suffix = aRoute.substring(0, spacePosition);
            String route = aRoute.substring(spacePosition+1);
            inMap.put(suffix, route);
        }
    }

    protected Map<String,String> getRoutesMap() { return mRoutes; }

    public boolean modifyOrder(Message anOrder) throws MarketceteraException {
        try {
            boolean isModified = false;

            Symbol symbolField = new Symbol();
            anOrder.getField(symbolField);
            String symbolString = (String) symbolField.getValue();
            if (symbolString != null) {
                MsgType msgTypeField = new MsgType();
                anOrder.getHeader().getField(msgTypeField);
                if (mMethod != null
                        && MsgType.ORDER_SINGLE.equals(msgTypeField.getValue())) {
                    int periodPosition;
                    if ((periodPosition = symbolString.lastIndexOf('.')) > 0) {
                        String routeKey = symbolString.substring(periodPosition + 1);
                        String rootSymbol = symbolString.substring(0, periodPosition);
                        String mappedRoute = mRoutes.get(routeKey);
                        if (mappedRoute != null) {
                            if (FIELD_57_METHOD.equals(mMethod)) {
                                anOrder.setField(new TargetSubID(mappedRoute));
                            } else if (FIELD_100_METHOD.equals(mMethod)) {
                                anOrder.setField(new ExDestination(mappedRoute));
                            }
                            anOrder.setField(new Symbol(rootSymbol));
                        }
                    }
                    isModified = true;
                }
                if (mSeparateSuffix) {
                    int suffixEnd = symbolString.length();
                    int slashPosition = 0;
                    if ((slashPosition = symbolString.lastIndexOf('/')) > 0) {
                        int periodPosition;
                        if ((periodPosition = symbolString.lastIndexOf('.')) > 0) {
                            suffixEnd = periodPosition;
                        }
                        if (slashPosition + 1 == suffixEnd) {
                            // special case for trailing slash, just leave it
                            anOrder.setField(new Symbol(symbolString.substring(0, slashPosition + 1)));
                        } else {
                            anOrder.setField(new Symbol(symbolString.substring(0, slashPosition)));
                            anOrder.setField(new SymbolSfx(symbolString.substring(slashPosition + 1, suffixEnd)));
                        }
                    }
                }
            }
            return isModified;
        } catch(FieldNotFound fnfEx) {
            throw MarketceteraFIXException.createFieldNotFoundException(fnfEx, anOrder);
        } catch (Exception ex) {
            throw new MarketceteraException(ex);
        }
    }

    /**
     * Parses the given string of mappings into an array of strings.  The serialized stringList
     * consists of length prefixed strings separated by commas.  For example the input string,
     * "4:ABCD,7:QWERTYU," would result in { "ABCD", "QWERTYU" }.
     *
     * @param stringList the serialized list
     * @return array of strings based on the input list
     */
    public static String [] parseStringHelper(String stringList){
        try {
            int startAt = 0;
            int totalLength = stringList.length();
            ArrayList<String> results = new ArrayList<String>();
            while (startAt < totalLength){
                String intString = stringList.substring(startAt, stringList.indexOf(':', startAt));
                int pieceLength = Integer.parseInt(intString);
                startAt += intString.length() + 1;
                results.add(stringList.substring(startAt, pieceLength+startAt));
                startAt += pieceLength + 1;
            }
            String [] resultsArray = new String[results.size()];
            int i = 0;
            for (String string : results) {
                resultsArray[i++] = string;
            }
            return resultsArray;
        } catch (Exception e){
            return new String[0];
        }
    }

}
