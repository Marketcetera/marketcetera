package org.rubypeople.rdt.internal.core.util;

import java.util.HashSet;
import java.util.Set;

public class SetUtil {
    public static Set create(Object obj1, Object obj2) {
        HashSet Set = create(obj1);
        Set.add(obj2);
        return Set;
    }

    public static HashSet create(Object obj1) {
        HashSet Set = new HashSet();
        Set.add(obj1);
        return Set;
    }

    public static Set create(Object obj1, Object obj2, Object obj3) {
        Set Set = create(obj1, obj2);
        Set.add(obj3);
        return Set;
    }
}
