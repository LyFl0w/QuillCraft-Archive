package net.quillcraft.core.utils;

import org.apache.commons.lang.Validate;

public class InventoryUtils {

    public static int getInventorySize(int maxSize) {
        Validate.isTrue(maxSize <= 54, "The size of an inventory cannot be greater than 54 (because 6*9 is double chest size) : ", maxSize);
        for(int i = 9; i <= 54; i += 9) {
            if(i >= maxSize) return i;
        }
        return -1;
    }

}
