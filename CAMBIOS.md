# CAMBIOS — últimas dos tandas de trabajo

Resumen de todo lo que se cambió en las dos últimas tandas:

1. **Tanda 1** — "hazme todos los test que falten" (ya commiteada como `c5824c4 locura la cantidad de test`).
2. **Tanda 2** — tests TDD desde el enunciado (`TP_Integrador.txt`) + corrección de `HALLAZGOS.md`
   (**sin commitear**, está en el working tree).

**Estado final de la suite:** 338 tests, **315 pasan y 23 fallan a propósito**. Los 23 fallos son
incumplimientos del enunciado documentados como H29–H34 en `HALLAZGOS.md`.

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
