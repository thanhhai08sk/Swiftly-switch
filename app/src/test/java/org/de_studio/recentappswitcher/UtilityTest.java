package org.de_studio.recentappswitcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by HaiNguyen on 8/19/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilityTest {
    @Test
    public void rightLeftOrBottom() {
        assertEquals(1, Utility.rightLeftOrBottom(10));
        assertEquals(2, Utility.rightLeftOrBottom(21));
        assertEquals(3, Utility.rightLeftOrBottom(31));
    }
}
