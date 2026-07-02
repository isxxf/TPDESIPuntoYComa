//1. Levantar la base de datos
-Abrir archivo SQL adjuntado en su IDE de base de datos
-Configura las credenciales en el archivo SQL:
-Modifica la contraseña del usuario de acuerdo a las credenciales de tu base local.
-Levanta la base de datos en tu localhost.

//2. Clonar el repositorio
-Abre tu terminal o IDE (como STS o Visual Studio).
Ejecuta el comando:
git clone <URL del repositorio>

//3. Configuración del proyecto
-Abre el proyecto en tu entorno (STS, Visual Studio, etc.).
-Verifica la configuración del datasource para asegurarte de que apunte al localhost y use las credenciales correctas.

//4. Levantar la aplicación
-Ejecuta el comando para levantar el servidor (por ejemplo, mvn spring-boot:run o el comando equivalente en tu IDE).
-Una vez levantado, abre tu navegador y accede a localhost:<puerto>.

//5. Probar operaciones

EPIC 1 - PROPIEDADES:

-Intenta cargar una nueva propiedad en la aplicación.

-Prueba editar o eliminar una propiedad existente.

-Controla el listado de tus propiedades.



EPIC 2 - PUBLICACIONES:

-Intenta registrar una nueva publicación seleccionando una propiedad (asegurarse de que la propiedad esté DISPONIBLE).

-Intentar crear una segunda publicación en estado ACTIVA para esa misma propiedad (el sistema debe arrojar error).

-Controla el listado utilizando los filtros combinados

-Edita una publicación existente y cambiar su estado a PAUSADA, y luego a FINALIZADA.

-Intentar modificar las condiciones de una publicación que ya se encuentra en estado FINALIZADA (no debería permitirlo).

-Intentar eliminar una publicación que se encuentre en estado PAUSADA o FINALIZADA (el sistema debe rechazarlo).

-Elimina una publicación en estado ACTIVA (verificar que desaparezca del listado pero persista en base de datos como borrado lógico).



EPIC 3 - CONTRATOS

-Crear un contrato en estado BORRADOR.

-Editarlo y pasarlo a ACTIVO.

-Verificar que la propiedad pase a ALQUILADA.

-Pasarlo a FINALIZADO o RESCINDIDO.

-Verificar que no permita volver a ACTIVO.

-Intentar eliminar un contrato ACTIVO.

-Eliminar un contrato BORRADOR.


EPIC 4 - FACTURAS

Alta de factura: alta exitosa, alta con pago, contrato borrador, fecha inválida e importe cero.

Modificación de factura: cambios entre estados (PENDIENTE → VENCIDA, VENCIDA → PAGADA y PENDIENTE → ANULADA), edición de facturas pagadas o anuladas y cambios inválidos.

Eliminación de factura: eliminar facturas pendientes o vencidas e intento de eliminar facturas pagadas.

Listado y filtros: listado general, filtros por estado, contrato, propiedad, inquilino y fecha, y verificación de columnas.

