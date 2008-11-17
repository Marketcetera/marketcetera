/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.core.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    public static List create(Object obj1, Object obj2) {
        ArrayList list = create(obj1);
        list.add(obj2);
        return list;
    }

    public static ArrayList create(Object obj1) {
        ArrayList list = new ArrayList();
        list.add(obj1);
        return list;
    }

    public static List create(Object obj1, Object obj2, Object obj3) {
        List list = create(obj1, obj2);
        list.add(obj3);
        return list;
    }

    public static List create() {
        return new ArrayList();
    }

}
