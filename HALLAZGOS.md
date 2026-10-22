# HALLAZGOS — TP Integrador UNQ-Travel

Registro de hallazgos, decisiones de diseño y diferencias entre el enunciado, el UML y el código.
Los tests se escriben a partir del enunciado y de las reglas de negocio; el código manda cuando el
enunciado no define un comportamiento.

**Estado:** Módulos 1 (Catálogo), 2 (Reserva y estados), 3 (Traslado) y 4 (Métodos de pago) entregados.
Módulo 5 (Buscador y filtros) pendiente: no hay código en el repo (ver H8/H28).

---

## Cómo se corrió

```bash
mvn -Dmaven.compiler.source=21 -Dmaven.compiler.target=21 test
```

El `pom.xml` declara `maven.compiler.source/target = 26` (igual que los otros TPs) y el JDK de esta
máquina es 21, por eso se compila con el override. El `pom` ya incluye JaCoCo 0.8.15; el reporte queda
en `target/site/jacoco/`.

## Dónde viven los tests

Este proyecto no usa `src/test/java`: el `pom.xml` define `sourceDirectory=${project.basedir}/src` y
`testSourceDirectory=${project.basedir}/test`. Los tests van en `test/unq/<paquete>/`.

## Qué NO se tocó

`src/` (producción) quedó intacto durante todo el trabajo. Sólo se escribieron tests en `test/`, este
documento y el `pom.xml` (para agregar JaCoCo). No se hizo `git commit` ni `git push`.

---

# Módulo 1 — Catálogo

## 1.1 Lectura del código

| Clase | Constructor | Colaboradores | Métodos públicos | Excepciones que lanza |
|---|---|---|---|---|
| `ServicioIndividual` | `ServicioIndividual(String codigo, String nombre, String descripcion, String operador, Categoria categoria, double precioBase)` | `AtributoDinamico` (lista interna), `Categoria` | `aplicarDescuentoPromocional(double)`, `precioBase()`, `precioFinal()`, `nombre()`, `descripcion()`, `contieneEnNombre(String)`, `esDeCategoria(Categoria)`, `agregarAtributoDinamico(AtributoDinamico)`, `setValorAtributo(String, Object)`, `validar()` | `ExcepcionDominio` |
| `AtributoDinamico` | `AtributoDinamico(String nombre, boolean obligatorio, TipoValor tipoValor)` | `TipoValor` | `setValor(Object)`, `validar()`, `tieneNombre(String)`, `mismoNombreQue(AtributoDinamico)` | — |
| `PaqueteTuristico` | `PaqueteTuristico(String nombre, String descripcion, int descuento)` | `Catalogo` (lista interna) | `agregarItem(Catalogo)`, `quitarItem(Catalogo)`, `precioBase()`, `precioFinal()`, `nombre()`, `descripcion()`, `contieneEnNombre(String)` | `ExcepcionDominio` (descuento fuera de 0..100) |
| `Catalogo` | interfaz | — | `nombre()`, `descripcion()`, `precioBase()`, `precioFinal()`, `contieneEnNombre(String)` | — |
| `TipoValor` | interfaz | — | `esValido(Object)` | — |
| `Texto`, `Numero`, `Fecha`, `Booleano` | — | — | `esValido(Object)` | — |
| `Categoria` | `Categoria(String nombre)` | — | `seLlama(String)`, `equals(Object)` | — |

## 1.2 Tabla de dobles

| Colaborador | Tipo de doble | Por qué |
|---|---|---|
| `AtributoDinamico` (en `ServicioIndividual`) | **Mock** | Colaborador con estado propio (nombre / obligatoriedad / valor / tipo). Se mockea para forzar el resultado de `validar()` y para verificar que `setValorAtributo` delegue **solo** en el atributo homónimo, sin depender de la regla de cada `TipoValor`. |
| `TipoValor` (en `AtributoDinamico`) | **Mock con stub** de `esValido(Object)` | Es la Strategy: el test de `AtributoDinamico` no debe conocer la regla de validación de cada tipo, sólo que se la consulta y propaga su respuesta. Permite comprobar además que **no** se consulta al tipo cuando no hay valor. |
| `Catalogo` (en `PaqueteTuristico`) | **Mock con stub** de `precioBase()` / `precioFinal()` | Permite fijar los precios de los hijos y aislar el cálculo de la suma y la aplicación del descuento del paquete, incluso con paquetes anidados. |
| `Categoria` | **Real** | Es un objeto valor sin comportamiento a doblar; la comparación por valor es justamente lo que se quiere verificar. |

## 1.3 Qué verifica cada test y cómo se usa Mockito

### `ServicioIndividualTest` (21 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `validar_conCodigoYNombrePresentesYSinDinamicos_noLanza` | El servicio completo y sin atributos dinámicos es válido | — (sin dobles) | `assertDoesNotThrow` |
| `validar_sinCodigo_lanzaExcepcionDeDominio` | El código es obligatorio | — | `assertThrows(ExcepcionDominio.class, ...)` |
| `validar_conCodigoEnBlanco_lanzaExcepcionDeDominio` | El código en blanco es tan inválido como el `null` | — | `assertThrows` |
| `validar_sinNombre_lanzaExcepcionDeDominio` | El nombre es obligatorio | — | `assertThrows` |
| `validar_sinOperador_lanzaExcepcionDeDominio` | El operador también es obligatorio (ver H6) | — | `assertThrows` |
| `validar_sinCategoria_lanzaExcepcionDeDominio` | La categoría es obligatoria (ver H6) | — | `assertThrows` |
| `validar_conUnAtributoDinamicoInvalido_lanzaExcepcionDeDominio` | Un atributo dinámico inválido invalida el servicio | **Mock** de `AtributoDinamico` con `when(invalido.validar()).thenReturn(false)` | `assertThrows` |
| `validar_recorreTodosLosAtributosDinamicosAunqueElPrimeroSeaValido` | La validación **no corta el recorrido** al primer atributo | Dos **mocks** (`valido` → `true`, `invalido` → `false`) + `verify(valido).validar()` y `verify(invalido).validar()` | `assertThrows` + ambos `verify` |
| `validar_conTodosLosAtributosDinamicosValidos_noLanza` | Varios atributos válidos no invalidan el servicio | Dos **mocks** con `validar()` → `true` | `assertDoesNotThrow` |
| `setValorAtributo_delegaSoloEnElAtributoConEseNombre` | `setValorAtributo` escribe **sólo** en el atributo homónimo | Dos **mocks** con `when(hotel.tieneNombre("CapacidadMaxima")).thenReturn(true)` / `(vuelo...) → false`; `verify(hotel).setValor(10)` y `verify(vuelo, never()).setValor(any())` | `verify` + `never()` |
| `setValorAtributo_conNombreInexistente_lanzaYNoModificaNinguno` | Un nombre desconocido no modifica nada | **Mock**; `verify(hotel, never()).setValor(any())` | `assertThrows` + `never()` |
| `agregarAtributoDinamico_conNombreRepetido_lanzaExcepcionDeDominio` | No se pueden registrar dos atributos con el mismo nombre | Dos **mocks** con `when(original.mismoNombreQue(repetido)).thenReturn(true)` | `assertThrows` |
| `precioFinal_sinDescuentoPromocional_esIgualAlPrecioBase` | Sin promoción, el precio final es el base | — | `assertEquals(15000.0, ...)` |
| `precioFinal_conDescuentoPromocional_aplicaElPorcentaje` | La promoción afecta sólo a ese servicio | — | `assertEquals(13500.0, ...)` |
| `aplicarDescuentoPromocional_conPorcentajeNegativo_lanzaExcepcionDeDominio` | El descuento promocional se valida | — | `assertThrows` |
| `aplicarDescuentoPromocional_conPorcentajeMayorACien_lanzaExcepcionDeDominio` | El descuento promocional se valida por arriba | — | `assertThrows` |
| `contieneEnNombre_conNombreNulo_esFalso` | Un servicio sin nombre no rompe la búsqueda | — | `assertFalse` |
| `contieneEnNombre_sinDistinguirMayusculas` | La búsqueda por nombre ignora mayúsculas | — | `assertTrue` |
| `contieneEnNombre_conTextoQueNoAparece_esFalso` | La búsqueda por nombre distingue textos | — | `assertFalse` |
| `esDeCategoria_conLaMismaCategoria_esVerdadero` | La comparación de categorías es por valor | — | `assertTrue` |
| `esDeCategoria_conOtraCategoria_esFalso` | La comparación de categorías distingue | — | `assertFalse` |

### `AtributoDinamicoTest` (9 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `validar_obligatorioSinValor_esInvalidoYNoConsultaElTipo` | Sin valor, un atributo obligatorio es inválido y **no** se pregunta al tipo | **Mock** de `TipoValor` + `verify(tipo, never()).esValido(any())` | `assertFalse` + `never()` |
| `validar_noObligatorioSinValor_esValidoYNoConsultaElTipo` | Sin valor, un atributo opcional es válido | **Mock** + `verify(tipo, never()).esValido(any())` | `assertTrue` + `never()` |
| `validar_conValorAceptadoPorElTipo_esValido` | Con valor, el resultado es el del tipo | **Mock** con `when(tipo.esValido(5)).thenReturn(true)` | `assertTrue` |
| `validar_conValorRechazadoPorElTipo_esInvalido` | Con valor incompatible, el atributo es inválido | **Mock** con `when(tipo.esValido("cinco")).thenReturn(false)` | `assertFalse` |
| `tieneNombre_conElMismoNombre_esVerdadero` | Comparación de nombres | — | `assertTrue` |
| `tieneNombre_conOtroNombre_esFalso` | Comparación de nombres | — | `assertFalse` |
| `tieneNombre_conNombreInternoNulo_esFalso` | Guarda contra `null` | — | `assertFalse` |
| `mismoNombreQue_conAtributosHomónimos_esVerdadero` | Detección de duplicados por nombre | — | `assertTrue` |
| `mismoNombreQue_conDistintoNombre_esFalso` | Detección de duplicados por nombre | — | `assertFalse` |

### `PaqueteTuristicoTest` (13 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `precioBase_esLaSumaDeLosPreciosBaseDeSusHijos` | El precio base es la suma de los precios base de los hijos | **Mocks** de `Catalogo` con `precioBase()` stubbeado (los `precioFinal` se stubean a `1` para probar que el cálculo usa el precio **base**) | `assertEquals(21000.0, ...)` |
| `precioFinal_casoDelEnunciado_devuelve18900` | El caso del enunciado: 15.000 + 4.000 + 2.000 con 10 % → 18.900 | **Mocks** con `precioBase()` y `precioFinal()` stubbeados | `assertEquals(18900.0, ...)` |
| `precioFinal_aplicaElDescuentoUnaSolaVezPorPaquete` | El descuento se aplica una vez, no por ítem | **Mock** de un solo ítem | `assertEquals(9000.0, ...)` |
| `precioBase_deUnPaqueteVacio_esCero` | Paquete sin ítems | — | `assertEquals(0.0, ...)` |
| `precioFinal_deUnPaqueteVacio_esCero` | Paquete sin ítems | — | `assertEquals(0.0, ...)` |
| `quitarItem_loSacaDeLaSumaDeSusPartes` | `quitarItem` descuenta el ítem | **Mock** de `Catalogo` | `assertEquals(0.0, ...)` |
| `paqueteAnidado_calculaElPrecioFinalRecursivamente` | Un paquete dentro de otro calcula bien en toda la jerarquía | **Paquete interno real** (con hijos mockeados) + **mock** de 5.000, dentro de un paquete externo con 20 % | `assertEquals(19120.0, ...)` |
| `agregarItem_conElMismoPaquete_noLanzaYNoSeValidaCiclo` | Documenta que hoy **no** se validan los ciclos (ver H2) | — | `assertDoesNotThrow` |
| `constructor_conDescuentoNegativo_lanzaExcepcionDeDominio` | El descuento se valida en el constructor | — | `assertThrows` |
| `constructor_conDescuentoMayorACien_lanzaExcepcionDeDominio` | El descuento se valida en el constructor | — | `assertThrows` |
| `contieneEnNombre_conNombreNulo_esFalso` | Guarda contra `null` | — | `assertFalse` |
| `contieneEnNombre_sinDistinguirMayusculas` | Búsqueda por nombre ignora mayúsculas | — | `assertTrue` |
| `contieneEnNombre_conTextoQueNoAparece_esFalso` | Búsqueda por nombre distingue textos | — | `assertFalse` |

### `TipoValorTest` (14 tests) y `CategoriaTest` (8 tests)

`TipoValorTest` no usa dobles: son las Strategies, se prueban directo (valor del tipo correcto, de tipo
incorrecto, `null`, y texto vacío que es válido porque sólo se valida el tipo — ver H5).

`CategoriaTest` tampoco usa dobles (es un objeto valor). Su test
`hashCode_conNombresIguales_deberiaSerIgual` **falla a propósito** y documenta H1.

---

# Módulo 2 — Reserva y estados (State)

## 2.1 Lectura del código

| Clase | Constructor | Colaboradores | Métodos públicos | Excepciones que lanza |
|---|---|---|---|---|
| `Reserva` (Context) | `Reserva(Direccion destino, int cantidadDePasajeros)` | `EstadoDeReserva` (estado actual), `Direccion`, `List<Catalogo>` | `agregarItem(Catalogo)`, `quitarItem(Catalogo)`, `confirmar()`, `hacerCheckin()`, `iniciar()`, `finalizar()`, `cancelar()`, `cambiarEstado(EstadoDeReserva)`, `bloquearCupos()`, `liberarCupos()`, `reembolsar()`, `cantidadDePasajeros()`, `valorTotal()`, `getDestino()` | — (los estados lanzan) |
| `EstadoDeReserva` | interfaz | — | `agregarItem(Reserva, Catalogo)`, `quitarItem(Reserva, Catalogo)`, `confirmar(Reserva)`, `hacerCheckin(Reserva)`, `iniciar(Reserva)`, `finalizar(Reserva)`, `cancelar(Reserva)` | — |
| `SolicitudPendiente` | — | `Reserva` | los 7 de la interfaz | `ExcepcionPorInvalido` en `hacerCheckin`, `iniciar`, `finalizar` |
| `Pagado` | — | `Reserva` | los 7 de la interfaz | `ExcepcionPorInvalido` en `agregarItem`, `quitarItem`, `confirmar`, `iniciar`, `finalizar` |
| `EnChecking` | — | `Reserva` | los 7 de la interfaz | `ExcepcionPorInvalido` en `agregarItem`, `quitarItem`, `confirmar`, `hacerCheckin`, `finalizar` |
| `EnCurso` | — | `Reserva` | los 7 de la interfaz | `ExcepcionPorInvalido` en `agregarItem`, `quitarItem`, `confirmar`, `hacerCheckin`, `iniciar` |
| `Finalizada` | — | `Reserva` | los 7 de la interfaz | `ExcepcionPorInvalido` en las 7 operaciones (terminal) |
| `Cancelada` | — | `Reserva` | los 7 de la interfaz | `ExcepcionPorInvalido` en las 7 operaciones (terminal) |
| `ExcepcionPorInvalido` | `ExcepcionPorInvalido(String mensaje)` | — | — | `extends RuntimeException` (ver H13) |

Métodos **package-private** de `Reserva` (usados por los estados, verificables desde el mismo paquete):
`agregarItemDirecto(Catalogo)` y `quitarItemDirecto(Catalogo)`.

## 2.2 Tabla de dobles

| Colaborador | Tipo de doble | Por qué |
|---|---|---|
| `EstadoDeReserva` (en `Reserva`) | **Mock** | `Reserva` es el Context y no debe saber qué hace cada estado: con un estado mockeado se verifica que cada operación pública **delega** y que le pasa **a sí misma**. También permite comprobar que `cambiarEstado` reemplaza al estado anterior. |
| `Reserva` (en los estados concretos) | **Mock** | Cada estado sólo conoce el contrato del Context. Mockeándolo se verifica a qué estado transiciona (`ArgumentCaptor`), qué efectos dispara (`bloquearCupos` / `liberarCupos` / `reembolsar`) y, en las operaciones inválidas, que **no** cambia el estado. |
| `Reserva` (en `ReservaTest`, para el flujo real) | **Spy** | Para verificar el camino completo con los estados reales (`confirmar` → `hacerCheckin` → `cancelar`) y comprobar los efectos sobre el Context real, sin que el test dependa de la lista interna de ítems. |
| `Catalogo` (ítem de la reserva) | **Real** | No tiene comportamiento a doblar; sólo se usa como dato para las operaciones `agregarItem` / `quitarItem`. |
| `Direccion` | **Real** | Objeto valor. Es el dato que consume el módulo de traslado. |

## 2.3 Qué verifica cada test y cómo se usa Mockito

### `ReservaTest` (18 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `agregarItem_delegaEnElEstadoActualPasandoLaReservaYElItem` | La Context delega y se pasa a sí misma | **Mock** de `EstadoDeReserva` inyectado con `cambiarEstado`; `verify(estado).agregarItem(reserva, item)` | `verify` |
| `quitarItem_delegaEnElEstadoActualPasandoLaReservaYElItem` | Ídem para quitar | **Mock** de estado | `verify` |
| `confirmar_delegaEnElEstadoActualPasandoLaReserva` | Ídem para confirmar | **Mock** de estado | `verify` |
| `hacerCheckin_delegaEnElEstadoActualPasandoLaReserva` | Ídem para el check-in | **Mock** de estado | `verify` |
| `iniciar_delegaEnElEstadoActualPasandoLaReserva` | Ídem para iniciar | **Mock** de estado | `verify` |
| `finalizar_delegaEnElEstadoActualPasandoLaReserva` | Ídem para finalizar | **Mock** de estado | `verify` |
| `cancelar_delegaEnElEstadoActualPasandoLaReserva` | Ídem para cancelar | **Mock** de estado | `verify` |
| `cambiarEstado_reemplazaElEstadoAnteriorPorElNuevo` | El estado nuevo sustituye al anterior | Dos **mocks**; `verify(nuevo).confirmar(reserva)` y `verify(anterior, never()).confirmar(any())` | `verify` + `never()` |
| `nuevaReserva_empiezaEnSolicitudPendiente` | El estado inicial es `SolicitudPendiente` | — (estados reales) | `assertDoesNotThrow` + `assertThrows(ExcepcionPorInvalido.class, ...)` |
| `agregarItem_enSolicitudPendiente_llegaALosItemsDeLaReserva` | El estado real agrega el ítem al Context | **Spy** de `Reserva`; `verify(espia).agregarItemDirecto(item)` (método package-private) | `verify` |
| `quitarItem_enSolicitudPendiente_llegaALosItemsDeLaReserva` | Ídem para quitar | **Spy** | `verify` |
| `confirmar_bloqueaElCupoDeLasUnidades` | Confirmar bloquea el cupo | **Spy**; `verify(espia).bloquearCupos()` | `verify` |
| `cancelar_desdeSolicitudPendiente_noLiberaCuposNiReembolsa` | Cancelar antes de confirmar no toca cupos ni reembolsos | **Spy**; `verify(espia, never()).liberarCupos()` / `reembolsar()` | `never()` |
| `cancelar_desdeEnChecking_liberaElCupoDeLasUnidades` | Cancelar en check-in libera el cupo | **Spy** con el flujo real `confirmar` → `hacerCheckin` → `cancelar` | `verify` |
| `cancelar_desdeEnChecking_reembolsaLosServiciosYTasasDeReserva` | Cancelar en check-in reembolsa | **Spy** con el flujo real | `verify` |
| `cantidadDePasajeros_devuelveLaCantidadDelConstructor` | Dato que necesita el traslado estándar | — | `assertEquals` |
| `getDestino_devuelveElDestinoDelConstructor` | Dato que necesita el traslado estándar | — | `assertSame` |
| `valorTotal_devuelveCero_mientrasLaFormulaNoEsteDefinida` | Documenta el placeholder actual (ver H3) | — | `assertEquals(0f, ...)` |

### `SolicitudPendienteTest` (10 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `agregarItem_agregaElItemALaReserva` | En solicitud pendiente se pueden agregar ítems | **Mock** de `Reserva`; `verify(reserva).agregarItemDirecto(item)` | `verify` |
| `quitarItem_quitaElItemDeLaReserva` | En solicitud pendiente se pueden quitar ítems | **Mock** de `Reserva` | `verify` |
| `confirmar_bloqueaElCupoDeLasUnidades` | Confirmar bloquea el cupo | **Mock** de `Reserva`; `verify(reserva).bloquearCupos()` | `verify` |
| `confirmar_cambiaElEstadoAPagado` | Confirmar transiciona a `Pagado` | **Mock** + `ArgumentCaptor<EstadoDeReserva>` | `assertInstanceOf(Pagado.class, captor.getValue())` |
| `confirmar_bloqueaElCupoAntesDeCambiarElEstado` | El orden de los efectos | **Mock** + `inOrder(reserva)` | verificaciones en orden |
| `cancelar_cambiaElEstadoACancelada` | Cancelar transiciona a `Cancelada` | **Mock** + `ArgumentCaptor` | `assertInstanceOf(Cancelada.class, ...)` |
| `cancelar_noLiberaCuposNiReembolsa` | Cancelar antes de confirmar no libera ni reembolsa | **Mock**; `never().liberarCupos()`, `never().reembolsar()` | `never()` |
| `hacerCheckin_lanzaExcepcionDeDominioYNoCambiaElEstado` | Operación inválida: lanza y no transiciona | **Mock**; `never().cambiarEstado(any())` | `assertThrows` + `never()` |
| `iniciar_lanzaExcepcionDeDominioYNoCambiaElEstado` | Ídem | **Mock** | `assertThrows` + `never()` |
| `finalizar_lanzaExcepcionDeDominioYNoCambiaElEstado` | Ídem | **Mock** | `assertThrows` + `never()` |

### `PagadoTest` (9 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `hacerCheckin_cambiaElEstadoAEnChecking` | Check-in transiciona a `EnChecking` | **Mock** + `ArgumentCaptor` | `assertInstanceOf(EnChecking.class, ...)` |
| `cancelar_liberaElCupoDeLasUnidades` | Cancelar desde confirmada libera el cupo | **Mock**; `verify(reserva).liberarCupos()` | `verify` |
| `cancelar_cambiaElEstadoACancelada` | Cancelar transiciona a `Cancelada` | **Mock** + `ArgumentCaptor` | `assertInstanceOf(Cancelada.class, ...)` |
| `cancelar_noReembolsa` | Cancelar desde confirmada no reembolsa | **Mock**; `never().reembolsar()` | `never()` |
| `agregarItem_lanzaExcepcionDeDominioYNoCambiaElEstado` | Ya no se pueden agregar ítems | **Mock** | `assertThrows` + `never()` |
| `quitarItem_lanzaExcepcionDeDominioYNoCambiaElEstado` | Ya no se pueden quitar ítems | **Mock** | `assertThrows` + `never()` |
| `confirmar_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se puede confirmar dos veces | **Mock** | `assertThrows` + `never()` |
| `iniciar_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se puede iniciar sin check-in | **Mock** | `assertThrows` + `never()` |
| `finalizar_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se puede finalizar estando pagada | **Mock** | `assertThrows` + `never()` |

### `EnCheckingTest` (10 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `iniciar_cambiaElEstadoAEnCurso` | Iniciar transiciona a `EnCurso` | **Mock** + `ArgumentCaptor` | `assertInstanceOf(EnCurso.class, ...)` |
| `cancelar_liberaElCupoDeLasUnidades` | Cancelar en check-in libera el cupo | **Mock**; `verify(reserva).liberarCupos()` | `verify` |
| `cancelar_reembolsaLosServiciosYTasasDeReserva` | Cancelar en check-in reembolsa servicios **y** tasas | **Mock**; `verify(reserva).reembolsar()` | `verify` |
| `cancelar_cambiaElEstadoACancelada` | Cancelar transiciona a `Cancelada` | **Mock** + `ArgumentCaptor` | `assertInstanceOf(Cancelada.class, ...)` |
| `cancelar_liberaElCupoAntesDeReembolsarYAntesDeCambiarElEstado` | Orden de los tres efectos | **Mock** + `inOrder(reserva)` | verificaciones en orden |
| `agregarItem_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se pueden agregar ítems | **Mock** | `assertThrows` + `never()` |
| `quitarItem_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se pueden quitar ítems | **Mock** | `assertThrows` + `never()` |
| `confirmar_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se puede confirmar otra vez | **Mock** | `assertThrows` + `never()` |
| `hacerCheckin_lanzaExcepcionDeDominioYNoCambiaElEstado` | El check-in ya se hizo | **Mock** | `assertThrows` + `never()` |
| `finalizar_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se puede finalizar en check-in | **Mock** | `assertThrows` + `never()` |

### `EnCursoTest` (9 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `finalizar_cambiaElEstadoAFinalizada` | Finalizar transiciona a `Finalizada` | **Mock** + `ArgumentCaptor` | `assertInstanceOf(Finalizada.class, ...)` |
| `cancelar_reembolsaLosServiciosNoDisfrutados` | Cancelar en curso reembolsa | **Mock**; `verify(reserva).reembolsar()` | `verify` |
| `cancelar_noLiberaElCupoPorqueLasUnidadesYaEstanEnUso` | En curso el cupo ya está consumido: **no** se libera | **Mock**; `never().liberarCupos()` | `never()` |
| `cancelar_cambiaElEstadoACancelada` | Cancelar transiciona a `Cancelada` | **Mock** + `ArgumentCaptor` | `assertInstanceOf(Cancelada.class, ...)` |
| `agregarItem_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se pueden agregar ítems | **Mock** | `assertThrows` + `never()` |
| `quitarItem_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se pueden quitar ítems | **Mock** | `assertThrows` + `never()` |
| `confirmar_lanzaExcepcionDeDominioYNoCambiaElEstado` | No se puede confirmar otra vez | **Mock** | `assertThrows` + `never()` |
| `hacerCheckin_lanzaExcepcionDeDominioYNoCambiaElEstado` | El check-in ya se hizo | **Mock** | `assertThrows` + `never()` |
| `iniciar_lanzaExcepcionDeDominioYNoCambiaElEstado` | El viaje ya está en curso | **Mock** | `assertThrows` + `never()` |

### `FinalizadaTest` (8) y `CanceladaTest` (8)

Cada estado terminal tiene un test por cada una de las 7 operaciones (`agregarItem`, `quitarItem`,
`confirmar`, `hacerCheckin`, `iniciar`, `finalizar`, `cancelar`) que verifica que lanzan
`ExcepcionPorInvalido`, más un test que verifica que **ninguna** operación cambia el estado
(`verify(reserva, never()).cambiarEstado(any())`). En `CanceladaTest` se verifica además que cancelar
no libera cupos.

---

# Módulo 3 — Traslado (Strategy + Adapter)

## 3.1 Lectura del código

| Clase | Constructor | Colaboradores | Métodos públicos | Excepciones que lanza |
|---|---|---|---|---|
| `MetodoTraslado` | interfaz | — | `calcularCosto(Reserva)` | — |
| `TrasladoEstandar` **implementa** `MetodoTraslado` | `TrasladoEstandar(PlataformaTransporte)` | `PlataformaTransporte` (inyectado) | `calcularCosto(Reserva)` | — |
| `TrasladoChauffeur` **no implementa** `MetodoTraslado` (ver H18) | `TrasladoChauffeur(TrasladoVIP)` | `TrasladoVIP` (inyectado) | `calcularCosto(Reserva)` | — |
| `PuntoEncuentro` **no implementa** `MetodoTraslado` (ver H18) | `PuntoEncuentro(String ubicacion, int cuposGuias)` | — | `calcularCosto(Reserva)`, `estaDisponible(Reserva)`, `getUbicacion()` | — |
| `TrasladoVIP` (stub de librería externa) | implícito | — | `calcularCosto(Float precio)` → `0` | — |
| `PlataformaTransporte` (stub de librería externa) | implícito | — | `estimarTraslado(int, Direccion)` → `0` | — |
| `Direccion` | `Direccion(String calle, int numero, String ciudad, String codigoPostal)` | — | (sin getters) | — |

**Mapa de responsabilidades tal como está el código:**

- `TrasladoEstandar` es la *Strategy* y, a la vez, el *Adapter* de `PlataformaTransporte`: lee los datos
  de la `Reserva` y delega la estimación en la plataforma.
- `TrasladoChauffeur` es la *Strategy* del traslado VIP y el *Adapter* de `TrasladoVIP`: delega el
  cálculo en el servicio VIP usando el valor total de la reserva.
- `PuntoEncuentro` no tiene librería: el costo es siempre 0 y la disponibilidad depende del cupo de
  guías.

**Datos que las estrategias leen de la `Reserva`:** `cantidadDePasajeros()`, `getDestino()` y
`valorTotal()`. Los tres existen, pero `valorTotal()` devuelve `0` (ver H3) → en producción el traslado
Chauffeur/VIP costaría siempre 0.

## 3.2 Tabla de dobles

| Colaborador | Tipo de doble | Por qué |
|---|---|---|
| `Reserva` | **Mock con stub** | Es el dato de entrada de las estrategias. No interesa su comportamiento, sólo los tres valores que leen (`cantidadDePasajeros`, `getDestino`, `valorTotal`). Mockeándola se evita depender de la `Reserva` real y de su estado, y se puede fijar el valor total sin tocar producción (H3). |
| `PlataformaTransporte` | **Mock** | El prompt la clasifica como librería externa: **siempre mock**, nunca se llama a la real. además la clase real es un stub que devuelve `0` ("para que no moleste"), así que mockearla es lo único que permite fijar un costo de verdad. |
| `TrasladoVIP` | **Mock** | Ídem: librería externa, siempre mock. La real devuelve `0` y toma un `Float` boxingado (H20). |
| `Direccion` | **Real** | Objeto valor sin comportamiento; se compara por identidad con el `verify` del mock de la plataforma. |

## 3.3 Qué verifica cada test y cómo se usa Mockito

### `TrasladoEstandarTest` (2 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `calcularCosto_devuelveElCostoEstimadoPorLaPlataforma` | El costo final es el que estima la plataforma | **Mock** de `PlataformaTransporte` con `when(plataforma.estimarTraslado(3, destino)).thenReturn(1500f)`; `Reserva` mock con `cantidadDePasajeros()` → 3 y `getDestino()` → `destino` | `assertEquals(1500f, costo, 0.001f)` |
| `calcularCosto_pasaLaCantidadDePasajerosYElDestinoALaPlataforma` | Se delegan los **dos** datos que la plataforma necesita | **Mock** de `PlataformaTransporte`; `verify(plataforma).estimarTraslado(3, destino)` (coincidencia por valor exacto de los 2 argumentos) | `verify` |

### `TrasladoChauffeurTest` (2 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `calcularCosto_devuelveElCostoCalculadoPorElServicioVIP` | El costo final es el que calcula el servicio VIP | **Mock** de `TrasladoVIP` con `when(servicioVIP.calcularCosto(15000f)).thenReturn(2000f)`; `Reserva` mock con `valorTotal()` → `15000f` | `assertEquals(2000f, costo, 0.001f)` |
| `calcularCosto_pasaElValorTotalDeLaReservaAlServicioVIP` | Se delega el **valor total de la reserva** (no el precio de un ítem suelto) | **Mock** de `TrasladoVIP`; `verify(servicioVIP).calcularCosto(15000f)` | `verify` |

### `PuntoEncuentroTest` (4 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `calcularCosto_esSiempreCero` | El punto de encuentro no tiene costo | `Reserva` **mock** (el método ni la usa) | `assertEquals(0f, costo, 0.001f)` |
| `estaDisponible_conCuposDeGuias_esVerdadero` | Con cupos de guías disponibles, el punto está disponible | `Reserva` **mock** | `assertTrue` |
| `estaDisponible_sinCuposDeGuias_esFalso` | Sin cupos de guías, el punto no está disponible | `Reserva` **mock** | `assertFalse` |
| `estaDisponible_noDependeDeLaCantidadDePasajerosDeLaReserva` | Documenta el comportamiento actual: la disponibilidad mira sólo el cupo de guías, no los pasajeros (ver H19) | `Reserva` **mock** con `cantidadDePasajeros()` → 50 | `assertTrue` |

### `MetodoTrasladoTest` (3 tests)

Verifica que los tres métodos de traslado sean **intercambiables** a través de la interfaz
`MetodoTraslado` (o sea, que el patrón Strategy esté implementado).

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `trasladoEstandar_esUnMetodoDeTraslado` | El traslado estándar es una estrategia | `PlataformaTransporte` **mock** (nunca se invoca) | `assertInstanceOf(MetodoTraslado.class, ...)` → **pasa** |
| `trasladoChauffeur_esUnMetodoDeTraslado` | El traslado Chauffeur/VIP es una estrategia | `TrasladoVIP` **mock** (nunca se invoca) | `assertInstanceOf(...)` → **FALLA** (H18) |
| `puntoDeEncuentro_esUnMetodoDeTraslado` | El punto de encuentro es una estrategia | — | `assertInstanceOf(...)` → **FALLA** (H18) |

---

# Módulo 4 — Métodos de pago (Template Method)

## 4.1 Lectura del código

| Clase | Constructor | Colaboradores | Métodos públicos | Excepciones que lanza |
|---|---|---|---|---|
| `MedioDePago` (abstracta, es la plantilla) | `MedioDePago(float monto, String codigoTransaccion)` | ninguno; guarda `monto` (**público**) y `codigoTransaccion` (**privado y nunca leído**, ver H23) | `procesoDePago()` (**no `final`**, ver H21) y los 4 pasos: `validarDatos()`, `reservarFondos()`, `ejecutarTransaccion()`, `notificarResultado()` (los 4 **abstractos**, ver H22) | `ExcepcionDominio("los datos no fueron validados correctamente")` en `procesoDePago()` cuando `validarDatos()` devuelve `false` |
| `TarjDeCredito` | `TarjDeCredito(int nroTarjeta, int cvv, LocalDateTime vencimiento, float monto, String codigoDeTransaccion)` | `APITarjetaDeCredito api` — **nunca se asigna** (no llega por constructor, ver H24) | los 4 pasos | `NullPointerException` (no es de dominio) al usar `validarDatos()` sin inyectar la API |
| `PagoTransferencia` | `PagoTransferencia(int CBU, String alias, LocalDateTime fechaProgramada, APIBancaria api, float monto, String codigoTransaccion)` | `APIBancaria api` (sí inyectada) | los 4 pasos | — |
| `BilleteraVirtual` | `BilleteraVirtual(float monto, APIBilletera api, String idCuenta, String codigoTransaccion)` | `APIBilletera api` (sí inyectada) | los 4 pasos | — |
| `APITarjetaDeCredito` (interfaz, **sólo definida**) | — | — | `validarDatos(TarjDeCredito)`, `validarCVV(int,int)`, `validarVencimiento(LocalDateTime)`, `preAutorizar(int,float)`, `transferirInmediata(float)` | — |
| `APIBancaria` (interfaz, **sólo definida**) | — | — | `validarDatos(PagoTransferencia)`, `validarCBU(int)`, `validarAlias(String)`, `transferirDeInmedioto(int,float)` | — |
| `APIBilletera` (interfaz, **sólo definida**) | — | — | `validarDatos(BilleteraVirtual)`, `bloquearElSaldo(String,float)`, `acreditarVendedor(String,float)`, `notificarPush(String,float)` | — |

**Mapa de responsabilidades tal como está el código:**

- `MedioDePago.procesoDePago()` es la plantilla: valida → reserva → ejecuta → notifica, y aborta con
  `ExcepcionDominio` si la validación falla.
- Los 3 pasos obligatorios (`validarDatos`, `reservarFondos`, `ejecutarTransaccion`) son abstractos y
  cada medio los implementa con su propia API.
- El 4º paso (`notificarResultado`) **debería** tener implementación por defecto y hoy es abstracto.

**Decisiones de diseño (no son bugs):**

- **Las interfaces API no están implementadas a propósito.** El enunciado dice literalmente *"No
  necesita implementar estas interfaces, sólo definirlas"*. Los tests las mockean siempre.
- **El monto se guarda en el constructor**, no como un paso de la plantilla. El prompt describe un paso
  "guardar el monto" dentro del método plantilla; el código lo resuelve como dato de construcción.

## 4.2 Tabla de dobles

| Colaborador | Tipo de doble | Por qué |
|---|---|---|
| Subclase mínima de `MedioDePago` declarada en los tests (`MedioDePagoMinimo`) | **Spy** | La plantilla es abstracta y no se puede instanciar. El prompt pide un spy sobre una subclase concreta mínima: así se verifica el **orden** de los pasos con `InOrder` sin atar el test a ninguna API externa. |
| `APITarjetaDeCredito` | **Mock** | Interfaz tipo API del enunciado: representa al banco emisor. Nunca se implementa ni se llama a una real. |
| `APIBancaria` | **Mock** | Ídem: representa al banco para la transferencia directa. |
| `APIBilletera` | **Mock** | Ídem: representa a la billetera / crypto. |
| `RegistroTransacciones` | **no existe** | El enunciado pide que la notificación por defecto "registre el código de transacción", pero no hay ninguna clase de registro en el repo (ver H22 y H23). Por eso ese paso no se puede verificar con un mock. |

**Nota sobre la inyección:** `TarjDeCredito` no recibe su API por constructor (ver H24), así que el test
tiene que inyectar el mock en el campo privado `api` con **reflexión**. Es un rodeo del test, no una
decisión de diseño: en cuanto se corrija H24, la reflexión se reemplaza por el parámetro del
constructor.

## 4.3 Qué verifica cada test y cómo se usa Mockito

### `MedioDePagoTest` (7 tests) — la plantilla

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `procesoDePago_ejecutaLosPasosEnElOrdenDelEnunciado` | La plantilla respeta el orden validar → reservar → ejecutar → notificar | **Spy** de la subclase mínima + `inOrder(medio)` | verificaciones en orden |
| `procesoDePago_conDatosValidos_ejecutaTodosLosPasos` | Con datos válidos se ejecutan los 4 pasos | **Spy** + `verify` | 4 `verify` |
| `procesoDePago_conDatosInvalidos_lanzaLaExcepcionDePagoInvalido` | Con datos inválidos el proceso se aborta con excepción | **Spy** con `doReturn(false).when(medio).validarDatos()` (stub que no ejecuta el método real) | `assertThrows(ExcepcionDominio.class, ...)` |
| `procesoDePago_conDatosInvalidos_noEjecutaNingunPasoPosterior` | Al abortar, ningún paso posterior corre | **Spy** + `verify(medio, never())` en los 3 pasos | `never()` |
| `elMontoQuedaGuardadoEnElMedioDePago` | El monto queda guardado (primer paso de la plantilla) | — | `assertEquals(1000f, medio.monto, 0.001f)` |
| `procesoDePago_esFinalParaQueNingunaSubclasePuedaCambiarElOrdenDeLosPasos` | La garantía de orden no se puede romper (ver H21) | reflexión sobre `Modifier.isFinal` | `assertTrue(...)` → **FALLA** |
| `notificarResultado_tieneUnaImplementacionPorDefectoEnLaClaseAbstracta` | La notificación por defecto existe (ver H22) | reflexión sobre `Modifier.isAbstract` | `assertFalse(...)` → **FALLA** |

### `TarjDeCreditoTest` (6 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `validarDatos_delegaLaValidacionEnLaApi` | La validación se delega a la API del banco emisor | **Mock** de `APITarjetaDeCredito` con `when(api.validarDatos(tarjeta)).thenReturn(true)`; mock inyectado por reflexión (H24) | `assertTrue` + `verify(api).validarDatos(tarjeta)` |
| `validarDatos_cuandoLaApiRechazaLosDatos_devuelveFalse` | Si la API rechaza, el medio queda inválido | **Mock** con `thenReturn(false)` | `assertFalse` |
| `reservarFondos_haceLaPreAutorizacionDelMontoEnLaTarjeta` | La reserva de fondos es la pre-autorización al banco emisor | **Mock**; `verify(api).preAutorizar(4539, 25000f)` | `verify` |
| `ejecutarTransaccion_transfiereElMontoDeInmediato` | La ejecución es la transferencia inmediata | **Mock**; `verify(api).transferirInmediata(25000f)` | `verify` |
| `notificarResultado_noGeneraElVoucherQuePideElEnunciado` | Documenta el vacío actual: el voucher que pide el enunciado no se genera (H22) | **Mock** + `verifyNoInteractions(api)` | `verifyNoInteractions` |
| `procesoDePago_conDatosValidos_validaPreAutorizaYTransfiereEnEseOrden` | El orden de las llamadas a la API | **Mock** + `inOrder(api)` | verificaciones en orden |

### `PagoTransferenciaTest` (5 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `validarDatos_delegaLaValidacionEnLaApi` | La validación se delega a la API bancaria | **Mock** de `APIBancaria` con `when(api.validarDatos(pago)).thenReturn(true)` | `assertTrue` + `verify` |
| `validarDatos_cuandoLaApiRechazaLosDatos_devuelveFalse` | Si la API rechaza, el medio queda inválido | **Mock** con `thenReturn(false)` | `assertFalse` |
| `reservarFondos_noInteractuaConLaApi` | En transferencia directa "no aplica" reservar fondos | **Mock** + `verifyNoInteractions(api)` | `verifyNoInteractions` |
| `ejecutarTransaccion_transfiereDeInmediatoElMontoALaCBU` | La transferencia inmediata usa la CBU y el monto | **Mock**; `verify(api).transferirDeInmedioto(12345678, 15000f)` | `verify` |
| `notificarResultado_noGeneraElComprobanteConElNumeroDeOperacion` | Documenta el vacío actual: el comprobante CBU con número de operación no se genera (H22) | **Mock** + `verifyNoInteractions(api)` | `verifyNoInteractions` |

### `BilleteraVirtualTest` (6 tests)

| Test | Qué verifica | Cómo se usa Mockito | Aserción clave |
|---|---|---|---|
| `validarDatos_delegaLaValidacionEnLaApi` | La validación (saldo suficiente) se delega a la API | **Mock** de `APIBilletera` con `when(api.validarDatos(billetera)).thenReturn(true)` | `assertTrue` + `verify` |
| `validarDatos_cuandoLaApiRechazaLosDatos_devuelveFalse` | Si la API rechaza, el medio queda inválido | **Mock** con `thenReturn(false)` | `assertFalse` |
| `reservarFondos_bloqueaElSaldoDeLaCuenta` | "Bloqueo del saldo hasta confirmar" | **Mock**; `verify(api).bloquearElSaldo("cuenta-123", 8000f)` | `verify` |
| `ejecutarTransaccion_acreditaElMontoAlVendedor` | "Acreditación en tiempo real al vendedor" | **Mock**; `verify(api).acreditarVendedor("cuenta-123", 8000f)` | `verify` |
| `notificarResultado_enviaLaNotificacionPushAlTitular` | "Notificación push" | **Mock**; `verify(api).notificarPush("cuenta-123", 8000f)` | `verify` |
| `procesoDePago_conDatosValidos_bloqueaAcreditaYNotificaEnEseOrden` | El orden de las llamadas a la API | **Mock** + `inOrder(api)` | verificaciones en orden |

---

# Hallazgos

### H1 — Bug: `Categoria` sobrescribe `equals` pero no `hashCode`

Viola el contrato de `Object`: dos categorías con el mismo nombre son `equals` pero tienen `hashCode`
distinto. Rompe cualquier uso de `Categoria` en `HashSet`/`HashMap`, que es exactamente lo que va a
necesitar el filtro "por categoría" de §2.6.

- Test que lo expone (deja fallando, sin `@Disabled`):
  `unq.catalogo.CategoriaTest#hashCode_conNombresIguales_deberiaSerIgual`
- **Cambio mínimo sugerido en producción** (`src/unq/catalogo/Categoria.java`):
  ```java
  @Override
  public int hashCode() {
      return Objects.hash(nombre);
  }
  ```

### H2 — Bug: `PaqueteTuristico` no valida ciclos

`agregarItem(paquete)` acepta el propio paquete (y cualquier paquete que lo contenga). A partir de ahí,
`precioBase()` y `precioFinal()` entran en recursión infinita (`StackOverflowError`). El enunciado no lo
pide explícitamente, pero es la alternativa frágil que conviene evitar.

- Test que documenta el comportamiento actual:
  `unq.catalogo.PaqueteTuristicoTest#agregarItem_conElMismoPaquete_noLanzaYNoSeValidaCiclo`
- **Cambio mínimo sugerido en producción**: en `agregarItem`, rechazar `item == this` y `item` sea un
  `PaqueteTuristico` que contenga a `this`.

### H3 — Hallazgo que afecta al Módulo 3 y al 4: `Reserva.valorTotal()` devuelve `0`

`src/unq/reserva/Reserva.java` tiene `public float valorTotal() { return 0; // hay que preguntar como se
calcula el valor }`. El traslado VIP/Chauffeur (§2.3) y los métodos de pago (§2.4) necesitan el **valor
monetario total de la reserva**. Hay que definir la fórmula (¿suma de `precioFinal()` de los ítems? ¿más
tasas? ¿descuentos de paquete?).

- Para **escribir los tests** del Módulo 3 no bloquea: como las estrategias reciben una `Reserva` **mock**,
  el valor total se stubbea (`when(reserva.valorTotal()).thenReturn(15000f)`) y el test verifica la
  delegación, no el valor real.
- Para **producción** sí bloquea: hoy el traslado Chauffeur/VIP costaría siempre 0, porque
  `TrasladoChauffeur.calcularCosto` le pasa `reserva.valorTotal()`.

`Reserva` sí expone `cantidadDePasajeros()` y `getDestino()`, que son los otros dos datos que necesitan
las estrategias de traslado.

### H4 — Decisión de diseño (no bug): precio base del paquete

El texto del enunciado dice que el precio base de un paquete es `suma × (1 − descuento%)`, pero la tabla
muestra precio base = suma ($21.000) y precio final = suma × (1 − descuento) ($18.900). **El código sigue
la tabla.** Queda para consulta al docente; no se cuenta como bug.

### H5 — Decisión de diseño: `Texto.esValido("")` devuelve `true`

Sólo se valida la consistencia entre el valor y el tipo declarado, no que el texto tenga contenido. Es
consistente con "sólo debe chequearse la asignación de un valor". Si el docente exige texto no vacío, el
cambio va en `Texto`, no en `AtributoDinamico`.

### H6 — Decisión de diseño: `ServicioIndividual.validar()` exige `operador` y `categoria`

El enunciado menciona explícitamente `nombre` y `código` como atributos fijos obligatorios; el código
además exige `operador` y `categoria` no nulos. Es razonable ("entre otros"), pero conviene defenderlo en
la entrevista.

### H7 — Código muerto: `AtributoInvalidoException` no lo usa nadie

`src/unq/excepciones/AtributoInvalidoException.java` no tiene ningún uso: el catálogo lanza
`ExcepcionDominio` genérica. Queda sin cubrir (50 % del paquete `unq.excepciones`). Decidir entre usarla
para atributos dinámicos inválidos (habría que pasarle el nombre del atributo) o eliminarla.

### H8 — Hallazgo de alcance: el Módulo 5 (Buscador y filtros) no existe

No hay paquete `unq.busqueda` ni tipos de criterio (`Criterio`/`Filtro`, `AND`, `OR`, `NOT`, y los
criterios simples). No hay nada que testear hasta que se implemente. Los Módulos 3 (`unq.traslado`) y 4
(`unq.metodosDePago`) sí existen en el repo: el 3 ya está testeado, el 4 sigue pendiente.

### H9 — Nota de entorno: `PlataformaTransporte` y `TrasladoVIP` son stubs del proyecto

`src/unq/traslado/PlataformaTransporte.java` y `src/unq/traslado/TrasladoVIP.java` son clases propias
(no librerías externas), así que en el Módulo 3 se mockean igual: nunca se llama a la real.

Además, las dos clases son **stubs vacíos**: `PlataformaTransporte.estimarTraslado(...)` y
`TrasladoVIP.calcularCosto(...)` devuelven `0` con el comentario *"para que no moleste"*. No son
adaptadores a la librería real, son placeholders. Consecuencia para la cobertura: por la regla del
prompt de **siempre mockearlas y nunca llamar a la real**, quedan con **0 % de cobertura**
(5 de las 7 líneas sin cubrir de `unq/traslado`). Si el equipo quiere cubrirlas, la opción es
convertirlas en clases abstractas/adaptadores reales y testearlas por separado, pero eso ya es
producción, no tests.

### H10 — Nota de entorno: el UML no está en el repo

El prompt menciona `UML-CASEFINALIZADO.drawio`, pero en el repo no hay ningún `.drawio` (en `~/Downloads`
sólo está `UMLTpIntegrador2.2.drawio`). La comparación **código vs UML** queda pendiente: si subís el
UML al repo, completo esa parte de este documento.

### H11 — `Reserva` no expone los ítems (no hace falta tocar producción)

La lista de ítems es privada y sólo se la modifica con `agregarItemDirecto` / `quitarItemDirecto`, que son
**package-private** a propósito. Los tests del mismo paquete (`unq.reserva`) los verifican con
`verify(reserva).agregarItemDirecto(item)` sobre un mock o un spy, sin necesidad de un getter. Es la
alternativa más frágil que evita el prompt: "Mi diseño evita getters a propósito".

### H12 — Cobertura: getters triviales sin cubrir

`PaqueteTuristico.nombre()` / `descripcion()` y `ServicioIndividual.precioBase()` / `nombre()` /
`descripcion()` quedan sin cubrir porque el prompt pide no probar getters triviales. Con 5 `assert`
adicionales la cobertura de líneas de `unq.catalogo` subiría de 95,1 % a 100 %.

### H13 — Bug de diseño: dos jerarquías de excepción de dominio paralelas

`ExcepcionDominio extends RuntimeException` (catálogo) y `ExcepcionPorInvalido extends RuntimeException`
(reserva) son hierarchies **independientes**. El enunciado pide "una excepción de dominio propia, no una
excepción genérica": hoy conviven dos raíces distintas, y un `catch (ExcepcionDominio)` no atrapa los
errores de la reserva.

- **Cambio mínimo sugerido en producción** (`src/unq/reserva/ExcepcionPorInvalido.java`):
  ```java
  public class ExcepcionPorInvalido extends ExcepcionDominio {
  ```
  (requiere `import unq.excepciones.ExcepcionDominio;`). Los tests no cambian: siguen esperando
  `ExcepcionPorInvalido`, que seguiría siendo la excepción concreta.

### H14 — Hallazgo: el cupo no está modelado

`bloquearCupos()` y `liberarCupos()` son **cuerpos vacíos**: no existe ninguna clase de cupo, ni
cantidades disponibles, ni fechas/destinos. El enunciado dice que al confirmar "se decrementa/bloquea el
cupo disponible" y que al cancelar "se incrementa/libera el cupo de las unidades"; además, el criterio de
búsqueda "por disponibilidad" (§2.6) necesita "cupos disponibles en al menos una fecha/destino". Hoy los
estados disparan el efecto, pero el efecto no hace nada.

- **Cambio mínimo sugerido**: una clase `Cupo` (o `Unidad`) con cantidad disponible y un `ServicioIndividual`
  que lo referencie; `Reserva.bloquearCupos()` / `liberarCupos()` delegan en ella.

### H15 — Hallazgo: el voucher de reembolso no está modelado

El enunciado: "Los reembolsos al cliente simplemente se resuelven generando y registrando un Voucher de
Crédito / Reembolso". `Reserva.reembolsar()` es un cuerpo vacío y no existe ninguna clase `Voucher` ni un
registro de vouchers. El Módulo 2 verifica que el estado **dispara** el efecto, no que el voucher exista.

### H16 — Hallazgo: el reembolso no distingue "servicios y tasas" de "sólo servicios"

El enunciado diferencia dos casos: en `EN_CHECKIN` se reembolsa "el costo de los servicios **y** el de las
tasas de reserva"; en `EN_CURSO` "solo se reembolsa el costo de los servicios no disfrutados pero no de
las tasas administrativas". El código tiene un único método `reembolsar()` sin parámetros, así que la
diferencia **no se puede expresar**.

- **Cambio mínimo sugerido**: `reembolsar(boolean incluyeTasasDeReserva)` o dos métodos
  (`reembolsarServiciosYTasas()` / `reembolsarServiciosNoDisfrutados()`), y que cada estado llame al que
  le corresponde.

### H17 — Nota: el test de reserva preexistente quedó reescrito

El `ReservaTest` que había en el repo usaba `new Reserva(mock.Destino(), 5)` (ni el import ni el tipo
existen, y `Reserva` ahora recibe `(Direccion, int)`): estaba roto y no compilaba. Se reescribió completo
como parte del Módulo 2, con la estrategia de dobles que pide el prompt.

### H18 — Bug de diseño: dos de los tres métodos de traslado no implementan `MetodoTraslado`

Sólo `TrasladoEstandar` declara `implements MetodoTraslado`. `TrasladoChauffeur` y `PuntoEncuentro`
tienen el método `calcularCosto(Reserva)` con la firma exacta de la interfaz, pero no la implementan:

- Consecuencia: **el patrón Strategy no es polimórfico**. No se puede escribir
  `List<MetodoTraslado> metodos = List.of(new TrasladoEstandar(p), new TrasladoChauffeur(v), new PuntoEncuentro(...))`
  ni hacer que el sistema elija el método de traslado según la reserva. El enunciado (§2.3) pide tres
  métodos de traslado con criterios distintos, y el código no permite ni seleccionarlos ni
  intercambiarlos.
- Tests que lo exponen (dejan fallando, sin `@Disabled`):
  - `unq.traslado.MetodoTrasladoTest#trasladoChauffeur_esUnMetodoDeTraslado`
  - `unq.traslado.MetodoTrasladoTest#puntoDeEncuentro_esUnMetodoDeTraslado`
- **Cambio mínimo sugerido en producción** (una palabra por clase):
  ```java
  public class TrasladoChauffeur implements MetodoTraslado { ... }
  public class PuntoEncuentro implements MetodoTraslado { ... }
  ```
  No hace falta tocar ningún cuerpo de método: las firmas ya coinciden. Con ese cambio los 3 tests
  pasan.

### H19 — Hallazgo: `PuntoEncuentro` no puede asignar guías ni descontar cupo

El enunciado pide que el punto de encuentro tenga un cupo de guías y que **asignar un guía descuente un
cupo y lance la excepción de dominio si no hay cupo**. El código sólo tiene:

```java
public boolean estaDisponible(Reserva reserva) { return cuposGuias > 0; }
```

No existe ningún método que **descuente** el cupo, así que un punto con 1 cupo queda "disponible"
indefinidamente. Tampoco hay forma de registrar al guía asignado.

- **Cambio mínimo sugerido en producción** (`src/unq/traslado/PuntoEncuentro.java`):
  ```java
  public void asignarGuia(Reserva reserva) {
      if (cuposGuias <= 0) {
          throw new ExcepcionDominio("No hay cupo de guías disponible en " + ubicacion);
      }
      cuposGuias--;
  }
  ```
  (importando `unq.excepciones.ExcepcionDominio`, que es la excepción de dominio del proyecto).
- Aparte: `estaDisponible` **ignora la cantidad de pasajeros** de la reserva. Si el punto tiene 1 cupo de
  guía y la reserva tiene 5 pasajeros, hoy devuelve `true`. Test que documenta el comportamiento actual:
  `unq.traslado.PuntoEncuentroTest#estaDisponible_noDependeDeLaCantidadDePasajerosDeLaReserva`.

### H20 — Menor: `TrasladoVIP.calcularCosto` usa `Float` (boxing) en vez de `float`

El enunciado define `float TrasladoVIP.calcularCosto(float precio)`. El código declara
`public float calcularCosto(Float precio)`: el parámetro es un `Float` boxingado, que admite `null` y
suma una conversión implícita. El impacto real está en `TrasladoChauffeur`, que le pasa
`reserva.valorTotal()` (un `float` primitivo) → el `float` se boxingea en cada llamada.

- **Cambio mínimo sugerido**: `public float calcularCosto(float precio)`.
- No rompe los tests: el `verify(servicioVIP).calcularCosto(15000f)` sigue funcionando con cualquiera de
  las dos firmas (con `float` el matcher es exacto; con `Float` también, por autoboxing).

### H21 — Bug: `procesoDePago()` no es `final`, así que la plantilla no garantiza el orden

El enunciado dice que *"el procesamiento de un pago siempre sigue los mismos pasos en el mismo orden"*.
Eso, en un Template Method, se garantiza con un método `final`: si `procesoDePago()` no es `final`,
cualquier subclase puede sobrescribirlo y saltarse la validación o el orden de los pasos.

Hoy `public void procesoDePago()` no es `final`.

- Test que lo expone (deja fallando, sin `@Disabled`):
  `unq.metodosDePago.MedioDePagoTest#procesoDePago_esFinalParaQueNingunaSubclasePuedaCambiarElOrdenDeLosPasos`
- **Cambio mínimo sugerido en producción** (`src/unq/metodosDePago/MedioDePago.java`):
  ```java
  public final void procesoDePago() { ... }
  ```
  No cambia ningún cuerpo de método ni ningún test.

### H22 — Bug: `notificarResultado()` no tiene implementación por defecto y dos de los tres medios no hacen nada

El enunciado: *"El paso de notificación tiene una implementación por defecto (registro del código de
transacción) que las subclases pueden personalizar sin estar obligadas a hacerlo."*

En el código `notificarResultado()` es **abstracto** en `MedioDePago` → las subclases están obligadas a
implementarlo (contradictorio con el enunciado), y las dos implementaciones están vacías:

| Medio | Lo que pide el enunciado | Lo que hace el código |
|---|---|---|
| `TarjDeCredito` | Voucher impreso/digital (**generarlo y registrarlo**) | cuerpo vacío |
| `PagoTransferencia` | Comprobante CBU con número de operación (**generarlo y registrarlo**) | cuerpo vacío (`// hay que hacerlo`) |
| `BilleteraVirtual` | Notificación push | `api.notificarPush(idCuenta, monto)` ✓ |

- Tests que lo exponen (dejan fallando, sin `@Disabled`):
  - `unq.metodosDePago.MedioDePagoTest#notificarResultado_tieneUnaImplementacionPorDefectoEnLaClaseAbstracta`
  - `unq.metodosDePago.TarjDeCreditoTest#notificarResultado_noGeneraElVoucherQuePideElEnunciado`
    (documenta el comportamiento actual, pasa)
  - `unq.metodosDePago.PagoTransferenciaTest#notificarResultado_noGeneraElComprobanteConElNumeroDeOperacion`
    (documenta el comportamiento actual, pasa)
- **Cambio mínimo sugerido en producción**:
  1. En `MedioDePago`, bajar el método a implementación por defecto:
     ```java
     public void notificarResultado() {
         registrarTransaccion(codigoTransaccion);
     }
     ```
  2. Agregar el colaborador `RegistroTransacciones` (no existe hoy) e inyectarlo por constructor de
     `MedioDePago`, con un `registrarTransaccion(String codigo)` que guarde el voucher / comprobante.
  3. `TarjDeCredito` y `PagoTransferencia` pueden sobrescribirlo **sólo** para agregar el voucher o el
     comprobante, pero dejan de estar obligadas a hacerlo.

### H23 — Campo muerto: `codigoTransaccion` se guarda y nunca se lee

`MedioDePago` lo recibe por constructor, lo guarda en un campo **privado** y no hay ningún getter ni
ningún uso en todo el proyecto. Es justamente el dato que debería consumir la implementación por defecto
de `notificarResultado()` (H22).

- **Cambio mínimo sugerido**: usarlo en el `registrarTransaccion(codigoTransaccion)` de H22 (o exponerlo
  con un getter `codigoTransaccion()`).

### H24 — Bug grave: `TarjDeCredito` nunca recibe ni asigna su API

`TarjDeCredito` es el único medio de pago que **no** recibe la API en el constructor:

```java
public TarjDeCredito(int nroTarjeta, int cvv, LocalDateTime vencimiento, float monto, String codigoDeTransaccion) {
    super(monto, codigoDeTransaccion);
    this.nroTarjeta = nroTarjeta;
    this.CVV = cvv;
    this.vencimiento = vencimiento;
    // falta this.api = api;
}
```

El campo `api` queda `null` → `validarDatos()` lanza `NullPointerException` → **`procesoDePago()` no
funciona en producción** para el medio de pago más usado. `PagoTransferencia` y `BilleteraVirtual` sí
reciben su API.

- **Cambio mínimo sugerido en producción**:
  ```java
  public TarjDeCredito(int nroTarjeta, int cvv, LocalDateTime vencimiento,
                       APITarjetaDeCredito api, float monto, String codigoDeTransaccion) {
      super(monto, codigoDeTransaccion);
      this.nroTarjeta = nroTarjeta;
      this.CVV = cvv;
      this.vencimiento = vencimiento;
      this.api = api;
  }
  ```
- **Consecuencia para los tests**: hasta que se aplique el cambio, el test tiene que inyectar el mock en
  el campo privado por **reflexión** (`TarjDeCreditoTest#inyectarApi`). Es un rodeo del test, no una
  decisión de diseño; con el fix, la reflexión se borra y el mock entra por constructor.

### H25 — Bug: los datos que debe validar la API son privados y sin getters, y los validadores declarados no se usan

La validación se delega con `api.validarDatos(this)`, pasándole el propio medio de pago. Pero los datos
que la API tiene que validar son **campos privados sin getters**:

| Clase | Dato | ¿Lo puede leer la API? |
|---|---|---|
| `TarjDeCredito` | `nroTarjeta`, `CVV`, `vencimiento` | no |
| `PagoTransferencia` | `CBU`, `alias`, `fechaProgramada` | no |
| `BilleteraVirtual` | `idCuenta` | no |

Además, las interfaces declaran validadores que **nadie invoca**: `APITarjetaDeCredito.validarCVV(int,int)`
y `validarVencimiento(LocalDateTime)`, `APIBancaria.validarCBU(int)` y `validarAlias(String)`.

Consecuencia: la **combinación** que pide el enunciado (número + CVV + vencimiento / CBU + alias) no
está implementada en ningún lado del código, y una implementación de la API no podría hacerla.

- **Cambio mínimo sugerido en producción** (dos opciones):
  1. Exponer los datos (getters) y combinar en el medio de pago:
     ```java
     @Override
     public boolean validarDatos() {
         return api.validarCVV(nroTarjeta, CVV)
             && api.validarVencimiento(vencimiento)
             && api.validarDatos(this);
     }
     ```
     con `public int nroTarjeta()`, `public int CVV()`, `public LocalDateTime vencimiento()` (y los
     equivalentes en `PagoTransferencia`).
  2. O dejar la combinación dentro de la API y pasarle los datos sueltos en vez del objeto.

### H26 — Menor: `MedioDePago.monto` es público y mutable

`public float monto;` es un campo público: cualquiera puede cambiar el monto de la reserva **después** de
construir el medio de pago, y la plantilla lo usa en `reservarFondos()` y `ejecutarTransaccion()` (es
justo el monto que se cobra).

- **Cambio mínimo sugerido**: `private final float monto;` con un getter `public float monto()`.

### H27 — No existe una excepción propia de "pago inválido"

El paso de validación del enunciado lanza "la excepción de pago inválido", y el enunciado en general
pide "una excepción de dominio propia, no una excepción genérica". El código lanza la
`ExcepcionDominio` genérica con el mensaje `"los datos no fueron validados correctamente"`, y en
`unq.excepciones` no hay ninguna clase de pago (`ExcepcionDominio` y `AtributoInvalidoException`).

Este hallazgo **no se puede cubrir con un test que falle**, porque la clase que haría falta no existe
(y el test no compilaría). Queda documentado sólo.

- **Cambio mínimo sugerido en producción**:
  ```java
  // src/unq/excepciones/ExcepcionPagoInvalido.java
  public class ExcepcionPagoInvalido extends ExcepcionDominio {
      public ExcepcionPagoInvalido(String mensaje) { super(mensaje); }
  }
  ```
  y que `procesoDePago()` la lance. Los tests existentes siguen funcionando porque
  `ExcepcionPagoInvalido` seguiría siendo una `ExcepcionDominio`.

### H28 — Nota: el Módulo 5 (Buscador y filtros) sigue sin existir

No cambió respecto de H8: no hay paquete `unq.busqueda` ni tipos de criterio. Con los Módulos 1 a 4
entregados, **el único módulo del alcance con código faltante es el 5**.

---

# Resultados

| Módulo | Tests | Cobertura de líneas | Cobertura de ramas |
|---|---|---|---|
| 1 — Catálogo (`unq.catalogo`) | 65 | 97/102 = **95,1 %** | 46/46 = **100 %** |
| 2 — Reserva (`unq.reserva`) | 72 | 97/97 = **100 %** | sin ramas |
| 3 — Traslado (`unq.traslado`) | 11 | 22/27 = **81,5 %** | 2/2 = **100 %** |
| 4 — Métodos de pago (`unq.metodosDePago`) | 24 | 44/44 = **100 %** | 2/2 = **100 %** |

- **Suite completa:** 172 tests, **5 fallos**:
  - 1 deliberado de **H1** (`Categoria` sin `hashCode`), que documenta el bug;
  - 2 deliberados de **H18** (`TrasladoChauffeur` y `PuntoEncuentro` no implementan `MetodoTraslado`),
    que documentan que el patrón Strategy no es polimórfico;
  - 2 deliberados de **H21 / H22** (`procesoDePago()` no es `final` y `notificarResultado()` no tiene
    implementación por defecto), que documentan que la plantilla no garantiza el orden ni el paso de
    notificación que pide el enunciado.
- **Líneas sin cubrir en `unq.catalogo`:** `PaqueteTuristico.java:51,56` y `ServicioIndividual.java:38,48,53`
  (getters triviales, ver H12).
- **Líneas sin cubrir en `unq.excepciones`:** `AtributoInvalidoException` (código muerto, ver H7).
- **Líneas sin cubrir en `unq.traslado`:** `PlataformaTransporte.java:4,7` y `TrasladoVIP.java:6`
  (clases "librería externa" que por regla del prompt **siempre se mockean y nunca se llaman**, ver H9)
  y `PuntoEncuentro.java:24` (`getUbicacion()`, getter trivial, misma regla que H12).
