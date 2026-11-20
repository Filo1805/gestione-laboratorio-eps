package it.unibs.eps.laboratorio.graphics;

import it.unibs.eps.laboratorio.Main;
import it.unibs.eps.laboratorio.controller.LaboratorioController;
import it.unibs.eps.laboratorio.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Interfaccia grafica Swing del laboratorio.
 *
 * Mostra tre contatori (disponibili, in prestito e in attesa), la tabella degli strumenti,
 * la tabella delle richieste in attesa e il registro degli eventi. Permette di configurare
 * (numero di utenti e durata del prestito), avviare e interrompere la simulazione e di salvare
 * e cariare i dati.
 *
 * Implementa LaboratorioListener (pattern Observer): viene avvisata dal laboratorio quando
 * qualcosa cambia. Quegli avvisi arrivano dai thread della simulazione.
 */

public class LaboratorioGUI extends JFrame implements LaboratorioListener {

    private final LaboratorioController controller;

    //Componenti Grafici
    private JTable tabellaStrumenti;
    private JTable tabellaRichieste;
    private JTextArea areaListaEventi;

    //Bottoni
    private JButton btnAvviaSimulazione;
    private JButton btnInterrompiSimulazione;
    private JButton btnSalva;
    private JButton btnCarica;

    //Componenti statistiche superiori
    private JLabel labelDisponibili;
    private JLabel labelInPrestito;
    private JLabel labelInAttesa;

    //Tabelle
    private DefaultTableModel modelloTabellaRichieste;
    private DefaultTableModel modelloTabellaStrumenti;

    /** Crea la finestra, si registra come listener e mostra lo stato iniziale */
    public LaboratorioGUI ( LaboratorioController controller) {

        this.controller = controller;
        this.controller.registraListener(this);

        setTitle("Gestione Prestito Strumenti - Laboratorio " + Main.version );
        setSize(950,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inizializzaComponenti();
        caricaDatiIniziali();
    }

    /** Costruisce la finestra */
    private void inizializzaComponenti () {
        setLayout(new BorderLayout(10, 10));

        //1. Pannello superiore ( Statistiche )
        JPanel panelTop = new JPanel(new GridLayout(1, 3, 10, 10));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

        labelDisponibili = new JLabel("0", SwingConstants.CENTER);
        labelInAttesa = new JLabel("0", SwingConstants.CENTER);
        labelInPrestito = new JLabel("0", SwingConstants.CENTER);

        panelTop.add(creaCardStatistica("Disponibili", labelDisponibili, Color.GREEN ));
        panelTop.add(creaCardStatistica("In Prestito", labelInPrestito, Color.RED));
        panelTop.add(creaCardStatistica("In Attesa", labelInAttesa, Color.YELLOW));

        add(panelTop, BorderLayout.NORTH);


        //2. Pannello centrale ( Tabella Strumenti )
        JPanel panelCentro = new JPanel(new GridLayout(2,1,10,10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

        //Tabella strumenti
        String[] colonneStrumenti = {"ID", "Tipo", "Stato"};
        modelloTabellaStrumenti = new DefaultTableModel(colonneStrumenti,0);
        tabellaStrumenti = new JTable((modelloTabellaStrumenti));
        tabellaStrumenti.getColumnModel().getColumn(2).setCellRenderer(new RenderStatoStrumento());
        JScrollPane scrollTabella = new JScrollPane(tabellaStrumenti);
        scrollTabella.setBorder(BorderFactory.createTitledBorder("Catalogo Strumenti del Laboratorio"));

        //Tabella richieste in attesa
        String[] colonneRichieste = {"Utente", "Strumento richiesto", "Stato"};
        modelloTabellaRichieste = new DefaultTableModel(colonneRichieste,0);
        tabellaRichieste = new JTable((modelloTabellaRichieste));
        tabellaRichieste.getColumnModel().getColumn(2).setCellRenderer(new RenderStatoRichiesta());
        JScrollPane scrollRichieste = new JScrollPane(tabellaRichieste);
        scrollRichieste.setBorder(BorderFactory.createTitledBorder("Richieste in Attesa"));

        JPanel panelTabelle = new JPanel(new GridLayout(1,2,10,10));
        panelTabelle.add(scrollTabella);
        panelTabelle.add(scrollRichieste);

        //Area lista eventi
        areaListaEventi = new JTextArea();
        areaListaEventi.setEditable(false);
        areaListaEventi.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLista = new JScrollPane(areaListaEventi);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Registro Eventi (Orario / Evento)"));

        panelCentro.add(panelTabelle);
        panelCentro.add(scrollLista);

        add(panelCentro, BorderLayout.CENTER);

        //3. Pannello inferiore (Bottoni di controllo)

        JPanel pannelloControlli = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pannelloControlli.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));

        btnAvviaSimulazione = new JButton("Avvia Simulazione");
        btnInterrompiSimulazione = new JButton("Interrompi");

        btnSalva = new JButton("Salva Dati");
        btnCarica = new JButton("Carica Dati");

        //Stile base bottoni
        btnAvviaSimulazione.setBackground(Color.GREEN);
        btnAvviaSimulazione.setForeground(Color.WHITE);
        btnAvviaSimulazione.setFocusPainted(false);

        btnInterrompiSimulazione.setBackground(Color.RED);
        btnInterrompiSimulazione.setForeground(Color.WHITE);
        btnInterrompiSimulazione.setFocusPainted(false);


        //Configurazione della simulazione: numero di utenti (almeno 5) e durata del prestito
        JSpinner spinnerUtenti = new JSpinner(new SpinnerNumberModel(5,5,20,1));
        JSpinner spinnerDurata = new JSpinner(new SpinnerNumberModel(3,1,30,1));

        //Inseriamo li Spinner nel pannello
        pannelloControlli.add(new JLabel("Utenti:"));
        pannelloControlli.add(spinnerUtenti);
        pannelloControlli.add(new JLabel("Durata:"));
        pannelloControlli.add(spinnerDurata);

        //Inseriamo i bottoni nel pannello
        pannelloControlli.add(btnAvviaSimulazione);
        pannelloControlli.add(btnInterrompiSimulazione);
        pannelloControlli.add(btnSalva);
        pannelloControlli.add(btnCarica);


        add(pannelloControlli, BorderLayout.SOUTH);

        //4. Generatore Eventi pulsanti
        btnAvviaSimulazione.addActionListener(e -> {
            areaListaEventi.append("[SISTEMA] Avvio simulazione utenti...\n");
            controller.avviaSimulazione((Integer) spinnerUtenti.getValue(), (Integer) spinnerDurata.getValue(), () -> SwingUtilities.invokeLater(() -> areaListaEventi.append("[SISTEMA] Simulazione terminata.\n")));
        });

        btnInterrompiSimulazione.addActionListener(e -> {
            //Aspettiamo la fine dei thread in un thread a parte, per non bloccare la GUI
            new Thread(() -> {

                controller.interrompiSimulazione();
                //Questo invokeLater si avvia dopo gli ultimi eventi dei thread interrotti,
                //in modo che non compaiano altri messaggi sotto quello di simulazione interrotta
                SwingUtilities.invokeLater(() -> {
                    areaListaEventi.setText(""); //pulisce l'area di testo
                    areaListaEventi.append("[SISTEMA] Simulazione interrotta.\n");
                });

            }).start();

        });

        btnSalva.addActionListener(e -> {
            controller.salvaDati();
            areaListaEventi.append("[SISTEMA] Dati salvati su File.\n");
        });

        btnCarica.addActionListener(e -> {
            if(controller.caricaDati()) {
                mostraStorico();
            }
            else {
                JOptionPane.showMessageDialog(this, "Impossibile caricare: ci sono strumenti in prestito o richieste in attesa.");
            }
        });

    }

    //Riempie l'area del registro eventi con lo storico attuale
    private void mostraStorico() {
        areaListaEventi.setText("");
        for ( Evento ev : controller.getStoricoEventi()) {
            areaListaEventi.append(ev.getTime() + " - " + ev.getDescrizione() + "\n" );
        }
    }

    /** Crea una card prefatta per l'aspetto dei contatori */
    private JPanel creaCardStatistica(String titolo, JLabel label, Color colore ) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
        panel.setBackground(Color.BLACK);

        JLabel labelTitolo = new JLabel(titolo, SwingConstants.CENTER);
        labelTitolo.setFont(new Font("SansSerif", Font.BOLD,12));
        labelTitolo.setForeground(Color.WHITE);

        label.setFont(new Font("SansSerif", Font.BOLD,24));
        label.setForeground(colore);

        panel.add(labelTitolo, BorderLayout.NORTH);
        panel.add(label, BorderLayout.CENTER);

        return panel;

    }

    /** Mostra subito le tabelle e lo storico caricato da file all'avvio*/
    private void caricaDatiIniziali() {
        onStatoAggiornato();
        mostraStorico();//Popola subito la tabella al primo avvio
    }


    /**
     * Chiamato dal laboratorio quando lo stato cambia: ricarica le due tabelle e i contatori.
     */
    @Override
    public void onStatoAggiornato(){
        SwingUtilities.invokeLater(() -> {

            //Tabella strumenti e contatori disponibili / in prestito
            List<Strumento> strumenti = controller.getListaStrumenti();
            modelloTabellaStrumenti.setRowCount(0);

            int disponibili = 0;
            int inPrestito = 0;

            for(Strumento s: strumenti) {
                modelloTabellaStrumenti.addRow(new Object[]{s.getId(),s.getTipo(),s.getStato()});
                if(s.getStato() == StatoStrumento.DISPONIBILE) {
                    disponibili++;
                }
                else if(s.getStato() == StatoStrumento.IN_PRESTITO ) {
                    inPrestito++;
                }

            }

            //Tabella richieste
            List<Richiesta> richieste = controller.getListaRichieste();
            modelloTabellaRichieste.setRowCount(0);

            for (Richiesta r: richieste) {
                modelloTabellaRichieste.addRow(new Object[]{r.getUtente().getName(), r.getTipoStrumentoRichiesto(), r.getStatoRichiesta()});
            }


            labelDisponibili.setText(String.valueOf(disponibili));
            labelInPrestito.setText(String.valueOf(inPrestito));
            labelInAttesa.setText(String.valueOf(controller.getRichiesteInAttesa()));

        });
    };

    /** Chiamato dal laboratorio a ogni nuovo evento: lo aggiunge in fondo al registro e scorre fino a mostrarla*/
    @Override
    public void onNuovoEvento(Evento evento) {
        SwingUtilities.invokeLater(() -> {
            areaListaEventi.append(evento.getTime() + " - " + evento.getDescrizione() + "\n");
            areaListaEventi.setCaretPosition(areaListaEventi.getDocument().getLength());
        });
    };


    /**
     * Render della colonna "Stato" della tabella delle richieste: colora il testo
     * in base allo stato
     */
    static class RenderStatoRichiesta extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            //Il componente base disegna testo e sfondo; cui si cambia solo colore del testo
            Component cella = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                switch ((StatoRichiesta) value) {
                    case IN_ATTESA:
                        cella.setBackground(Color.YELLOW);
                        break;
                    case IN_PRESTITO:
                        cella.setBackground(Color.RED);
                        break;
                    case ANNULLATA:
                        cella.setBackground(Color.RED);
                        break;
                    case RESTITUITA:
                        cella.setBackground(Color.GREEN);
                        break;
                }

            }

            return cella;
        }
    }

    /**
     * Render della colonna "Stato" dello strumento: colora la cella in base allo stato
     */
    static class RenderStatoStrumento extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            //Il componente base disegna testo e sfondo; cui si cambia solo colore del testo
            Component cella = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                switch ((StatoStrumento) value) {
                    case DISPONIBILE:
                        cella.setBackground(Color.GREEN);
                        break;
                    case IN_PRESTITO:
                        cella.setBackground(Color.RED);
                        break;
                }

            }

            return cella;
        }
    }
}
