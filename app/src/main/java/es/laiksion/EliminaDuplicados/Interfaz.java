package es.laiksion.EliminaDuplicados;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author Juan <Laiksion>
 */
public class Interfaz {

    private static final Scanner inpt;

    static {
        inpt = new Scanner(System.in);
    }

    public static int leerNumero(String request) {
        boolean valido = false;
        int number = 0;
        do {
            valido = true;
            System.out.println(request);
            System.out.print("> ");
            try {
                number = Integer.parseInt(inpt.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("¡Entrada incorrecta!");
                valido = false;
            } catch (NoSuchElementException e) {
                System.err.println("¡Debes introducir algo!");
                valido = false;
            }
        } while (!valido);

        return number;
    }

    public static int leerNumero() {
        return leerNumero("Introduce un numero");
    }

    public static boolean leerCondicion(String request) {
        boolean valido = false;
        boolean resultado = true;

        do {
            System.out.println(request);
            System.out.println("Opciones(T/F,V/F,Y/N,True/False,1/0)");
            System.out.print("> ");
            valido = true;

            try {
                String in = inpt.nextLine();
                if (in.equalsIgnoreCase("T") || in.equalsIgnoreCase("V")
                        || in.equalsIgnoreCase("Y") || in.equalsIgnoreCase("TRUE")
                        || in.equalsIgnoreCase("1"))
                    resultado = true;
                else if (in.equalsIgnoreCase("F") || in.equalsIgnoreCase("N")
                        || in.equalsIgnoreCase("FALSE") || in.equalsIgnoreCase("0"))
                    resultado = false;
                else
                    throw new NumberFormatException("Entrada sin equivalencia");
            } catch (NumberFormatException e) {
                System.err.println("¡Entrada incorrecta!");
                valido = false;
            } catch (NoSuchElementException e) {
                System.err.println("¡Debes introducir algo!");
                valido = false;
            }

        } while (!valido);

        return resultado;
    }

    public static boolean leerCondicion() {
        return leerCondicion("Introduce una confirmacion");
    }

    public static Path leerPath(String request) {
        boolean valido = false;
        Path ruta = null;
        do {
            System.out.println(request);
            System.out.print("> ");
            String entrada = inpt.nextLine();
            valido = true;

            try {
                ruta = Path.of(entrada).toAbsolutePath();
            } catch (NoSuchElementException e) {
                System.err.println("¡Debes introducir algo!");
                valido = false;
            } catch (InvalidPathException e) {
                System.err.println("¡La ruta no es valida!");
                valido = false;
            }
        } while (!valido);

        return ruta;
    }

    public static Path leerPath() {
        return leerPath("Introduce la ruta");
    }
}
