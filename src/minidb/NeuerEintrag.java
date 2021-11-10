package de.fernschulen.minidb;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class NeuerEintrag extends JDialog{
	//automatisch über Eclipse erzeugt
	private static final long serialVersionUID = -5496318621928815910L;

	//für die Eingabefelder
	private JTextField name, nachname, strasse, plz, ort, telefon;
	//für die Schaltflächen 
	private JButton ok, abbrechen;
	
	//die innere Klasse für den ActionListener
	class NeuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//wurde auf OK geklickt?
			if (e.getActionCommand().equals("ok"))
				//dann die Daten übernehmen
				uebernehmen();
				
			//wurde auf Abbrechen geklickt?
			if (e.getActionCommand().equals("abbrechen"))
				//dann Dialog schließen
				dispose();
		}
	}
	
	//der Konstruktor
	public NeuerEintrag(JFrame parent, boolean modal) {
		super(parent, modal);
		setTitle("Neuer Eintrag");
		//die Oberfläche erstellen
		initGui();
		
		//Standardoperation setzen
		//hier den Dialog ausblenden und löschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void initGui() {
		setLayout(new GridLayout(0, 2));
		//für die Eingabe
		add(new JLabel("Vorname:"));
		name = new JTextField();
		add(name);
		add(new JLabel("Nachname:"));
		nachname = new JTextField();
		add(nachname);
		add(new JLabel("Strasse:"));
		strasse = new JTextField();
		add(strasse);
		add(new JLabel("PLZ:"));
		plz = new JTextField();
		add(plz);
		add(new JLabel("Ort:"));
		ort = new JTextField();
		add(ort);
		add(new JLabel("Telefon:"));
		telefon = new JTextField();
		add(telefon);
		
		//die Schaltflächen
		ok = new JButton("OK");
		ok.setActionCommand("ok");
		abbrechen = new JButton("Abbrechen");
		abbrechen.setActionCommand("abbrechen");
		
		NeuListener listener = new NeuListener();
		ok.addActionListener(listener);
		abbrechen.addActionListener(listener);
		
		add(ok);
		add(abbrechen);

		//packen und anzeigen
		pack();
		setVisible(true);
	}
	
	//die Methode legt einen neuen Datensatz an
	private void uebernehmen() {
		Connection verbindung;
		ResultSet ergebnisMenge;
		try{
			//Verbindung herstellen und Ergebnismenge beschaffen
			verbindung=MiniDBTools.oeffnenDB("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:adressenDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM adressen");
        	//zur "Einfügezeile" bewegen
        	ergebnisMenge.moveToInsertRow();
        	//die Nummer wird automatisch gesetzt
        	//angegeben werden die Nummer der Spalte und der neue Wert
        	ergebnisMenge.updateString(2, name.getText());
        	ergebnisMenge.updateString(3, nachname.getText());
        	ergebnisMenge.updateString(4, strasse.getText());
        	ergebnisMenge.updateString(5, plz.getText());
        	ergebnisMenge.updateString(6, ort.getText());
        	ergebnisMenge.updateString(7, telefon.getText());   	
  	
        	//und einfügen
        	ergebnisMenge.insertRow();
        	//Ergebnismenge und Verbindung schließen
	        ergebnisMenge.close();
        	verbindung.close();
        	//und das Datenbank-System auch
        	MiniDBTools.schliessenDB("jdbc:derby:adressenDB");
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
}
