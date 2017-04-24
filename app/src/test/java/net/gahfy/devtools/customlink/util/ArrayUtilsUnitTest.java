package net.gahfy.devtools.customlink.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests the util.ArrayUtils class
 */
public class ArrayUtilsUnitTest {
    @Test
    public void array_util_instance() throws Exception {
        ArrayUtils arrayUtils = new ArrayUtils();
        Assert.assertEquals("Test int[] ArrayUtils.add(int[], int)", arrayUtils.getClass(), ArrayUtils.class);
    }

    @Test
    public void add_in_array() throws Exception {
        int[] testArray = new int[]{1, 2, 3};
        testArray = ArrayUtils.add(testArray, 4);
        Assert.assertArrayEquals("Test int[] ArrayUtils.add(int[], int)", new int[]{1, 2, 3, 4}, testArray);
    }

    @Test
    public void remove_in_array() throws Exception {
        int[] testArray = new int[]{1, 2, 3};
        testArray = ArrayUtils.removeIndex(testArray, 1);
        Assert.assertArrayEquals("Test int[] ArrayUtils.removeIndex(int[], int)", new int[]{1, 3}, testArray);
    }
}
