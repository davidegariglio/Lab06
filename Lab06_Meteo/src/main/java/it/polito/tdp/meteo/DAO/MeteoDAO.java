package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	
	public MeteoDAO() {

	}

	public List<String> gettCitta(){
		
		final String sql = "SELECT localita " + 
				"FROM situazione AS s " + 
				"GROUP BY s.localita ";

		List<String> citta = new ArrayList<String>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				String c = rs.getString("Localita");
				citta.add(c);
			}

			conn.close();
			return citta;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiMese(int mese){
		
		final String sql = "SELECT Localita, Data, Umidita " + 
				"FROM situazione AS " + 
				"WHERE MONTH(s.Data) = ? " + 
				"ORDER BY data ASC";
		
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento> ();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
				
			}
			
			conn.close();
			return rilevamenti;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		
		final String sql = "SELECT Localita, Data, Umidita " + 
				"FROM situazione AS s " + 
				"WHERE MONTH(s.Data) = ? AND s.localita = ?";
		
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento> ();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			st.setString(2, localita);
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
				
			}
			
			conn.close();
			return rilevamenti;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	public List<Rilevamento> getAllRilevamentiLocalitaIntervalloTempo(int mese, String localita, int numeroGiorniTotali) {
		final String sql = "SELECT Localita, Data, Umidita " + 
				"FROM situazione AS s " + 
				"WHERE MONTH(s.Data) = ? AND s.localita = ? " + 
				"LIMIT ? ";
		
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento> ();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			st.setString(2, localita);
			st.setInt(3, numeroGiorniTotali);
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
				
			}
			
			conn.close();
			return rilevamenti;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	public Set<Integer> getDateRilevamenti(int mese, int numeroGiorniTotali) {
		final String sql = "SELECT Data " + 
				"FROM situazione AS s " + 
				"WHERE MONTH(s.Data) = ? " + 
				"GROUP BY Data " + 
				"LIMIT ? ";
		
		Set<Integer> rimanenti = new LinkedHashSet<> ();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			st.setInt(2, numeroGiorniTotali);
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				
				String day = rs.getString("Data");
				String[] parametri = day.split("-");
				rimanenti.add(Integer.parseInt(parametri[2]));
				
			}
			
			conn.close();
			return rimanenti;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}


}
