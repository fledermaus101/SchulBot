package tk.ungeschickt.test;

import org.junit.Test;
import static org.junit.Assert.*;
import tk.ungeschickt.main.Main;

public class ClassMethodName {
    @Test
    public void testGetClassName() {
        assertEquals("Main", Main.class.getSimpleName());
    }

    @Test
    public void testGetMethodName() {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        assertEquals("testGetMethodName", methodName);
    }
}
