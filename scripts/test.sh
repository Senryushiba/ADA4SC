#!/usr/bin/env bash
# Compila el proyecto y ejecuta las pruebas unitarias.
# Uso (desde cualquier carpeta):  bash scripts/test.sh
set -euo pipefail

# Ubica la raíz del proyecto (la carpeta superior a scripts/)
cd "$(dirname "${BASH_SOURCE[0]}")/.."

SALIDA="out"

if ! command -v javac >/dev/null 2>&1; then
  echo "Error: no se encontró 'javac'. Instala un JDK 17 o superior y verifica que esté en el PATH." >&2
  exit 1
fi

echo ">> Compilando..."
rm -rf "$SALIDA"
mkdir -p "$SALIDA"
javac -encoding UTF-8 -Xlint:all -d "$SALIDA" $(find src -name '*.java')

echo ">> Ejecutando pruebas..."
java -Dfile.encoding=UTF-8 -cp "$SALIDA" test.VentaServiceManualTest
