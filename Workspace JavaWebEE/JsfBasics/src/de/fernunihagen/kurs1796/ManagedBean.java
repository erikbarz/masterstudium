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
