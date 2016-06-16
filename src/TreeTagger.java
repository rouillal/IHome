import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.annolab.tt4j.*;
import static java.util.Arrays.asList;

/**
 * Analyse lexicale : lemmatisation du programme exprimé en langage naturel libre par l'habitant<br>
 * Outil TreeTagger : http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/
 * @author Lorrie Rouillaux
 */
public class TreeTagger {

	/**
	 * @param query
	 * Programme exprimé en langage naturel par l'habitant
	 * @return
	 * Programme lemmatisé
	 */
	public static String toLemma (String query){
		IHome.log("TreeTagger...  ");
		String chaine = "";

		//prétraitement résolvant des erreurs d'analyse lexicale
		Pattern p = Pattern.compile("Etein(s|dre)") ;  
		Matcher m = p.matcher(query) ;  
		query = m.replaceAll("éteins") ;
		
		p = Pattern.compile("allumé(e|s|es)") ;  
		m = p.matcher(query) ;  
		query = m.replaceAll("allumer") ;
		
		if (query.contains("A "))
			query = query.replace("A ", "à ");

		query = query.toLowerCase();

		if (query.contains("j'"))
			query = query.replace("j'", "j' ");

		if (query.contains("n'"))
			query = query.replace("n'", "n' ");
		
		//exécution de TreeTagger
		try{
			// Point TT4J to the TreeTagger installation directory
			System.setProperty("treetagger.home", "TreeTagger");
			TreeTaggerWrapper <String> tt = new TreeTaggerWrapper <String>();
			final ArrayList <String> res = new ArrayList <String>();
			try {
				tt.setModel("TreeTagger/french.par");
				tt.setHandler(new TokenHandler<String>() {
					public void token(String token, String pos, String lemma) {
						res.add( token + "\t" + pos + "\t" + lemma);
					}
				});
				tt.process(asList(query.split(" ")));
			}
			finally {
				tt.destroy();
			}
			for(String s : res){
				chaine += s+"\n";
			}
			chaine = split(chaine); //appel de la méthode pour récupérer uniquement le lemme de chaque mot de l'énoncé traité
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("OK");
		return chaine;
	}

	/**
	 * Récupération du lemme de chaque mot du tableau généré par TreeTagger
	 * @param treeTagger
	 * Tableau résultant du traitement de TreeTagger
	 * @return
	 * Programme en langage naturel lemmatisé
	 */
	public static String split (String treeTagger){
		//get lemma
		String []tokens = null;
		String []lignes = null;
		String retour = "";

		lignes = treeTagger.split("\n");
		for (String l : lignes)
		{
			tokens = l.split("\t");
			retour += tokens[2]+" "; //lemme
		}
		return retour;
	}
}