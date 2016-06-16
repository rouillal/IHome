import java.util.ArrayList;

/**
 * Liste des services de l'habitat
 * @author Lorrie Rouillaux
 */
public class Services {

	private ArrayList <Device> services; // liste des appareils
	
	/**
	 * Constructeur
	 */
	public Services(){
		this.services = new ArrayList<>();
	}
	
	/**
	 * Méthode d'ajout d'un appareil à la liste
	 * @param d
	 * Appareil à ajouter
	 */
	public void add(Device d){
		this.services.add(d);
	}
	
	/**
	 * Getter de la liste de tous les appareils de l'habitat
	 * @return
	 * Liste de tous les appareils de l'habitat
	 */
	public ArrayList<Device> getServices(){
		return this.services;
	}
}