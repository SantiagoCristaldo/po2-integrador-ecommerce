# HALLAZGOS — TP Integrador UNQ-Travel

Registro de hallazgos, decisiones de diseño y diferencias entre el enunciado (`TP_Integrador.txt`) y el
código. Los tests se escriben **a partir del enunciado** (TDD): si el código no cumple lo que pide el
enunciado, el test queda en rojo, sin `@Disabled`, y el hallazgo se documenta acá con el test que lo
expone. Cuando un hallazgo se corrige, se borra de este documento.

**Estado de la suite:** 388 tests, **347 pasan y 41 fallan**. Los 41 fallos corresponden a los
hallazgos pendientes H29–H34 y H38–H41. Cobertura (JaCoCo): 96.4% de instrucciones, 95.9% de ramas.

---

## Cómo se corre

```bash
mvn -Dmaven.compiler.source=21 -Dmaven.compiler.target=21 test
```

El `pom.xml` declara `maven.compiler.source/target = 26`; si el JDK de la máquina es más viejo se
compila con el override. El `pom` incluye JaCoCo 0.8.15; el reporte queda en `target/site/jacoco/`.

Este proyecto no usa `src/test/java`: el `pom.xml` define `sourceDirectory=${project.basedir}/src` y
`testSourceDirectory=${project.basedir}/test`. Los tests van en `test/unq/<paquete>/`.

---

# Hallazgos pendientes (tests en rojo)

### H29 — Bug: `ServicioIndividual.precioFinal()` ignora el descuento promocional

Enunciado 2.1: *"precio final (precio con un descuento promocional solo de ese servicio)"*.
`aplicarDescuentoPromocional` guarda el porcentaje, pero `precioFinal()` devuelve `precioBase`.

- Test: `ServicioIndividualTest#precioFinal_conDescuentoPromocional_aplicaElPorcentaje`
- Cambio sugerido: `return precioBase * (1 - descuentoPromocional / 100);`

### H30 — Bug: `PaqueteTuristico` no aplica su descuento (división entera)

`precioFinal()` hace `descuento / 100` y `factorDeDescuento()` hace `1 - descuento / 100`, con
`descuento` de tipo `int`. Para cualquier descuento entre 1 y 99 el resultado es `0`, así que el paquete
nunca descuenta nada (el Pack Escapada Urbana da $21.000 en vez de $18.900). Además `precioBase()` suma
el `precioFinal()` de los hijos en lugar de su `precioBase()` (ver H4).

- Tests:
  - `PaqueteTuristicoTest#precioFinal_casoDelEnunciado_devuelve18900`
  - `PaqueteTuristicoTest#precioFinal_aplicaElDescuentoUnaSolaVezPorPaquete`
  - `PaqueteTuristicoTest#paqueteAnidado_calculaElPrecioFinalRecursivamente`
  - `PaqueteTuristicoTest#precioBase_esLaSumaDeLosPreciosBaseDeSusHijos`
  - `ReporteServiciosMasReservadosTest#generar_conElPackEscapadaUrbana_cobraLosServiciosConEl10PorCientoDeDescuento`
  - `ReporteServiciosMasReservadosTest#generar_conPaquetesAnidados_acumulaLosDescuentosDeTodaLaJerarquia`
  - `ReporteServiciosMasReservadosTest#generar_servicioVendidoSueltoYDentroDeUnPaquete_promediaAmbosPrecios`
- Cambio sugerido: dividir por `100.0` en los dos métodos, y que `precioBase()` sume
  `Catalogo::precioBase` (el `precioFinal()` sigue sumando `precioFinal()` de los hijos para que el
  descuento de un paquete anidado se respete).

### H31 — Bug: agregar un ítem a un paquete le consume un cupo

`PaqueteTuristico.agregarItem(item)` llama a `item.decrementarCupo()`. Armar un paquete no es reservar:
el cupo se tiene que bloquear cuando se **confirma** una reserva (enunciado 2.2), y eso ya lo hace
`Reserva.bloquearCupos()`. Hoy, cada paquete que incluye un hotel le resta un cupo para siempre.

- Test: `PaqueteTuristicoTest#agregarItem_noConsumeElCupoDelItem`
- Cambio sugerido: borrar la línea `item.decrementarCupo();` de `agregarItem`.

### H32 — Bug: `GeneradorComprobantes` nunca genera la factura ni el voucher

Enunciado 2.5: *"crea el comprobante fiscal y voucher cuando la reserva **alcanza** el estado
FINALIZADA"*. El código pregunta `estadoAnterior.debeGenerarVoucher()`, o sea, genera cuando la reserva
**sale** de `Finalizada`. Como `Finalizada` es terminal, eso no pasa nunca.

- Tests: los 5 de `GeneradorComprobantesTest` que esperan comprobantes al pasar a `Finalizada`.
- Cambio sugerido: `if (estadoActual.debeGenerarVoucher())`.

### H33 — Bug: los mails no llegan al cliente

Enunciado 2.5: el notificador *"envía un correo **al cliente** con el nuevo estado"* y el club *"se le
envía un mensaje"*. `NotificadorEmail` y `ClubViajeros` llaman a `enviarMail(null, ...)`. La reserva ya
guarda el email del cliente (`Reserva.getEmail()`).

Además `NotificadorEmail` arma el mensaje con `"...a" + estadoActual`, que muestra el `toString()` del
objeto (`unq.reserva.Pagado@1b2c3d`) en lugar del nombre del estado (y le falta un espacio).

- Tests:
  - `NotificadorEmailTest#elMailSeEnviaAlEmailDelCliente`
  - `NotificadorEmailTest#elMensajeNoMuestraElObjetoInternoDelEstado`
  - `ClubViajerosTest#elMensajeSeEnviaAlClienteDeLaReserva`
- Cambio sugerido: usar `reserva.getEmail()` como destino en los dos, y `estadoActual.nombre()` en el
  mensaje.

### H34 — Bug: `PuntoEncuentro` mira el cupo de guías del destino y no el del punto

Enunciado 2.3: *"La estimación de disponibilidad depende de si hay cupo de guías **en dicho punto**"*.
`estaDisponible(destino)` y `asignarGuia(destino)` consultan y descuentan el cupo de la `Direccion`
recibida por parámetro, no el de `ubicacion` (la dirección del punto). Un punto sin guías aparece
disponible si el hotel de destino tiene cupo.

- Tests: los 6 de `PuntoEncuentroTest` sobre disponibilidad y asignación de guías.
- Cambio sugerido: usar `ubicacion.getCuposDisponibles()` y `ubicacion.asignarGuia()`. El parámetro
  `destino` deja de ser necesario.

### H38 — Falta: `ServicioIndividual.validar()` no valida los atributos dinámicos

Enunciado 2.1: *"Un servicio debería poder ser validado [...] La validación de un servicio comprueba (a)
que todo atributo obligatorio —fijo o dinámico— tenga valor"*. Hoy `validar()` sólo mira los fijos. La
obligatoriedad se chequea en el constructor de `AtributoDinamico`, así que no se puede **definir** un
atributo (nombre + obligatoriedad + tipo) y asignarle el valor después. Además `Texto/Numero/...
.esValido(null)` da `false`, entonces ni siquiera un atributo **opcional** se puede crear sin valor.

- Tests:
  - `ServicioIndividualTest#validar_*` (4 tests de atributos dinámicos)
  - `AtributoDinamicoTest#constructor_obligatorioSinValor_noLanza`,
    `#setValor_nuloEnObligatorio_noLanza_loDetectaLaValidacionDelServicio` y los 6 que ya fallaban
    (`constructor_noObligatorioSinValor_noLanza`, `tieneNombre_*`, `mismoNombreQue_*`).
- Cambio sugerido: en `AtributoDinamico.validar(valor)` chequear el tipo sólo si `valor != null`; agregar
  `boolean esValido()` (`!obligatorio || valor != null`) y que `ServicioIndividual.validar()` lance
  `ExcepcionDominio` si algún atributo dinámico no es válido.

### H39 — `EstadoDeReserva` acepta en silencio las operaciones que un estado no redefine

Enunciado 2.2: *"Intentar una operación inválida debe lanzar una excepción de dominio propia"*. Los
métodos de `EstadoDeReserva` tienen cuerpo vacío: un estado nuevo que no redefina `confirmar` lo acepta
sin hacer nada. En el patrón State conviene que el estado abstracto declare todo como inválido y que
cada estado concreto redefina sólo sus transiciones válidas (además elimina los ~30 `throw` repetidos
de los estados concretos).

- Tests: los 7 de `EstadoDeReservaTest`.
- Cambio sugerido: que cada método de `EstadoDeReserva` lance `ExcepcionPorInvalido`; después se pueden
  borrar de los estados concretos los métodos que sólo lanzan.

### H40 — `TarjDeCredito` ejecuta la transacción aunque el banco rechace la pre-autorización

`api.preAutorizar(...)` devuelve `boolean`, pero `reservarFondos()` lo ignora: con la pre-autorización
rechazada igual se transfiere y se registra el voucher.

- Test: `TarjDeCreditoTest#procesoDePago_conPreAutorizacionRechazada_lanzaPagoInvalidoYNoTransfiere`
- Cambio sugerido: en `reservarFondos()`, si `preAutorizar` da `false`, lanzar `ExcepcionPagoInvalido`.

### H41 — `PagoTransferencia` no soporta la transferencia programada

Enunciado 2.4: *"Transferencia inmediata o programada"*. `fechaProgramada` se guarda pero nunca se usa.
Se agregó a la interfaz `APIBancaria` el mensaje `programarTransferencia(cbu, monto, fecha)` (sólo la
definición, como pide el enunciado).

- Test: `PagoTransferenciaTest#ejecutarTransaccion_conFechaProgramada_programaLaTransferenciaParaEsaFecha`
- Cambio sugerido: en `ejecutarTransaccion()`, si `fechaProgramada != null` llamar a
  `programarTransferencia`; si no, a `transferirDeInmediato`.

---

# Decisiones de diseño (no son bugs, conviene defenderlas en la entrevista)

### H4 — Precio base del paquete

El texto del enunciado dice que el precio base de un paquete es `suma × (1 − descuento%)`, pero la tabla
muestra precio base = suma ($21.000) y precio final = suma × (1 − descuento) ($18.900). **Los tests
siguen la tabla.** Queda para consulta al docente.

### H5 — `Texto.esValido("")` devuelve `true`

Sólo se valida la consistencia entre el valor y el tipo declarado, no que el texto tenga contenido. Es
consistente con "sólo debe chequearse la asignación de un valor". Si el docente exige texto no vacío, el
cambio va en `Texto`, no en `AtributoDinamico`.

### H6 — `ServicioIndividual.validar()` exige `operador` y `categoria`

El enunciado menciona explícitamente `nombre` y `código` como atributos fijos obligatorios; el código
además exige `operador` y `categoria` no nulos. Es razonable ("entre otros").

### H35 — Reembolsos: qué se devuelve en cada estado

- **Cancelar en `EN_CHECKIN`** → `Reserva.reembolsarServiciosYTasas()`: voucher por el valor de los
  servicios más el costo del método de traslado (las "tasas de reserva"). Sin método de traslado
  elegido, las tasas son 0.
- **Cancelar en `EN_CURSO`** → `Reserva.reembolsarServicios()`: voucher sólo por los servicios, sin
  tasas. El enunciado habla de "servicios no disfrutados", pero el modelo no registra qué servicios ya se
  usaron, así que se reembolsan todos los servicios.

Cada reembolso queda registrado en la reserva (`getVouchersDeReembolso()`).

### H36 — Cupos por pasajero

Al confirmar, `Reserva.bloquearCupos()` descuenta **un cupo por pasajero** en cada ítem; al cancelar
desde `CONFIRMADA` o `EN_CHECKIN`, `liberarCupos()` los devuelve. El descuento se hace en cada fecha
de la reserva, y `ReporteOcupacion` lee esos mismos cupos del `Cronograma` de cada servicio.

### H37 — Comprobantes de pago

La notificación por defecto de `MedioDePago` registra un `ComprobantePago` con el código de transacción.
`TarjDeCredito` registra un voucher y `PagoTransferencia` un comprobante CBU con el número de operación
(el código de transacción). `BilleteraVirtual` sólo manda la notificación push, como pide la tabla.

---

# Notas

### H9 — `PlataformaTransporte` y `TrasladoVIP` son stubs del proyecto

El enunciado las presenta como librerías externas ("que sólo debe utilizar"). En el repo son clases
propias con un cuerpo de relleno (`return 0` / `return precio`). En los tests **siempre se mockean**: no
tiene sentido testear un relleno, porque cualquier test sobre ellas verificaría la implementación falsa
y no el enunciado.

### H10 — El UML no está en el repo

No hay ningún `.drawio` en el repositorio. La comparación **código vs UML** queda pendiente hasta que se
suba el diagrama.
