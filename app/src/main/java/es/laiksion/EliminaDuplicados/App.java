package es.laiksion.EliminaDuplicados;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Juan <Laiksion>
 */
public class App {

    public static void main(String[] args) throws IOException, FileNotFoundException, NoSuchAlgorithmException {
        System.out.println("Directorio base: " + args[0]);
        Path mainRuta = Paths.get(args[0]).resolve("Duplicados").toAbsolutePath();
        System.out.println("""
                           
                           
                           ------------------------------------------------------------------------------
                           | ¡Bienvenido a RemoveDuplicates!                                            |
                           | En los siguientes menus puedes seleccionar las opciones que deseas utlizar.|
                           | Tambien puedes configurar parametros para personalizar estas herramientas. |
                           |                                                                            |
                           | (C) Juan                                                                   |
                           | (C) 2023                                                                   |
                           ------------------------------------------------------------------------------
                           
                           
                           """);
        // Incio Programa
        startUpStruct(mainRuta);
        ArrayList<Path> whiteList = null;
        ArrayList<Path> blackList = null;

        LinkedList<Path> tree = null;
        //---
        boolean preservarAntiguo = true;
        boolean ignorarSistema = true;
        boolean ignorarOcultos = false;
        //---
        ArrayList<Fichero> archivos = null;
        ArrayList<File> omitidos = null;
        //---
        HashMap<String, LinkedList<Fichero>> procesados = null;
        int informes = 0;
        boolean procesadosProcessed = false;
        // Inicio UI
        int opcion = 0;
        do {
            System.out.println("""
                               \nIntroduce el numero correspondiente a la opcion que quieres usar!
                               --------------------------------
                               | 0. Salir del programa
                               | 1. Configuracion directorios
                               | 2. Configuracion banderas
                               | 3. Seleccionar Operaciones
                               | 4. Help
                               --------------------------------
                               """);

            opcion = Interfaz.leerNumero();
            while (opcion < 0 || opcion > 4) {
                System.err.println("Fuera del rango permitido!");
                opcion = Interfaz.leerNumero();
            }

            int opt = 0;
            switch (opcion) {
                case 0 -> {
                }
                case 1 -> {
                    do {
                        System.out.println("""
                               \nIntroduce el numero correspondiente a la opcion que quieres usar!
                               --------------------------------
                               | 0. Retroceder
                               | 1. Añadir Directorio
                               | 2. Excluir Directorio
                               | 3. Vaciar WhiteList
                               | 4. Vaciar BlackList
                               | 5. Ver WhiteList
                               | 6. Ver BlackList
                               | 7. Generar Arbol
                               | 8. Imprimir Arbol
                               --------------------------------
                               """);

                        opt = Interfaz.leerNumero();
                        while (opt < 0 || opt > 8) {
                            System.err.println("Fuera del rango permitido!");
                            opt = Interfaz.leerNumero();
                        }

                        switch (opt) {
                            case 1 -> {
                                if (whiteList == null) {
                                    whiteList = new ArrayList<>();
                                }

                                Path ruta = Interfaz.leerPath();

                                System.out.println("Quieres añadir: " + ruta.toString());

                                if (Interfaz.leerCondicion()) {
                                    whiteList.add(ruta);
                                    System.out.println("Ruta añadida");
                                } else {
                                    System.out.println("Operacion cancelada");
                                }
                            }
                            case 2 -> {
                                if (blackList == null) {
                                    blackList = new ArrayList<>();
                                }

                                Path ruta = Interfaz.leerPath();

                                System.out.println("Quieres excluir: " + ruta.toString());

                                if (Interfaz.leerCondicion()) {
                                    blackList.add(ruta);
                                    System.out.println("Ruta exluida!");
                                } else {
                                    System.out.println("Operacion Cancelada");
                                }

                            }
                            case 3 -> {
                                whiteList = null;
                                System.out.println("WhiteList limpia");
                            }
                            case 4 -> {
                                blackList = null;
                                System.out.println("Black List limpia");
                            }
                            case 5 -> {
                                System.out.println("Lista de directorios (White):");
                                if (whiteList != null) {
                                    for (Path ruta : whiteList) {
                                        System.out.println("> " + ruta.toString());
                                    }
                                } else {
                                    System.out.println("Vacio");
                                }
                            }
                            case 6 -> {
                                System.out.println("Lista de directorios(Black):");
                                if (blackList != null) {
                                    for (Path ruta : blackList) {
                                        System.out.println("> " + ruta.toString());
                                    }
                                } else {
                                    System.out.println("Vacio");
                                }
                            }
                            case 7 -> {
                                if (whiteList != null && !whiteList.isEmpty()) {
                                    long ti = System.currentTimeMillis();
                                    System.out.println("Iniciando");

                                    // Inicio
                                    tree = new LinkedList<>();
                                    for (Path ruta : whiteList) {
                                        tree.add(ruta);
                                    }

                                    if (blackList != null) {
                                        for (Path ruta : blackList) {
                                            for (int i = 0; i < tree.size(); i++) {
                                                Path obj = tree.get(i); //Ruta en la lista

                                                if (ruta.startsWith(obj)) { //La ruta negra esta debajo/= que la del arbol (Recortar)
                                                    tree.remove(i--);

                                                    File objF = obj.toFile();
                                                    if (objF.exists() && objF.isDirectory()) {
                                                        for (File nFile : objF.listFiles()) {
                                                            tree.add(i + 1, nFile.toPath());
                                                        }
                                                    }
                                                } else if (obj.startsWith(ruta)) {//La ruta blanca esta debajo/= del la negra (Borrar)
                                                    tree.remove(i--);
                                                } // Sin relacion
                                            }
                                        }
                                    }

                                    // Ordenar la LinkedList de Paths alfabéticamente
                                    Collections.sort(tree, Comparator.comparing(Path::toString));

                                    //Eliminar Duplicados
                                    if (tree.size() > 1) {
                                        ListIterator<Path> it = tree.listIterator();

                                        Path actual = it.next();
                                        while (it.hasNext()) {
                                            Path next = it.next();
                                            if (actual.toString().equals(next.toString())) {
                                                tree.removeFirstOccurrence(actual); //Borramos el actual
                                            }

                                            actual = next; //Actualiza si no coinciden y si coinciden para eliminar el primero
                                        }
                                    }
                                    // Fin
                                    long tf = System.currentTimeMillis();

                                    long tt = tf - ti;
                                    System.out.println("Finalizado [Duracion: " + formatearTiempo(tt) + " ]");
                                } else {
                                    System.out.println("Directorios no disponibles!");
                                }
                            }
                            case 8 -> {
                                if (tree != null && !tree.isEmpty()) {
                                    System.out.println("Lista de directorios:");
                                    for (Path ruta : tree) {
                                        System.out.println("> " + ruta.toString());
                                    }
                                } else {
                                    System.out.println("Arbol vacio!");
                                }
                            }
                        }
                    } while (opt != 0);
                }
                case 2 -> {
                    do {
                        System.out.println("""
                               \nIntroduce el numero correspondiente a la opcion que quieres usar!
                               --------------------------------
                               | 0. Retroceder
                               | 1. Imprimir flags
                               | 2. Ocultos
                               | 3. Sistema
                               | 4. Preservar (Antiguo/Nuevo)
                               --------------------------------
                               """);

                        opt = Interfaz.leerNumero();
                        while (opt < 0 || opt > 4) {
                            System.err.println("Fuera del rango permitido!");
                            opt = Interfaz.leerNumero();
                        }

                        switch (opt) {
                            case 1 -> {
                                System.out.println("Ignorar ocultos: " + ignorarOcultos);
                                System.out.println("Ignorar Sistema: " + ignorarSistema);
                                System.out.println("Preservar el mas antiguo: " + preservarAntiguo);
                            }
                            case 2 -> {
                                ignorarOcultos = Interfaz.leerCondicion("Ignorar ocultos:");
                            }
                            case 3 -> {
                                ignorarSistema = Interfaz.leerCondicion("Ignorar Sitema:");
                            }
                            case 4 -> {
                                preservarAntiguo = Interfaz.leerCondicion("Preservar el mas antiguo:");
                            }
                        }
                    } while (opt != 0);
                }
                case 3 -> {
                    do {
                        System.out.println("""
                               \nIntroduce el numero correspondiente a la opcion que quieres usar!
                               --------------------------------
                               | 0. Retroceder
                               | 1. Analizar Archivos
                               | 2. Analizar Duplicados
                               | 3. Generar Informe Archivos Analizados
                               | 4. Generar Informe Resultados
                               | 5. Imprime Estadisticas
                               | 6. Generar Informe Archivos Omitidos
                               | 7. Mover Ficheros
                               --------------------------------
                               """);

                        opt = Interfaz.leerNumero();
                        while (opt < 0 || opt > 7) {
                            System.err.println("Fuera del rango permitido!");
                            opt = Interfaz.leerNumero();
                        }

                        switch (opt) {
                            case 1 -> { //Analiza todos los archivos
                                if (tree != null && !tree.isEmpty()) {
                                    long ti = System.currentTimeMillis();
                                    System.out.println("Iniciando");
                                    // Inicio
                                    archivos = new ArrayList<>();
                                    omitidos = new ArrayList<>();
                                    procesados = null;

                                    ProgressBar bar = new ProgressBar(tree.size());

                                    int i = 0;
                                    bar.toString(i, "Entradas del arbol" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");
                                    for (Path directorio : tree) {
                                        recorrer(directorio.toFile(), archivos, omitidos, ignorarSistema, ignorarOcultos);
                                        bar.toString(++i, "Entradas del arbol" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");
                                    }

                                    bar.toString(i, "Entradas del arbol" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");
                                    // Fin
                                    long tf = System.currentTimeMillis();

                                    long tt = tf - ti;
                                    System.out.println("Finalizado [Duracion: " + formatearTiempo(tt) + " ]");
                                } else {
                                    System.out.println("Aun no hay directorios");
                                }

                            }
                            case 2 -> { //Procesa Duplicados
                                if (archivos != null && !archivos.isEmpty()) {
                                    long ti = System.currentTimeMillis();
                                    System.out.println("Iniciando");
                                    // Inicio
                                    procesados = new HashMap<>((int) ((archivos.size() * 2) / 3), 0.6f);

                                    ProgressBar bar = new ProgressBar(archivos.size());
                                    int counter = 0;

                                    bar.toString(counter, "Archivos Procesados" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");
                                    for (Fichero f : archivos) {
                                        add(f, procesados, preservarAntiguo);
                                        bar.toString(++counter, "Archivos Procesados" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");
                                    }

                                    bar.toString(counter, "Archivos Procesados" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");

                                    // Fin
                                    long tf = System.currentTimeMillis();

                                    long tt = tf - ti;
                                    System.out.println("Finalizado [Duracion: " + formatearTiempo(tt) + " ]");
                                    procesadosProcessed = false;
                                } else {
                                    System.out.println("Aun no hay archivos!");
                                }
                            }
                            case 3 -> { //Informe analizados
                                if (archivos != null && !archivos.isEmpty()) {
                                    StringBuilder informe = new StringBuilder();
                                    for (File file : archivos) {
                                        informe.append(file.toString()).append('\n');
                                    }
                                    informe.deleteCharAt(informe.length() - 1);

                                    writeFile(mainRuta.resolve("Analizados.txt"), informe.toString());
                                } else {
                                    System.out.println("Archivos Vacio!");
                                }
                            }
                            case 4 -> { //Informe Duplicados
                                if (procesados != null && !procesados.isEmpty()) {
                                    StringBuilder informe = new StringBuilder();
                                    informe.append("\n[Informe]\n");
                                    informe.append("\nArchivos Totales(").append(archivos.size()).append(")").append('\n');
                                    informe.append("\nArchivos Unicos(").append(procesados.size()).append(")").append('\n');
                                    informe.append("\nArchivos Repetidos(").append(archivos.size() - procesados.size()).append(")").append('\n');
                                    informe.append("<--------------------------------------------------------------------------------------------->️");

                                    for (LinkedList<Fichero> lkF : procesados.values()) {
                                        if (lkF.size() > 1) {
                                            informe.append("\n[Nº Repetidos:").append(lkF.size() - 1).append("]\n");
                                            for (Fichero f : lkF) {
                                                informe.append(f.toString());
                                            }
                                            informe.append("<--------------------------------------------------------------------------------------------->️");
                                        }
                                    }

                                    writeFile(mainRuta.resolve("Duplicados" + (++informes) + ".txt"), informe.toString());
                                } else {
                                    System.out.println("Duplicados Vacio!");
                                }
                            }
                            case 5 -> { //Estadisticas
                                System.out.println("ESTADISTICAS:");
                                if (archivos != null) {
                                    System.out.println("Direcciones Omitidas: " + omitidos.size());
                                    System.out.println("Archivos Aptos (Total): " + archivos.size());
                                }

                                if (procesados != null) {
                                    System.out.println("Archivos Duplicados: " + (archivos.size() - procesados.size()));
                                    System.out.println("Archivos Unicos: " + procesados.size());
                                }
                                System.out.println("FIN ESTADISTICAS:");
                            }
                            case 6 -> { //Informe omitidos
                                if (omitidos != null && !omitidos.isEmpty()) {
                                    StringBuilder informe = new StringBuilder();
                                    for (File file : omitidos) {
                                        informe.append(file.toString()).append('\n');
                                    }
                                    informe.deleteCharAt(informe.length() - 1);

                                    writeFile(mainRuta.resolve("Omitidos.txt"), informe.toString());
                                } else {
                                    System.out.println("Omitidos Vacio!");
                                }
                            }
                            case 7 -> { //Mueve archivos
                                if (procesados != null && !procesados.isEmpty() && !procesadosProcessed) {
                                    long ti = System.currentTimeMillis();
                                    System.out.println("Iniciando");

                                    // Generamos la carpeta con la fecha en ms hex
                                    Path ruta = mainRuta.resolve(Long.toHexString(System.currentTimeMillis()).toUpperCase());

                                    // Creamos la barra de progreso
                                    ProgressBar bar = new ProgressBar(procesados.size());

                                    // Iniciamos barra de hashes unicos
                                    int counter = 0;
                                    bar.toString(counter, "Archivos unicos" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");

                                    // Procesamos todos los hashes unicos
                                    for (LinkedList<Fichero> lkF : procesados.values()) {
                                        if (lkF.size() > 1) {
                                            // Archivo original
                                            Fichero archivoOriginal = lkF.getFirst();

                                            // Ruta del archivo original
                                            Path pathOriginal = archivoOriginal.toPath().toAbsolutePath();

                                            // Ruta de la carpeta para el hash
                                            Path rutaLocal = ruta.resolve(archivoOriginal.getHexHash());

                                            // Crear directorios
                                            startUpStruct(rutaLocal);

                                            // Ruta del archivo de enlace al conservado
                                            Path enlace = rutaLocal.resolve("0#" + archivoOriginal.getName() + ".bat").toAbsolutePath();

                                            // Crear el archivo de enlace
                                            startUpFile(enlace);

                                            // Escribir el archivo de enlace
                                            writeFile(enlace, "explorer /select, \"" + pathOriginal + "\"");

                                            // Procesamos los duplicados
                                            for (int i = 1; i < lkF.size(); i++) {
                                                // Obtenemos el nuevo Fichero con ambas ubicaciones
                                                Fichero nuevo = lkF.get(i).move(rutaLocal, (i + "#"));
                                                lkF.set(i, nuevo);

                                                // Obtenemos la ruta de un archivo para devolver el duplicado
                                                Path rutaLink = rutaLocal.resolve(i + "#Restore.bat").toAbsolutePath();

                                                // Creamos el archivo para devolver
                                                startUpFile(rutaLink);

                                                // Escribimos el comando
                                                writeFile(rutaLink, "move \"" + nuevo.getPath() + "\" \"" + nuevo.getOldPath() + "\"\ndel \"%~f0\"");
                                            }
                                        }

                                        // Imprimimos la barra despues de cada iteracion del hash
                                        bar.toString(++counter, "Archivos unicos" + "[Duracion: " + formatearTiempo(System.currentTimeMillis() - ti) + " ]");
                                    }

                                    // Fin
                                    long tf = System.currentTimeMillis();

                                    long tt = tf - ti;
                                    System.out.println("Finalizado [Duracion: " + formatearTiempo(tt) + " ]");
                                    procesadosProcessed = true;
                                } else {
                                    System.out.println("Aun no hay duplicados");
                                }
                            }
                        }
                    } while (opt != 0);
                }
                case 4 -> {
                    System.out.println("""
                                       \nInstrucciones de uso:
                                       1. Genera un arbol, esto sera lo que el programa ve.
                                       2. Procesa los datos (Analiza Archivos -> Analiza Duplicados)
                                       3. Opera con los datos
                                       4. El sistema no elimina, lo envia a una carpeta nueva y crea referencias a sus ubicaciones originales
                                       Consideraciones:
                                        ->  WhiteList & BlackList:> Primero se considera toda la whiteList, luego se eliminan todos los directorios que esten dentro de la blackList.
                                            En caso de que sean el mismo se elimina.
                                        ->  La whiteList & BlackList de extensiones no puedes añadir una en ambos lados!
                                       
                                       """);
                }
                default ->
                    System.err.println("Fuera del rango permitido!");
            }
        } while (opcion != 0);

        System.out.println("""
                           
                           -------
                           | FIN |
                           | DEL |
                           | JAR |
                           -------
                           
                           """);
    }

    public static void startUpStruct(Path direccion) throws IOException {
        if (!Files.exists(direccion.toAbsolutePath())) {
            Files.createDirectories(direccion.toAbsolutePath());
        }
    }

    public static File startUpFile(Path direccion) throws IOException {
        File archivo = direccion.toFile();
        //Establecer el documento
        if (archivo.exists()) {
            archivo.delete();
        }
        archivo.createNewFile();

        return archivo;
    }

    public static void writeFile(Path ruta, String info) throws FileNotFoundException, IOException {
        startUpStruct(ruta);
        File doc = startUpFile(ruta);

        // Creamos un OutputStreamWriter que convierte caracteres a bytes y lo asociamos con el OutputStream
        try (OutputStream oS = new FileOutputStream(doc); OutputStreamWriter wr = new OutputStreamWriter(oS)) {
            wr.write(info);
        }
    }

    // Método para formatear el tiempo en formato hh:mm:ss:SSS
    private static String formatearTiempo(long milisegundos) {
        long segundos = milisegundos / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;

        milisegundos %= 1000;
        segundos %= 60;
        minutos %= 60;

        return String.format("%02d:%02d:%02d:%03d", horas, minutos, segundos, milisegundos);
    }

    private static void recorrer(File ubI, ArrayList<Fichero> archivos, ArrayList<File> omitir,
            boolean ignorarSistema, boolean ignorarOcultos)
            throws IOException, FileNotFoundException, NoSuchAlgorithmException {
        ArrayList<File> subList = new ArrayList<>();
        subList.add(ubI);
        for (int i = 0; i < subList.size(); i++) {
            File ub = subList.get(i);
            if (ub.exists()) {
                boolean isSystem = false;
                if (ignorarSistema) {
                    try {
                        // Obtener los atributos específicos de Windows
                        DosFileAttributes dosAttributes = Files.readAttributes(ub.toPath(), DosFileAttributes.class);

                        isSystem = dosAttributes.isSystem();
                    } catch (IOException e) {
                        System.err.println("Error al leer los atributos del archivo: " + e.getMessage());
                        throw new IOException("ERROR FATAL!");
                    }
                }

                if (ignorarOcultos && ub.isHidden()) {
                    omitir.add(ub);
                } else if (isSystem) {
                    omitir.add(ub);
                } else {
                    if (ub.isDirectory()) {
                        subList.addAll(Arrays.asList(ub.listFiles()));
                    } else if (ub.isFile()) {
                        //System.out.println("Fichero Creado");
                        archivos.add(new Fichero(ub.toPath()));
                    } else {
                        System.err.println("El archivo es cosa rara: " + ub.toString());
                    }

                }
            } else {
                System.err.println("EL ARCHIVO NO EXISTE! " + ub.toString());
            }
        }
    }

    public static void add(Fichero fc, HashMap<String, LinkedList<Fichero>> procesados, boolean prioridadOld) throws IOException {
        LinkedList<Fichero> lk = procesados.get(fc.getHexHash());
        if (lk != null) {
            for (Fichero f : lk)
                if (fc.getFile().getAbsolutePath().equals(f.getFile().getAbsolutePath()))
                    return;

            Fichero primero = lk.getFirst();
            if (primero.comparar(fc)) {
                //Inciamos solucion de colisiones
                if (prioridadOld) {
                    if (primero.getModify() <= fc.getModify())
                        lk.addLast(fc);
                    else
                        lk.addFirst(fc);
                } else {
                    if (primero.getModify() >= fc.getModify())
                        lk.addLast(fc);
                    else
                        lk.addFirst(fc);
                }
            } else
                System.err.println("Comparacion erronea, mismo hash");
        } else {
            lk = new LinkedList<>();
            lk.add(fc);
            procesados.put(fc.getHexHash(), lk);
        }
    }
}
