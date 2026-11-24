# La Costurería Nata Sam — módulo clientes
Evidencia GA7-220501096-AA2-EV01 | Samuel Andres Afanador Moreno | SENA

## Instalación en Windows
1. Instalar JDK 17 o superior, Maven 3.9, MySQL 8 y Apache Tomcat 10.1. Verificar `java -version` y `mvn -version`.
2. Abrir MySQL Workbench, conectarse al servidor y ejecutar `sql/schema.sql`. Crear un usuario local con permisos SELECT, INSERT, UPDATE y DELETE sobre costureria.* (o usar una cuenta local de desarrollo ya configurada).
3. Abrir PowerShell en la carpeta del proyecto. Configurar las variables de entorno en esa misma sesión (sustituir los valores):
```powershell
$env:DB_USER='usuario_local'
$env:DB_PASSWORD='clave_local'
$env:DB_URL='jdbc:mysql://localhost:3306/costureria'
mvn clean package
```
4. Copiar `target/costureria.war` a la carpeta `webapps` de Tomcat. Desde la misma sesión ejecutar `& 'C:\ruta\tomcat\bin\catalina.bat' run` (ajustar ruta). Abrir http://localhost:8080/costureria/.
5. No publicar credenciales ni datos reales. Aplicación académica local, sin autenticación; no desplegar públicamente.

## Pruebas manuales pendientes
Registrar un cliente ficticio (Ana Prueba, 3000000000, ana@example.com); comprobar que aparece; editar teléfono; recargar; eliminar y verificar ausencia. Intentar registrar campos vacíos y comprobar rechazo. Un cliente asociado a una orden no puede eliminarse por integridad referencial.

## Diseño
El modelo CLIENTE coincide con EV01 del modelo relacional; se incluyen las cinco tablas de esa evidencia. Solo se implementa el CRUD de clientes. Java record, DAO con JDBC y PreparedStatement, Servlet Jakarta y vista HTML/CSS. Convenciones: clases PascalCase, métodos y variables camelCase, paquetes minúsculos. No se verificaron los artefactos de historias de usuario o prototipos originales.

## GitHub
Crear repositorio vacío privado `Costureria-Nata-Sam` en GitHub (sin README). En Git Bash dentro de esta carpeta ejecutar `git log --oneline`, `git remote add origin https://github.com/TU_USUARIO/Costureria-Nata-Sam.git`, `git push -u origin main`. Si el instructor necesita acceso, invitarlo o acordar la visibilidad. Actualizar ENLACE_REPOSITORIO.txt con la URL real y registrar ese cambio con git add, commit y push. Volver a comprimir la carpeta para entregar.

## Verificación
El modelo y DAO se compilaron localmente con javac. La compilación Maven, el servlet Jakarta y la ejecución con MySQL/Tomcat requieren validación en el equipo del aprendiz.
