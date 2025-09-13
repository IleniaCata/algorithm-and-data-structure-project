/*
Ilenia Cataudella

Sottoproblema:
    Il sottoproblema è rappresentato da combinazioni[i], ovvero il numero di modi di decodificare una data sotto stringa
    che contiene i primi i caratteri.
Caso base:
    Il caso base è rappresentato dalla stringa vuota, che, per definizione, ha un solo modo per essere decodificata
    ovvero nessun carattere (combinazioni[0] = 1).
Caso generale:
    Il numero di possibili combinazioni per un prefisso di una data lunghezza i è dato dalla somma di tutte le
    decodifiche valide per i prefissi più brevi con lunghezza i-len, considerando i codici di lunghezza len uguali
    agli ultimi len caratteri del prefisso.

Costo computazionale:
- L'algoritmo utilizza due cicli annidati: il ciclo esterno itera su ogni posizione della sequenza (da 1 a n)
  per un costo di O(n); il ciclo interno itera, invece, sui k codici validi (k=8) svolgendo i confronti sulle varie
  sotto stringhe di lunghezza m (m<=4), per un costo di O(m * k).
Il costo totale è quindi O(n*k*m), dato che m e k sono costanti il costo complessivo sarà O(n).

Strutture dati utilizzate:
Sono stati utilizzati due array, uno per memorizzare i codici e uno per le possibili combinazioni, dove il costo di
accesso a un elemento è O(1):
  - Per i codici si è scelto di utilizzare un array per via del numero ridotto e costante di codici.
  - Per le combinazioni è stato utilizzato un array perchè consente di memorizzare i risultati parziali in ordine e
    di recuperarli facilmente, inoltre anche in questo caso non c'è bisogno di variare la dimensione della struttura.
*/

import java.util.*;
import java.io.*;

public class Esercizio2 {

    private static final String[] codici = {
            "0",
            "00",
            "001",
            "010",
            "0010",
            "0100",
            "0110",
            "0001"
    };

    /* In questo metodo viene implementato l'algoritmo tramite programmazione dinamica.
    Costruisce la soluzione a partire dal caso più semplice (caso base) fino a valutare tutte le sotto stringhe successive.
    L'array delle combinazioni ha lunghezza n+1 per considerare anche il caso base.
    L'ultimo elemento dell'array combinazioni, alla fine del ciclo esterno, rappresenta il numero di combinazioni possibili
    perchè accumula tutte le soluzioni parziali, fino alla sequenza completa.*/
    private static int decodifichePossibili(String sequenza) {
        int lunghezza = sequenza.length();
        int[] combinazioni = new int[lunghezza + 1];
        combinazioni[0] = 1;

        for (int i = 1; i <= lunghezza; i++) {
            for (String codice : codici) {
                int len = codice.length();
                if (i >= len && sequenza.substring(i - len, i).equals(codice)) {
                    combinazioni[i] += combinazioni[i - len];
                }
            }
        }

        return combinazioni[lunghezza];
    }

    /* Nel main viene letto il file che contiene la sequenza da analizzare, la lettura non
    è stata fatta con un while perchè si assume che il file in input contenga una sola riga.
    Stampa l'output del metodo decodifichePossibili.*/
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length != 1) {
            System.out.println("Parametro mancante\nEsempio: java Esercizio2 <nome_file>");
            return;
        }

        try {
            Scanner scanner = new Scanner(new File(args[0]));
            if (scanner.hasNextLine()) {
                String sequenza = scanner.nextLine();
                System.out.println(decodifichePossibili(sequenza));
            } else {
                System.out.println("File vuoto");
            }
        } catch (IOException e) {
            System.out.println("Errore nella lettura del file: " + e.getMessage());
        }
    }
}