import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mécanisme de recherche de motifs d'expression pour se départir de la négation, des structures impersonnelles
 * et remplacer les synonymes par des variables comprises par le système à l'aide des dictionnaires des synonymes pour chaque appareil et fonction
 * @author Lorrie Rouillaux
 */
public class Normalisation {

	/**
	 * Recherche et traitement de motifs d'expression et de synonymes
	 * @param query
	 * Programme exprimé en langage naturel libre par l'habitant préalablement lemmatisé
	 * @return
	 * Programme en langage naturel simplifié (plus de négation, plus de structure impersonnelles et synonymes remplacés par des variables connues par le système
	 */
	public static String normalisation (String query){

		//traitement expression impersonnelle
		Pattern p = Pattern.compile("il falloir (que tu)?") ;  
		Matcher m = p.matcher(query) ;  
		query = m.replaceAll("") ;

		//traitement négation
		p = Pattern.compile("ne être pas") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("ne pas être") ;

		p = Pattern.compile("ne (faire|mettre|cesser|devoir) pas") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("ne pas") ;

		//gestion des synonymes pour les fonctions
		//synonymes "être éteint"
		try{
			InputStream ips=new FileInputStream("Synonymes/Fonctions/etreEteint.txt"); //parcourir le fichier des synonymes
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				if (query.contains(line))
					query = query.replace(line, "être éteindre"); //remplacement du synonyme par une variable comprise par le système
			}
			br.close(); 
		}		
		catch (Exception e){
			System.err.println(e.toString());
			System.exit(1);
		}
		//synonymes "éteindre"
		try{
			InputStream ips=new FileInputStream("Synonymes/Fonctions/eteindre.txt"); //parcourir le fichier des synonymes
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				if (query.contains(line))
					query = query.replace(line, "éteindre"); //remplacement du synonyme par une variable comprise par le système
			}
			br.close(); 
		}		
		catch (Exception e){
			System.err.println(e.toString());
			System.exit(1);
		}
		//synonymes "allumer"
		try{
			InputStream ips=new FileInputStream("Synonymes/Fonctions/allumer.txt"); //parcourir le fichier des synonymes
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				if (query.contains(line))
					query = query.replace(line, "allumer"); //remplacement du synonyme par une variable comprise par le système
			}
			br.close(); 
		}		
		catch (Exception e){
			System.err.println(e.toString());
			System.exit(1);
		}
		//synonymes "être allumé"
		try{
			InputStream ips=new FileInputStream("Synonymes/Fonctions/etreAllume.txt"); //parcourir le fichier des synonymes
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				if (query.contains(line))
					query = query.replace(line, "être allumer"); //remplacement du synonyme par une variable comprise par le système
			}
			br.close(); 
		}		
		catch (Exception e){
			System.err.println(e.toString());
			System.exit(1);
		}

		//gestion des synonymes pour les appareils
		//TV
		try{
			InputStream ips=new FileInputStream("Synonymes/Appareils/tv.txt"); //parcourir le fichier des synonymes
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				if (query.contains(line))
					query = query.replace(line, "tv"); //remplacement du synonyme par une variable comprise par le système
			}
			br.close(); 
		}		
		catch (Exception e){
			System.err.println(e.toString());
			System.exit(1);
		}
		//Ventilateur
		try{
			InputStream ips=new FileInputStream("Synonymes/Appareils/ventilateur.txt"); //parcourir le fichier des synonymes
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				if (query.contains(line))
					query = query.replace(line, "ventilateur"); //remplacement du synonyme par une variable comprise par le système
			}
			br.close(); 
		}		
		catch (Exception e){
			System.err.println(e.toString());
			System.exit(1);
		}
		//Lampe
		try{
			InputStream ips=new FileInputStream("Synonymes/Appareils/lampe.txt"); //parcourir le fichier des synonymes
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				if (query.contains(line))
					query = query.replace(line, "lampe"); //remplacement du synonyme par une variable comprise par le système
			}
			br.close(); 
		}		
		catch (Exception e){
			System.err.println(e.toString());
			System.exit(1);
		}

		query = negation(query); //appel de la méthode pour se départir de la négation
		return query; 
	}

	/**
	 * Méthode pour se départir de la négation
	 * @param query
	 * Programme en langage naturel simplifié
	 * @return
	 * Programme en langage naturel ôté de la négation
	 */
	public static String negation (String query){

		Pattern p = Pattern.compile("ne allumer pas|ne pas allumer") ;  
		Matcher m = p.matcher(query) ;  
		query = m.replaceAll("éteindre") ;

		p = Pattern.compile("ne pas être allumer") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("être éteindre") ;

		p = Pattern.compile("ne éteindre pas | ne pas éteindre") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("allumer") ;

		p = Pattern.compile("ne pas être éteindre") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("être allumer") ;

		return query;
	}
}