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
