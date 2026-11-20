# Registro de cambios

Todos los cambios relevantes del proyecto se anotan en este archivo.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el
versionado sigue [Versionado Semántico](https://semver.org/lang/es/).

## [Sin publicar]

Aquí se anotan los cambios de cada rama antes de integrarlos en `main`.
Al integrarlos, se mueven a una versión nueva con su fecha.

## [0.2.0] - 2026-11-19

Versión base profesionalizada, preparada para trabajar con control de versiones.
**No cambia el comportamiento del sistema**: la lógica de `VentaService` y los
9 casos de prueba son los mismos de la versión 0.1.0.

### Añadido

- `README.md`: descripción, requisitos, estructura, cómo compilar y probar, tabla de
  casos de prueba y limitaciones conocidas.
- `CHANGELOG.md`: este registro de cambios.
- `CONTRIBUTING.md`: convención de nombres de ramas, formato de mensajes de commit y
  flujo de trabajo con Git.
- `docs/ARQUITECTURA.md`: capas, diagrama de clases (Mermaid), flujos de registrar y
  cancelar venta, y decisiones de diseño.
- `scripts/test.sh`: compila con `-Xlint:all` y ejecuta las pruebas con un solo comando.
- `.gitignore`: excluye `out/`, `bin/`, `*.class`, archivos de IDE (`.vscode/`, `.idea/`)
  y archivos del sistema operativo.
- `.gitattributes`: normaliza los saltos de línea (LF) para evitar diferencias entre
  Windows y Linux/macOS.
- Javadoc en todas las clases, interfaces y enumeraciones, y en los métodos públicos de
  `VentaService`, con `@param`, `@return` y `@throws`. El Javadoc se validó con
  `javadoc -Xdoclint:all` sin errores.
- Constante `serialVersionUID` en las 6 excepciones de `src/exception/`.

### Cambiado

- `src/testlib/MiniRunner.java`: las pruebas ahora se ejecutan en **orden alfabético**.
  Antes el orden dependía de la reflexión y podía variar entre ejecuciones.
- `src/domain/DetalleVenta.java` y `src/domain/Venta.java`: el parámetro de
  `setPrecioUnitario` (`p`) y de `setUsuarioRegistro` (`u`) ahora se llaman igual que el
  campo, para mayor legibilidad.
- `src/domain/EstadoVenta.java` y `src/domain/TipoMovimiento.java`: cada constante en su
  propia línea, para poder documentarlas.
- Todos los archivos `.java` terminan ahora con un salto de línea. Antes ninguno lo tenía y
  Git marcaba `\ No newline at end of file` en cada diff de la última línea.

### Corregido

- Las 6 advertencias `[serial]` que producía `javac -Xlint:all`. La compilación queda
  sin advertencias.

### Conocido y sin resolver

Se documentan en el `README.md` (sección *Limitaciones conocidas*) y no se corrigen en
esta versión porque cambian el comportamiento:

- Una venta rechazada puede dejar stock descontado y movimientos guardados.
- El stock modificado no se guarda con `ProductoRepository.save`.
- No se validan las cantidades (cero, negativas o nulas).

## [0.1.0] - 2026-11-12

Estado original del proyecto entregado en la ADA 4.

### Añadido

- Capa de dominio: `Producto`, `Venta`, `DetalleVenta`, `MovimientoInventario`,
  `EstadoVenta` y `TipoMovimiento`.
- Seis excepciones de negocio en `src/exception/`.
- Interfaces de repositorio para productos, ventas y movimientos de inventario.
- `VentaService` con `registrarVenta` y `cancelarVenta`.
- Mini framework de pruebas (`MiniTest`, `MiniRunner`) y 9 pruebas unitarias
  (PU-VEN-01 a PU-VEN-09) en `VentaServiceManualTest`.
