# CAMBIOS — últimas tandas de trabajo

Resumen de todo lo que se cambió en las últimas tandas:

1. **Tanda 1** — "hazme todos los test que falten" (ya commiteada como `c5824c4 locura la cantidad de test`).
2. **Tanda 2** — tests TDD desde el enunciado (`TP_Integrador.txt`) + corrección de `HALLAZGOS.md`.
3. **Tanda 3** — clase `Cronograma`: cupos por fecha (**sin commitear**, está en el working tree).
4. **Tanda 4** — refactor de `reportes` y unión con el cronograma (**sin commitear**, está en el working tree).

**Estado actual de la suite:** 392 tests, **366 pasan y 26 fallan**. Los 26 son los mismos de la tanda 3
y ninguno viene de estas tandas (detalle en 3.6). Al cierre de la tanda 2 eran 338 tests con 23 fallos.

---

## Tanda 4 — Refactor de `reportes` y unión con el cronograma (sin commitear)

### 4.1 Unión con el cronograma

`ReporteOcupacion` ya no cuenta reservas: lee de los cronogramas los cupos de cada servicio en las fechas
del período. El cronograma es la única fuente de la ocupación, así que el reporte y la confirmación de
reservas no pueden contradecirse.

**Esto cambia los números del reporte:**

| | Antes | Ahora |
|---|---|---|
| **Unidad** | Cupos | Cupos por día: un hotel de 10 cupos tiene 310 totales en un período de 31 días |
| **Qué reservas entran** | Las **creadas** en el período (`getFecha()`) | Los días de la reserva que **caen dentro** del período |
| **Reserva de 2 pasajeros, 3 días** | Ocupa 2 | Ocupa 6 |
| **Reserva que cruza el período** | Cuenta entera o nada | Cuenta sólo los días de adentro |
| **Cupo de una fecha puntual** | Se ignoraba | Se refleja en los totales |

Por qué cupos por día: el enunciado pide cupos "por categoría y período", y un cupo por fecha sólo se puede
sumar en un período si se cuenta por día. Las columnas de los exportadores conservan su nombre.

**Cambios en `catalogo`:**
- `Cronograma.java`: nuevo mapa `reservadosPorFecha` y métodos `cuposTotalesEntre(inicio, fin)` y
  `cuposReservadosEntre(inicio, fin)`. El mapa hace falta porque con sólo el cupo libre no se distingue una
  fecha reservada de una con menos capacidad. Total de una fecha = libres + reservados.
- `ServicioIndividual.java`: delega esos dos métodos en el cronograma. Se eliminan `cupoTotal` y
  `getCupoTotal()`, que quedaron sin uso.

**Comportamientos a tener en cuenta:**
- Una reserva **cancelada en curso** sigue ocupando sus cupos en el reporte, porque ese estado no los libera
  (enunciado 2.2). Antes dejaba de contar, porque una reserva cancelada no cuenta como venta.
- Una reserva **finalizada** sigue ocupando: es ocupación histórica.
- Sólo se cuentan los servicios del catálogo que recibe `generar`. Antes también sumaban los reservados que
  no estuvieran en esa lista.
- `ReporteServiciosMasReservados` **no** usa el cronograma: sigue filtrando por fecha de creación, porque
  mide cuándo se vendió y no cuándo se viaja.

### 4.2 Simplificación

El módulo bajó de 604 a 460 líneas. Se mantiene el Visitor que sugiere el enunciado y la salida de los
reportes de ventas y exposición no cambia ni un carácter.

| Archivo | Cambio |
|---|---|
| `Formato.java` | `conFilas(encabezado, filas, linea)` recibe la función que arma cada línea. Se saca el parámetro `pie`. |
| `ExportadorCSV` / `ExportadorTextoPlano` | Sin bucles repetidos ni métodos auxiliares: cada `visitar` declara su encabezado y su línea. |
| `ExportadorHTML` | Un solo método `tabla(titulo, columnas, filas, celdas)` y un solo `fila(etiqueta, celdas)`, en vez de tres encabezados y dos métodos de fila casi iguales. |
| `ReporteServiciosMasReservados` | Se saca el `IdentityHashMap`, que duplicaba la lista de filas, y la recursión pasa de dos métodos a uno. |
| `ReporteExposicionProveedor` | Se sacan tres métodos auxiliares y la constante `PORCENTAJE_TOTAL`. |
| `ReporteOcupacion` | Sin el conteo de reservas quedan menos métodos. Las filas se agrupan con un `LinkedHashMap` por categoría. |
| `FilaOcupacion` | Un solo `sumar(totales, reservados)`. Se sacan `getPorcentajeOcupacion()`, que sólo usaban los tests, y el `Math.max` de libres, porque lo reservado ya no puede superar al total. |

**Queda sin resolver:** `Reporte.generar` recibe `reservas`, pero sólo el reporte de ventas las usa. Se dejó
así para no cambiar la interfaz; cada reporte que las ignora lo aclara en un comentario.

### 4.3 Tests

- `ReporteOcupacionTest`: reescrito con reservas y cronogramas reales. Casos nuevos: reserva que cruza el
  período, fuera del período, pendiente, cancelada, cancelada en curso, cupo definido en una fecha y
  categorías con el mismo nombre.
- `ReportesCaracterizacionTest`: sólo cambian los datos y los valores esperados de **ocupación**, y el helper
  de datos deja de recibir el cupo. Los valores esperados de ventas y exposición siguen iguales: son la
  prueba de que el refactor no cambió esas salidas.
- `FilasTest` y `ReportesDeEjemplo`: adaptados a `sumar`. Se sacó el caso de libres negativos.
- `CronogramaTest`: 6 casos nuevos de totales y reservados.

Comparando contra la tanda 3 no hay fallos nuevos ni arreglados. Todas las clases de `reportes` y
`Cronograma` quedan con 100% de cobertura de instrucciones y ramas en JaCoCo (no se reportan las interfaces,
que no tienen código).

---

## Tanda 3 — `Cronograma`: cupos por fecha (sin commitear)

### 3.1 Qué problema resuelve

Antes cada servicio tenía **un solo número** de cupos (`cuposDisponibles`), sin relación con las fechas:
reservar un hotel en enero le quitaba cupo también para marzo. Ahora el cupo se maneja **por fecha** y la
reserva tiene una fecha de inicio y una fecha final.

### 3.2 La clase `Cronograma` (`src/unq/catalogo/Cronograma.java`, nueva)

Cada `ServicioIndividual` tiene su propio `Cronograma`, que guarda un `Map<LocalDate, Integer>`
(fecha → cupo de esa fecha).

| Método | Qué hace |
|---|---|
| `cupoEn(fecha)` | Cupo de esa fecha. |
| `definirCupo(fecha, cupo)` | Fija el cupo de una fecha puntual. Lanza `ExcepcionDominio` si es negativo. |
| `tieneCupo(inicio, fin, cantidad)` | Indica si **todas** las fechas del rango tienen al menos `cantidad`. |
| `decrementar(inicio, fin, cantidad)` | Descuenta `cantidad` en cada fecha del rango. Si alguna no alcanza, lanza `ExcepcionDominio` y no descuenta ninguna. |
| `incrementar(inicio, fin, cantidad)` | Devuelve `cantidad` en cada fecha del rango (al cancelar). |
| `cupoMaximo()` | El mayor cupo de todas las fechas. Es mayor a cero si hay cupo en al menos una fecha. |

### 3.3 Cambios en las demás clases

**`catalogo`**
- `Catalogo.java`: `tieneCupo`, `decrementarCupo` e `incrementarCupo` ahora reciben `(inicio, fin, cantidad)`.
  Nuevo método estático `decrementarCupoDeTodos(items, inicio, fin, cantidad)`.
- `ServicioIndividual.java`: el campo `cuposDisponibles` se reemplaza por un `Cronograma`, al que le delega.
  Nuevos `getCuposDisponiblesEn(fecha)` y `definirCupo(fecha, cupo)`. `getCupoTotal()` no cambia.
- `PaqueteTuristico.java`: delega las nuevas operaciones en sus ítems, como antes (Composite).

**`reserva`**
- `Reserva.java`: el constructor ahora es `Reserva(email, destino, pasajeros, fechaInicio, fechaFin)` y
  lanza `ExcepcionDominio` si el fin es anterior al inicio. Nuevos `getFechaInicio()` y `getFechaFin()`.
- `bloquearCupos()` (al confirmar) y `liberarCupos()` (al cancelar) usan ese rango de fechas.
- `getFecha()` sigue siendo la fecha de **creación** de la reserva, y es la que usan los reportes.

**Comportamiento al confirmar:** si algún ítem no tiene cupo en alguna fecha del rango se lanza
`ExcepcionDominio`, no se descuenta nada en ningún ítem y la reserva sigue en `SolicitudPendiente`.

### 3.4 Decisiones de diseño

- **Las fechas sin definir tienen el cupo por defecto.** El `int` de cupos que recibe el constructor de
  `ServicioIndividual` es el cupo de cualquier fecha, y el mapa guarda sólo las que se apartan de ese valor.
  Así no hay que cargar fecha por fecha un calendario sin fin. Un servicio de temporada se arma con cupo
  por defecto 0 y `definirCupo` en sus fechas.
- **El rango incluye las dos puntas.** Una reserva del 10 al 12 descuenta los días 10, 11 y 12. Si un
  hotel no debería contar el día de salida, hay que cambiarlo en `Cronograma.fechasEntre`.
- **Sigue siendo un cupo por pasajero** (H36): cada fecha del rango baja `cantidadDePasajeros`.
- **Todo o nada con deshacer, no con preguntar antes.** Preguntar `tieneCupo` a cada ítem y después
  descontar deja cupos descontados a medias cuando un servicio aparece dos veces (suelto y dentro de un
  paquete): cada pregunta por separado da que sí, pero el segundo descuento falla. Por eso
  `decrementarCupoDeTodos` descuenta y, si algo falla, devuelve lo ya descontado. Lo usan `Reserva` y
  `PaqueteTuristico`.
- **`getCuposDisponibles()` ahora es `cupoMaximo()`**, para que `FiltroPorCupos` cumpla "tiene cupos
  disponibles en al menos una fecha" (enunciado 2.6) sin modificarlo.

**Limitación conocida:** en un paquete, `getCuposDisponibles()` es el mínimo de esos máximos de sus ítems.
Si un ítem sólo tiene cupo el día 1 y otro sólo el día 2, el paquete figura disponible aunque ninguna fecha
sirva para ambos. No afecta a la confirmación, que sí valida fecha por fecha.

**Limitación resuelta en la tanda 4:** `ReporteOcupacion` usaba `getCupoTotal()` y no miraba el cronograma,
así que sus cupos totales no reflejaban los definidos con `definirCupo`.

### 3.5 Arreglos necesarios para compilar

`main` no compilaba antes de esta tanda. Se corrigió lo mínimo:

- `ServicioIndividual.java`: `ExceptionDominio` estaba mal escrito (la línea luego se reemplazó por el `Cronograma`).
- `Catalogo.java` / `PaqueteTuristico.java`: faltaba `factorDeDescuento()`, que `ReporteServiciosMasReservados`
  usa y `ServicioIndividual` ya tenía. En el paquete es `1 - descuento / 100.0`.
- `ServicioIndividual.java`: se sacó el 8.º parámetro del constructor (`descuentoPromocional`). Se ignoraba
  y ningún test ni clase lo pasaba. El descuento se sigue aplicando con `aplicarDescuentoPromocional`.

### 3.6 Tests

- **Nuevo:** `catalogo/CronogramaTest` (16 tests).
- **`reserva/ReservaTest`:** usa el nuevo constructor y verifica cupos por fecha. Casos nuevos: todas las
  fechas del rango bajan, las de afuera no, sin cupo lanza excepción y sigue pendiente, no quedan
  descuentos a medias (ítem sin cupo, servicio repetido, paquete).
- **`catalogo/PaqueteTuristicoTest` y `ServicioIndividualTest`:** casos nuevos de cupos por fecha.

Comparando contra la suite con sólo los arreglos de 3.5 (348 tests, 35 fallos):

- **Se arreglaron 9** de `ReservaTest`, que fallaban porque un `Catalogo` mockeado responde `false` a
  `tieneCupo` y `bloquearCupos` ya no le pregunta.
- **No hay fallos nuevos.**
- **Siguen 26 fallos, ajenos a esta tanda:**

| Test | Fallos | Causa |
|---|---|---|
| `PuntoEncuentroTest` | 6 | H34 |
| `AtributoDinamicoTest` | 6 | Rechaza el valor del atributo por tipo ("no es del tipo esperado"). No está en `HALLAZGOS.md`. |
| `GeneradorComprobantesTest` | 5 | H32 |
| `PaqueteTuristicoTest` | 4 | H30 |
| `NotificadorEmailTest` | 2 | H33 |
| `ClubViajerosTest` | 1 | H33 |
| `ReporteServiciosMasReservadosTest` | 1 | H30 |
| `ServicioIndividualTest` | 1 | H29 |

---

## Tanda 1 — Tests para los paquetes que no tenían (commit `c5824c4`)

Se agregaron 26 archivos de test (+1642 líneas), sin tocar código de producción.

| Paquete | Tests nuevos |
|---|---|
| `buscador` | `BuscadorCatalogoTest`, `FiltroAndTest`, `FiltroOrTest`, `FiltroNotTest`, `FiltroCategoriaTest`, `FiltroNombreTest`, `FiltroPorCuposTest`, `FiltroPrecioMaximoTest` |
| `excepciones` | `ExcepcionDominioTest` |
| `notificaciones` | `NotificadorTest`, `NotificadorEmailTest`, `ClubViajerosTest`, `GeneradorComprobantesTest`, `ComprobantesTest` |
| `reportes` | `PeriodoTest`, `FilasTest`, `ReporteOcupacionTest`, `ReporteExposicionProveedorTest`, `ReporteServiciosMasReservadosTest`, `ExportadorCSVTest`, `ExportadorHTMLTest`, `ExportadorTextoPlanoTest`, `ReportesDeEjemplo` (helper) |
| `traslado` | `DireccionTest`, `PlataformaTransporteTest`, `TrasladoVIPTest` |

> Problema de esa tanda: varios tests se escribieron mirando el código y no el enunciado, por lo que
> pasaban aunque el código estuviera mal. Eso se corrigió en la tanda 2.

---

## Tanda 2 — Tests TDD + corrección de hallazgos (sin commitear)

### 2.1 Cambios en código de producción

#### Archivos nuevos

| Archivo | Qué es |
|---|---|
| `src/unq/excepciones/ExcepcionPagoInvalido.java` | Excepción de dominio (hereda de `ExcepcionDominio`) que se lanza cuando un pago no valida. **(H27)** |
| `src/unq/metodosDePago/ComprobantePago.java` | Comprobante registrado por un medio de pago: `tipo`, `codigoTransaccion`, `monto` con sus getters. **(H22/H23)** |

#### Archivos modificados

**`catalogo`**
- `Catalogo.java`: nuevo método `void incrementarCupo()` en la interfaz (necesario para devolver cupos al cancelar).
- `ServicioIndividual.java`: implementa `incrementarCupo()`.
- `PaqueteTuristico.java`:
  - Implementa `incrementarCupo()` (delegando en sus componentes).
  - `agregarItem` lanza `ExcepcionDominio("Un paquete no puede contenerse a sí mismo")` si el ítem es el
    propio paquete o lo contiene en cualquier nivel (método privado recursivo `contieneA`). **(H2)**

**`reserva`**
- `ExcepcionPorInvalido.java`: ahora hereda de `unq.excepciones.ExcepcionDominio`. **(H13)**
- `Reserva.java`:
  - Guarda el `email` del cliente y lo expone con `getEmail()`.
  - `agregarObservador(...)` / `quitarObservador(...)`, que delegan en el `Notificador`.
  - `bloquearCupos()` / `liberarCupos()` descuentan / devuelven **un cupo por pasajero** en cada ítem. **(H14)**
  - El viejo `reembolsar()` se reemplazó por:
    - `reembolsarServiciosYTasas()` → servicios + costo del traslado (0 si no hay método de traslado).
    - `reembolsarServicios()` → sólo servicios.
  - Cada reembolso genera un `VoucherReembolso` que queda guardado en la reserva
    (`getVouchersDeReembolso()`, devuelve copia). **(H15/H16)**
- `EnChecking.java`: `cancelar` llama a `reembolsarServiciosYTasas()`.
- `EnCurso.java`: `cancelar` llama a `reembolsarServicios()`.

**`metodosDePago`**
- `MedioDePago.java` (Template Method):
  - `monto` pasa a ser `private`, con `getMonto()` / `setMonto()`. **(H26)**
  - `procesoDePago()` es `final` y lanza `ExcepcionPagoInvalido` si los datos no validan. **(H27)**
  - `notificarResultado()` por defecto registra un `ComprobantePago("Registro de transaccion", codigo, monto)`. **(H22)**
  - Nuevos `protected registrar(ComprobantePago)` y `getComprobantes()` (devuelve copia).
- `TarjDeCredito.java`:
  - `validarDatos` = `api.validarCVV(nroTarjeta, CVV) && api.validarVencimiento(vencimiento)`. **(H25)**
  - `notificarResultado` registra un comprobante de tipo **Voucher**. **(H23)**
  - Nuevo `getNroTarjeta()`; usa `getMonto()`.
- `PagoTransferencia.java`:
  - `validarDatos` = `api.validarCBU(CBU) && api.validarAlias(alias)`. **(H25)**
  - `notificarResultado` registra un **Comprobante CBU** con el número de operación. **(H23)**
  - Nuevo `getCBU()`.
- `BilleteraVirtual.java`: deja de pisar `monto` directamente, usa `getMonto()`; nuevo `getIdCuenta()`.
- `APITarjetaDeCredito.java` y `APIBancaria.java`: se eliminó el método genérico `validarDatos(...)` que no se usaba.

**`notificaciones`**
- `GeneradorComprobantes.java`: ahora guarda las facturas y vouchers generados (`getFacturas()`,
  `getVouchers()`) para poder testearlo. La condición `estadoAnterior.debeGenerarVoucher()` **se dejó
  como estaba** a propósito, para que el test TDD quede en rojo (H32).

### 2.2 Cambios en tests

#### Modificados

| Test | Cambio |
|---|---|
| `reserva/ReservaTest` | Cupos por pasajero con un `ServicioIndividual` real (confirmar 10→8; cancelar desde Confirmada/En check-in vuelve a 10; cancelar En curso queda en 8; cancelar pendiente queda en 10). Montos de voucher (check-in con traslado 17500, sin traslado 15000; en curso 15000). `getEmail`, observadores notificados con el estado real, `quitarObservador`, operación inválida no notifica, operación inválida lanza `ExcepcionDominio`. |
| `reserva/EnCheckingTest`, `EnCursoTest`, `PagadoTest`, `SolicitudPendienteTest` | Actualizados a los nuevos métodos de reembolso, verificando con `never()` que no se llame al otro. |
| `catalogo/PaqueteTuristicoTest` | El test de ciclo ahora espera excepción; nuevos tests de ciclo profundo, de "sin ciclo" y `agregarItem_noConsumeElCupoDelItem`. |
| `metodosDePago/MedioDePagoTest` | Espera `ExcepcionPagoInvalido`; prueba `getMonto`/`setMonto`, que `monto` sea privado, el comprobante por defecto y que `getComprobantes()` devuelva copia. Se sacó el override de `notificarResultado` del medio de pago mínimo. |
| `metodosDePago/TarjDeCreditoTest`, `PagoTransferenciaTest` | Reescritos: validadores específicos, comprobante registrado, `ExcepcionPagoInvalido` sin cobro. Se eliminó la inyección por reflexión. |
| `notificaciones/NotificadorEmailTest`, `ClubViajerosTest`, `GeneradorComprobantesTest`, `NotificadorTest` | Reescritos a partir del punto 2.5 del enunciado, con estados reales y `reserva.getEmail()`. |
| `reportes/ReporteServiciosMasReservadosTest` | Casos reales: Pack Escapada Urbana (10% de descuento), paquetes anidados (Kit Gran Tour) y servicio vendido suelto + dentro de un paquete. |
| `buscador/BuscadorCatalogoTest` | Ejemplos de consultas anidadas del punto 2.6 con ítems reales. |
| `traslado/PuntoEncuentroTest` | Reescrito: el cupo de guías es el del **punto** (`ubicacion`), no el del destino. |

#### Eliminados

- `traslado/PlataformaTransporteTest` y `traslado/TrasladoVIPTest`: sólo probaban el cuerpo de relleno
  de clases que el enunciado presenta como librerías externas (ver H9).

### 2.3 Cambios en `HALLAZGOS.md`

- **Eliminados por estar corregidos u obsoletos:** H1, H2, H3, H7, H8, H11–H28 y las tablas viejas de
  tests por módulo.
- **Se mantienen:** H4, H5, H6 (decisiones de diseño), H9 (actualizado) y H10.
- **Nuevos bugs pendientes (tests en rojo):**

| Hallazgo | Bug | Tests en rojo |
|---|---|---|
| H29 | `ServicioIndividual.precioFinal()` ignora el descuento promocional. | 1 |
| H30 | `PaqueteTuristico` divide `descuento / 100` con enteros → nunca descuenta; `precioBase()` suma precios finales. | 4 + 3 de reportes |
| H31 | `PaqueteTuristico.agregarItem` consume un cupo del ítem. | 1 |
| H32 | `GeneradorComprobantes` mira el estado anterior en vez del nuevo → nunca genera factura/voucher. | 5 |
| H33 | Los mails se envían a `null` y el mensaje muestra el `toString()` del estado. | 3 |
| H34 | `PuntoEncuentro` usa el cupo de guías del destino y no el del punto. | 6 |

- **Nuevas decisiones de diseño documentadas:** H35 (qué se reembolsa en cada estado), H36 (cupos por
  pasajero) y H37 (comprobantes de cada medio de pago).

### 2.4 Otros

- `TP_Integrador.txt` (el enunciado) está en la carpeta pero **sin trackear** en git.
- Nada de la tanda 2 fue commiteado.
