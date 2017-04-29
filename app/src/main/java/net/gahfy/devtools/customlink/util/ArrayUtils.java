package net.gahfy.devtools.customlink.util;

public class ArrayUtils {
    public static int[] add(int[] array, int value){
        int[] result = new int[array.length+1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = value;
        return result;
    }

    public static int[] removeIndex(int[] array, int index){
        int[] result = new int[array.length - 1];
        if (index > 0)
            System.arraycopy(array, 0, result, 0, index);
        if (index < array.length - 1)
            System.arraycopy(array, index + 1, result, index, array.length - index - 1);

        return result;
    }

}
