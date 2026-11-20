# ADA4SC: módulo de ventas e inventario

Módulo de ventas de un sistema de inventario escrito en Java. Permite **registrar**
ventas (validando productos y descontando stock) y **cancelarlas** (devolviendo el
stock). Cada operación deja un movimiento de inventario para auditoría.

Proyecto académico de la asignatura *Construcción de software*.

**Versión actual:** 0.2.0. El historial completo está en el [CHANGELOG](CHANGELOG.md).

## Requisitos

- JDK 17 o superior (el código usa `record`; probado con JDK 21).
- Git.
- Bash para el script de pruebas. En Windows basta con *Git Bash*, que se instala junto con Git.

## Estructura del proyecto

```
ADA4SC/
├── README.md
├── CHANGELOG.md          Registro de cambios por versión
├── CONTRIBUTING.md       Flujo de trabajo con Git (ramas y commits)
├── docs/
│   └── ARQUITECTURA.md   Capas, diagrama de clases y flujos
├── scripts/
│   └── test.sh           Compila y ejecuta las pruebas
└── src/
    ├── domain/           Entidades y enumeraciones
    ├── exception/        Excepciones de negocio
    ├── repository/       Interfaces de acceso a datos
    ├── service/          Lógica de negocio (VentaService)
    ├── test/             Pruebas unitarias (VentaServiceManualTest)
    └── testlib/          Mini framework de pruebas (MiniTest, MiniRunner)
```

## Compilar y ejecutar las pruebas

Con el script:

```bash
bash scripts/test.sh
```

O manualmente, desde la raíz del proyecto:

```bash
# Bash / Git Bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out test.VentaServiceManualTest
```

```powershell
# PowerShell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out test.VentaServiceManualTest
```

En un IDE, ejecuta el método `main` de `test.VentaServiceManualTest`. Resultado esperado:

```
Total: 9 | OK: 9 | Fallas: 0
```

## Casos de prueba

| Caso | Método | Qué verifica |
|------|--------|--------------|
| PU-VEN-01 | `testRegistrarVenta_exitoso` | Venta de un producto: total, estado, stock descontado y movimiento de salida |
| PU-VEN-02 | `testRegistrarVenta_multiplesProductos` | Venta de varios productos: total y stock de cada uno |
| PU-VEN-03 | `testRegistrarVenta_stockInsuficiente` | Lanza `StockInsuficienteException` y no guarda nada |
| PU-VEN-04 | `testRegistrarVenta_productoInactivo` | Lanza `ProductoInactivoException` |
| PU-VEN-05 | `testRegistrarVenta_productoNoExiste` | Lanza `ProductoNoEncontradoException` |
| PU-VEN-06 | `testRegistrarVenta_sinItems` | Lanza `VentaSinProductosException` |
| PU-VEN-07 | `testCancelarVenta_exitosa` | Estado `CANCELADA`, stock revertido y movimiento de entrada |
| PU-VEN-08 | `testCancelarVenta_yaCancelada` | Lanza `VentaYaCanceladaException` |
| PU-VEN-09 | `testCancelarVenta_noExiste` | Lanza `VentaNoEncontradaException` |

## Limitaciones conocidas

Defectos detectados durante la revisión de la versión 0.2.0 y que siguen pendientes:

1. **Una venta rechazada puede dejar el inventario a medias.** Si un ítem posterior
   falla (por ejemplo, por falta de stock), los ítems anteriores ya descontaron stock
   y generaron movimientos. Comprobado: con dos productos, tras una venta rechazada el
   stock del primero bajó de 10 a 7 y se guardó un movimiento.
2. **El stock no se persiste.** `registrarVenta` y `cancelarVenta` modifican el
   `Producto` en memoria pero nunca llaman a `ProductoRepository.save`. Con los
   repositorios falsos de las pruebas funciona porque comparten la misma referencia;
   con una base de datos real el cambio se perdería.
3. **No se validan las cantidades.** Una cantidad cero o negativa se acepta (con -4
   el total sale negativo y el stock *aumenta*) y una cantidad `null` provoca
   `NullPointerException`.

## Contribuir

El flujo de ramas y la convención de mensajes de commit están en
[CONTRIBUTING.md](CONTRIBUTING.md). La arquitectura se describe en
[docs/ARQUITECTURA.md](docs/ARQUITECTURA.md).
