# Buscaminas multijugador con TCP

Proyecto de IntelliJ IDEA. El código está en `juego/src/main/java`, separado en
`servidor` y `cliente`.

## Cómo abrirlo en IntelliJ

1. `File > Open...` y elige la carpeta `BuscaminasTCP` (la que contiene `juego.iml`).
2. Si IntelliJ pide un JDK, ve a `File > Project Structure > Project` y selecciona uno (8 o superior).
3. Arriba a la derecha ya hay dos configuraciones de ejecución: **Servidor** y **Cliente**.

## Cómo jugar

1. Ejecuta **Servidor** (una sola vez).
2. Ejecuta **Cliente** tantas veces como jugadores quieras (la configuración permite varias instancias a la vez).
3. En cada cliente escribe las coordenadas separadas por un espacio: `columna fila`, de 0 a 9 (ejemplo: `1 2`).
4. Escribe `salir` para desconectarte.

## Funcionamiento

- Tablero fijo de 10x10 con 15 minas al azar.
- Versión simplificada: el juego termina apenas se pisa una bomba y las casillas adyacentes no revelan información (no es como el buscaminas normal).
- Si se destapan todas las casillas seguras, ganan todos los jugadores.
- Para que el tablero se mantenga actualizado en tiempo real para todos los jugadores simultáneamente, el cliente usa un hilo secundario (`pendienteDeLosTableros` extiende de `Thread`).
- Al terminar una ronda el servidor genera un tablero nuevo y los clientes se desconectan.

## Protocolo

| Servidor -> cliente | Significado |
|---|---|
| `TABLERO` | le siguen 10 líneas (`-` oculta, `O` segura, `*` bomba) |
| `BOMBA` | alguien pisó una bomba, fin |
| `VICTORIA` | se destapó todo lo seguro, fin |
| otro texto | aviso, se muestra como `Servidor: ...` |

Cliente -> servidor: `columna fila`.

## Estructura

    juego/src/main/java/
      servidor/  Servidor.java, ManejadorCliente.java, Tablero.java
      cliente/   Cliente.java, pendienteDeLosTableros.java
