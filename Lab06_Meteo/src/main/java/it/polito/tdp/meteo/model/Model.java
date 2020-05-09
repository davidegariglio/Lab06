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
	private List<Citta> citta;
	
	private Map<Date, List<Rilevamento>> rilevamentiMap;
		
	private Soluzione best;
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		this.dao = new MeteoDAO();
		this.citta = new ArrayList<>();
		rilevamentiMap = new LinkedHashMap<>();
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
			citta.add(c);
		}
		
		for(Citta c : citta) {
			result += "Valore medio registrato a: " + c.getNome()+" = "+c.calcolaUmiditaMedia()+"% \n";
		}
		
		return result;
		
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		//pulizia dataSet
		citta.clear();
		this.best = new Soluzione();
		String result = "";
		Soluzione parziale = new Soluzione();
		
		//	ACQUISIZIONE DATI PRIMI 15 GIORNI	
		List<String> elencoLocalita = this.dao.gettCitta();
				
		for(String l : elencoLocalita) {
			List<Rilevamento> rilevamentiCitta = this.dao.getAllRilevamentiLocalitaIntervalloTempo(mese, l, NUMERO_GIORNI_TOTALI);
			Citta c = new Citta(l, rilevamentiCitta);
			citta.add(c);
			}
		/*for(Citta c : citta) {
			for(Rilevamento r : c.getRilevamenti()) {
			}
		}
		*/
		
		//L'idea era quella di creare una mappa con chiave data e valore una lista di rilevamenti, uno per citta' in quella data
		//Così sembra funzionare
		for(Citta c : citta) {
			for(Rilevamento r : c.getRilevamenti()) {
				//Se la data e' gia' stata acquisita, salvo il rilevamento per quella data
				if(this.rilevamentiMap.containsKey(r.getData())) {
					this.rilevamentiMap.get(r.getData()).add(r);
				}
				//Altrimenti salvo data e rilevamento
				else {
					this.rilevamentiMap.put(r.getData(), new ArrayList<Rilevamento>());
					this.rilevamentiMap.get(r.getData()).add(r);
				}
			}
		}
		
		//Chiamo ricorsione
		cerca(parziale, this.rilevamentiMap, 0);

		return result;
	
	}

	/**
	 * 
	 * @param parziale lista di rilevamenti inseriti nella soluzione parziale
	 * @param l livello = numero di giorni analizzati fino ad ora 
	 * @param citta lista di tutte le città
	 */
	private void cerca(Soluzione parziale, Map<Date, List<Rilevamento>> mapRilevamenti, int livello) {
		
		//Casi terminali
		if(calcolaCosto(parziale) > best.getCosto()) {
			best = new Soluzione (parziale);
		}
		if(livello==NUMERO_GIORNI_TOTALI) {
			if(calcolaCosto(parziale) > best.getCosto()) {
				best = new Soluzione (parziale);
				return;
			}
		}
		
		for(Date d : mapRilevamenti.keySet()) {
			if(!parziale.containsDay(d)) {
				for(Rilevamento r : mapRilevamenti.get(d)) {
					parziale.add(r);
					cerca(parziale, mapRilevamenti, livello+1);
					
					parziale.remove(r);
					cerca(parziale, mapRilevamenti, livello+1);
				}	
			}
		}
		
	}

	private int calcolaCosto(Soluzione parziale) {
		int costo = 0;
		for(Rilevamento r : parziale.getRilevamenti()) {
			costo += r.getUmidita();
		}
		return costo;
	}

}
