/*
Ilenia Cataudella

Struttura dati utilizzata:
- La struttura dati utilizzata per tenere traccia dei cammini precedentemente calcolati per una coppia di nodi
  è la lista cammini, implementata come ArrayList; ciascun oggetto salvato di tipo cammino contiene il costo totale e
  la sequenza di nodi attraversata. La scelta di ArrayList è giustificata perchè bisogna salvare un numero ristretto
  di cammini (<= 3), ha un accesso rapido utile per la stampa e l'inserimento con add è efficiente.
- A supporto di questa struttura viene anche utilizzato un HashSet archiUsati, per registrare gli archi già utilizzati
  nei cammini precedenti, in modo da garantire cammini completamente distinti tra loro; infatti, prima di aggiungere
  un cammino alla lista, viene fatto un controllo su archiUsati.
  Entrambe le strutture hanno costo di inserimento di O(1).

Costo computazionale:
Dati n (numero di nodi) e m (numero di archi):
- costruisciGrafo: crea la lista di adiacenza che rappresenta il grafo, per ogni riga del file si fanno operazioni di
  inserimento; per allocare la struttura avremo costo O(n) e per leggere e inserire gli archi O(m).
  Pertanto, avremo un costo totale di O(n + m).
- dijkstra: utilizza l'algoritmo di Dijkstra implementato con MinHeap.
  Prima di iniziare la ricerca deve inizializzare e allocare tre array: distanze (per le stime delle distanze minime di
  un percorso), padri (per ricostruire i cammini) e visitati (a ogni nodo viene assegnato true se è stato visitato)
  per un costo di O(n).
  Il ciclo while scorre tutti i nodi svolgendo al massimo n estrazioni di costo O(log n) dalla coda,
  per un totale di O(n log n).
  A ogni iterazione deve controllare che gli m archi uscenti da quel nodo non siano già stati utilizzati in cammini
  già calcolati per quei nodi (costo O(1)) e aggiornare la coda (costo O(log n)) per un costo di O(m log n).
  La ricostruzione del percorso tramite i padri ha costo O(n).
  Il costo totale di questo metodo è O((n + m) log n).
- Il metodo trovaCamminiDistinti richiama il metodo dijkstra per un massimo di 3 volte, per un costo
  di 3 * O((n + m) log n), la costante viene tuttavia ignorata nella notazione O, quindi avremo O((n + m) log n).
- Il metodo stampa contiene un ciclo che scorre sulla lista cammini facendo al massimo C iterazioni
  e ogni percorso conterrà al massimo k nodi. Il costo ottenuto è O(C*k), ma dato che C è una costante e
  k è minore o uguale a n, possiamo dire che il costo è O(n).
- Il main utilizza due cicli for annidati per ottenere tutte le coppie sorgente-destinazione, le coppie totali
  sono n(n-1), ovvero Θ(n^2). Su ogni coppia viene chiamato il metodo trovaCamminiDistinti e il metodo
  stampa (trascurabile rispetto a trovaCamminiDistinti).
Il costo totale è di O(n^2 * (n+m) log n).
*/

import java.io.*;
import java.util.*;

public class Esercizio3 {

    static int n;
    static int m;
    static Vector<LinkedList<Arco>> listaAdiacenza;

    /* Questa classe implementa un MinHeap, dove l'elemento con priorità minima viene estratto per primo; 
    questo meccanismo viene sfruttato da dijkstra per individuare il prossimo nodo a distanza minima
    da visitare. Per ogni nodo viene memorizzato nell'array posizione il suo indice corrente nell'heap,
    utile per velocizzare l'accesso ai nodi.
    Estrazione, aggiornamento e inserimento hanno complessità O(log n), perchè un heap di n elementi
    ha al massimo altezza log n.*/
    private static class MinHeap {

        private class elementoHeap {
            public final int nodo;
            public double priorita;

            public elementoHeap(int nodo, double priorita) {
                this.nodo = nodo;
                this.priorita = priorita;
            }
        }

        elementoHeap[] heap;
        int[] posizione;
        int dimensione, dimensioneMassima;

        public MinHeap(int dimensioneMassima) {
            this.heap = new elementoHeap[dimensioneMassima];
            this.dimensioneMassima = dimensioneMassima;
            this.dimensione = 0;
            this.posizione = new int[dimensioneMassima];
            Arrays.fill(this.posizione, -1);
        }

        private boolean valido(int i) {
            return ((i >= 0) && (i < dimensione));
        }

        /* Il metodo scambia viene utilizzato per invertire la posizione di due elementi nell'heap;
        è richiamato nei metodi risali e scendi.*/
        private void scambia(int i, int j) {
            elementoHeap temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
            posizione[heap[i].nodo] = i;
            posizione[heap[j].nodo] = j;
        }

        private int genitore(int i) {
            return (i + 1) / 2 - 1;
        }

        private int figlioSinistro(int i) {
            return (i + 1) * 2 - 1;
        }

        private int figlioDestro(int i) {
            return figlioSinistro(i) + 1;
        }

        /* Il metodo figlioMinimo confronta figlioDestro e figlioSinistro, individuando quello con priorità minore;
        è richiamato da scendi per individuare il figlio da scambiare. */
        private int figlioMinimo(int i) {
            final int sin = figlioSinistro(i);
            final int des = figlioDestro(i);
            int risultato = -1;
            if (valido(sin)) {
                risultato = sin;
                if (valido(des) && (heap[des].priorita < heap[sin].priorita)) {
                    risultato = des;
                }
            }
            return risultato;
        }

        /* Metodo fondamentale per garantire la proprietà principale di MinHeap (ogni nodo deve avere priorità minore o
        uguale a quella dei figli). Si occupa di spostare un nodo figlio verso l'alto se ha priorità minore del padre.
        Il confronto avviene recuperando il nodo genitore e confrontando le priorità.*/
        private void risali(int i) {
            int p = genitore(i);
            while ((p >= 0) && (heap[i].priorita < heap[p].priorita)) {
                scambia(i, p);
                i = p;
                p = genitore(i);
            }
        }

        /* Come risali anche scendi serve a garantire la proprietà del MinHeap. In questo caso sposta un nodo verso il
        basso se ha priorità maggiore dei figli. Utilizza un do-while per calcolare il figlio minimo corrente e
        confrontare la priorità con il nodo in esame. */
        private void scendi(int i) {
            boolean finito = false;
            do {
                int dst = figlioMinimo(i);
                if (valido(dst) && (heap[dst].priorita < heap[i].priorita)) {
                    scambia(i, dst);
                    i = dst;
                } else {
                    finito = true;
                }
            } while (!finito);
        }

        public boolean vuoto() {
            return dimensione == 0;
        }

        /*Inserisce un nuovo elemento, richiama risali per mantenere le proprietà della coda. */
        public void inserisci(int nodo, double priorita) {
            int i = dimensione++;
            heap[i] = new elementoHeap(nodo, priorita);
            posizione[nodo] = i;
            risali(i);
        }

        public int minimo() {
            return heap[0].nodo;
        }

        /* Il metodo serve a eliminare l'elemento minimo. Il nodo radice (minimo per proprietà MinHeap) viene spostato
        in fondo all'heap per semplificare l'eliminazione, effettuandola senza compromettere l'intera struttura;
        infine, utilizza scendi per riordinare correttamente i nodi. */
        public void eliminaMinimo() {
            scambia(0, dimensione - 1);
            posizione[heap[dimensione - 1].nodo] = -1;
            dimensione--;
            if (dimensione > 0) {
                scendi(0);
            }
        }

        /* Il metodo modifica la priorità di un elemento già presente nell'heap. Il cambio della priorità comporta anche
        lo spostamento del nodo, a seconda del nuovo valore inserito viene richiamato il metodo scendi o risali.*/
        public void cambiaPriorita(int nodo, double nuovaPriorita) {
            int i = posizione[nodo];
            double vecchiaPriorita = heap[i].priorita;
            heap[i].priorita = nuovaPriorita;
            if (nuovaPriorita > vecchiaPriorita){
                scendi(i);
            } else {
                risali(i);
            }
        }
    }

    //La classe rappresenta un arco, lancia una eccezione qualora fosse presente un arco con peso negativo.
    private static class Arco {
        final int sorgente;
        final int destinazione;
        final double peso;

        Arco(int s, int d, double p) {
            if (p < 0.0) {
                throw new IllegalArgumentException("Non è consentito l'uso di un grafo con arco con peso negativo");
            }
            sorgente = s;
            destinazione = d;
            peso = p;
        }
    }

    //Definisce un oggetto cammino con costo, nodi e archi
    private static class Cammino {
        double costo;
        List<Integer> nodi;
        Set<String> archi;

        Cammino(double costo, List<Integer> nodi, Set<String> archi) {
            this.costo = costo;
            this.nodi = nodi;
            this.archi = archi;
        }
    }

    /* Legge il file in input e costruisce il grafo rappresentato, effettuando parsing sulla stringa.
    Dalla stringa vengono estratti nodi e peso, per ogni coppia di nodi vengono inseriti due archi (uno per verso)
    per rappresentare un grafo non orientato. Il for serve a preparare la lista di adiacenza che rappresenterà
    il grafo, la struttura dati scelta è un vettore di linkedlist (per ogni indice è presente una lista contenente
    tutti gli archi connessi a quel nodo). */
    public static void costruisciGrafo(String nomeFile) {
        try (Scanner sc = new Scanner(new FileReader(nomeFile))) {
            n = Integer.parseInt(sc.nextLine().split("#")[0].trim());
            m = Integer.parseInt(sc.nextLine().split("#")[0].trim());

            listaAdiacenza = new Vector<>(n);
            for (int i = 0; i < n; i++) {
                listaAdiacenza.add(new LinkedList<>());
            }

            while (sc.hasNextLine()) {
                String riga = sc.nextLine().trim();

                if (riga.isEmpty()) {
                    continue;
                }

                String[] parti = riga.split("\\(|\\)");
                String[] nodi = parti[1].trim().split(" ");
                double peso = Double.parseDouble(parti[2].trim());

                int s = Integer.parseInt(nodi[0].substring(1));
                int d = Integer.parseInt(nodi[1].substring(1));

                listaAdiacenza.get(s).add(new Arco(s, d, peso));
                listaAdiacenza.get(d).add(new Arco(d, s, peso));
            }
        } catch (IOException e) {
            System.out.println("Errore nella lettura del file: " + e.getMessage());
            System.exit(1);
        }
    }

    /* Questo metodo implementa l'algoritmo Dijkstra con MinHeap per la ricerca dei cammini minimi; sfrutta le proprietà
    di MinHeap per recuperare in tempo logaritmico il nodo con distanza minima.
    Prima di iniziare la ricerca del cammino minimo inizializza distanze e padri e riempie la coda MinHeap con tutti i
    nodi del grafo. Una volta effettuata l'estrazione del nodo con i metodi minimo ed eliminaMinimo, itera su tutti
    gli archi connessi al nodo in esame (grazie alla lista di adiacenza) e per ogni arco crea un identificativo unico
    utile a garantire cammini distinti; il peso dell'arco selezionato viene sommato al peso del resto del percorso e,
    qualora fosse stata individuata una distanza minore, utilizza il metodo cambiaPriorità per correggere l'heap.
    Il percorso viene ricostruito partendo dalla destinazione e risalendo i padri.*/
    public static Cammino dijkstra(int sorgente, int destinazione, Set<String> archiEsclusi) {
        double[] distanze = new double[n];
        int[] padri = new int[n];
        Arrays.fill(distanze, Double.POSITIVE_INFINITY);
        Arrays.fill(padri, -1);
        distanze[sorgente] = 0.0;

        MinHeap coda = new MinHeap(n);
        for (int i = 0; i < n; i++) {
            coda.inserisci(i, distanze[i]);
        }

        boolean[] visitati = new boolean[n];

        while (!coda.vuoto()) {
            int u = coda.minimo();
            coda.eliminaMinimo();

            if (visitati[u]) {
                continue;
            }

            visitati[u] = true;

            if (u == destinazione) {
                break;
            }

            for (Arco arco : listaAdiacenza.get(u)) {
                int v = arco.destinazione;
                String idArco = (u < v) ? u + "-" + v : v + "-" + u;
                if (archiEsclusi.contains(idArco)) {
                    continue;
                }
                double stima = distanze[u] + arco.peso;
                if (stima < distanze[v]) {
                    distanze[v] = stima;
                    padri[v] = u;
                    coda.cambiaPriorita(v, stima);
                }
            }
        }

        if (padri[destinazione] == -1) {
            return null;
        }

        LinkedList<Integer> nodiPercorso = new LinkedList<>();
        Set<String> archiPercorso = new HashSet<>();

        int v = destinazione;
        while (v != -1) {
            nodiPercorso.addFirst(v);
            int u = padri[v];
            if (u != -1) {
                String idArco = (u < v) ? u + "-" + v : v + "-" + u;
                archiPercorso.add(idArco);
            }
            v = u;
        }

        return new Cammino(distanze[destinazione], nodiPercorso, archiPercorso);
    }

    /* Calcola fino a 3 cammini distinti tra due nodi richiamando il metodo dijkstra.
    Dopo aver trovato il primo cammino minimo, ne confronta il costo con quelli trovati successivamente, se esistono,
    per garantire che le alternative trovate abbiano lo stesso costo minimo e siano dunque equivalenti; solo
    i cammini con costo uguale al costo minimo del primo cammino vengono considerati validi e aggiunti alla lista 
    cammini. Riempe la struttura archiUsati con gli archi del percorso valido trovato, per garantire cammini distinti. */
    public static List<Cammino> trovaCamminiDistinti(int sorgente, int destinazione) {
        List<Cammino> cammini = new ArrayList<>();
        Set<String> archiUsati = new HashSet<>();

        Cammino c1 = dijkstra(sorgente, destinazione, archiUsati);
        if(c1 == null) {
            return cammini;
        }
        double costoMinimo = c1.costo;
        cammini.add(c1);
        archiUsati.addAll(c1.archi);

        for (int i = 1; i < 3; i++) {
            Cammino c = dijkstra(sorgente, destinazione, archiUsati);
            if (c == null || c.costo != costoMinimo) {
                break;
            }
            cammini.add(c);
            archiUsati.addAll(c.archi);
        }

        return cammini;
    }

    /* Stampa i cammini trovati tra un nodo sorgente e destinazione, scorrendo nella lista cammini costruita in
    trovaCamminiDistinti, qualora la struttura fosse vuota, perchè non è stato trovato nessun cammino, lo segnala. */
    public static void stampaRisultati(int sorgente, int destinazione, List<Cammino> cammini) {
        if (cammini.isEmpty()) {
            System.out.println("Nessun cammino da N" + sorgente + " a N" + destinazione);
            return;
        }

        System.out.println("Cammino da N" + sorgente + " a N" + destinazione);

        for (int i = 0; i < cammini.size(); i++) {
            Cammino c = cammini.get(i);
            System.out.println("Cammino " + (i+1) + ": " + c.nodi + ", costo: " + c.costo);
        }
    }

    /* Il main utilizza un doppio ciclo for annidato, per scorrere tutte le possibili coppie di nodi, per ogni
    coppia richiama il metodo torvaCamminiDistinti. Calcola il tempo totale impiegato per ottenere la soluzione
    tramite currentTimeMillis. */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length != 1) {
            System.out.println("Parametro mancante\nEsempio: java Esercizio1 <nome_file>");
            return;
        }

        long inizioTempo = System.currentTimeMillis();

        costruisciGrafo(args[0]);

        for (int s = 0; s < n; s++) {
            for (int d = 0; d < n; d++) {
                if (s != d) {
                    List<Cammino> cammini = trovaCamminiDistinti(s, d);
                    stampaRisultati(s, d, cammini);
                }
            }
        }

        long fineTempo = System.currentTimeMillis();
        double tempoTotale = (fineTempo - inizioTempo) / 1000.0;
        System.out.println("Tempo totale: " + tempoTotale + " secondi" );
    }
}
