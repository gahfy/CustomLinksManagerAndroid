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
        for(int i=0; i<array.length; i++){
            if(i < index)
                result[i] = array[i];
            else if(i > index)
                result[i-1] = array[i];
        }
        return result;
    }
}
