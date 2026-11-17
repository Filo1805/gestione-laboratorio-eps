package it.unibs.eps.laboratorio.graphics;

import it.unibs.eps.laboratorio.controller.LaboratorioController;
import it.unibs.eps.laboratorio.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LaboratorioGUI extends JFrame implements LaboratorioListener {

    private final LaboratorioController controller;

    //Componenti Grafici
    private JTable tabellaStrumenti;
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

    public LaboratorioGUI ( LaboratorioController controller) {
        this.controller = controller;

        this.controller.registraListener(this);

        setTitle("Gestione Prestito Strumenti - Laboratorio");
        setSize(950,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inizializzaComponenti();
        caricaDatiIniziali();
    }

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

        String[] colonne = {"ID", "Tipo", "Stato"};
        modelloTabellaStrumenti = new DefaultTableModel(colonne,0);
        tabellaStrumenti = new JTable((modelloTabellaStrumenti));
        JScrollPane scrollTabella = new JScrollPane(tabellaStrumenti);
        scrollTabella.setBorder(BorderFactory.createTitledBorder("Catalogo Strumenti del Laboratorio"));

        //Area lista eventi
        areaListaEventi = new JTextArea();
        areaListaEventi.setEditable(false);
        areaListaEventi.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLista = new JScrollPane(areaListaEventi);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Registro Eventi (Orario / Evento)"));

        panelCentro.add(scrollTabella);
        panelCentro.add(scrollLista);

        add(panelCentro, BorderLayout.CENTER);

        //3. Pannello inferiore (Bottoni di controllo)

        //Pulsante di avvio simulazione
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


        pannelloControlli.add(btnAvviaSimulazione);
        pannelloControlli.add(btnInterrompiSimulazione);
        pannelloControlli.add(btnSalva);
        pannelloControlli.add(btnCarica);

        add(pannelloControlli, BorderLayout.SOUTH);

        //4. Generatore Eventi pulsanti
        btnAvviaSimulazione.addActionListener(e -> {
            areaListaEventi.append("[SISTEMA] Avvio simulazione utenti...\n");
            controller.avviaSimulazioneDefault(() -> SwingUtilities.invokeLater(() -> areaListaEventi.append("[SISTEMA] Simulazione terminata.\n")));
        });

        btnInterrompiSimulazione.addActionListener(e -> {
            //Aspettiamo la fine dei thread in un thread a parte, per non bloccare la GUI
            new Thread(() -> {

                controller.interrompiSimulazione();
                //questo invokeLater si avvia dopo gli ultimi eventi dei thread interrotti in modo che non compaiano altri messaggi sotto quello di simulazione interrotta
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

    private JPanel creaCardStatistica(String titolo, JLabel label, Color colore ) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1));
        panel.setBackground(Color.WHITE);

        JLabel labelTitolo = new JLabel(titolo, SwingConstants.CENTER);
        labelTitolo.setFont(new Font("SansSerif", Font.BOLD,12));
        labelTitolo.setForeground(Color.DARK_GRAY);

        label.setFont(new Font("SansSerif", Font.BOLD,24));
        label.setForeground(colore);

        panel.add(labelTitolo, BorderLayout.NORTH);
        panel.add(label, BorderLayout.CENTER);

        return panel;

    }

    private void caricaDatiIniziali() {
        onStatoAggiornato(); //Popola subito la tabella al primo avvio
    }


    @Override
    public void onStatoAggiornato(){
        SwingUtilities.invokeLater(() -> {

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

            labelDisponibili.setText(String.valueOf(disponibili));
            labelInPrestito.setText(String.valueOf(inPrestito));
            labelInAttesa.setText(String.valueOf(controller.getRichiesteInAttesa()));

        });
    };

    @Override
    public void onNuovoEvento(Evento evento) {
        SwingUtilities.invokeLater(() -> {
            areaListaEventi.append(evento.getTime() + " - " + evento.getDescrizione() + "\n");
            areaListaEventi.setCaretPosition(areaListaEventi.getDocument().getLength());
        });
    };
}
