# Progetto di algoritmi e strutture dati 
Il progetto (svolto per l'esame di algoritmi e strutture dati) è composto da tre esercizi, ognuno dei quali risponde ad una precisa consegna. 
Per ogni esercizio è presente un commento inziale contenente il calcolo del costo computazionale e una breve spiegazione sulle strutture dati utilizzate.

# Esercizio 1
*Consegna:*
In biologia, un albero filogenetico rappresenta le relazioni evolutive tra cladi. Ogni nodo è un clado, gli archi indicano discendenza diretta. 
Vogliamo identificare, per ogni livello di profondità (radice = livello 0), la specie con il maggior numero di discendenti (diretti + indiretti).
Si scriva un programma che legga da linea di comando il nome di un file di testo composto da una sequenza di linee, ognuna delle quali rappresenta 
una relazione di discendenza diretta nel formato `<inferiore>,<superiore>`. Una volta verificato che gli elementi così relazionati formano un singolo 
albero, il programma, usando un algoritmo ricorsivo, deve analizzare l’albero e mostrare in output, per livelli crescenti, il valore numerico del 
livello (da 0) seguito da `: ` e dal nome dell’elemento con il maggior numero di discendenti. In caso di parità fra più elementi, questi vanno visualizzati 
in ordine lessicografico separati da `, `.

*Soluzione proposta:*
Ho implementato un algoritmo DFS ricorsivo che riesce a: 
- calcolare la profondità e il numero di discendenti di ogni nodo;
- individuare cicli (controlla la validità dell'albero);
- creare una lista che per ogni livello contenga i nodi con numero massimo di discendenti.
Utilizzo un metodo per la lettura e creazione dell'albero, un metodo per individuare la radice e un metodo per effettuare l'ordinamento dei nodi.

# Esercizio 2
*Consegna:*
Data una stringa binaria S, determinare il numero totale di sequenze di caratteri che hanno la stessa codifica S utilizzando i codici dati; 
non è richiesto di stampare anche le sequenze di caratteri con la stessa codifica. Si presti attenzione al fatto che esistono stringhe che non possono 
essere decodificate in alcuna sequenza di caratteri (esempio: S = 1111); in tal caso l'algoritmo deve restituire il valore zero.
Il programma accetta sulla riga di comando un unico parametro che rappresenta il nome del file di input, che contiene (su un'unica riga) la stringa S 
composta esclusivamente di caratteri 0 e 1, come segue:
0010001001001001000110001000001000101000010100000010010100
L'output è composto da un intero che rappresenta il numero di possibili decodifiche di S, utilizzando i codici dati. 
Nel caso dell'esempio sopra, il programma stampa:
153408

Codici: 0, 00, 001, 010, 0010, 0100, 0110, 0001.

*Soluzione proposta:*
Ho implementato un algoritmo di programmazione dinamica che, per ogni codice, controlla se l’ultima parte del prefisso corrente corrisponde al codice, se sì, 
si aggiunge al conteggio totale il numero di decodifiche della sottostringa precedente, già memorizzato nell’array combinazioni. In questo modo, per ogni 
prefisso della sequenza vengono considerate tutte le possibili decodifiche senza rifare calcoli già eseguiti. Alla fine, l’ultimo elemento dell’array contiene 
il numero totale di decodifiche della sequenza completa. 

# Esercizio 3
*Consegna:*
Il programma Java richiede un file di input contenente la descrizione della rete di telecomunicazione (o del grafo non orientato e pesato) composta da 40 nodi e 89 link bidirezionali/edges.
Da questo file di input, si dovrebbe leggere e salvare nelle strutture dati adatte solo i dati che ci interessano: 
1) il numero totale di nodi, n;
2) il numero totale di link (o edge), m;
3) i link stessi e iv) i pesi di questi link.
Il programma da realizzare deve calcolare fino a TRE cammini (completamente distinti), se possibile o se esistono, di costo minimo fra tutte le coppie di nodi per il grafo non orientato
e pesato. Due cammini sono definiti completamente distinti se non c’è nessun arco in comune fra il primo e il secondo cammino calcolato. Si richiede di usare in questo esercizio
una implementazione dell'algoritmo di Dijkstra che utilizza una struttura dati efficiente durante il calcolo dei cammini minimi.
Il programma deve stampare a video:
1) Da uno a tre cammini completamente distinti di costo minimo, se esistono, fra il nodo sorgente ed il nodo destinazione, ed il costo totale del cammino minimo (si nota che possono
   esistere uno o più cammini di costo minimo fra una coppia di nodi).
2) Il tempo totale in secondi per trovare la soluzione (stampare il tempo prima di uscire dal programma).
   
*Soluzione proposta:*
Ho implementato l'algoritmo di Dijkstra con MinHeap modificato in modo da calcolare i cammini minimi di un grafo costituito man mano da meno archi, per garantire cammini minimi disgiunti 
per ogni coppia di nodi. Il metodo Dijkstra viene richiamato più volte da un metodo ausiliare che permette di calcolare, qualora esistano, fino a tre cammini di costo minimo efficienti.
Sono stati implementati inoltre: un metodo per la lettura del grafo da file e creazione del grafo stesso e un metodo per la stampa dei cammini trovati. 






