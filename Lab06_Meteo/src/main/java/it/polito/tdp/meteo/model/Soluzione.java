package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Soluzione {
	
	private List<Rilevamento> rilevamenti;
	private int costo;
	
	public Soluzione() {
		this.rilevamenti = new LinkedList<>();
		costo = 0;
	}
	
	public Soluzione(Soluzione s) {
		super();
		this.rilevamenti= new ArrayList<>(s.getRilevamenti());
		this.costo = calcolaCostoRilevamenti(this.rilevamenti);
	}

	public boolean containsDay(Date d) {
		for(Rilevamento r : this.rilevamenti) {
			if(r.getData().equals(d)) {
				return true;
			}
		}
		return false;
	}
	
	private int calcolaCostoRilevamenti(List<Rilevamento> rilevamenti) {
		int result = 0;
		for(Rilevamento r : rilevamenti) {
			result += r.getUmidita();
		}
		return result;
	}

	public void remove(Rilevamento r) {
		this.rilevamenti.remove(r);
	}
	public List<Rilevamento> getRilevamenti() {
		return rilevamenti;
	}

	public void add(Rilevamento rilevamento) {
		rilevamenti.add(rilevamento);
		costo += rilevamento.getUmidita(); 
	}

	public int getCosto() {
		return costo;
	}
	
	public void addCostoFisso() {
		costo += 100;
	}

	@Override
	public String toString() {
		String result = "";
		for(Rilevamento r : rilevamenti) {
			result += r.getLocalita().substring(0, 2) + "\n";
		}
		result += "per un costo totale di: " + costo + " euro\n";
		return result;
	}
	
	


	
	

}
