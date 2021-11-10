package de.fernschulen.minidb;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

public class BearbeitenEintrag extends JDialog {
	//automatisch über Eclipse erzeugt
	private static final long serialVersionUID = 2674865770208476234L;
	
	//für die Eingabefelder
	private JTextField name, nachname, strasse, plz, ort, telefon;
	//für die Anzeige
	private JLabel nummer;
	//Für das Label der Datensatzanzeige
	private JLabel anzeigeDatensatz;
	 //für die zwei Zahlen der Datensatzanzeige
	private int aktuell, letzter;
	//für die Aktionen
	private MeineAktionen loeschenAct, vorAct, zurueckAct, startAct, endeAct, aktualisierenAct;
	//für die Verbindung
	private Connection verbindung;
	private ResultSet ergebnisMenge;
	//für die Abfrage
	private String sqlAbfrage;
	
	//eine innere Klasse für die Aktionen
	class MeineAktionen extends AbstractAction {
		//automatisch über Eclipse ergänzt
		private static final long serialVersionUID = 8673560298548765044L;

		//der Konstruktor 
		public MeineAktionen(String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
			//den Konstruktor der übergeordneten Klasse mit dem Text und dem Icon aufrufen
			super(text, icon);
			//die Beschreibung setzen für den Bildschirmtipp
			putValue(SHORT_DESCRIPTION, beschreibung);
			//den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			//das ActionCommand
			putValue(ACTION_COMMAND_KEY, actionText);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("vor"))
				ganzVor();
			if (e.getActionCommand().equals("zurueck")) 
				ganzZurueck();
			if (e.getActionCommand().equals("einenvor")) 
				einenVor();
			if (e.getActionCommand().equals("einenzurueck")) 
				einenZurueck();
			if (e.getActionCommand().equals("loeschen")) 
				loeschen();
			if (e.getActionCommand().equals("aktualisieren")) 
				aktualisieren();
		}
	}
	
	//die innere Klasse für die Fensterereignisse
	class FensterListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			//die Datenbankverbindung trennen
			//ergebnisMenge und verbindung sind Variablen der äußeren Klasse 
			try {
				BearbeitenEintrag.this.ergebnisMenge.close();
				BearbeitenEintrag.this.verbindung.close();
				MiniDBTools.schliessenDB("jdbc:derby:");
			}
			catch(Exception exc) {
				JOptionPane.showMessageDialog(null, "Problem: \n" + exc.toString());
			}
		}
	}
	
	//der Konstruktor der Klasse BearbeitenEintrag
	public BearbeitenEintrag(JFrame parent, boolean modal) {
		
		super(parent, modal);
		setTitle("Einträge bearbeiten");
		//wir nehmen ein Borderlayout
		setLayout(new BorderLayout());
		//die Aktionen erstellen
		loeschenAct = new MeineAktionen("Datensatz löschen", 
				new ImageIcon("icons/Delete24.gif"), 
				"Löscht den aktuellen Datensatz", 
				null,
				"loeschen");
		vorAct = new MeineAktionen("Einen Datensatz weiter", 
				new ImageIcon("icons/Forward24.gif"), 
				"Blättert einen Datensatz weiter", 
				null, 
				"einenvor");
		zurueckAct = new MeineAktionen("Einen Datensatz zurück", 
				new ImageIcon("icons/Back24.gif"), 
				"Blättert einen Datensatz zurück", 
				null, 
				"einenzurueck");
		startAct = new MeineAktionen("Zum ersten Datensatz",
				new ImageIcon("icons/Front24.gif"), 
				"Geht zum ersten Datensatz", 
				null, 
				"vor");
		endeAct = new MeineAktionen("Zum letzten Datensatz", 
				new ImageIcon("icons/End24.gif"), 
				"Geht zum letzten Datensatz", 
				null, 
				"zurueck");
		aktualisierenAct = new MeineAktionen("Änderungen speichern", 
				new ImageIcon("icons/Save24.gif"), 
				"Speichert Änderungen am aktuellen Datensatz", 
				null, 
				"aktualisieren");
		
		//die Symbolleiste oben einfügen
		add(symbolleiste(), BorderLayout.NORTH);

		//die Oberfläche erstellen und einfügen
		add(initGui(), BorderLayout.CENTER);
		//zuerst nehmen wir alle Einträge aus der Tabelle adressen
		sqlAbfrage = "SELECT * FROM adressen";
		//diese Abfrage wählt nur alle Müllers aus
		//sqlAbfrage = "SELECT * FROM adressen WHERE nachname = 'Müller'";

		//die Datenbankverbindung herstellen
		initDB();
		
		add(initLabel(), BorderLayout.SOUTH);
		//die Verbindung mit dem Listener des Fensters herstellen
		addWindowListener(new FensterListener());
		
		//packen und anzeigen
		pack();
		setVisible(true);
		//Standardoperation setzen
		//hier den Dialog ausblenden und löschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	//fügt die Felder in ein Panel ein und liefert das Panel zurück
	private JPanel initGui() {
		JPanel tempPanel = new JPanel();
		//im GridLayout mit zwei Spalten
		tempPanel.setLayout(new GridLayout(0, 2));
		//für die Nummer (nur Anzeige)
		tempPanel.add(new JLabel("ID-Nummer:"));
		nummer = new JLabel();
		tempPanel.add(nummer);
		//für die anderen Felder
		tempPanel.add(new JLabel("Vorname:"));
		name = new JTextField();
		tempPanel.add(name);
		tempPanel.add(new JLabel("Nachname:"));
		nachname = new JTextField();
		tempPanel.add(nachname);
		tempPanel.add(new JLabel("Strasse:"));
		strasse = new JTextField();
		tempPanel.add(strasse);
		tempPanel.add(new JLabel("PLZ:"));
		plz = new JTextField();
		tempPanel.add(plz);
		tempPanel.add(new JLabel("Ort:"));
		ort = new JTextField();
		tempPanel.add(ort);
		tempPanel.add(new JLabel("Telefon:"));
		telefon = new JTextField();
		tempPanel.add(telefon);
		//zurückgeben
		return tempPanel;
	}
	
	//die Symbolleiste erzeugen und zurückgeben
	private JToolBar symbolleiste() {
		JToolBar leiste = new JToolBar();
		//die Symbole über die Aktionen einbauen
		leiste.add(loeschenAct);
		leiste.add(aktualisierenAct);
		//Abstand einbauen
		leiste.addSeparator();
		leiste.add(startAct);
		leiste.add(zurueckAct);
		leiste.add(vorAct);
		leiste.add(endeAct);
		
		//die komplette Leiste zurückgeben
		return (leiste);
	}
	
	//Das Label für die Datensatzanzeige erzeugen und zurück geben
	private JLabel initLabel() {
		anzeigeDatensatz = new JLabel();
		//setzt den Text 
		setLabel();
		return anzeigeDatensatz;
	}
	
	// Aktualisiert das Label nach jeder neuen Aktion
	// der einzelnen aufgerufenen Methoden
	private void setLabel() {
		anzeigeDatensatz.setText("Datensatz " + aktuell + " von "+ letzter);
	}

	//die Verbindung zur Datenbank herstellen
	private void initDB() {
		try{
			//Verbindung herstellen und Ergebnismenge beschaffen
			verbindung=MiniDBTools.oeffnenDB("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:adressenDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, sqlAbfrage);
			//Jeden Eintrag durchgehen bis Ende erreicht ist und ..
			 while(ergebnisMenge.next()) {
				 //.. (int)letzter infolge 
				 //dessen inkrementieren
				letzter++;
			 }
			 //(int)aktuell inkrementieren
			 aktuell++;
			 //und den Zeiger der ErgebnisMenge 
			 //wieder auf Anfang setzen
			 ergebnisMenge.first();
			 datenLesen();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//die Methode liest die Daten und schreibt sie in die Felder
	private void datenLesen() {
		try {
			nummer.setText(Integer.toString(ergebnisMenge.getInt(1)));
			name.setText(ergebnisMenge.getString(2));
			nachname.setText(ergebnisMenge.getString(3));
			strasse.setText(ergebnisMenge.getString(4));
			plz.setText(ergebnisMenge.getString(5));
			ort.setText(ergebnisMenge.getString(6));
			telefon.setText(ergebnisMenge.getString(7));
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//die Methode geht zum ersten Datensatz
	private void ganzVor() {
		try {
			//ganz nach vorne gehen
			ergebnisMenge.first();
			//aktuell anpassen
			aktuell = 1;
			//Label Datensatzanzeige setzen
			setLabel();
			datenLesen();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//die Methode geht zum letzten Datensatz
	private void ganzZurueck() {
		try {
			//ganz nach hinten gehen
			ergebnisMenge.last();
			//aktuell anpassen
			aktuell = letzter;
			//Label Datensatzanzeige setzen
			setLabel();
			datenLesen();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
}

	//die Methode geht einen Datensatz weiter
	private void einenVor() {
		try {
			//stehen wir jetzt bei dem letzten Eintrag?
			if (ergebnisMenge.isLast()) 
				// dann zurück
				return;
			//gibt es noch einen Datensatz?
			else if (ergebnisMenge.next())
				//aktuell anpassen
				aktuell++;
				//Label Datensatzanzeige setzen
				setLabel();
				datenLesen();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
	
	//die Methode geht einen Datensatz zurück
	private void einenZurueck() {
		try {
			//stehen wir jetzt an dem ersten Eintrag?
			if (ergebnisMenge.isFirst()) 
				//dann zurück
				return;
			//gibt es noch einen Datensatz davor?
			else if (ergebnisMenge.previous()) 
				//aktuell anpassen
				aktuell--;
				//Label Datensatzanzeige setzen
				setLabel();
				datenLesen();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
	
	//die Methode löscht einen Datensatz
	private void loeschen() {
		try {
			//wir müssen uns merken, wo wir sind
			int position;
			position = ergebnisMenge.getRow();
			//den Eintrag löschen
			ergebnisMenge.deleteRow();
        	//Ergebnismenge schließen
	        ergebnisMenge.close();
	        // und neu öffnen
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, sqlAbfrage);
			
			//und wieder zur "alten" Position gehen
			ergebnisMenge.absolute(position);
			//stehen wir jetzt hinter dem letzten?
			if (ergebnisMenge.isAfterLast())
				//dann zum letzten gehen
				ergebnisMenge.last();
			//letzter anpassen
			letzter--;
			//Label Datensatzanzeige setzen
			setLabel();
			//die Daten neu lesen
			datenLesen();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//die Methode aktualisiert einen Eintrag
	private void aktualisieren() {
		try {
			//wir müssen uns merken, wo wir sind
			int position;
			position = ergebnisMenge.getRow();
			
			//die Daten aktualisieren
        	ergebnisMenge.updateString(2, name.getText());
        	ergebnisMenge.updateString(3, nachname.getText());
        	ergebnisMenge.updateString(4, strasse.getText());
        	ergebnisMenge.updateString(5, plz.getText());
        	ergebnisMenge.updateString(6, ort.getText());
        	ergebnisMenge.updateString(7, telefon.getText());   	
        	//den Datensatz aktualisieren
        	ergebnisMenge.updateRow();
        	//Ergebnismenge schließen
	        ergebnisMenge.close();
	        // und neu öffnen
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, sqlAbfrage);
			//und wieder zur "alten" Position gehen
			ergebnisMenge.absolute(position);
			//die Daten neu lesen
			datenLesen();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
}
