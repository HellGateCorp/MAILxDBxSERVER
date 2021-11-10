package de.fernschulen.minidb;

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ListenAnzeige extends JDialog{
	//automatisch über Eclipse erzeugt
	private static final long serialVersionUID = -5140210296788483971L;

	//für die Schaltfläche 
	private JButton ok;
	
	//die innere Klasse für den ActionListener
	class ListenListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//wurde auf OK geklickt?
			if (e.getActionCommand().equals("ok"))
				//dann Dialog schließen
				dispose();
		}
	}
	
	//der Konstruktor
	public ListenAnzeige(JFrame parent, boolean modal) {
		super(parent, modal);
		setTitle("Listenanzeige");
		setLayout(new FlowLayout(FlowLayout.CENTER));

		//die Daten lesen 
		lesen();
		//die Schaltfläche
		ok = new JButton("OK");
		ok.setActionCommand("ok");
		ok.addActionListener(new ListenListener());
		add(ok);

		//Größe setzen und anzeigen
		setSize(220, 300);
		setVisible(true);
		
		//Standardoperation setzen
		//hier den Dialog ausblenden und löschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	//die Methode liest die Daten ein und zeigt sie an
	private void lesen() {
		Connection verbindung;
		ResultSet ergebnisMenge;
		//ein Panel für die Anzeige
		JPanel panel = new JPanel();
		//das Layout für das Panel setzen
		panel.setLayout(new GridLayout(0,2));
		//ein Container mit Bildlaufleisten
		JScrollPane pane = new JScrollPane(panel);
		pane.setPreferredSize(new Dimension(200, 220));
		
		try{
			//Verbindung herstellen und Ergebnismenge beschaffen
			verbindung=MiniDBTools.oeffnenDB("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:adressenDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM adressen");
			//solange Daten da sind
			while (ergebnisMenge.next()) {
				panel.add(new JLabel("ID-Nummer: "));
				//beim Lesen wird die Spaltennummer angegeben
				panel.add(new JLabel(Integer.toString(ergebnisMenge.getInt(1))));
				panel.add(new JLabel("Vorname:"));
				panel.add(new JLabel(ergebnisMenge.getString(2)));
				panel.add(new JLabel("Nachname:"));
				panel.add(new JLabel(ergebnisMenge.getString(3)));
				panel.add(new JLabel("Strasse:"));
				panel.add(new JLabel(ergebnisMenge.getString(4)));
				panel.add(new JLabel("PLZ:"));
				panel.add(new JLabel(ergebnisMenge.getString(5)));
				panel.add(new JLabel("Ort:"));
				panel.add(new JLabel(ergebnisMenge.getString(6)));
				panel.add(new JLabel("Telefon:"));
				panel.add(new JLabel(ergebnisMenge.getString(7)));
				panel.add(new JLabel("--------------"));
				panel.add(new JLabel("--------------"));
			}
			add(pane);
			
        	//Ergebnismenge und Verbindung schließen
	        ergebnisMenge.close();
        	verbindung.close();
        	//und das Datenbanksystem auch
        	MiniDBTools.schliessenDB("jdbc:derby:adressenDB");
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
}
