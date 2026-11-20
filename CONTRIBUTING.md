# Guía de contribución

Flujo de trabajo con Git para este proyecto.

## Ramas

- `main` contiene siempre una versión que compila y pasa todas las pruebas.
- Cada cambio se desarrolla en una **rama propia creada desde `main`**.
- El nombre va en minúsculas, con guiones, y describe el cambio. Se recomienda un prefijo:

| Prefijo | Uso | Ejemplo |
|---------|-----|---------|
| `funcionalidad-` | Funcionalidad nueva | `funcionalidad-reporte-ventas` |
| `mejora-` | Mejora de algo existente | `mejora-validacion-cantidades` |
| `correccion-` | Corrección de un error | `correccion-atomicidad-venta` |
| `docs-` | Solo documentación | `docs-actualiza-readme` |

## Mensajes de commit

Formato: `tipo: descripción breve en presente`, de 72 caracteres o menos.

| Tipo | Cuándo |
|------|--------|
| `feat` | Funcionalidad nueva |
| `fix` | Corrección de un error |
| `test` | Pruebas nuevas o modificadas |
| `docs` | Documentación |
| `refactor` | Reorganización del código sin cambiar su comportamiento |
| `chore` | Tareas de mantenimiento (configuración, scripts) |

Ejemplos:

```
feat: valida que la cantidad de cada ítem sea mayor que cero
test: agrega casos PU-VEN-10 a PU-VEN-12 para cantidades inválidas
docs: registra la validación de cantidades en el CHANGELOG
```

Un commit debe tener un solo propósito y su mensaje debe permitir entender qué
cambió y por qué sin abrir el código.

## Flujo de trabajo

```bash
git switch main                    # 1. Parte de main
git pull                           #    (actualizado, si hay remoto)
git switch -c mejora-mi-cambio     # 2. Crea y cambia a tu rama
# ... edita el código ...
bash scripts/test.sh               # 3. Verifica que compile y pase las pruebas
git status                         # 4. Revisa qué cambió
git diff                           #    y el detalle de las líneas
git add <archivos>                 # 5. Prepara los archivos
git commit -m "feat: descripción"  # 6. Registra el commit
git push -u origin mejora-mi-cambio  # 7. Sube la rama al remoto
```

Después se integra la rama en `main` (pull request en la plataforma, o
`git switch main` y `git merge mejora-mi-cambio`).

## Antes de integrar en `main`

- [ ] `bash scripts/test.sh` termina sin fallas.
- [ ] El cambio está anotado en la sección *Sin publicar* del [CHANGELOG](CHANGELOG.md).
- [ ] Si cambia el comportamiento o resuelve una limitación conocida, el
      [README](README.md) está actualizado.
- [ ] No se versionan archivos generados (`out/`, `*.class`): ya los excluye `.gitignore`.
