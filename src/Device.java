import java.util.ArrayList;

/**
 * Gestion des appareils
 * @author Lorrie Rouillaux
 */
public class Device{

	private String nom; //nom de l'appareil dans le système
	private ArrayList <String> fonctions; //fonctions de l'appareil (ex : éteindre, allumer, etc)
	private TypeDevice type; //type d'appareil (ex : lampe, tv, prise, etc)
	private ArrayList <String> proprietes; //propriétés de l'appareil (ex : switch, son, etc)

	/**
	 * @param nom
	 * Nom de l'appareil dans le système
	 * @param t
	 * Type d'appareil (ex : lampe, tv, etc)
	 */
	public Device(String nom, TypeDevice t){
		this.nom = nom;
		this.type = t;
		this.fonctions = new ArrayList<>();
		this.proprietes = new ArrayList<>();
	}

	/**
	 * Getter nom de l'appareil
	 * @return
	 * Nom de l'appreil
	 */
	public String getNom(){
		return this.nom;
	}

	/**
	 * Getter type de l'appareil
	 * @return
	 * Type de l'appareil
	 */
	public TypeDevice getType(){
		return this.type;
	}

	/**
	 * Ajout de fonctions à l'appareil
	 * @param f
	 * Fonction à ajouter
	 */
	public void addFonctions(String f){
		this.fonctions.add(f);
	}

	/**
	 * Ajout d'une propriété à l'appareil
	 * @param p
	 * Propriété à ajouter à l'appareil
	 */
	public void addProprietes(String p){
		this.proprietes.add(p);
	}

	/**
	 * Getter lise des fonctions de l'appareil
	 * @return
	 * Liste des fonctions de l'appareil
	 */
	public ArrayList<String> getFonctions(){
		return this.fonctions;
	}
	
	/**
	 * Getter liste des propriétés de l'appareil
	 * @return
	 * Liste des propriétés de l'appareil
	 */
	public ArrayList<String> getPropriete(){
		return this.proprietes;
	}

	/**
	 * Méthode de création des appareils
	 * @param S
	 * Liste des appareils de l'habitat intelligent
	 */
	public static void createDevice(Services S){
		Device TV = new Device("tv", TypeDevice.TV); //device télévision
		TV.addFonctions("allumer");
		TV.addFonctions("éteindre");
		S.add(TV);

		Device lampeSalon = new Device("lampe", TypeDevice.Lampe); //device lampe
		lampeSalon.addFonctions("allumer"); 
		lampeSalon.addFonctions("éteindre");
		lampeSalon.addProprietes("couleur");
		S.add(lampeSalon);

		Device priseVentilo = new Device("ventilateur", TypeDevice.Prise); //device priseVentilo
		priseVentilo.addFonctions("allumer"); 
		priseVentilo.addFonctions("éteindre");
		priseVentilo.addProprietes("vitesse");
		S.add(priseVentilo);
	}

}