import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.StringTokenizer;
import java.util.Vector;

public class Persona {

    private String nome;
    private String cognome;
    private String telefono;
    private int eta;
    private double stipendio;
    private int posizione;
    private static String desktopPath = System.getProperty("user.home") + "\\Desktop\\";
    private static String filePath = System.getProperty("user.home") + "\\Desktop\\file.dat";
    private static File rubrica = new File(filePath);

    public Persona(String nome, String cognome, String telefono, int eta, double stipendio, int posizione) {
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.eta = eta;
        this.stipendio = stipendio;
        this.posizione = posizione;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getEta() {
        return eta;
    }

    public double getStipendio() {
        return stipendio;
    }

    public int getPosizione() {
        return posizione;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public void setStipendio(double stipendio) {
        this.stipendio = stipendio;
    }

    public void setPosizione(int posizione) {
        this.posizione = posizione;
    }

    static void avviso(String titolo, String messaggio) {
        JOptionPane.showMessageDialog(null, messaggio, titolo, JOptionPane.INFORMATION_MESSAGE);
    }

    static int avvisoScelta(String[] scelte, String titolo, String messaggio) {
        int risultato = JOptionPane.showOptionDialog(null, messaggio, titolo, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, scelte, 0);
        return risultato;
    }

    static int contaPersona() {
        int i = 0;
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader fin = new BufferedReader(fr);
            while (fin.readLine() != null) {
                i++;
            }
        } catch (IOException e) {}
        return i;
    }

    static Persona[] aggiorna() {
        int totale = 0;
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader fin = new BufferedReader(fr);
            while (fin.readLine() != null) {
                totale++;
            }
            fin.close();
            fr.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore: " + e.getMessage(), "Errore", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Persona[] persone = new Persona[totale];
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader fin = new BufferedReader(fr);
            for (int i = 0; i < totale; i++) {
                String riga = fin.readLine();
                StringTokenizer tokenizer = new StringTokenizer(riga, ",");
                String nome = tokenizer.nextToken();
                String cognome = tokenizer.nextToken();
                String telefono = tokenizer.nextToken();
                int eta = Integer.parseInt(tokenizer.nextToken());
                float stipendio = Float.parseFloat(tokenizer.nextToken());
                persone[i] = new Persona(nome, cognome, telefono, eta, stipendio, i+1);
            }
            fin.close();
            fr.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore: " + e.getMessage(), "Errore", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return persone;
    }

    static void inizializza() {
        if (!rubrica.exists()) {
            try {
                FileWriter fw = new FileWriter(filePath);
                JOptionPane.showMessageDialog(null, "File creato!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                fw.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Errore: " + e.getMessage(), "Errore", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    static boolean azzera() {
        if (rubrica.exists()) {
            String[] scelte = {"Si", "No"};
            int scelta = JOptionPane.showOptionDialog(null, "Sei sicuro di voler azzerare il file?", "Conferma", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, scelte, 0);
            if (scelta == 0) {
                JOptionPane.showMessageDialog(null, "File azzerato!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                try {
                    FileWriter fw = new FileWriter(filePath);
                    fw.close();
                    return true;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Errore: " + e.getMessage(), "Errore", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        return false;
    }

    static void aggiungi(String nome, String cognome, String telefono, int eta, double stipendio) {
        if (!rubrica.exists()) {
            try {
                FileWriter fw = new FileWriter(filePath);
                fw.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Errore: " + e.getMessage(), "Errore", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        try {
            FileWriter fw = new FileWriter(filePath, true);
            PrintWriter fout = new PrintWriter(fw);
            String riga = nome + "," + cognome + "," + telefono + "," + eta + "," + stipendio;
            fout.println(riga);
            fout.flush();
            fout.close();
            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Errore: " + e.getMessage(), "Errore", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    static int modifica(Persona p, int totale, boolean rimuovi, Persona modificare) {
        boolean rimosso = false;
        double rStipendio = p.getStipendio();
        rStipendio *= 100;
        rStipendio = Math.round(rStipendio);
        rStipendio /= 100;
        String rigaPersona0 = p.nome + "," + p.cognome + "," + p.telefono + "," + p.eta + "," + rStipendio;
        String rigaPersona = null;
        if (rigaPersona0.charAt(rigaPersona0.length()-1) == '0') {
            rigaPersona = rigaPersona0.substring(0, rigaPersona0.length()-2);
        }
        Vector<String> riscrivere = new Vector<>();
        String rigaPersonaModificare = "";
        if (modificare != null) {
            rStipendio = modificare.getStipendio();
            rStipendio *= 100;
            rStipendio = Math.round(rStipendio);
            rStipendio /= 100;
            rigaPersonaModificare = modificare.nome + "," + modificare.cognome + "," + modificare.telefono + "," + modificare.eta + "," + rStipendio;
        }
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader fin = new BufferedReader(fr);
            for (int i = 0; i < totale; i++) {
                riscrivere.addElement(fin.readLine());
            }
            fin.close();
            fr.close();
            FileWriter fw = new FileWriter(filePath);
            PrintWriter fout = new PrintWriter(fw);
            for (int i = 0; i < riscrivere.size(); i++) {
                String riga = riscrivere.elementAt(i);
                StringTokenizer tokenizer = new StringTokenizer(riga, ",");
                String nomeLetto = tokenizer.nextToken();
                String cognomeLetto = tokenizer.nextToken();
                tokenizer.nextToken();
                tokenizer.nextToken();
                tokenizer.nextToken();
                if ((riga.toLowerCase().equals(rigaPersona.toLowerCase()) || riga.toLowerCase().equals(rigaPersona0.toLowerCase())) && !rimosso) {
                    if (rimuovi) {
                        avviso("Successo", "Persona cancellata correttamente");
                    } else {
                        avviso("Successo", "Persona modificata correttamente");
                        fout.println(rigaPersonaModificare);
                    }
                    rimosso = true;
                } else {
                    fout.println(riga);
                }
            }
            fw.close();
            fout.close();
            if (!rimosso) {
                avviso("Errore", "Errore nella rimozione");
                return 1;
            }
        } catch (IOException e) {
            avviso("Errore", "Errore: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    static void correggi(Vector elenco) {
        try {
            FileWriter fw = new FileWriter(filePath);
            PrintWriter fout = new PrintWriter(fw);
            for (int i = 0; i < elenco.size(); i++) {
                Persona p = (Persona)elenco.elementAt(i);
                double stipendio = p.stipendio;
                stipendio *= 100;
                stipendio = Math.round(stipendio);
                stipendio /= 100;
                fout.println(p.nome + "," + p.cognome + "," + p.telefono + "," + p.eta + "," + stipendio);
                fout.flush();
            }
            fw.close();
            fout.close();
        } catch (IOException e) {
            avviso("Errore", "Errore: " + e.getMessage());
        }
    }

}
