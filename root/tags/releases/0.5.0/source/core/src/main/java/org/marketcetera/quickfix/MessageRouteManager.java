package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
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
public class MessageRouteManager implements MessageModifier {


    public static final String FIELD_57_METHOD = "field:57";
    public static final String FIELD_100_METHOD = "field:100";
    public static final String FIELD_128_METHOD = "field:128";

    private Map<String, String> mRoutes;
    private String routeMethod;
    private boolean separateSuffix;

    /** Creates a new instance of MessageRouteManager */
    public MessageRouteManager() {
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
            (!(FIELD_57_METHOD.equals(routeMethod) || FIELD_100_METHOD.equals(routeMethod) || FIELD_128_METHOD.equals(routeMethod)))) {
                throw new IllegalArgumentException(MessageKey.ERROR_UNRECOGNIZED_ROUTE.getLocalizedMessage(routeMethod));
        }
    }

    public void setSeparateSuffix(boolean inSeparateSuffix)
    {
        separateSuffix = inSeparateSuffix;
    }

    public boolean isSeparateSuffix() {
        return separateSuffix;
    }

    protected Map<String,String> getRoutesMap() { return mRoutes; }

    public boolean modifyMessage(Message anOrder, FIXMessageAugmentor augmentor) throws MarketceteraException {
        try {
            boolean isModified = false;

            Symbol symbolField = new Symbol();
            anOrder.getField(symbolField);
            String symbolString = (String) symbolField.getValue();
            if (symbolString != null) {
                MsgType msgTypeField = new MsgType();
                anOrder.getHeader().getField(msgTypeField);
                if (routeMethod != null
                        && (MsgType.ORDER_SINGLE.equals(msgTypeField.getValue())  ||
                            MsgType.ORDER_CANCEL_REPLACE_REQUEST.equals(msgTypeField.getValue()) ||
                            MsgType.ORDER_CANCEL_REQUEST.equals(msgTypeField.getValue()))) {
                    int periodPosition;
                    if ((periodPosition = symbolString.lastIndexOf('.')) > 0) {
                        String routeKey = symbolString.substring(periodPosition + 1);
                        String rootSymbol = symbolString.substring(0, periodPosition);
                        String mappedRoute = mRoutes.get(routeKey);
                        if (mappedRoute != null) {
                            if (FIELD_57_METHOD.equals(routeMethod)) {
                                anOrder.getHeader().setField(new TargetSubID(mappedRoute));
                            } else if (FIELD_100_METHOD.equals(routeMethod)) {
                                anOrder.setField(new ExDestination(mappedRoute));
                            } else if (FIELD_128_METHOD.equals(routeMethod)) {
                                anOrder.getHeader().setField(new DeliverToCompID(mappedRoute));
                            }
                            anOrder.setField(new Symbol(rootSymbol));
                        }
                    }
                    isModified = true;
                }
                // don't separate suffixes on Forex orders (SecurityType may not be set on all orders so check explicitly)
                String securityType = (anOrder.isSetField(SecurityType.FIELD)) ? anOrder.getString(SecurityType.FIELD) : null;
                if (isSeparateSuffix() && !SecurityType.FOREIGN_EXCHANGE_CONTRACT.equals(securityType)) {
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
                        isModified = true;
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
