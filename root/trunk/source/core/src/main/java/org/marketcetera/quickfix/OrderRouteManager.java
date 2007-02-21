package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.util.HashMap;
import java.util.Map;

/**
 * $Id$
 * @author gmiller
 */
@ClassVersion("$Id$")
public class OrderRouteManager implements OrderModifier {


    public static final String FIELD_57_METHOD = "field:57";
    public static final String FIELD_100_METHOD = "field:100";

    private Map<String, String> mRoutes;
    private String routeMethod;
    private boolean separateSuffix;

    /** Creates a new instance of OrderRouteManager */
    public OrderRouteManager() {
        mRoutes = new HashMap<String, String>();
    }

    public void setRoutes(Map<String, String> routeMap)
    {
        for (String suffix : routeMap.keySet()) {
            addOneRoute(suffix, routeMap.get(suffix));
        }
    }

    public void addOneRoute(String key, String route)
    {
        if(route != null && !"".equals(route)) {
            mRoutes.put(key, route);
        }
    }

    public void setRouteMethod(String inMethod)
    {
        // assume it's all stored in one field...
        routeMethod = inMethod;
        if (routeMethod != null &&
            (!(FIELD_57_METHOD.equals(routeMethod) || FIELD_100_METHOD.equals(routeMethod)))) {
                throw new IllegalArgumentException(
                        "Could not recognize route method " + routeMethod);
        }
    }

    public void setSeparateSuffix(boolean inSeparateSuffix)
    {
        separateSuffix = inSeparateSuffix;
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
                if (routeMethod != null
                        && MsgType.ORDER_SINGLE.equals(msgTypeField.getValue())) {
                    int periodPosition;
                    if ((periodPosition = symbolString.lastIndexOf('.')) > 0) {
                        String routeKey = symbolString.substring(periodPosition + 1);
                        String rootSymbol = symbolString.substring(0, periodPosition);
                        String mappedRoute = mRoutes.get(routeKey);
                        if (mappedRoute != null) {
                            if (FIELD_57_METHOD.equals(routeMethod)) {
                                anOrder.setField(new TargetSubID(mappedRoute));
                            } else if (FIELD_100_METHOD.equals(routeMethod)) {
                                anOrder.setField(new ExDestination(mappedRoute));
                            }
                            anOrder.setField(new Symbol(rootSymbol));
                        }
                    }
                    isModified = true;
                }
                if (separateSuffix) {
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
}
