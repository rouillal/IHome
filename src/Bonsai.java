import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyse syntaxique : récupération des relations de dépendances entre les mots du programme exprimé en langage naturel libre par l'habitant 
 * préalablement lemmatisé et normalisé <br>
 * Outil Bonsaï : http://alpage.inria.fr/statgram/frdep/fr_stat_dep_parsing.html
 * @author Lorrie Rouillaux
 */
public class Bonsai {

	/**
	 * Pré-traitement du programme en langage naturel afin de réussir l'analyse syntaxique
	 * @param query
	 * Programme exprimé par l'habitant en langage naturel et normalisé
	 * @return
	 * Programme pré-traité pour l'analyse syntaxique
	 */
	public static String preTraitement(String query){ //obligatoire, cause des problèmes de dépendances dans Bonsaï sinon
		Pattern p = Pattern.compile("devoir allumer") ;  
		Matcher m = p.matcher(query) ;  
		query = m.replaceAll("allumer") ;

		p = Pattern.compile("devoir éteindre") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("éteindre") ;


		p = Pattern.compile("devoir être allumer") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("doit être allumé") ;

		p = Pattern.compile("devoir être éteindre") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("doit être éteint") ;

		p = Pattern.compile("être allumer") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("est allumé") ;

		p = Pattern.compile("être éteindre") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("est éteint") ;

		p = Pattern.compile("allumer") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("allume") ;

		p = Pattern.compile("éteindre") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("éteins") ;

		return query;
	}
	
	/**
	 * Appel du script treeDependency.sh pour générer les relations de dépendances entre les termes du programme en langage naturel
	 */
	public static void callScriptBonsai() {
		try {
			IHome.log("Création du fichier des dépendances Bonsai...  ");
			Process proc = Runtime.getRuntime().exec("bash bonsai/treeDependency.sh"); 
			BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			try {
				proc.waitFor();
				System.out.println("OK");
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
			while (read.ready()) {
				System.out.println(read.readLine());
			}
		} catch (IOException e) {
			System.err.println("KO");
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Parse le tableau des relations de dépendances de maniere à obtenir le lemme et le numéro du lemme dont il dépend
	 * @return
	 * Tableau à 2 colonnes : <br>
	 * Première colonne : lemme<br>
	 * Seconde colonne : numéro du lemme dont il dépend
	 */
	public static String[][] parseBonsai(){
		String chaine = "";
		String tokens[] = null;
		String lignes[] = null;
		String dep[][] = null;

		//lecture du fichier des relations de dépendance	
		try{
			IHome.log("Parsing des dépendances... ");
			InputStream ips=new FileInputStream("bonsai/dependencies.txt"); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine())!=null){
				chaine += line + "\n";
			}
			br.close(); 
			//récupération des lemmes et leur dépendance
			lignes = chaine.split("\n"); 
			dep = new String[lignes.length][2];
			for (int i = 0; i < lignes.length; i++){
				tokens = lignes[i].split("\t");
				dep[i][0] = tokens[2].toString(); //lemme
				dep[i][1] = tokens[6].toString(); //dépendance
			}	
			System.out.println("OK");
		}		
		catch (Exception e){
			System.err.println("KO");
			System.err.println(e.toString());
			System.exit(1);
		}
		return dep;
	}
}