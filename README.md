# EliminaDuplicados
Un programa para eliminar duplicados en base al hash del archivo

Obten el app.jar y ejecutalo usando un start.bat con este contenido:

    @echo off
    :: Obtener la ubicación del directorio del archivo start.cmd
    set "directory=%~dp0"
    
    :: Ejecutar la aplicación Java desde su directorio y pasa la ubicación como parámetro
    cd /d "%directory%"
    java -jar app.jar %directory%
    
    :: Pausa para mantener la ventana abierta
    pause