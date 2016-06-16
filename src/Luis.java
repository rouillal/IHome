import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.logging.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Analyse sémantique : compréhension du sens de la phrase et du sens des mots du programme exprimé en langage naturel par l'habitant 
 * préalablement lemmatisé et normalisé<br>
 * Service web LUIS : https://www.luis.ai
 * @author Lorrie Rouillaux
 */
public class Luis {

	/**
	 * Récupération du résultat de l'analyse sémantique sous forme de fichier JSon
	 * @param _url
	 * URL de connexion pour se connecter au projet LUIS
	 * @param request
	 * Programme exprimé en langage naturel libre par l'habitant préalablement lemmatisé et normalisé
	 */
	public static void getTextFile(String _url, String request) {
		BufferedReader reader = null;
		try {
			request = URLEncoder.encode(request, "UTF-8");
			_url = _url + request;
			URL url = new URL(_url);
			URLConnection urlConnection = url.openConnection();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			System.out.println("OK");
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			IHome.creerFichier(sb.toString(), "LUIS.json");
		} catch (IOException ex) {
			Logger.getLogger(Luis.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				Logger.getLogger(Luis.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Connexion au service web LUIS
	 * @param query
	 * Programme exprimé en langage naturel libre par l'habitant préalablement lemmatisé et normalisé
	 */
	public static void query(String query){
		IHome.log("Connexion au service web Luis...  ");
		String urlLuis = "https://api.projectoxford.ai/luis/v1/application?id=5c6c15ed-a991-4c0c-8ddf-162500394a0e&subscription-key=311fe610c71c496b90631c6d60ded1da&q=";
		Luis.getTextFile(urlLuis,query);
	}
	
	/**
	 * Méthode pour parser le fichier JSon retourné par l'analyseur sémantique LUIS
	 * @param s
	 * Nom du fichier JSon à parser
	 * @return
	 * Chaine de caractères avec l'identification du sens de la phrase et du sens des mots clefs pour l'habitat intelligent
	 */
	public static String fichParse(String s) {
		String retour = "";
		try {
			// read the json file
			FileReader reader = new FileReader(s);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
						
			// get intent
			JSONArray intent= (JSONArray) jsonObject.get("intents");
			JSONObject innerObj = (JSONObject) intent.get(0);
			retour += "intent "+ innerObj.get("intent") + "\n";
			
			// get entities
			JSONArray entities= (JSONArray) jsonObject.get("entities");
			Iterator<?> i = entities.iterator();
			while (i.hasNext()) {
				JSONObject innerObjE = (JSONObject) i.next();
				String e = innerObjE.get("entity").toString();
				String t = innerObjE.get("type").toString();
				retour += t +" "+ e + "\n";
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return retour;
	}
	
	/**
	 * Appel de la méthode pour parser le fichier JSon retourné par LUIS
	 * @return
	 * Chaine de caractères avec l'identification du sens de la phrase et du sens des mots clefs pour l'habitat intelligent
	 */
	public static String jsonParsing(){
		IHome.log("Parsing du fichier JSON...  ");
		String jsonParse = fichParse("LUIS.json");
		System.out.println("OK");
		System.out.println("***\n"+jsonParse+"***");
		return jsonParse;
	}
}