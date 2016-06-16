/**
 * Enumération des types d'appareils
 * @author Lorrie Rouillaux
 */
public enum TypeDevice {
	
	TV("TV"), Lampe("Lampe"), Prise("Prise");
	private String type="";
	
	/**
	 * Constructeur
	 * @param t
	 * Type d'appareil (ex : tv, lampe, etc)
	 */
	TypeDevice(String t){
		this.type = t;
	}
	
	@Override
	public String toString(){
		return type;
	}
}