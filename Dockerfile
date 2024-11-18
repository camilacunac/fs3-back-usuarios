# Usar una versión de OpenJDK estable
FROM openjdk:21-ea-24-oracle

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR generado
COPY target/usuarios-1.0-SNAPSHOT.jar app.jar

# Copiar los archivos del Oracle Wallet
COPY Wallet_HEALZJHB0K6M53N7 /app/wallet

# Configurar el Oracle Wallet
ENV TNS_ADMIN=/app/wallet

# Exponer el puerto 8080
EXPOSE 8080

# Comando de inicio
CMD [ "java", "-jar", "app.jar" ]
