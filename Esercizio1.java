/*
Ilenia Cataudella

Costo computazionale:
- Nella fase di costruzione dell'albero ogni riga del file viene letta una volta, con n righe avremo costo O(n);
  l'inserimento nelle hashmap e negli arraylist ha costo O(1), per un costo complessivo di O(n).
- La ricerca della radice dell'albero consiste in una iterazione sui valori dell'albero, dove per ogni lista di figli
  è prevista la rimozione di ogni figlio (la rimozione dall'hashset ha costo O(1)), per un totale di O(n).
- L'algoritmo ricorsivo DFS visita ogni nodo e arco una sola volta; le assegnazioni dei livelli, il conteggio
  dei discendenti e le colorazioni hanno costo O(1), per un totale di O(n).
- Il metodo di stampa con collection sort nel caso peggiore ha costo totale di O(n log n), ovvero se tutti i nodi si
  trovano sullo stesso livello.
Il costo totale dell'algoritmo è O(n log n).

Strutture dati utilizzate:
- Per rappresentare l'albero viene utilizzata una lista di adiacenza (HashMap), dove ogni nodo ha una lista
  dei suoi figli, questa ha costo O(1) per costruzione e accesso.
- La mappa numeroDiscendenti associa a ogni nodo il numero di discendenti;
  allo stesso modo, la mappa livelli ci consente di capire la profondità di ogni nodo.
- La mappa colore invece viene utilizzata dall'algoritmo dfs per valutare i nodi visitati e la presenza di cicli,
  di conseguenza, la validità dell'albero stesso.
- La mappa maxDiscendenti raccoglie per ogni livello i nodi con il massimo numero di discendenti, utilizzata per
  mettere successivamente nell'ordine corretto i nodi.
Sono state scelte delle mappe per gestire le varie funzioni perchè hanno costo di accesso e aggiornamento costante e
permettono di associare a ogni stringa un indice, senza aver bisogno di effettuare ulteriori conversioni.
*/

import java.io.*;
import java.util.*;

public class Esercizio1 {

    private enum Colore {GRIGIO, NERO}
    private static Map<String, List<String>> albero = new HashMap<>();
    private static Map<String, Integer> numeroDiscendenti = new HashMap<>();
    private static Map<String, Integer> livelli = new HashMap<>();
    private static Map<String, Colore> colore = new HashMap<>();
    private static Map<Integer, List<String>> maxDiscendenti = new HashMap<>();

    /* Questo metodo costruisce l'albero a partire dalle coppie <inferiore>,<superiore> presenti nel file di testo.
    La rappresentazione scelta è la lista di adiacenza che viene riempita tramite lo split delle righe.
    Se il nodo genitore non è ancora stato registrato allora viene inizializzato con una lista vuota dei figli,
    inoltre ogni nodo figlio viene inizializzato con una lista vuota, per garantire l'esistenza della chiave e
    permettere di inserire altri nodi discendenti o individuare quali sono i nodi foglia (lista vuota). */
    private static boolean costruisciAlbero(String nomeFile) {
        try {
            Scanner scanner = new Scanner(new FileReader(nomeFile));
            while (scanner.hasNextLine()) {
                String riga = scanner.nextLine();
                if (riga.isEmpty()) {
                    continue;
                }

                String[] parte = riga.split(",");
                String figlio = parte[0].trim();
                String genitore = parte[1].trim();

                if (!albero.containsKey(genitore)) {
                    albero.put(genitore, new ArrayList<>());
                }
                albero.get(genitore).add(figlio);

                if (!albero.containsKey(figlio)) {
                    albero.put(figlio, new ArrayList<>());
                }
            }
            scanner.close();
	        return true;

        } catch (IOException e) {
            System.out.println("Errore nella lettura del file: " + e.getMessage());
            return false;
        }
    }

    /* Il metodo trova radice viene utilizzato per trovare le radici dell'albero creato,
    qualora esistesse più di una radice allora l'albero non viene considerato valido.
    Il nodo che non compare mai come figlio è la radice, questo viene individuato in quanto
    vengono rimossi tutti i figli dalla mappa radici possibili (il nodo restante è la radice). */
    private static String trovaRadice() {
        Set<String> radiciPossibili = new HashSet<>(albero.keySet());

        for (List<String> figli : albero.values()) {
            for (String figlio : figli) {
                radiciPossibili.remove(figlio);
            }
        }

        if (radiciPossibili.size() != 1) {
            return null;
        }

        return radiciPossibili.iterator().next();
    }

    /* La visita DFS post-order utilizza i colori per rappresentare lo stato di visita dei nodi
    (grigio in fase di visita, nero già visitato), grazie a questo riesce a rilevare l'assenza di cicli
    (se viene trovato un nodo grigio allora è presente un ciclo e l'albero non è valido).
    I discendenti vengono calcolati ricorsivamente: viene sommato 1 (il figlio del nodo in visita) al numero di
    discendenti calcolati durante la ricorsione e salvati in numeroDiscendenti.
    Il livello di ciascun nodo figlio dovrà essere incrementato di 1 rispetto a quello del padre (salvato
    ricorsivamente nella mappa livelli), per ciascun livello aggiorna la lista maxDiscendenti che conterrà i nodi
    con numero massimo di discendenti. */
    private static boolean dfs(String nodo) {
        colore.put(nodo, Colore.GRIGIO);
        int count = 0;

        for (String figlio : albero.get(nodo)) {
            livelli.put(figlio, livelli.get(nodo) + 1);
            if (!colore.containsKey(figlio)) {
                boolean visita = dfs(figlio);
                if (!visita) {
                    return false;
                }
                count += 1 + numeroDiscendenti.get(figlio);
            } else if (colore.get(figlio) == Colore.GRIGIO) {
                return false;
            }
        }

        numeroDiscendenti.put(nodo, count);
        int livello = livelli.getOrDefault(nodo, 0);
        int discendenti = numeroDiscendenti.get(nodo);

        List<String> migliori = maxDiscendenti.getOrDefault(livello, new ArrayList<>());
        if (migliori.isEmpty()) {
            migliori.add(nodo);
        } else {
            int discCorrente = numeroDiscendenti.get(migliori.get(0));
            if (discendenti > discCorrente) {
                migliori.clear();
                migliori.add(nodo);
            } else if (discendenti == discCorrente) {
                migliori.add(nodo);
            }
        }

        maxDiscendenti.put(livello, new ArrayList<>(migliori));

        colore.put(nodo, Colore.NERO);

        return true;
    }

    /*Questo metodo viene utilizzato per ordinare i livelli in ordine crescente, per ogni livello
    vengono stampati in ordine lessicografico gli elementi con numero di discendenti massimo, trovati
    durante l'algoritmo DFS. Gli ordinamenti vengono svolti tramite Collection Sort. */
    private static void stampa() {
        List<Integer> livelliOrdinati = new ArrayList<>(maxDiscendenti.keySet());
        Collections.sort(livelliOrdinati);

        for (int livello : livelliOrdinati) {
            List<String> nodi = maxDiscendenti.get(livello);
            Collections.sort(nodi);
            System.out.println(livello + ": " + String.join(", ", nodi));
        }
    }

    /* Il main richiama i metodi implementati, tramite delle stampe notifica eventuali errori. */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length != 1) {
            System.out.println("Parametro mancante\nEsempio: java Esercizio1 <nome_file>");
            return;
        }

	    if (!costruisciAlbero(args[0])) {
	        return;
        }

        String radice = trovaRadice();
        if (radice == null){
            System.out.println("I dati inseriti non rappresentano un albero singolo");
            return;
        }
        livelli.put(radice, 0);

        if(!dfs(radice)) {
            System.out.println("I dati inseriti non rappresentano un albero valido");
            return;
        }

        stampa();

    }
}