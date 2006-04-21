package org.marketcetera.photon;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FieldNameMap;

import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

/** Helper class for translating between constant and text strings for FIX names
 * @author Graham Miller
 * $Id$
 */

@ClassVersion("$Id$")
public class TextUtils {

    public static FieldNameMap<Character> sTimeInForceNameMap = new FieldNameMap<Character>(
            TimeInForce.FIELD, TimeInForce.class);
    public static FieldNameMap<Character> sStatusNameMap = new FieldNameMap<Character>(
            OrdStatus.FIELD, OrdStatus.class);
    public static FieldNameMap<Character> sSideNameMap = new FieldNameMap<Character>(
            Side.FIELD, Side.class);

    public static String getSideName(char side){
        return sSideNameMap.getName(side);
    }
    public static String getOrdStatusName(char side){
        return sStatusNameMap.getName(side);
    }
    public static String getTimeInForceName(char side){
        return sTimeInForceNameMap.getName(side);
    }

}
