package de.fernunihagen.kurs1796;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class ManagedBean {

    // Deklaration der Properties
    private String vorname;
    private String nachname;
    private String alter;
    private String plz;
    private String strasse;
    private String mail;
    private String gewicht;
    private String ort; 
    
    private String name;

    
    // getter und setter
	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
    public String getAlter() {
		return alter;
	}

	public void setAlter(String alter) {
		this.alter = alter;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getGewicht() {
		return gewicht;
	}

	public void setGewicht(String gewicht) {
		this.gewicht = gewicht;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public void onButtonClicked() {
    	// Der zusammengesetzte Name wird erzeugt
        setName(vorname + " " + nachname);
        
        try
        {
            // Die ManagedBean leitet den Anwender auf die JSF-Seite result.xhtml weiter
            FacesContext.getCurrentInstance().getExternalContext().dispatch("result.xhtml");
        }
        catch (IOException e)
        { 
            // Wenn ein Fehler auftritt, wird dieser in der Konsole ausgegeben
            System.out.println("Fehler aufgetreten: " + e.getMessage());
        }
    }
	
}
