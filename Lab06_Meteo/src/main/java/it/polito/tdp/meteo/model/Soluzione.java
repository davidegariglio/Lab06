package it.polito.tdp.meteo.model;

import java.util.LinkedList;
import java.util.List;

public class Soluzione {
	
	private List<Rilevamento> rilevamenti;
	private int costo;
	
	public Soluzione() {
		this.rilevamenti = new LinkedList<>();
		costo = 0;
	}
	
	public Soluzione(List<Rilevamento> rilevamenti) {
		super();
		this.rilevamenti = new LinkedList<>(rilevamenti);
		this.costo = calcolaCostoRilevamenti(this.rilevamenti);
	}

	private int calcolaCostoRilevamenti(List<Rilevamento> rilevamenti) {
		int result = 0;
		for(Rilevamento r : rilevamenti) {
			result += r.getUmidita();
		}
		return result;
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
		String result = "********** \nUna soluzione e': \n";
		for(Rilevamento r : rilevamenti) {
			result += r.getLocalita().substring(0, 2) + "\n";
		}
		result += "per un costo totale di: " + costo + " euro\n";
		return result;
	}
	
	


	
	

}
