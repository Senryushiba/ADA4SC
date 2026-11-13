package testlib;

import java.math.BigDecimal;

public class MiniTest {

    public static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError("Se esperaba true. " + msg);
    }

    public static void assertFalse(boolean cond, String msg) {
        if (cond) throw new AssertionError("Se esperaba false. " + msg);
    }

    public static void assertEquals(Object esperado, Object real, String msg) {
        if (esperado == null ? real != null : !esperado.equals(real)) {
            throw new AssertionError(
                "Esperado: " + esperado + " | Real: " + real + " | " + msg);
        }
    }

    public static void assertEqualsInt(int esperado, int real, String msg) {
        if (esperado != real) {
            throw new AssertionError(
                "Esperado: " + esperado + " | Real: " + real + " | " + msg);
        }
    }

    public static void assertBigDecimal(String esperado, BigDecimal real, String msg) {
        if (real == null || new BigDecimal(esperado).compareTo(real) != 0) {
            throw new AssertionError(
                "Esperado: " + esperado + " | Real: " + real + " | " + msg);
        }
    }

    public static <T extends Throwable> T assertThrows(
            Class<T> tipo, Runnable bloque, String msg) {
        try {
            bloque.run();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) return tipo.cast(t);
            throw new AssertionError(
                "Esperaba " + tipo.getSimpleName() + " pero llegó "
                + t.getClass().getSimpleName() + " | " + msg);
        }
        throw new AssertionError(
            "Esperaba " + tipo.getSimpleName() + " pero no lanzó nada | " + msg);
    }
}