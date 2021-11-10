package de.fernschulen.minidb;

//die Klasse stellt statische Methoden zur Arbeit mit Datenbanken 
//zur Verfügung 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class MiniDBTools {
	
	//die Methode lädt den Treiber und stellt die Verbindung her
	//zurückgeliefert wird ein Connection-Objekt
	//übergeben werden der Treiber und Argumente für das Öffnen
	public static Connection oeffnenDB(String treiber, String arg) {
		Connection verbindung = null;
		try {
			//den Treiber laden
			Class.forName(treiber).getDeclaredConstructor().newInstance();
			//die Datenbank ist adressenDB 
			verbindung = DriverManager.getConnection(arg);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Problem: \n" + e.toString());
		}
		return verbindung;
	}
	
	//die Methode liefert eine Ergebnismenge
	//übergeben wird die Verbindung und der SQL-Ausdruck
	public static ResultSet liefereErgebnis(Connection verbindung, String sqlAnweisung) {
		ResultSet ergebnisMenge = null;
		try {
			Statement state = verbindung.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ergebnisMenge = state.executeQuery(sqlAnweisung);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Problem: \n" + e.toString());
		}
		return ergebnisMenge;
	}
	
	//die Methode fährt das gesamte Datenbanksystem herunter
	//das wird bei der "embedded" Version von Apache Derby ausdrücklich empfohlen
	//übergeben wird das Protokoll
	public static void schliessenDB(String protokoll) {
		//das Herunterfahren löst bei Erfolg eine Exception aus!!!
		boolean erfolg = false;
		try {
			DriverManager.getConnection(protokoll + ";shutdown=true");
		}
		catch (SQLException e) {
			erfolg = true;
		}
		if (erfolg !=true) 
			JOptionPane.showMessageDialog(null, "Das DBMS konnte nicht heruntergefahren werden.");
	}
}
