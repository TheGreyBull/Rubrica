import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

public class Frame extends JFrame implements MouseListener, ActionListener, KeyListener {

    private JPanel schermataLista;
    private JPanel schermataAltro;
    private JLabel mostraTotale;
    private JLabel lNumero, lNome, lCognome, lTelefono, lEta, lStipendio, lAvanti, lIndietro;
    private JLabel[][] campi;
    // lAggiungi verranno usati anche nella ricerca
    private JLabel[] lAggiungi;
    private JLayeredPane schermataPrincipale;
    private JTextField[] input;
    private JTextField[] mInput = new JTextField[5];
    private JButton bCreaPersona, bVaiIndietro, bAzzera;
    private JButton bAggiungi, bCerca, bResetta;
    private JButton bConfermaAggiungi, bIndietro, bCorreggi;
    private JButton[] bRimuovi, bModifica;
    private JButton bConfermaModifica, bAnnullaModifica;
    private int pagina = 1;
    private int indiciTrovatiAttivi[];
    private int indiceModifica = -1, posizioneModifica = 0;
    private static int personeTotali = 0;
    private static Vector elenco;
    private static Vector trovati;
    private static Vector elencoOrdinato;
    private boolean aggiornato = false;
    private Dimension dimensioniSchermo = Toolkit.getDefaultToolkit().getScreenSize();
    // Le misure sono state prese su una dimensione di 1920 x 1080: vengono create delle costanti di proporzionalita'
    final private double ALTEZZA = (dimensioniSchermo.getHeight() - 75) / 1080;
    final private double LARGHEZZA = (dimensioniSchermo.getWidth()) / 1920;
    private Color mainBackground = new Color(0x250062);
    private Color panelBackground = new Color(0x3A1078);
    private Color labelBackground = new Color(0x290042);
    private Color editBackground = new Color(0x3C0042);
    private Color buttonBackground = new Color(0x110C44);
    private Color ordinatoBackground = new Color(0x300D42);
    private boolean cerca = false;
    private boolean modificaAggiunto = false;
    private boolean modificando = false;
    private boolean nomeOrdinato = false;
    private boolean cognomeOrdinato = false;
    private boolean telefonoOrdinato = false;
    private boolean etaOrdinata = false;
    private boolean stipendioOrdinato = false;

    private void visualizza(int pagina, Vector mostra) {

        int inc = 0;
        for (int i = 8*(pagina-1); i < 8*pagina && i < mostra.size(); i++) {
            Persona p = (Persona) mostra.elementAt(i);
            indiciTrovatiAttivi[inc] = p.getPosizione() - 1;
            inc++;
        }

        for (int i = 0; i < 8; i++) {
            try {
                schermataLista.remove(campi[i][0]);
                schermataLista.remove(campi[i][1]);
                schermataLista.remove(campi[i][2]);
                schermataLista.remove(campi[i][3]);
                schermataLista.remove(campi[i][4]);
                schermataLista.remove(campi[i][5]);
                schermataLista.remove(campi[i][6]);
            } catch (Exception e) {
                break;
            }
        }
        try {
            schermataLista.remove(mInput[0]);
            schermataLista.remove(mInput[1]);
            schermataLista.remove(mInput[2]);
            schermataLista.remove(mInput[3]);
            schermataLista.remove(mInput[4]);
            schermataLista.remove(bAnnullaModifica);
            schermataLista.remove(bConfermaModifica);
        } catch (Exception e) {}
        this.repaint();
        int j = 8*(pagina-1);
        for (int i = 0; i < 8; i++) {
            if (i >= personeTotali) {
                return;
            }
            int x, y, larghezza, altezza;
            Persona p;
            try {
                p = (Persona) mostra.elementAt(j);
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
            // La y e' fissa
            y = 0;
            switch (i) {
                case 0:
                    y = (int) (ALTEZZA * 160);
                    break;
                case 1:
                    y = (int) (ALTEZZA * 265);
                    break;
                case 2:
                    y = (int) (ALTEZZA * 370);
                    break;
                case 3:
                    y = (int) (ALTEZZA * 475);
                    break;
                case 4:
                    y = (int) (ALTEZZA * 580);
                    break;
                case 5:
                    y = (int) (ALTEZZA * 685);
                    break;
                case 6:
                    y = (int) (ALTEZZA * 790);
                    break;
                case 7:
                    y = (int) (ALTEZZA * 895);
                    break;
            }

            x = (int) (LARGHEZZA * 5);
            larghezza = (int) (LARGHEZZA * 95);
            altezza = (int) (ALTEZZA * 100);
            campi[i][0].setBounds(x, y, larghezza, altezza);
            campi[i][0].setText(p.getPosizione() + "");

            x = (int) (LARGHEZZA * 105);
            larghezza = (int) (LARGHEZZA * 255);
            altezza = (int) (ALTEZZA * 100);
            campi[i][1].setBounds(x, y, larghezza, altezza);
            campi[i][1].setText(p.getNome());

            x = (int) (LARGHEZZA * 360);
            larghezza = (int) (LARGHEZZA * 255);
            altezza = (int) (ALTEZZA * 100);
            campi[i][2].setBounds(x, y, larghezza, altezza);
            campi[i][2].setText(p.getCognome());

            x = (int) (LARGHEZZA * 615);
            larghezza = (int) (LARGHEZZA * 355);
            altezza = (int) (ALTEZZA * 100);
            campi[i][3].setBounds(x, y, larghezza, altezza);
            campi[i][3].setText(p.getTelefono());

            x = (int) (LARGHEZZA * 970);
            larghezza = (int) (LARGHEZZA * 155);
            altezza = (int) (ALTEZZA * 100);
            campi[i][4].setBounds(x, y, larghezza, altezza);
            campi[i][4].setText(p.getEta() + "");

            x = (int) (LARGHEZZA * 1125);
            larghezza = (int) (LARGHEZZA * 250);
            altezza = (int) (ALTEZZA * 100);
            campi[i][5].setBounds(x, y, larghezza, altezza);
            // Risolvere il problema della scorretta approssimazione di un numero a causa del double
            double stipendio = p.getStipendio();
            stipendio *= 100;
            stipendio = Math.round(stipendio);
            stipendio /= 100;
            campi[i][5].setText(String.valueOf(stipendio));

            x = (int) (LARGHEZZA * 1380);
            larghezza = (int) (LARGHEZZA * 65);
            altezza = (int) (ALTEZZA * 100);
            campi[i][6].setBounds(x, y, larghezza, altezza);
            campi[i][6].setText("...");

            schermataLista.add(campi[i][0]);
            schermataLista.add(campi[i][1]);
            schermataLista.add(campi[i][2]);
            schermataLista.add(campi[i][3]);
            schermataLista.add(campi[i][4]);
            schermataLista.add(campi[i][5]);
            schermataLista.add(campi[i][6]);
            this.repaint();
            j++;
        }
    }

    private void aggiorna() {
        Persona[] persone = Persona.aggiorna();
        personeTotali = persone.length;
        elenco.removeAllElements();
        try {
            for (int i = 0; i < persone.length; i++) {
                elenco.addElement(persone[i]);
            }
            aggiornato = !aggiornato;
        } catch (Exception k) {
            Persona.avviso("Errore", "Errore: " + k.getMessage());
        }
    }

    Frame() {
        this.setVisible(true);
        this.setSize((int) dimensioniSchermo.getWidth(), (int) dimensioniSchermo.getHeight());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Rubrica");
        this.setLayout(null);

        this.getContentPane().setBackground(mainBackground);
        int x, y, larghezza, altezza;

        indiciTrovatiAttivi = new int[8];

        Persona.inizializza();
        personeTotali = 0;
        aggiornato = false;

        schermataLista = new JPanel();
        schermataLista.setLayout(null);
        schermataLista.setBackground(panelBackground);
        x = (int) (LARGHEZZA * 40);
        y = (int) (ALTEZZA * 40);
        larghezza = (int) (LARGHEZZA * 1450);
        altezza = (int) (ALTEZZA * 1000);
        schermataLista.setBounds(x, y, larghezza, altezza);

        schermataAltro = new JPanel();
        schermataAltro.setLayout(null);
        schermataAltro.setBackground(panelBackground);
        x = (int) (LARGHEZZA * 1525);
        y = (int) (ALTEZZA * 40);
        larghezza = (int) (LARGHEZZA * 355);
        altezza = (int) (ALTEZZA * 1000);
        schermataAltro.setBounds(x, y, larghezza, altezza);

        lNumero = new JLabel("N.");
        lNumero.setOpaque(true);
        lNumero.setBackground(labelBackground);
        lNumero.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 5);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 95);
        altezza = (int) (ALTEZZA * 95);
        lNumero.setBounds(x, y, larghezza, altezza);
        lNumero.setFont(new Font("Futura", Font.PLAIN, 36));
        lNumero.setHorizontalAlignment(JLabel.CENTER);
        lNumero.setVerticalAlignment(JLabel.CENTER);
        lNumero.addMouseListener(this);

        lNome = new JLabel("Nome");
        lNome.setOpaque(true);
        lNome.setBackground(labelBackground);
        lNome.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 105);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 250);
        altezza = (int) (ALTEZZA * 95);
        lNome.setBounds(x, y, larghezza, altezza);
        lNome.setFont(new Font("Futura", Font.PLAIN, 36));
        lNome.setHorizontalAlignment(JLabel.CENTER);
        lNome.setVerticalAlignment(JLabel.CENTER);
        lNome.addMouseListener(this);

        lCognome = new JLabel("Cognome");
        lCognome.setOpaque(true);
        lCognome.setBackground(labelBackground);
        lCognome.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 360);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 250);
        altezza = (int) (ALTEZZA * 95);
        lCognome.setBounds(x, y, larghezza, altezza);
        lCognome.setFont(new Font("Futura", Font.PLAIN, 36));
        lCognome.setHorizontalAlignment(JLabel.CENTER);
        lCognome.setVerticalAlignment(JLabel.CENTER);
        lCognome.addMouseListener(this);

        lTelefono = new JLabel("Telefono");
        lTelefono.setOpaque(true);
        lTelefono.setBackground(labelBackground);
        lTelefono.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 615);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 350);
        altezza = (int) (ALTEZZA * 95);
        lTelefono.setBounds(x, y, larghezza, altezza);
        lTelefono.setFont(new Font("Futura", Font.PLAIN, 36));
        lTelefono.setHorizontalAlignment(JLabel.CENTER);
        lTelefono.setVerticalAlignment(JLabel.CENTER);
        lTelefono.addMouseListener(this);

        lEta = new JLabel("Eta'");
        lEta.setOpaque(true);
        lEta.setBackground(labelBackground);
        lEta.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 970);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 150);
        altezza = (int) (ALTEZZA * 95);
        lEta.setBounds(x, y, larghezza, altezza);
        lEta.setFont(new Font("Futura", Font.PLAIN, 36));
        lEta.setHorizontalAlignment(JLabel.CENTER);
        lEta.setVerticalAlignment(JLabel.CENTER);
        lEta.addMouseListener(this);

        lStipendio = new JLabel("Stipendio");
        lStipendio.setOpaque(true);
        lStipendio.setBackground(labelBackground);
        lStipendio.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 1125);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 250);
        altezza = (int) (ALTEZZA * 95);
        lStipendio.setBounds(x, y, larghezza, altezza);
        lStipendio.setFont(new Font("Futura", Font.PLAIN, 36));
        lStipendio.setHorizontalAlignment(JLabel.CENTER);
        lStipendio.setVerticalAlignment(JLabel.CENTER);
        lStipendio.addMouseListener(this);

        lAvanti = new JLabel(">");
        lAvanti.setOpaque(true);
        lAvanti.setBackground(labelBackground);
        lAvanti.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 1380);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 65);
        altezza = (int) (ALTEZZA * 46);
        lAvanti.setBounds(x, y, larghezza, altezza);
        lAvanti.setFont(new Font("Futura", Font.PLAIN, 36));
        lAvanti.setHorizontalAlignment(JLabel.CENTER);
        lAvanti.setVerticalAlignment(JLabel.CENTER);
        lAvanti.addMouseListener(this);

        lIndietro = new JLabel("<");
        lIndietro.setOpaque(true);
        lIndietro.setBackground(labelBackground);
        lIndietro.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 1380);
        y = (int) (ALTEZZA * 54);
        larghezza = (int) (LARGHEZZA * 65);
        altezza = (int) (ALTEZZA * 46);
        lIndietro.setBounds(x, y, larghezza, altezza);
        lIndietro.setFont(new Font("Futura", Font.PLAIN, 36));
        lIndietro.setHorizontalAlignment(JLabel.CENTER);
        lIndietro.setVerticalAlignment(JLabel.CENTER);
        lIndietro.addMouseListener(this);

        bAggiungi = new JButton("Aggiungi");
        bAggiungi.setBackground(buttonBackground);
        bAggiungi.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 5);
        y = (int) (ALTEZZA * 370);
        larghezza = (int) (LARGHEZZA * 345);
        altezza = (int) (ALTEZZA * 100);
        bAggiungi.setBounds(x, y, larghezza, altezza);
        bAggiungi.setFont(new Font("Futura", Font.PLAIN, 36));
        bAggiungi.setHorizontalAlignment(JLabel.CENTER);
        bAggiungi.setVerticalAlignment(JLabel.CENTER);
        bAggiungi.setFocusable(false);
        bAggiungi.addActionListener(this);

        bCerca = new JButton("Cerca");
        bCerca.setBackground(buttonBackground);
        bCerca.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 5);
        y = (int) (ALTEZZA * 475);
        larghezza = (int) (LARGHEZZA * 345);
        altezza = (int) (ALTEZZA * 100);
        bCerca.setBounds(x, y, larghezza, altezza);
        bCerca.setFont(new Font("Futura", Font.PLAIN, 36));
        bCerca.setHorizontalAlignment(JLabel.CENTER);
        bCerca.setVerticalAlignment(JLabel.CENTER);
        bCerca.setFocusable(false);
        bCerca.addActionListener(this);

        bConfermaAggiungi = new JButton("+");
        bConfermaAggiungi.setBackground(buttonBackground);
        bConfermaAggiungi.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 128);
        y = (int) (ALTEZZA * 737);
        larghezza = (int) (LARGHEZZA * 100);
        altezza = (int) (ALTEZZA * 100);
        bConfermaAggiungi.setBounds(x, y, larghezza, altezza);
        bConfermaAggiungi.setFont(new Font("Futura", Font.PLAIN, 36));
        bConfermaAggiungi.setHorizontalAlignment(JLabel.CENTER);
        bConfermaAggiungi.setVerticalAlignment(JLabel.CENTER);
        bConfermaAggiungi.setFocusable(false);
        bConfermaAggiungi.addActionListener(this);

        bIndietro = new JButton("<");
        bIndietro.setBackground(buttonBackground);
        bIndietro.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 275);
        y = (int) (ALTEZZA * 915);
        larghezza = (int) (LARGHEZZA * 75);
        altezza = (int) (ALTEZZA * 75);
        bIndietro.setBounds(x, y, larghezza, altezza);
        bIndietro.setFont(new Font("Futura", Font.PLAIN, 36));
        bIndietro.setHorizontalAlignment(JLabel.CENTER);
        bIndietro.setVerticalAlignment(JLabel.CENTER);
        bIndietro.setFocusable(false);
        bIndietro.addActionListener(this);

        bCorreggi = new JButton("Correggi maiuscole");
        bCorreggi.setBackground(buttonBackground);
        bCorreggi.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 5);
        y = (int) (ALTEZZA * 580);
        larghezza = (int) (LARGHEZZA * 345);
        altezza = (int) (ALTEZZA * 100);
        bCorreggi.setBounds(x, y, larghezza, altezza);
        bCorreggi.setFont(new Font("Futura", Font.PLAIN, 36));
        bCorreggi.setHorizontalAlignment(JLabel.CENTER);
        bCorreggi.setVerticalAlignment(JLabel.CENTER);
        bCorreggi.setFocusable(false);
        bCorreggi.addActionListener(this);

        schermataPrincipale = new JLayeredPane();
        schermataPrincipale.setBounds(0, 0, (int)(LARGHEZZA * 1920), (int)(ALTEZZA * 1080));
        schermataPrincipale.setLayout(null);
        this.add(schermataPrincipale);

        schermataPrincipale.add(schermataLista, Integer.valueOf(1));
        schermataPrincipale.add(schermataAltro, Integer.valueOf(1));
        schermataLista.add(lNumero);
        schermataLista.add(lNome);
        schermataLista.add(lCognome);
        schermataLista.add(lTelefono);
        schermataLista.add(lEta);
        schermataLista.add(lStipendio);
        schermataLista.add(lAvanti);
        schermataLista.add(lIndietro);
        schermataAltro.add(bAggiungi);
        schermataAltro.add(bCerca);
        schermataAltro.add(bCorreggi);
        this.repaint();

        elenco = new Vector();
        personeTotali = Persona.contaPersona();

        campi = new JLabel[8][7];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                campi[i][j] = new JLabel("");
                campi[i][j].setOpaque(true);
                campi[i][j].setBackground(labelBackground);
                campi[i][j].setForeground(Color.WHITE);
                campi[i][j].setFont(new Font("Futura", Font.PLAIN,28));
                campi[i][j].setHorizontalAlignment(JLabel.CENTER);
                campi[i][j].setVerticalAlignment(JLabel.CENTER);
                if (j == 6) {
                    campi[i][6].addMouseListener(this);
                    campi[i][6].setFont(new Font("Futura", Font.BOLD,30));
                }
            }
        }
        aggiorna();
        visualizza(pagina, elenco);

        mostraTotale = new JLabel("Persone: " + personeTotali);
        mostraTotale.setOpaque(true);
        mostraTotale.setBackground(labelBackground);
        mostraTotale.setForeground(Color.LIGHT_GRAY);
        x = (int) (LARGHEZZA * 5);
        y = (int) (ALTEZZA * 5);
        larghezza = (int) (LARGHEZZA * 150);
        altezza = (int) (ALTEZZA * 50);
        mostraTotale.setBounds(x, y, larghezza, altezza);
        mostraTotale.setFont(new Font("Futura", Font.PLAIN, 20));
        mostraTotale.setHorizontalAlignment(JLabel.CENTER);
        mostraTotale.setVerticalAlignment(JLabel.CENTER);

        bResetta = new JButton("Azzera il file");
        bResetta.setBackground(buttonBackground);
        bResetta.setForeground(Color.WHITE);
        x = (int) (LARGHEZZA * 5);
        y = (int) (ALTEZZA * 685);
        larghezza = (int) (LARGHEZZA * 345);
        altezza = (int) (ALTEZZA * 100);
        bResetta.setBounds(x, y, larghezza, altezza);
        bResetta.setFont(new Font("Futura", Font.PLAIN, 36));
        bResetta.setHorizontalAlignment(JLabel.CENTER);
        bResetta.setVerticalAlignment(JLabel.CENTER);
        bResetta.setFocusable(false);
        bResetta.addActionListener(this);

        schermataAltro.add(mostraTotale);
        schermataAltro.add(bResetta);

        bRimuovi = new JButton[8];
        bModifica = new JButton[8];
        for (int i = 0; i < 8; i++) {
            bRimuovi[i] = new JButton("Rimuovi");
            bModifica[i] = new JButton("Modifica");
            x = (int) (LARGHEZZA * 1420);
            int yR = 0, yM = 0;
            larghezza = (int) (LARGHEZZA * 250);
            altezza = (int) (ALTEZZA * 75);
            switch (i) {
                case 0:
                    yR = (int) (ALTEZZA * 300);
                    yM = (int) (ALTEZZA * 375);
                    break;
                case 1:
                    yR = (int) (ALTEZZA * 405);
                    yM = (int) (ALTEZZA * 480);
                    break;
                case 2:
                    yR = (int) (ALTEZZA * 510);
                    yM = (int) (ALTEZZA * 585);
                    break;
                case 3:
                    yR = (int) (ALTEZZA * 615);
                    yM = (int) (ALTEZZA * 690);
                    break;
                case 4:
                    yR = (int) (ALTEZZA * 720);
                    yM = (int) (ALTEZZA * 795);
                    break;
                case 5:
                    yR = (int) (ALTEZZA * 825);
                    yM = (int) (ALTEZZA * 900);
                    break;
                case 6:
                    yR = (int) (ALTEZZA * 930);
                    yM = (int) (ALTEZZA * 1005);
                    break;
                case 7:
                    yR = (int) (ALTEZZA * 1035);
                    yM = (int) (ALTEZZA * 1110);
                    break;
            }
            bottoniAggiungi(x, yR, larghezza, altezza, bRimuovi[i]);
            bottoniAggiungi(x, yM, larghezza, altezza, bModifica[i]);
            schermataAltro.remove(bRimuovi[i]);
            schermataAltro.remove(bModifica[i]);
        }

        this.addMouseListener(this);
        this.repaint();
    }

    private void bottoniAggiungi(int x, int y, int larghezza, int altezza, JButton bottone) {
        bottone.setBackground(buttonBackground);
        bottone.setForeground(Color.WHITE);
        bottone.setBounds(x, y, larghezza, altezza);
        bottone.setFont(new Font("Futura", Font.BOLD, 36));
        bottone.setHorizontalAlignment(JLabel.CENTER);
        bottone.setVerticalAlignment(JLabel.CENTER);
        bottone.setFocusable(false);
        bottone.addActionListener(this);
        schermataAltro.add(bottone);
    }

    private void ordinaNome(boolean alfabetico, boolean oNome) {
        /*String[] nomi = new String[personeTotali];
        for (int i = 0; i < personeTotali; i++) {
            if (oNome) {
                nomi[i] = ((Persona) elenco.elementAt(i)).getNome().toLowerCase();
            } else {
                nomi[i] = ((Persona) elenco.elementAt(i)).getCognome().toLowerCase();
            }
        }
        for (int i = 0; i < personeTotali; i++) {
            for (int j = i+1; j < personeTotali; j++) {
                if (nomi[i].compareTo(nomi[j]) > 0) {
                    String temp = nomi[i];
                    nomi[i] = nomi[j];
                    nomi[j] = temp;
                }
            }
        }
        Vector ordinato = new Vector();
        for (int i = 0; i < personeTotali; i++) {
            for (int j = 0; j < personeTotali; j++) {
                Persona p;
                try {
                    p = (Persona) elenco.elementAt(j);
                    if ((nomi[i].equals(p.getNome().toLowerCase()) && oNome) || (nomi[i].equals(p.getCognome().toLowerCase()) && !oNome)) {
                        ordinato.addElement(p);
                        elenco.setElementAt(null, j);
                    }
                } catch (Exception e) {}
            }
        }
        aggiorna();
        elencoOrdinato = new Vector();
        if (alfabetico) {
            for (int i = 0; i < personeTotali; i++) {
                elencoOrdinato.addElement((Persona) ordinato.elementAt(i));
            }
        } else {
            for (int i = personeTotali-1; i >= 0; i--) {
                elencoOrdinato.addElement((Persona) ordinato.elementAt(i));
            }
        }
        visualizza(1, elencoOrdinato);*/
    }

    private void coloraOrdinato(JLabel ordinato, JLabel nonOrdinato1, JLabel nonOrdinato2, JLabel nonOrdinato3, JLabel nonOrdinato4, JLabel nonOrdinato5, boolean ripristina) {
        if (!ripristina) {
            ordinato.setBackground(ordinatoBackground);
        } else {
            ordinato.setBackground(labelBackground);
        }
        nonOrdinato1.setBackground(labelBackground);
        nonOrdinato2.setBackground(labelBackground);
        nonOrdinato3.setBackground(labelBackground);
        nonOrdinato4.setBackground(labelBackground);
        nonOrdinato5.setBackground(labelBackground);

    }

    public void togliOrdinaSeRicerca() {
        elencoOrdinato = null;
        pagina = 1;
        if (cerca) {
            try {
                visualizza(pagina, trovati);
            } catch (Exception k) {
                visualizza(pagina, elenco);
            }
        }  else {
            visualizza(pagina, elenco);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getModifiers() & e.BUTTON1_MASK) != 0) {
            if (e.getSource() == lNumero) {

            } else if (e.getSource() == lNome) {
                if (!nomeOrdinato) {
                    String[] scelte = {"Alfabetico (A-Z)", "Non alfabetico (Z-A)", "Annulla"};
                    int risultato = Persona.avvisoScelta(scelte, "Ordinamento nome", "Come si vuole ordinare il nome?");
                    if (risultato == 0) {
                        ordinaNome(true, true);
                    } else if (risultato == 1) {
                        ordinaNome(false, true);
                    }
                    if (risultato < 2) {
                        if (cerca) {
                            ricerca(input[0].getText(), input[1].getText(), input[2].getText(), input[3].getText(), input[4].getText());
                        }
                        nomeOrdinato = true;
                        cognomeOrdinato = false;
                        telefonoOrdinato = false;
                        etaOrdinata = false;
                        stipendioOrdinato = false;
                        coloraOrdinato(lNome, lCognome, lTelefono, lEta, lStipendio, lNumero, false);
                    }
                } else {
                    nomeOrdinato = false;
                    aggiorna();
                    togliOrdinaSeRicerca();
                    coloraOrdinato(lNome, lCognome, lTelefono, lEta, lStipendio, lNumero, true);
                }
            } else if (e.getSource() == lCognome) {
                if (!cognomeOrdinato) {
                    String[] scelte = {"Alfabetico (A-Z)", "Non alfabetico (Z-A)", "Annulla"};
                    int risultato = Persona.avvisoScelta(scelte, "Ordinamento cognome", "Come si vuole ordinare il cognome?");
                    if (risultato == 0) {
                        ordinaNome(true, false);
                    } else if (risultato == 1) {
                        ordinaNome(false, false);
                    }
                    if (risultato < 2) {
                        if (cerca) {
                            ricerca(input[0].getText(), input[1].getText(), input[2].getText(), input[3].getText(), input[4].getText());
                        }
                        nomeOrdinato = false;
                        cognomeOrdinato = true;
                        telefonoOrdinato = false;
                        etaOrdinata = false;
                        stipendioOrdinato = false;
                        coloraOrdinato(lCognome, lNome, lTelefono, lEta, lStipendio, lNumero, false);
                    }
                } else {
                    cognomeOrdinato = false;
                    aggiorna();
                    togliOrdinaSeRicerca();
                    coloraOrdinato(lCognome, lNome, lTelefono, lEta, lStipendio, lNumero, true);
                }
            } else if (e.getSource() == lTelefono) {

            } else if (e.getSource() == lEta) {

            } else if (e.getSource() == lStipendio) {

            } else if (e.getSource() == lAvanti) {
                if (modificando) {
                    Persona.avviso("Attenzione", "Prima di cambiare pagina devi terminare la modifica!");
                    return;
                }
                if (pagina * 8 < personeTotali) {
                    pagina++;
                    if (!cerca) {
                        visualizza(pagina, elenco);
                    } else {
                        visualizza(pagina, trovati);
                    }
                }
            } else if (e.getSource() == lIndietro) {
                if (modificando) {
                    Persona.avviso("Attenzione", "Prima di cambiare pagina devi terminare la modifica!");
                    return;
                }
                if (pagina > 1) {
                    pagina--;
                    if (!cerca) {
                        visualizza(pagina, elenco);
                    } else {
                        visualizza(pagina, trovati);
                    }
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    if (e.getSource() == campi[i][6]) {
                        if (modificando) {
                            Persona.avviso("Attenzione", "Prima di effettuare altre operazioni su una persona devi terminare la modifica!");
                            return;
                        }
                        if (!modificaAggiunto) {
                            schermataPrincipale.add(bRimuovi[i], Integer.valueOf(2));
                            schermataPrincipale.add(bModifica[i], Integer.valueOf(2));
                            this.repaint();
                        }
                        modificaAggiunto = true;
                    }
                }
            }
        }
    }

    private void togliModifica() {
        if (modificaAggiunto) {
            for (int i = 0; i < 8; i++) {
                schermataPrincipale.remove(bRimuovi[i]);
                schermataPrincipale.remove(bModifica[i]);
            }
            modificaAggiunto = false;
            this.repaint();
        }
    }

    private void ricerca(String nome, String cognome, String telefono, String eta, String stipendio) {
        togliModifica();
        trovati = new Vector(personeTotali);
        for (int i = 0; i < personeTotali; i++) {
            Persona p;
            try {
                p = (Persona) elencoOrdinato.elementAt(i);
            } catch (Exception k) {
                p = (Persona) elenco.elementAt(i);
            }
            String pNome = p.getNome().toLowerCase();
            String pCognome = p.getCognome().toLowerCase();
            String pTelefono = p.getTelefono();
            String pEta = String.valueOf(p.getEta());
            String pStipendio = String.valueOf(p.getStipendio());
            if (pNome.contains(nome) && pCognome.contains(cognome) && pTelefono.contains(telefono) && pEta.contains(eta) && pStipendio.contains(stipendio)) {
                trovati.addElement(p);
            }
        }
        pagina = 1;
        visualizza(pagina, trovati);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        togliModifica();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        togliModifica();
        if (e.getSource() == bAggiungi) {
            lAggiungi = new JLabel[6];
            input = new JTextField[5];
            schermataAltro.removeAll();

            lAggiungi[5] = new JLabel("Aggiungi");
            lAggiungi[5].setOpaque(true);
            lAggiungi[5].setForeground(Color.WHITE);
            lAggiungi[5].setBackground(labelBackground);
            lAggiungi[5].setFont(new Font("Futura", Font.PLAIN, 36));
            lAggiungi[5].setHorizontalAlignment(JLabel.CENTER);
            lAggiungi[5].setVerticalAlignment(JLabel.CENTER);
            int x, y, larghezza, altezza;
            x = (int) (LARGHEZZA * 5);
            y = (int) (ALTEZZA * 5);
            larghezza = (int) (LARGHEZZA * 345);
            altezza = (int) (ALTEZZA * 95);
            lAggiungi[5].setBounds(x, y, larghezza, altezza);
            schermataAltro.add(lAggiungi[5]);

            for (int i = 0; i < 5; i++) {
                input[i] = new JTextField();
                input[i].setEditable(true);
                input[i].setOpaque(true);
                input[i].setBackground(labelBackground);
                input[i].setForeground(Color.WHITE);
                input[i].setFont(new Font("Futura", Font.PLAIN, 28));
                input[i].setHorizontalAlignment(JLabel.CENTER);
                input[i].setBorder(null);
                input[i].setFocusable(true);
                switch (i) {
                    case 0:
                        lAggiungi[i] = new JLabel("Nome");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 265);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 1:
                        lAggiungi[i] = new JLabel("Cognome");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 343);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 2:
                        lAggiungi[i] = new JLabel("Telefono");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 421);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 3:
                        lAggiungi[i] = new JLabel("Eta'");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 499);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 4:
                        lAggiungi[i] = new JLabel("Stipendio");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 577);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                }
                lAggiungi[i].setOpaque(true);
                lAggiungi[i].setForeground(Color.WHITE);
                lAggiungi[i].setBackground(labelBackground);
                lAggiungi[i].setFont(new Font("Futura", Font.PLAIN, 24));
                lAggiungi[i].setHorizontalAlignment(JLabel.CENTER);
                lAggiungi[i].setVerticalAlignment(JLabel.CENTER);

                schermataAltro.add(lAggiungi[i]);
                schermataAltro.add(input[i]);
            }

            bCreaPersona = new JButton("+");
            x = (int) (LARGHEZZA * 128);
            y = (int) (ALTEZZA * 737);
            larghezza = (int) (LARGHEZZA * 100);
            altezza = (int) (ALTEZZA * 100);
            bottoniAggiungi(x, y, larghezza, altezza, bCreaPersona);

            bVaiIndietro = new JButton("<");
            x = (int) (LARGHEZZA * 275);
            y = (int) (ALTEZZA * 920);
            larghezza = (int) (LARGHEZZA * 75);
            altezza = (int) (ALTEZZA * 75);
            bottoniAggiungi(x, y, larghezza, altezza, bVaiIndietro);

            bAzzera = new JButton("Azzera");
            x = (int) (LARGHEZZA * 5);
            y = (int) (ALTEZZA * 920);
            larghezza = (int) (LARGHEZZA * 125);
            altezza = (int) (ALTEZZA * 75);
            bottoniAggiungi(x, y, larghezza, altezza, bAzzera);
            bAzzera.setFont(new Font("Futura", Font.PLAIN, 28));

            this.repaint();
        } else if (e.getSource() == bCerca) {
            if (modificando) {
                Persona.avviso("Attenzione", "Prima di cercare una persona devi terminare la modifica!");
                return;
            }
            cerca = true;
            lAggiungi = new JLabel[6];
            input = new JTextField[5];
            schermataAltro.removeAll();

            lAggiungi[5] = new JLabel("Cerca");
            lAggiungi[5].setOpaque(true);
            lAggiungi[5].setForeground(Color.WHITE);
            lAggiungi[5].setBackground(labelBackground);
            lAggiungi[5].setFont(new Font("Futura", Font.PLAIN, 36));
            lAggiungi[5].setHorizontalAlignment(JLabel.CENTER);
            lAggiungi[5].setVerticalAlignment(JLabel.CENTER);
            int x, y, larghezza, altezza;
            x = (int) (LARGHEZZA * 5);
            y = (int) (ALTEZZA * 5);
            larghezza = (int) (LARGHEZZA * 345);
            altezza = (int) (ALTEZZA * 95);
            lAggiungi[5].setBounds(x, y, larghezza, altezza);
            schermataAltro.add(lAggiungi[5]);

            for (int i = 0; i < 5; i++) {
                input[i] = new JTextField();
                input[i].setEditable(true);
                input[i].setOpaque(true);
                input[i].setBackground(labelBackground);
                input[i].setForeground(Color.WHITE);
                input[i].setFont(new Font("Futura", Font.PLAIN, 28));
                input[i].setHorizontalAlignment(JLabel.CENTER);
                input[i].setBorder(null);
                input[i].setFocusable(true);
                switch (i) {
                    case 0:
                        lAggiungi[i] = new JLabel("Nome");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 265);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 1:
                        lAggiungi[i] = new JLabel("Cognome");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 343);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 2:
                        lAggiungi[i] = new JLabel("Telefono");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 421);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 3:
                        lAggiungi[i] = new JLabel("Eta'");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 499);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                    case 4:
                        lAggiungi[i] = new JLabel("Stipendio");
                        x = (int) (LARGHEZZA * 5);
                        y = (int) (ALTEZZA * 577);
                        larghezza = (int) (LARGHEZZA * 125);
                        altezza = (int) (ALTEZZA * 75);
                        lAggiungi[i].setBounds(x, y, larghezza, altezza);

                        x = (int) (LARGHEZZA * 135);
                        larghezza = (int) (LARGHEZZA * 215);
                        input[i].setBounds(x, y, larghezza, altezza);
                        break;
                }
                lAggiungi[i].setOpaque(true);
                lAggiungi[i].setForeground(Color.WHITE);
                lAggiungi[i].setBackground(labelBackground);
                lAggiungi[i].setFont(new Font("Futura", Font.PLAIN, 24));
                lAggiungi[i].setHorizontalAlignment(JLabel.CENTER);
                lAggiungi[i].setVerticalAlignment(JLabel.CENTER);

                schermataAltro.add(lAggiungi[i]);
                schermataAltro.add(input[i]);
            }

            bVaiIndietro = new JButton("<");
            x = (int) (LARGHEZZA * 275);
            y = (int) (ALTEZZA * 920);
            larghezza = (int) (LARGHEZZA * 75);
            altezza = (int) (ALTEZZA * 75);
            bottoniAggiungi(x, y, larghezza, altezza, bVaiIndietro);

            bAzzera = new JButton("Azzera");
            x = (int) (LARGHEZZA * 5);
            y = (int) (ALTEZZA * 920);
            larghezza = (int) (LARGHEZZA * 125);
            altezza = (int) (ALTEZZA * 75);
            bottoniAggiungi(x, y, larghezza, altezza, bAzzera);
            bAzzera.setFont(new Font("Futura", Font.PLAIN, 28));
            this.repaint();

            cerca = true;
            CompletableFuture.runAsync(() -> {
                String nome = "", cognome = "", telefono = "", eta = "", stipendio = "";
                while (cerca) {
                    if (!nome.toLowerCase().equals(input[0].getText().toLowerCase())) {
                        nome = input[0].getText();
                        ricerca(nome, cognome, telefono, eta, stipendio);
                    } else if (!cognome.toLowerCase().equals(input[1].getText().toLowerCase())) {
                        cognome = input[1].getText();
                        ricerca(nome, cognome, telefono, eta, stipendio);
                    } else if (!telefono.equals(input[2].getText())) {
                        telefono = input[2].getText();
                        ricerca(nome, cognome, telefono, eta, stipendio);
                    } else if (!eta.equals(input[3].getText())) {
                        eta = input[3].getText();
                        ricerca(nome, cognome, telefono, eta, stipendio);
                    } else if (!stipendio.equals(input[4].getText())) {
                        stipendio = input[4].getText();
                        ricerca(nome, cognome, telefono, eta, stipendio);
                    }
                }
            });
        } else if (e.getSource() == bCorreggi) {
            String[] scelte = {"Si", "No", "Annulla"};
            int risultato = Persona.avvisoScelta(scelte, "Conferma", "Vuoi applicare le modifiche anche sul file?");
            if (risultato < 2) {
                Vector temp = new Vector();
                for (int i = 0; i < personeTotali; i++) {
                    Persona p = (Persona)elenco.elementAt(i);
                    String lower = String.valueOf(p.getNome().charAt(0));
                    char upper = lower.toUpperCase().charAt(0);
                    String modificato = p.getNome();
                    modificato = upper + modificato.substring(1);
                    p.setNome(modificato);

                    lower = String.valueOf(p.getCognome().charAt(0));
                    upper = lower.toUpperCase().charAt(0);
                    modificato = p.getCognome();
                    modificato = upper + modificato.substring(1);
                    p.setCognome(modificato);

                    temp.addElement(p);
                }
                elenco.removeAllElements();
                for (int i = 0; i < personeTotali; i++) {
                    elenco.addElement(temp.elementAt(i));
                }
                temp.removeAllElements();
                visualizza(pagina, elenco);
            }
            if (risultato == 0) {
                Persona.correggi(elenco);
                bCorreggi.setEnabled(false);
            }
            try {
                Persona p = (Persona) elenco.elementAt(indiceModifica);
                String[] temp = new String[5];
                temp[0] = p.getNome();
                temp[1] = p.getCognome();
                temp[2] = p.getTelefono();
                temp[3] = String.valueOf(p.getEta());
                temp[4] = String.valueOf(p.getStipendio());
                mostraModifica(temp);
            } catch (Exception k) {}
        } else if (e.getSource() == bVaiIndietro) {
            if (modificando && cerca) {
                Persona.avviso("Attenzione", "Prima di andare indietro devi terminare la modifica!");
                return;
            }
            schermataAltro.removeAll();
            schermataAltro.add(bAggiungi);
            schermataAltro.add(bCerca);
            schermataAltro.add(bCorreggi);
            schermataAltro.add(mostraTotale);
            schermataAltro.add(bResetta);
            try {
                trovati.removeAllElements();
            } catch (Exception k) {}
            cerca = false;
            visualizza(1, elenco);
            try {
                Persona p = (Persona) elenco.elementAt(indiceModifica);
                String[] temp = new String[5];
                temp[0] = p.getNome();
                temp[1] = p.getCognome();
                temp[2] = p.getTelefono();
                temp[3] = String.valueOf(p.getEta());
                temp[4] = String.valueOf(p.getStipendio());
                mostraModifica(temp);
            } catch (Exception k) {}
            this.repaint();
        } else if (e.getSource() == bAzzera) {
            if (modificando && cerca) {
                Persona.avviso("Attenzione", "Prima di azzerare devi terminare la modifica!");
                return;
            }
            for (int i = 0; i < 5; i++) {
                input[i].setText("");
            }
            input[4].transferFocus();
        } else if (e.getSource() == bCreaPersona) {
            if (modificando) {
                Persona.avviso("Attenzione", "Prima di creare una persona devi terminare la modifica!");
                return;
            }
            String in1 = input[0].getText();
            String in2 = input[1].getText();
            String in3 = input[2].getText();
            String in4 = input[3].getText();
            String in5 = input[4].getText();
            // Controlla se la stringa e' vuota oppure rimuove gli spazi vuoti non necessari
            if (in1.equals("") || in2.equals("") || in3.equals("") || in4.equals("") || in5.equals("")) {
                Persona.avviso("Errore", "Compilare i campi vuoti");
            } else if (in3.contains(" ")) {
                Persona.avviso("Errore", "Eliminare gli spazi vuoti nel campo \"Telefono\"");
            } else if (in4.contains(" ")) {
                Persona.avviso("Errore", "Eliminare gli spazi vuoti nel campo \"Eta'\"");
            } else if (in5.contains(" ")) {
                Persona.avviso("Errore", "Eliminare gli spazi vuoti nel campo \"Stipendio\"");
            } else if (in1.charAt(0) == ' ') {
                Persona.avviso("Errore", "Eliminare gli spazi vuoti all'inizio del campo \"Nome\"");
            } else if (in1.charAt(in1.length()-1) == ' ') {
                Persona.avviso("Errore", "Eliminare gli spazi vuoti alla fine del campo \"Nome\"");
            } else if (in2.charAt(0) == ' ') {
                Persona.avviso("Errore", "Eliminare gli spazi vuoti all'inizio del campo \"Cognome\"");
            } else if (in2.charAt(in2.length()-1) == ' ') {
                Persona.avviso("Errore", "Eliminare gli spazi vuoti alla fine del campo \"Cognome\"");
            } else {
                int eta;
                double stipendio;
                try {
                    int telefono = Integer.parseInt(in3);
                } catch (Exception k) {
                    Persona.avviso("Errore", "Numero di telefono non valido");
                    return;
                }
                try {
                    eta = Integer.parseInt(in4);
                } catch (Exception k) {
                    Persona.avviso("Errore", "Eta' non valida");
                    return;
                }
                try {
                    stipendio = Double.parseDouble(in5);
                    stipendio *= 100;
                    stipendio = Math.round(stipendio);
                    stipendio /= 100;
                } catch (Exception k) {
                    Persona.avviso("Errore", "Stipendio non valido");
                    return;
                }
                for (int i = 0; i < elenco.size(); i++) {
                    Persona p = (Persona)elenco.elementAt(i);
                    if (p.getTelefono().equals(in3)) {
                        Persona.avviso("Errore", "Numero di telefono gia' utilizzato");
                        input[1].transferFocus();
                        return;
                    }
                }
                Persona p = new Persona(in1, in2, in3, eta, stipendio, personeTotali+1);
                personeTotali++;
                mostraTotale.setText("Persone: " + personeTotali);
                elenco.addElement(p);
                Persona.aggiungi(in1, in2, in3, eta, stipendio);
                for (int i = 0; i < 5; i++) {
                    input[i].setText("");
                }
                input[4].transferFocus();
                aggiorna();
                pagina = personeTotali / 8 + 1;
                visualizza(pagina, elenco);
                bCorreggi.setEnabled(true);
                bResetta.setEnabled(true);
                bCerca.setEnabled(true);
                this.repaint();
            }
        } else if (e.getSource() == bResetta) {
            boolean successo = Persona.azzera();
            if (successo) {
                elenco.removeAllElements();
                pagina = 1;
                visualizza(pagina, elenco);
                bResetta.setEnabled(false);
                bCorreggi.setEnabled(false);
                bCerca.setEnabled(false);
                personeTotali = 0;
                mostraTotale.setText("Persone: " + personeTotali);
                this.repaint();
            }
        } else if (e.getSource() == bAnnullaModifica) {
            modificando = false;
            posizioneModifica = 0;
            indiceModifica = -1;
            if (cerca) {
                visualizza(pagina, trovati);
            } else {
                visualizza(pagina, elenco);
            }
        } else if (e.getSource() == bConfermaModifica) {
            String scelte[] = {"Si", "No", "Annulla"};
            int scelta = Persona.avvisoScelta(scelte, "Avviso", "Sei sicuro di voler modificare questa persona?");
            if (scelta != 0) {
                return;
            }
            modificando = false;
            posizioneModifica = 0;
            String nome = mInput[0].getText(), cognome = mInput[1].getText(), telefono = mInput[2].getText();
            int eta;
            double stipendio;
            try {
                eta = Integer.parseInt(mInput[3].getText());
            } catch (Exception k) {
                Persona.avviso("Errore", "Eta' non valida");
                return;
            }
            try {
                stipendio = Double.parseDouble(mInput[4].getText());
            } catch (Exception k) {
                Persona.avviso("Errore", "Stipendio non valido");
                return;
            }
            int posizione;
            if (cerca) {
                Persona temp = (Persona) trovati.elementAt(indiceModifica);
                posizione = temp.getPosizione();
            } else {
                Persona temp = (Persona) elenco.elementAt(indiceModifica);
                posizione = temp.getPosizione();
            }
            Persona p = new Persona(nome, cognome, telefono, eta, stipendio, posizione);
            int risultato = Persona.modifica((Persona) elenco.elementAt(indiciTrovatiAttivi[indiceModifica]), personeTotali, false, p);
            if (risultato != 0) {
                return;
            }
            schermataLista.remove(bAnnullaModifica);
            schermataLista.remove(bConfermaModifica);
            aggiorna();
            if (cerca) {
                visualizza(pagina, trovati);
                ricerca(input[0].getText(), input[1].getText(), input[2].getText(), input[3].getText(), input[4].getText());
            } else {
                visualizza(pagina, elenco);
            }
            mostraTotale.setText(String.valueOf(personeTotali));
            indiceModifica = -1;
            this.repaint();
        } else if (!modificaAggiunto){
            for (int i = 0; i < 8; i++) {
                if (e.getSource() == bRimuovi[i]) {
                    String scelte[] = {"Si", "No", "Annulla"};
                    int scelta = Persona.avvisoScelta(scelte, "Avviso", "Sei sicuro di voler rimuovere questa persona?");
                    if (scelta != 0) {
                        return;
                    }
                    int risultato = Persona.modifica((Persona) elenco.elementAt(indiciTrovatiAttivi[i]), personeTotali, true, null);
                    if (risultato == 0) {
                        aggiorna();
                        if (cerca) {
                            visualizza(pagina, trovati);
                            ricerca(input[0].getText(), input[1].getText(), input[2].getText(), input[3].getText(), input[4].getText());
                        } else {
                            visualizza(pagina, elenco);
                        }
                        mostraTotale.setText(String.valueOf(personeTotali));
                        this.repaint();
                    }
                } else if (e.getSource() == bModifica[i]) {
                    Persona p = (Persona) elenco.elementAt(indiciTrovatiAttivi[i]);
                    String temp[] = new String[5];
                    temp[0] = p.getNome();
                    temp[1] = p.getCognome();
                    temp[2] = p.getTelefono();
                    temp[3] = String.valueOf(p.getEta());
                    temp[4] = String.valueOf(p.getStipendio());
                    bConfermaModifica = new JButton("✓");
                    bAnnullaModifica = new JButton("X");
                    int x = (int)(LARGHEZZA * 1380), y = 0, larghezza = (int)(LARGHEZZA * 65), altezza = (int)(ALTEZZA * 48);
                    switch (i) {
                        case 0:
                            y = (int)(ALTEZZA * 160);
                            break;
                        case 1:
                            y = (int)(ALTEZZA * 265);
                            break;
                        case 2:
                            y = (int)(ALTEZZA * 370);
                            break;
                        case 3:
                            y = (int)(ALTEZZA * 475);
                            break;
                        case 4:
                            y = (int)(ALTEZZA * 580);
                            break;
                        case 5:
                            y = (int)(ALTEZZA * 685);
                            break;
                        case 6:
                            y = (int)(ALTEZZA * 790);
                            break;
                        case 7:
                            y = (int)(ALTEZZA * 895);
                            break;
                    }
                    bottoniAggiungi(x, y, larghezza, altezza, bAnnullaModifica);
                    bottoniAggiungi(x, y+50, larghezza, altezza, bConfermaModifica);
                    schermataAltro.remove(bAnnullaModifica);
                    schermataAltro.remove(bConfermaModifica);
                    indiceModifica = i;
                    modificando = true;
                    mostraModifica(temp);
                    this.repaint();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void mostraModifica(String temp[]) {
        schermataLista.add(bAnnullaModifica);
        schermataLista.add(bConfermaModifica);
        for (int k = 0; k < 5; k++) {
            mInput[k] = new JTextField(temp[k]);
            mInput[k].setBounds(campi[indiceModifica][k+1].getBounds());
            mInput[k].setEditable(true);
            mInput[k].setOpaque(true);
            mInput[k].setBackground(editBackground);
            mInput[k].setForeground(Color.WHITE);
            mInput[k].setFont(new Font("Futura", Font.PLAIN, 28));
            mInput[k].setHorizontalAlignment(JLabel.CENTER);
            mInput[k].setBorder(null);
            mInput[k].setFocusable(true);
            schermataLista.remove(campi[indiceModifica][k+1]);
            schermataLista.add(mInput[k]);
        }
        schermataLista.remove(campi[indiceModifica][6]);
    }
}
