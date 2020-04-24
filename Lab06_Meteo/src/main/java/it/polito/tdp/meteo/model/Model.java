package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private MeteoDAO dao;
	private Map<String, Citta> citta;
	
	private List<Soluzione> soluzioni;
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		this.dao = new MeteoDAO();
		this.citta = new LinkedHashMap<>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {	
		//pulizia dataSet
		citta.clear();
		
		String result = "";		
		List<String> elencoLocalita = this.dao.gettCitta();
		
		for(String l : elencoLocalita) {
			List<Rilevamento> rilevamentiCitta = this.dao.getAllRilevamentiLocalitaMese(mese, l);
			Citta c = new Citta(l, rilevamentiCitta);
			citta.put(c.getNome(), c);
		}
		
		for(Citta c : citta.values()) {
			result += "Valore medio registrato a: " + c.getNome()+" = "+c.calcolaUmiditaMedia()+"% \n";
		}
		
		return result;
		
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		//pulizia dataSet
		citta.clear();

		//PRIMA DELLA RICORSIONE ACQUISISCO TUTTE LE 
		//RILEVAZIONI DI TUTTE LE CITTA' NEI PRIMI N GIORNI
		List<String> elencoLocalita = this.dao.gettCitta();
			
			for(String l : elencoLocalita) {
				List<Rilevamento> rilevamentiCitta = this.dao.getAllRilevamentiLocalitaIntervalloTempo(mese, l, NUMERO_GIORNI_TOTALI);
				Citta c = new Citta(l, rilevamentiCitta);
				citta.put(c.getNome(), c);
			}
		
			System.out.println("ALMENO HO ACQUISITO I DATI");
			
	//acquisiti i dati continuo a creare l'ambiente
	
		String result = "";
		this.soluzioni = new LinkedList<>();
	
		//Creo la soluzione vuota
		Soluzione parziale = new Soluzione();
		//	Creo il set rimanenti contenente i giorni ancora da utilizzare
		//	Al liv = 0 rimanenti = {1, 2, ..., 15}
		Set<Integer> rimanenti = new LinkedHashSet<Integer> ();
		rimanenti = this.dao.getDateRilevamenti(mese, NUMERO_GIORNI_TOTALI);
		
		System.out.println("STO PER INIZIARE LA RICORSIONE");
		cerca(parziale, 0, citta, rimanenti);
		
		// prima controllo che esistano soluzioni, altrimenti lo dico
		if(this.soluzioni.isEmpty()) {
			return "Non esistono soluzioni";
		}
		
		//per ogni soluzione ammissibile quando cambio città aumento di 100
		//ritorno risultato sotto forma di stringa
		for(Soluzione s : this.soluzioni) {
			for(int i = 1; i < s.getRilevamenti().size(); i++) {
				// se la citta' e' diversa dalla precedente, aggiungo 
				// un cossto di 100 alla soluzione
				if(s.getRilevamenti().get(i).getLocalita().compareTo
						(s.getRilevamenti().get(i-1).getLocalita()) != 0) {
					s.addCostoFisso();
				}
			}
			result += s.toString();
		}
		
		return result;			
	
	}

	/**
	 * 
	 * @param parziale soluzione parziale trovata fino ad ora al livello l-1
	 * @param l indica il livello della ricorsione il quale corrisponde ai 
	 * 			giorni precedentemente occupati
	 * @param rimanenti Insieme di giorni ancora da occupare
	 */
	private void cerca(Soluzione parziale, int l, Map<String, Citta> rilevamentiCitta, Set<Integer> rimanenti) {

		System.out.println("Sono dentro la ricorsione al livello : "+l);
		//	CASI TERMINALI
		//1.
		if(rimanenti.size()==0) {
			this.soluzioni.add(parziale);
		}
		//2.
		// se ho meno di 3 giorni rimanenti, non ha senso esplorare altre soluzioni 
		// poiche' non ammissibili per il vincolo dei 3 gg consecutivi
		if(rimanenti.size()<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
			return;
		}
		
		//	CASO NORMALE
		//	Finche' ci sono giorni ancora disponibili, prendo il nome della citta,
		//	ottengo i rilevamenti 
		while(!rimanenti.isEmpty()) {
			// per ogni citta' al lvello l
			
			for(String s : rilevamentiCitta.keySet()) {
				
				// se la citta' e' stata visitata piu' di 6 gg, cambio citta' (Nota: sto potando, non sto terminando)
				if(rilevamentiCitta.get(s).getCounter() == NUMERO_GIORNI_CITTA_MAX) {
					return;
				}
				
				// INIZIALIZZO COUNTER PER GIORNI CONSECUTIVI
				int cGiorniCons = 0;
				for(Integer i : rimanenti) {
					//	clono la soluzione del livello superiore
					Soluzione aggiornata = new Soluzione(parziale.getRilevamenti());
					
					//	aggiungo un giorno alla soluzione
					aggiornata.getRilevamenti().add(rilevamentiCitta.get(s).getRilevamenti().get(i));
					
					// aggiorno counter citta'
					rilevamentiCitta.get(s).increaseCounter();
					
					// aggiorno counter giorni consecutivi
					cGiorniCons++;
					
					//	BACKTRACKING
					//  clono il set dei giorni disponibili e rimuovo il giorno inserito nella soluzione aggiornata
					
					Set<Integer> disponibili = new LinkedHashSet<>(rimanenti);
					disponibili.remove(l+1); // nota: rimuovo liv+1 perche' nel set parto dal giorno 1
					l++;
					i = l;
					//Se ho inserito 3 giorni, scendo di livello
					//NOTA: non per forza cambia di citta!!!! prima ci sono degli if per potare
					/*if(cGiorniCons >= NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
						cerca(aggiornata, l+cGiorniCons, rilevamentiCitta, disponibili);
					}*/
					cerca(aggiornata, l+cGiorniCons, rilevamentiCitta, disponibili);
					
				}
			}
		}
	}
	
	

	

}
