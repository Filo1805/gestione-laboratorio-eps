# Gestione Laboratorio EPS 
Progetto per il corso di Elementi di Progettazione Software 2026/2027

## Gruppo di Lavoro
* **Nome Gruppo:** Pane e Salamina
* **Componenti:**
  * Alessandro Filisetti - Matricola: 750823
  * Angelo Cominelli - Matricola: 750041

## Descrizione Progetto:
L'applicazione è un software desktop sviluppato in **Java** (con interfaccia grafica in **Swing**) progettata per gestire il prestito di strumenti di un laboratorio.
Il sistema gestisce la concorrenza tra più utenti (rappresentati da thread distinti) che richiedono strumenti simultaneamente, regolando le code di attesa e restituzione tramite un **oggetto monitor** dedicato (`synchronized`, `wait`, `notifyAll`).

## Caratteristiche Principali:
1. **Catalogo Strumenti:** Gestione di identificativi univoci, tipi e stati (`DISPONIBILE`, `IN_PRESTITO`).
2. **Sincronizzazione Concorrente (Monitor):** Assegnazione sicura degli strumenti e gestione delle attese.
3. **Interfaccia Grafica (GUI):** Dashboard interattiva in Java Swing per configurare, avviare e osservare l'andamento della simulazione in tempo reale.
4. **Pattern Observer:** Aggiornamento dinamico di tabelle e registro eventi tramite ascoltatori dedicati.
5. **Persistenza su File:** Salvataggio automatico/manuale di catalogo strumenti e storico eventi, con ripristino intelligente dello stato all'avvio.

## Istruzioni per Compilare ed Eseguire il Programma

### Prerequisiti
* **Java Development Kit (JDK)** versione 11 o superiore.
* Un ambiente di sviluppo integrato (IDE).

### Esecuzione tramite IDE 
1. Clona o scarica la repository del progetto.
2. Apri l'IDE e seleziona **Open** scegliendo la cartella principale del progetto (`gestione-laboratorio-eps`).
3. Assicurati che la cartella `src\main\java` sia configurata come **Sources Root**.
4. Individua la classe principale: `it.unibs.eps.laboratorio.Main`.
5. Clicca sul tasto **Run** accanto al 'main' per avviare l'applicazione grafica.

### Utilizzo della Simulazione
1. All'apertura della finestra, vedrai il catalogo degli strumenti caricato da file (`catalogo_strumenti.txt`).
2. Clicca sul pulsante **"Avvio Simulazione"** per avviare i thread degli utenti in concorrenza.
3. Osserva in tempo reale il cambio di stato degli strumenti e la registrazione degli eventi nel log sottostante.
4. È possibile interrompere la simulazione in qualsiasi momento tramite il tasto **"Interrompi"** o salvare/ricaricare i dati su file.

## Struttura dei Package
* `it.unibs.eps.laboratorio` -> Classe `Main` di avvio.
* `it.unibs.eps.laboratorio.model` -> Classi di entità (`Strumento`, `Utente`, `Evento`, `Laboratorio`, `LaboratorioListener`).
* `it.unibs.eps.laboratorio.monitor` -> Logica di sincronizzazione (`MonitorLaboratorio`).
* `it.unibs.eps.laboratorio.controller` -> Gestore della logica di comunicazione (`LaboratorioController`).
* `it.unibs.eps.laboratorio.simulation` -> Task dei thread utente (`UtenteTask`).
* `it.unibs.eps.laboratorio.graphics` -> Interfaccia grafica Swing (`LaboratorioGUI`)[cite: 13].
* `it.unibs.eps.laboratorio.fileManager` -> Gestione della persistenza su file di testo (`FilePersistenceManager`).

## Versionamento
* v1.0.0 : Creato package `it.unibs.eps.laboratorio.model` e le classi (`Strumento`, `Utente`, `Evento`, `Laboratorio`, `StatoStrumento`).
* v1.1.0 : Creato package `it.unibs.eps.laboratorio.fileManager` e la classe (`FilePersistenceManager`).
* v1.2.0 : Creato package `it.unibs.eps.laboratorio.monitor` e la classe (`MonitorLaboratorio`), aggiunti la gestione del salvataggio Eventi su file.
* v1.3.0 : Creato package `it.unibs.eps.laboratorio.simulation` e la classe (`UtenteTask`), completato il Main con simulazione di 4 utenti che richiedono e usano gli strumenti del laboratorio.
* v.2.0.0 : Creati package `it.unibs.eps.laboratorio.controller` e `it.unibs.eps.laboratorio.graphics` per implementazione interfaccia grafica, creata classe (`LaboratorioController`) per osservare attività del Laboratorio e comunicarle alla GUI.
* v2.1.0 : Creata la GUI e metodi di avvio e interrompi simulazione, per adesso simulazione ancora statica con richieste e utenti inizializzati in `LaboratorioController`.
* v2.2.0 : Aggiunta possibilità per l'utilizzatore finale di decidere il numero degli utenti e il tempo di utilizzo degli strumenti, simulazione dinamica.
