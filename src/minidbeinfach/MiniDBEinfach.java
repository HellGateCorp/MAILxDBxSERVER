package de.fernschulen.minidbeinfach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

//ACHTUNG!! Das Programm lässt sich nur ausführen, wenn derby.jar eingebunden ist

public class MiniDBEinfach{
	
	public static void main(String[] args) {
	 	try {
			//den Treiber registrieren
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").getDeclaredConstructor().newInstance();
			//die Verbindung über den Treibermanager herstellen
			//die Datenbank ist adressen
			//ACHTUNG! Es gibt mehrere Connection-Klassen!
			//benötigt wird die Klasse aus dem Paket java.sql.
			Connection verbindung = DriverManager.getConnection("jdbc:derby:adressenDB");
			//Änderungen von außen werden sichtbar und der Cursor kann beliebig bewegt werden
			//Es gibt auch mehrere Statement-Klassen!
			Statement state = verbindung.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			//die Ergebnismenge erzeugen
			//dazu beschaffen wir über eine SQL-Anweisung alle Einträge in der Tabelle
			ResultSet ergebnisMenge = state.executeQuery("SELECT * FROM adressen");
	        //zum Test alle Werte in der Konsole ausgeben
	        while (ergebnisMenge.next()) {
	       	 	System.out.println(ergebnisMenge.getInt("iNummer"));
	       	 	System.out.println(ergebnisMenge.getString("vorname"));
	       	 	System.out.println(ergebnisMenge.getString("nachname"));
	       	 	System.out.println(ergebnisMenge.getString("strasse"));
	       	 	System.out.println(ergebnisMenge.getString("plz"));
	       	 	System.out.println(ergebnisMenge.getString("ort"));
	       	 	System.out.println(ergebnisMenge.getString("telefon"));
	        }
			//Verbindung schließen
			state.close();
			ergebnisMenge.close();
			verbindung.close();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Problem: \n" + e.toString());
		}
	}
}
