package testlib;

import java.lang.reflect.Method;

public class MiniRunner {

    public static void correr(Class<?> clase) {
        int pasadas = 0, fallidas = 0;
        System.out.println("Ejecutando pruebas de: " + clase.getSimpleName());

        for (Method m : clase.getDeclaredMethods()) {
            if (!m.getName().startsWith("test")) continue;
            m.setAccessible(true);
            try {
                Object instancia = clase.getDeclaredConstructor().newInstance();
                m.invoke(instancia);
                pasadas++;
                System.out.println("  [OK]      " + m.getName());
            } catch (Throwable ex) {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                fallidas++;
                System.out.println("  [FALLÓ]   " + m.getName() + " → " + causa.getMessage());
            }
        }

        System.out.println("---------------------------------------------");
        System.out.println("Total: " + (pasadas + fallidas)
                + " | OK: " + pasadas + " | Fallas: " + fallidas);
        if (fallidas > 0) System.exit(1);
    }
}