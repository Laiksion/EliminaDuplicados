package es.laiksion.EliminaDuplicados;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 *
 * @author Juan <Laiksion>
 */
public class Fichero extends File {

    private final static String ALGORITMO = "MD5"; //"SHA-1"
    private final static DateFormat FORMATO = new SimpleDateFormat("[dd/MM/yyyy] HH:mm:ss.SSS");

    private Path rutaInicial;
    private final byte[] fileHash;

    static {
        FORMATO.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    public Fichero(Path ruta) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        super(ruta.toUri());

        if (!(this.exists() && this.isFile() && this.canRead()))
            throw new FileNotFoundException("Fichero Incorrecto!:\nExist: " + this.exists()
                    + "\nIsFile: " + this.isFile() + "\nCanRead: " + this.canRead());

        this.rutaInicial = null;
        byte[] hash;
        try (InputStream file = new FileInputStream(this)) {
            MessageDigest md = MessageDigest.getInstance(ALGORITMO);

            try (DigestInputStream dg = new DigestInputStream(file, md)) {
                md = dg.getMessageDigest();

                byte[] bytes = new byte[/*4096*/8192];

                while (dg.available() > 0) {
                    dg.read(bytes);
                }
            }

            hash = md.digest();
        }
        this.fileHash = hash;
    }

    public Fichero(Path ruta, byte[] fileHash) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        super(ruta.toUri());

        if (!(this.exists() && this.isFile() && this.canRead()))
            throw new FileNotFoundException("Fichero Incorrecto!:\nExist: " + this.exists()
                    + "\nIsFile: " + this.isFile() + "\nCanRead: " + this.canRead());

        this.rutaInicial = null;
        this.fileHash = fileHash;
    }

    public Fichero(Path ruta, Path rutaInicial, byte[] fileHash) throws IOException, FileNotFoundException, NoSuchAlgorithmException {
        this(ruta, fileHash);
        this.rutaInicial = rutaInicial;
    }

    public Fichero(File fl) throws IOException, FileNotFoundException, NoSuchAlgorithmException {
        this(fl.toPath());
    }

    public Fichero(File fl, Path rutaInicial) throws IOException, FileNotFoundException, NoSuchAlgorithmException {
        this(fl.toPath());
        this.rutaInicial = rutaInicial;
    }

    public byte[] getHash() {
        return this.fileHash;
    }

    public String getHexHash() {
        StringBuilder textHash = new StringBuilder();
        for (Byte b : this.fileHash)
            textHash.append(String.format("%02x", b));

        return textHash.toString().toUpperCase();
    }

    public String getOriginalName() {
        String rutaString = this.rutaInicial.toString();

        int pos = Integer.max(rutaString.lastIndexOf('\\'), rutaString.lastIndexOf('/'));
        return rutaString.substring(pos);
    }

    public File getFile() {
        return this;
    }

    public long getModify() {
        return this.lastModified();
    }

    public Path getOldPath() {
        return this.rutaInicial;
    }

    public Fichero move(Path directory, String prefix) throws IOException, FileNotFoundException, NoSuchAlgorithmException {
        Fichero newFile;
        Path local = directory.toAbsolutePath().resolve(prefix + this.getName());

        newFile = new Fichero(Files.move(this.toPath().toAbsolutePath(), local), this.toPath(), this.fileHash);

        return newFile;
    }

    /**
     * Compara la cantidad de bytes
     *
     * @param archivo Archivo a comparar con el propio
     *
     * @return Si tienen el mismo numero de bytes
     */
    public boolean comparar(Fichero archivo) {
        return this.length() == archivo.length();
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String pad) {
        StringBuilder out = new StringBuilder(pad + '\n');
        out.append("|> File: ").append(pad).append(this.getName());
        if (this.rutaInicial != null)
            out.append('\n').append(pad).append("\tRuta I.:\t").append(this.rutaInicial);
        out.append('\n').append(pad).append("\tRuta A.:\t").append(this.getAbsolutePath());
        out.append('\n').append(pad).append("\tTamaño(B):\t").append(this.length());
        out.append('\n').append(pad).append("\tMod. (+0):\t").append(FORMATO.format(this.lastModified()));
        out.append('\n').append(pad).append("\tHASH:\t\t").append(this.getHexHash());

        out.append('\n');

        return out.toString();
    }

}
