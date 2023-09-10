package es.laiksion.EliminaDuplicados;

/**
 *
 * @author Juan <Laiksion>
 */
public class ProgressBar {

    private final int ANCHURA;
    private final int TOTAL;
    private final String TITULO;

    private final static char LLENO = '█'; // Carácter que representa una parte completa de la barra
    private final static char VACIO = '|'; // Carácter que representa una parte vacía de la barra  

    /**
     * Crea una barra de progreso customizada
     * 
     * @param totalElementos Numero de elementos maximos, para alcanzar el 100%
     */
    public ProgressBar(int totalElementos) {
        this(totalElementos, "Progreso:");
    }

    /**
     * Crea una barra de progreso customizada
     * 
     * @param totalElementos Numero de elementos maximos, para alcanzar el 100%
     * @param titulo Titulo de la barra (Sin separador final)
     */
    public ProgressBar(int totalElementos, String titulo) {
        this(50, totalElementos, titulo);
    }

    /**
     * Crea una barra de progreso customizada
     * 
     * @param anchura Anchura en caracteres de la barra
     * @param totalElementos Numero de elementos maximos, para alcanzar el 100%
     * @param titulo Titulo de la barra (Sin espaciador final)
     */
    public ProgressBar(int anchura, int totalElementos, String titulo) {
        this.ANCHURA = anchura;
        this.TOTAL = totalElementos;
        this.TITULO = titulo;
    }

    /**
     * Crea una barra con el progreso actual
     * 
     * @param completado Numero de elementos completados
     * @param mensaje Texto al final de la barra
     * @return Una barra de progreso customizada
     */
    public String toString(int completado, String mensaje) {
        StringBuilder barra = new StringBuilder().append('\r');
        barra.append(this.TITULO);

        final double PORCENTAJE;

        if (TOTAL < completado)
            PORCENTAJE = 1;
        else if (completado < 0)
            PORCENTAJE = 0;
        else
            PORCENTAJE = (double) completado / TOTAL;

        int caracteresCompletos = (int) (ANCHURA * PORCENTAJE);

        barra.append(" [");
        for (int i = 0; i < ANCHURA; i++) {
            if (i <= caracteresCompletos)
                barra.append(LLENO);
            else
                barra.append(VACIO);
        }
        barra.append("] ");

        barra.append(String.format(" %.2f%%", PORCENTAJE * 100));

        barra.append('\s');
        
        barra.append(mensaje);

        return barra.toString();
    }
    
    public String toString(int completado) {
        return toString(completado,"");
    }

}
