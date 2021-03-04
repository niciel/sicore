package com.niciel.superduperitems.utils;

import java.util.Collection;

public final class CollectionUtils {


    public static boolean isInCollectionRange(Collection c , int minSize) {
        return minSize >= 0 && c.size() >= minSize;
    }

    public static boolean isInCollectionRange(Object[] c , int minSize) {
        return minSize >= 0 && c.length >= minSize;
    }

}
