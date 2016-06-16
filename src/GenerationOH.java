import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fusion des connaissances des analyses syntaxique et sémantique 
 * et génération du code openHAB correspondant au programme formulé en langage naturel libre par l'habitant
 * @author Lorrie Rouillaux
 */
public class GenerationOH {

	private static String feedback; //retour utilisateur
	private static boolean ok; //booléen règle openHAB générée
	private static boolean errorFunction; //booléen fonction non comprise par le système
	private static boolean errorDevice; //booléen appareil non compris par le système
	private static boolean noDevice; //booléen donnée appareil manquante
	private static boolean noFunction; //booléen donnée fonction manquante
	private static boolean noTime; //booléen donnée temporelle manquante
	private static boolean errorTime; //booléen donnée temporelle non comprise par le système
	private static boolean bnone; //booléen pour intent None
	private static boolean baction; //booléen pour intent RegleAction
	private static boolean betat; //booléen pour intent regleEtat
	private static boolean btempo; //booléen pour règleTemporelle
	private static String tempsUser; //entrée utilisateur pour une donnée temporelle
	private static String serviceUser; //entrée utilisateur pour un appareil
	private static Device device; //appareil
	private static String functionUser; //entrée utilisateur pour une fonction d'un appareil

	/**
	 * Génération du code openHAB pour une règle temporelle
	 * @param device
	 * Appareil sur lequel porte une action à effectuer
	 * @param fonction
	 * Action à effectuer sur l'appareil
	 * @param time
	 * Donnée temporelle à laquelle s'exécute le programme
	 * @return
	 * Code openHAB correspondant au programme formulé en langage naturel libre par l'habitant pour une donnée temporelle<br>
	 * Case 1 : nom de la règle pour la création du fichier<br>
	 * Case 2 : corps de la règle
	 */
	public static String[][] regleTemporelle(String device, String fonction, String time){
		String retour[][] = new String [1][2];
		//construction de la règle OpenHab
		retour [0][0] = "rule"+device+fonction+time.replaceAll(" ", ""); //nom de la règle
		String chaine = "rule "+device+fonction+time.replaceAll(" ", "")+"\n";
		chaine += "when\n\tTime cron "+'"'+time+" * * ?"+'"'+"\n"; //évènement déclencheur (donnée temporelle)
		chaine += "then\n\tsendCommand("+device+","+fonction+")\n"; //action à effectuer
		chaine += "end";
		retour [0][1] = chaine; //corps de la règle
		return retour;
	}

	/**
	 * Génération du code openHAB pour une règle d'action
	 * @param todoDevice
	 * Appareil sur lequel on doit effectuer l'action
	 * @param whenDevice
	 * Appareil à partir duquel la fonction portée déclenche l'action à effectuer
	 * @param todoFunction
	 * Action à effectuer
	 * @param whenFunction
	 * Action à partir de laquelle l'action à réaliser se déclenche
	 * @return
	 * Code openHAB correspondant au programme formulé en langage naturel libre par l'habitant pour une action particulière<br>
	 * Case 1 : nom de la règle pour la création du fichier<br>
	 * Case 2 : corps de la règle
	 */
	public static String[][] regleAction(String todoDevice, String whenDevice, String todoFunction, String whenFunction){
		String retour[][] = new String [1][2];
		//construction de la règle OpenHab
		retour [0][0] = "rule"+todoDevice+todoFunction+"when"+whenDevice+whenFunction; //nom de la règle
		String chaine = "rule "+todoDevice+todoFunction+"when"+whenDevice+whenFunction+"\n";
		chaine += "when\n\tItem "+whenDevice+" changed to "+whenFunction+"\n"; //évènement déclencheur (action particulière)
		chaine += "then\n\tsendCommand("+todoDevice+","+todoFunction+")\n"; //action à effectuer
		chaine += "end";
		retour [0][1] = chaine; //corps de la règle
		return retour;
	}

	/**
	 * Génération du code openHAB pour une règle d'état
	 * @param todoDevice
	 * Appareil sur lequel on doit effectuer l'action
	 * @param whenDevice
	 * Appareil dont l'état déclenche l'action à effectuer
	 * @param todoFunction
	 * Action à effectuer
	 * @param whenEtat
	 * Etat dans lequel se trouve l'appareil et qui va déclencher l'action à effectuer
	 * @return
	 * Code openHAB correspondant au programme formulé en langage naturel libre par l'habitant pour un certain état du système<br>
	 * Case 1 : nom de la règle pour la création du fichier<br>
	 * Case 2 : corps de la règle
	 */
	public static String[][] regleEtat(String todoDevice, String whenDevice, String todoFunction, String whenEtat){
		String retour[][] = new String [1][2];
		//construction de la règle OpenHab
		retour [0][0] = "rule"+todoDevice+todoFunction+"whenEtat"+whenDevice+whenEtat; //nom de la règle
		String chaine = "rule "+todoDevice+todoFunction+"whenEtat"+whenDevice+whenEtat+"\n";
		chaine += "when\n\tItem "+whenDevice+" received update "+whenEtat+"\n"; //évènement déclencheur (un certain état du système)
		chaine += "then\n\tsendCommand("+todoDevice+","+todoFunction+")\n"; //action à effectuer
		chaine += "end";
		retour [0][1] = chaine; //corps de la règle
		return retour;
	}

	/**
	 * Méthode pour récupérer le sens du programme formulé par l'habitant en langage naturel libre (i.e. type de règle) et générer le code openHAB correspondant
	 * @param query
	 * Programme formulé par l'habitant en langage naturel libre
	 * @param luis
	 * Résultat de l'analyse sémantique
	 * @param S
	 * Liste des appareils de l'habitat
	 * @param depBonsai
	 * Résultat de l'analyse syntaxique
	 * @return
	 * Code openHAB généré en fonction du type de règle identifié
	 */
	public static String[][] toOpenHab(String query, String luis, Services S, String depBonsai[][]){
		IHome.log("Code OpenHab...\n");
		String oh[][] = new String[1][2]; //code openHAB avec le nom de la règle dans la première case et le corps de la règle dans la seconde
		String []tokens = null;
		String []lignes = null;
		bnone = false;
		btempo = false;
		baction = false;
		betat = false;
		lignes = luis.split("\n"); 

		for (String l : lignes){ //parcourir le résultat de l'analyse sémantique par luis
			tokens = l.split(" "); 
			if (tokens[0].toString().equals("intent")){ //récupérer le type de règle identifié
				if(tokens[1].toString().equals("RegleTemporelle")){
					oh = toOHregleTempo(query, luis, S);
					btempo = true;
				}
				if(tokens[1].toString().equals("RegleAction")){
					oh = toOHregleAction(query, luis, S, depBonsai);
					baction = true;
				}
				if(tokens[1].toString().equals("RegleEtat")){
					oh = toOHregleEtat(query, luis, S, depBonsai);
					betat = true;
				}
				if(tokens[1].toString().equals("None")){
					bnone = true;
					System.out.println(luis);
					ok = false; 
					feedback = "Je n'ai pas compris ce que signifie: "+'"'+query+'"'+".\nPouvez-vous reformuler s'il vous plait ?\n";
					System.out.println(feedback);
					oh = null;
				}
			}
		}
		return oh;
	}

	/**
	 * Fusion des analyses syntaxique et sémantique pour générer le code openHAB correspondant au programme de l'habitant formulé en langage naturel libre pour une règle temporelle
	 * @param query
	 * Programme formulé par l'habitant en langage naturel libre
	 * @param luis
	 * Résultat de l'analyse sémantique
	 * @param S
	 * Liste des appareils de l'habitat
	 * @return
	 * Code openHAB généré pour une règle temporelle
	 */
	public static String [][] toOHregleTempo(String query, String luis, Services S){
		String []tokens = null;
		String []lignes = null;
		String nomDevice = ""; //nom appareil compris par le système
		String fonction = ""; //fonction comprise par le système
		String time = null; //donnée temporelle comprise par le système
		boolean trouve = false;
		String oh[][] = new String [1][2]; //code openHAB avec le nom de la règle dans la première case et le corps de la règle dans la seconde
		device = null;
		noDevice = true;
		noTime = true;
		noFunction = true;
		errorFunction = false;
		errorDevice = false;
		errorTime = false;
		tempsUser = "";
		serviceUser = "";
		functionUser = "";
		lignes = luis.split("\n"); 

		for (String l : lignes){ //parcourir les services identifiés par l'analyse sémantique
			tokens = l.split(" "); 
			if (tokens[0].toString().equals("Service")){
				noDevice = false;
				for (Device s: S.getServices()){ //matcher le service identifié par l'analyse sémantique avec les variables appareils du système
					serviceUser = tokens[1];
					if(s.getNom().equals(serviceUser)){
						nomDevice = s.getNom();
						device = s;
						trouve = true;
						break;
					}
				}
				if(!trouve){ //pas de match, donc erreur
					errorDevice = true;
				}
			}
		}

		for (String l : lignes){ //parcourir les fonctions identifiées par l'analyse sémantique
			tokens = l.split(" "); 
			if (tokens[0].toString().equals("Fonctions")){
				noFunction = false;
				if (!noDevice && !errorDevice){ //si un service a été reconnu par le système
					trouve = false;
					for (String f : device.getFonctions()){
						functionUser = tokens[1];
						if(f.equals(functionUser)){ //matcher les fonctions connues par le système pour cet appareil avec la fonction identifiée par l'analyse sémantique
							//System.out.println(fonction);
							trouve = true;
							if (functionUser.equals("allumer")){
								fonction = "ON";
							}
							else if (functionUser.equals("éteindre")){
								fonction = "OFF";
							}
							else{
								errorFunction = true;
							}
							break;
						}
					}
					if(!trouve){ //pas de match donc erreur dans la fonction identifiée
						errorFunction = true;
					}
				}
				else if (noDevice || errorDevice){ //si une fonction a été identifiée par l'analyse sémantique mais que l'appareil n'est pas reconnu par le système
					functionUser = tokens[1];
					if (functionUser.equals("allumer")){
						fonction = "allumer";
					}
					else if (functionUser.equals("éteindre")){
						fonction = "éteindre";
					}
					else{
						errorFunction = true;
					}
				}
			}

			if (tokens[0].toString().equals("DateTime")){ //parcourir donnée temporelle identifiée par l'analyse sémantique
				noTime = false;
				tempsUser = tokens[1].toString();
				time = convertHour(tempsUser); //conversion de la date en format openHAB
				if (time == null) //échec de la conversion
					errorTime = true;
			}
		}

		if (!noDevice && !noFunction && !noTime && !errorDevice && !errorFunction && !errorTime){ //fusion des analyses syntaxique et sémantique réussie
			oh = GenerationOH.regleTemporelle(nomDevice, fonction, time); //appel de la méthode pour générer le code openHAB
			ok = true;
		}
		else //erreur dans la fusion des analyses syntaxique et sémantique
			ok = false;

		feedback = Dialogue.DialogueTempo(ok, noTime, errorTime, noDevice, errorDevice, noFunction, errorFunction, tempsUser, functionUser, serviceUser, fonction, device); //génération du feedback
		System.out.println(feedback);

		if (oh[0][1] != null && oh[0][0] != null){
			System.out.println("***\n"+oh[0][1]+"\n***");
			System.out.println("OK");
			return oh;
		}
		else
			return null;
	}

	/**
	 * Fusion des analyses syntaxique et sémantique pour générer le code openHAB correspondant au programme de l'habitant formulé en langage naturel libre pour une règle d'action
	 * @param query
	 * Programme formulé par l'habitant en langage naturel libre
	 * @param luis
	 * Résultat de l'analyse sémantique
	 * @param S
	 * Liste des appareils de l'habitat
	 * @param depBonsai
	 * Résultat de l'analyse syntaxique
	 * @return
	 * Code openHAB généré pour une règle d'action
	 */
	public static String [][] toOHregleAction(String query, String luis, Services S, String [][] depBonsai){
		String []tokens = null;
		String []lignes = null;
		ArrayList <Device> deviceTab = new ArrayList <Device>(); //liste des appareils identifiés par l'analyse sémantique
		ArrayList <String> functionTab = new ArrayList <String>(); //liste des fonctions identifiées par l'analyse sémantique
		ArrayList <Integer> dep = new ArrayList <Integer>(); //numéro fonction dépendante d'un service
		int cpt = 0;
		boolean trouve = false;
		String oh[][] = new String [1][2]; //code openHAB avec le nom de la règle dans la première case et le corps de la règle dans la seconde
		boolean okdep = false; //vérifier la récupération des relations de dépendances
		int index = -1;
		boolean okS = true; //vérifier donnée appareil non manquante
		boolean okF = true; //vérifier donnée fonction non manquante
		String todoFunction = ""; //action à effectuer
		Device todoDevice = null; //appareil sur lequel l'action doit être effectuée
		String whenFunction = ""; //fonction à partir de laquelle l'action va se déclencher
		Device whenDevice = null; //appareil dont la fonction déclenche l'action à effectuer

		lignes = luis.split("\n"); 
		if(depBonsai != null){
			for (String l : lignes){
				trouve = false;
				tokens = l.split(" "); 
				if (tokens[0].toString().equals("Service")){ //parcours des services identifiés par l'analyse sémantique
					for (int i = 0; i < depBonsai.length; i++){
						//récupère la fonction dépendante du device
						if(depBonsai[i][0].equals("lumier")) //erreur récurrente dans bonsai
							depBonsai[i][0] = "lumière";
						if(depBonsai[i][0].equals(tokens[1])){
							dep.add(Integer.parseInt(depBonsai[i][1])); //ajouter le numéro de la fonction dont dépend le service
							okdep = true;
							break;
						}
					}
					if(!okdep){
						System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- Service");
						okS = false;
					}
					else{
						for (Device s: S.getServices()){
							if(s.getNom().equals(tokens[1])){
								trouve = true;
								deviceTab.add(s); //ajouter l'appareil à la liste des appareils identifiés par l'analyse sémantique	et reconnus par le système				
							}
						}
						if(!trouve){
							System.err.println("internal error -- toOpenHab class GenerationOH device error");
							okS = false;
						}
					}
				}
			}

			if(!dep.isEmpty() && okdep && okS){
				for (String l : lignes){
					tokens = l.split(" ");
					if (tokens[0].toString().equals("Fonctions")){ //parcours des fonctions identifiées par l'analyse sémantique
						trouve = false; 
						okdep = false;
						//recuperer l'indice+1 de la fonction dans le tableau des dépendances
						for (int i = 0; i < depBonsai.length; i++){ 
							if(depBonsai[i][0].equals(tokens[1])){
								cpt = i+1;
								okdep = true;
								break;
							}
						}
						if(!okdep){
							System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- récupérer indice fonction dans depBonsai");
							okF = false;
						}
						else{
							trouve = false;
							//matcher l'indice de la fonction avec son numéro de dépendance dans l'arraylist dep et recupérer son index
							for (int depDevice : dep){ 
								if (depDevice == cpt){
									index = dep.indexOf(depDevice);
									trouve = true;
									break;
								}
							}
							if (!trouve){ //regarder les dépendances secondaires
								int depSec = Integer.parseInt(depBonsai[cpt-1][1]);
								for (int depDevice : dep){ 
									if (depDevice == depSec){
										index = dep.indexOf(depDevice);
										trouve = true;
										break;
									}
								}
							}
							if(!trouve){
								System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- matcher fonction/numéro dépendance");
								okF = false;
							}
							else{
								trouve = false;
								//matcher la fonction avec son device et ajouter l'ajouter dans la liste des fonctions identifiées par l'analyse sémantique et reconnues par le système
								for (String f : deviceTab.get(index).getFonctions()){ 
									if(f.equals(tokens[1])){
										trouve = true;
										if (f.equals("allumer")){
											functionTab.add(f);
										}
										else if (f.equals("éteindre")){
											functionTab.add(f);
										}
										else{
											System.err.println("internal error -- toOpenHab class GenerationOH function setEtat && ON/OFF error");
											okF = false;
										}
									}
								}
								if(!trouve){
									System.err.println("internal error -- toOpenHab class GenerationOH function error");
									okF = false;
								}
							}
						}
					}
				}
				if (okF){
					//chercher la fonction à réaliser (root)
					trouve = false;
					for (int i = 0; i < depBonsai.length; i++){
						if (depBonsai[i][1].equals("0") && !depBonsai[i][0].equals("falloir") && !depBonsai[i][0].equals("devoir")){
							todoFunction = depBonsai[i][0];
							index = i + 1;
							trouve = true;
						}
						else if (depBonsai[i][1].equals("0")){ //chercher dans les dépendances secondaires (falloir, devoir)
							for (int j = 0; j < depBonsai.length; j++){
								if (Integer.parseInt(depBonsai[j][1]) == i+1){
									for (String fun : functionTab){
										if (fun.equals(depBonsai[j][0])){
											todoFunction = depBonsai[j][0];
											index = i + 1;
											trouve = true;
										}
									}
								}
							}
						}
					}
					if(!trouve){
						System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher fonction root");
					}
					else{
						//chercher le device qui a la fonction root
						trouve = false;
						for (int de: dep){
							if (de == index){
								todoDevice = deviceTab.get(dep.indexOf(de));
								trouve = true;
							}
						}
						if(!trouve){
							System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher device root");
						}
						else{
							trouve = false;
							//chercher la fonction évènement
							for (int i=0; i< depBonsai.length; i++){
								if (depBonsai[i][0].equals("quand") || depBonsai[i][0].equals("lorsque") || depBonsai[i][0].equals("si")){
									cpt = i + 1;
									trouve = true;
								}
							}
							if(!trouve){
								System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher when");
							}
							else{
								for (int i=0; i< depBonsai.length; i++){
									if (Integer.parseInt(depBonsai[i][1]) == cpt){
										whenFunction = depBonsai[i][0];
										index = i +1;
									}
								}
								if(!trouve){
									System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher function when");
								}
								else{
									//chercher le device qui a la fonction évènement
									trouve = false;
									for (int de: dep){
										if (de == index){
											whenDevice = deviceTab.get(dep.indexOf(de));
											trouve = true;
										}
									}
									if(!trouve){
										System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher device when");
									}
								}
							}
						}
					}
					//construction de la règle action en openHab 
					if (trouve && todoDevice.getNom() != "" && whenDevice.getNom() != "" && (todoFunction.equals("allumer") 
							|| todoFunction.equals("éteindre")) && (whenFunction.equals("allumer") || whenFunction.equals("éteindre"))){ //si fusion des analyses réussie
						ok = true;
						feedback = "Vous avez réussi à programmer un automatisme !\n";
						feedback += "Je dois "+todoFunction+" l'appareil "+todoDevice.getNom()+" si vous "+whenFunction+" l'appareil "+whenDevice.getNom()+".\n";
						System.out.println(feedback);

						if (todoFunction.equals("allumer"))
							todoFunction = "ON";
						else if(todoFunction.equals("éteindre"))
							todoFunction = "OFF";
						if (whenFunction.equals("allumer"))
							whenFunction = "ON";
						else if(whenFunction.equals("éteindre"))
							whenFunction = "OFF";
						oh = GenerationOH.regleAction(todoDevice.getNom(), whenDevice.getNom(), todoFunction, whenFunction); //appel de la méthode générant le code openHAB d'une règle d'action
					}
					else{
						ok = false;
					}
				}
				else{
					System.err.println("internal error -- toOpenHab class GenerationOH error -- function empty");
					ok = false;
				}
			}
			else{
				System.err.println("internal error -- toOpenHab class GenerationOH error -- array of device dependencies empty");
				ok = false;
			}
		}
		else{
			System.err.println("internal error -- toOpenHab class GenerationOH error -- tableau des dépendances nul");
			ok = false;
		}
		if (!ok){ //erreur de fusion des analyses
			feedback = "Je n'ai pas compris ce que signifie: "+'"'+query+'"'+".\nPouvez-vous reformuler s'il vous plait ?\n";
			System.out.println(feedback);
		}

		if (oh[0][1] != null && oh[0][0] != null){
			System.out.println("***\n"+oh[0][1]+"\n***");
			System.out.println("OK");
			return oh;
		}
		else{
			return null;
		}
	}

	/**
	 * Fusion des analyses syntaxique et sémantique pour générer le code openHAB correspondant au programme de l'habitant formulé en langage naturel libre pour une règle d'état
	 * @param query
	 * Programme formulé par l'habitant en langage naturel libre
	 * @param luis
	 * Résultat de l'analyse sémantique
	 * @param S
	 * Liste des appareils de l'habitat
	 * @param depBonsai
	 * Résultat de l'analyse syntaxique
	 * @return
	 * Code openHAB généré pour une règle état
	 */
	public static String [][] toOHregleEtat(String query, String luis, Services S, String [][] depBonsai){
		String []tokens = null;
		String []lignes = null;
		ArrayList <Device> deviceTab = new ArrayList <Device>(); //liste des appareils identifiés par l'analyse sémantique
		ArrayList <String> functionTab = new ArrayList <String>(); //liste des fonctions identifiées par l'analyse sémantique
		ArrayList <Integer> dep = new ArrayList <Integer>(); //numéro fonction dépendante d'un service
		ArrayList <String> etatTab = new ArrayList <String>(); //liste des états identifiés par l'analyse sémantique
		etatTab.add("null");etatTab.add("null");
		int cpt = 0;
		boolean trouve = false;
		String oh[][] = new String [1][2]; //code openHAB avec le nom de la règle dans la première case et le corps de la règle dans la seconde
		boolean okdep = false; //vérifier la récupération des relations de dépendances
		int index = -1;
		String todoFunction = ""; //action à effectuer
		Device todoDevice = null; //appareil sur lequel l'action doit être effectuée
		Device whenDevice = null; //appareil dont la fonction déclenche l'action à effectuer
		String whenEtat = ""; //état d'un appareil qui va déclencher l'action à effectuer
		boolean okS = true; //vérifier donnée appareil non manquante
		boolean okF = true; //vérifier donnée fonction non manquante
		boolean okE = true; //vérifier donnée état non  manquante

		lignes = luis.split("\n");
		if(depBonsai != null){
			for (String l : lignes){
				trouve = false;
				tokens = l.split(" "); 
				if (tokens[0].toString().equals("Service")){ //parcourir les services identifiés par l'analyse sémantique
					for (int i = 0; i < depBonsai.length; i++){
						//récupère la fonction dépendante du device
						if(depBonsai[i][0].equals("lumier"))
							depBonsai[i][0] = "lumière";
						if(depBonsai[i][0].equals(tokens[1])){
							dep.add(Integer.parseInt(depBonsai[i][1])); //ajoute le numéro de la fonction dont l'appareil dépend
							okdep = true;
							break;
						}
					}
					if(!okdep){
						System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- Service");
						okS = false;
					}
					else{
						for (Device s: S.getServices()){
							if(s.getNom().equals(tokens[1])){ //matcher le service identifié par l'analyse sémantique avec une variable appareil du système
								trouve = true;
								deviceTab.add(s); //ajout de l'appareil à la liste des appareils reconnues par le système					
							}
						}
						if(!trouve){
							System.err.println("internal error -- toOpenHab class GenerationOH device error");
							okS = false;
						}
					}
				}
			}
			if(!dep.isEmpty() && okdep && okS){
				for (String l : lignes){
					tokens = l.split(" ");
					if (tokens[0].toString().equals("Fonctions")){ //parcourir les fonctions identifiées par l'analyse sémantique
						trouve = false; 
						okdep = false;
						//recuperer l'indice+1 de la fonction dans le tableau des dépendances
						for (int i = 0; i < depBonsai.length; i++){ 
							if(depBonsai[i][0].equals(tokens[1])){
								cpt = i+1;
								okdep = true;
								break;
							}
						}
						if(!okdep){
							System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- récupérer indice fonction dans depTab");
							okF = false;
						}
						else{
							trouve = false;
							//matcher l'indice de la fonction avec son numéro de dépendance dans l'arraylist dep et recupérer son index
							for (int depDevice : dep){ 
								if (depDevice == cpt){
									index = dep.indexOf(depDevice);
									trouve = true;
									break;
								}
							}
							if (!trouve){ //chercher dans les dépendances secondaires
								int depSec = Integer.parseInt(depBonsai[cpt-1][1]);
								for (int depDevice : dep){ 
									if (depDevice == depSec){
										index = dep.indexOf(depDevice);
										trouve = true;
										break;
									}
								}
							}
							if(!trouve){
								System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- matcher fonction/numéro dépendance");
								okF = false;
							}
							else {
								trouve = false;
								//matcher la fonction avec son device et l'ajouter à la liste des fonctions identifiées par l'analyse sémantique et reconnues par le système
								for (String f : deviceTab.get(index).getFonctions()){ 
									if(f.equals(tokens[1])){
										trouve = true;
										if (f.equals("allumer")){
											//f = "ON";
											functionTab.add(f);
										}
										else if (f.equals("éteindre")){
											//f = "OFF";
											functionTab.add(f);
										}
										else{
											System.err.println("internal error -- toOpenHab class GenerationOH function setEtat error");
											okF = false;
										}
									}
								}
								if(!trouve){
									System.err.println("internal error -- toOpenHab class GenerationOH function error");
									okF = false;
								}
							}
						}
					}
				}
				if (okF){
					for (String l : lignes){
						tokens = l.split(" ");
						if (tokens[0].toString().equals("Etat")){ //parcourir les états identifiés par l'analyse sémantique
							trouve = false; 
							okdep = false;
							//recuperer l'indice+1 de l'état dans le tableau des dépendances
							for (int i = 0; i < depBonsai.length; i++){ 
								if(depBonsai[i][0].equals(tokens[2])){
									cpt = i+1;
									okdep = true;
									break;
								}
							}
							if(!okdep){
								System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- récupérer indice etat dans depTab");
								okE = false;
							}
							else {
								trouve = false;
								//matcher l'indice de l'état avec son numéro de dépendance dans l'arraylist dep et recupérer son index
								for (int depDevice : dep){ 
									if (depDevice == cpt){
										index = dep.indexOf(depDevice);
										trouve = true;
										break;
									}
								}
								if(!trouve){
									System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- matcher etat/numéro dépendance");
									okE = false;
								}
								else {
									trouve = false;
									//matcher l'état avec son device
									if (tokens[2].equals("allumer")){
										trouve = true;
									}
									else if (tokens[2].equals("éteindre")){
										trouve = true;
									}
									if (trouve){
										etatTab.add(index, tokens[2]); //ajouter l'état à la liste des états identifiés par l'analyse sémantique et reconnus par le système
									}
									else{
										System.err.println("internal error -- toOpenHab class GenerationOH état error --regleEtat");
										okE = false;
									}
								}
							}
						}
					}
					if (okE){
						//chercher la fonction à réaliser (root)
						trouve = false;
						for (int i = 0; i < depBonsai.length; i++){
							if (depBonsai[i][1].equals("0") && !depBonsai[i][0].equals("falloir") && !depBonsai[i][0].equals("devoir")){
								todoFunction = depBonsai[i][0];
								index = i + 1;
								trouve = true;
							}
							else if (depBonsai[i][1].equals("0")){ //chercher dans les dépendances secondaires
								for (int j = 0; j < depBonsai.length; j++){
									if (Integer.parseInt(depBonsai[j][1]) == i+1){
										for (String fun : functionTab){
											if (fun.equals(depBonsai[j][0])){
												todoFunction = depBonsai[j][0];
												index = i + 1;
												trouve = true;
											}
										}
									}
								}
							}
						}
						if(!trouve){
							System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher fonction root");
						}
						else {
							//chercher le device qui a la fonction root
							trouve = false;
							for (int de: dep){
								if (de == index){
									todoDevice = deviceTab.get(dep.indexOf(de));
									trouve = true;
								}
							}
							if(!trouve){
								System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher device root");
								//System.exit(1);
							}
							else {
								trouve = false;
								//chercher l'état évènement
								for (int i=0; i< depBonsai.length; i++){
									if (depBonsai[i][0].equals("quand") || depBonsai[i][0].equals("lorsque") || depBonsai[i][0].equals("si")){
										cpt = i + 1;
										trouve = true;
									}
								}
								if(!trouve){
									System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher when");
								}
								else{
									for (int i=0; i< depBonsai.length; i++){
										if (Integer.parseInt(depBonsai[i][1]) == cpt){
											whenEtat = depBonsai[i][0];
											index = i +1;
										}
									}
									if(!trouve){
										System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher function when");
									}
									else{
										//chercher le device qui a l'état évènement
										trouve = false;
										for (int de: dep){
											if (de == index){
												whenDevice = deviceTab.get(dep.indexOf(de));
												trouve = true;
											}
										}
										if(!trouve){
											System.err.println("internal error -- toOpenHab class GenerationOH dependencies error -- chercher device when");
										}
									}
								}
							}
						}
						//construction de la règle etat en openHab 
						if (trouve && todoDevice.getNom() != "" && whenDevice.getNom() != "" && (todoFunction.equals("allumer") //fusion des analyses syntaxique et sémantique réussie
								|| todoFunction.equals("éteindre")) && (whenEtat.equals("allumer") || whenEtat.equals("éteindre"))){
							if (todoFunction.equals("allumer"))
								todoFunction = "ON";
							else if(todoFunction.equals("éteindre"))
								todoFunction = "OFF";
							if (whenEtat.equals("allumer"))
								whenEtat = "ON";
							else if(whenEtat.equals("éteindre"))
								whenEtat = "OFF";
							ok = true;

							feedback = "Vous avez réussi à programmer un automatisme!\n";
							feedback += "Je dois "+todoFunction+" l'appareil "+todoDevice.getNom()+" si l'appareil "+whenDevice.getNom()+" est "+whenEtat+"\n";
							System.out.println(feedback);
							oh = GenerationOH.regleEtat(todoDevice.getNom(), whenDevice.getNom(), todoFunction, whenEtat); //génération du code openHAB pour une règle d'état
						}
						else{
							System.err.println("internal error -- toOpenHab class GenerationOH Regle etat error");
							ok = false;
						}
					}
					else {
						System.err.println("internal error -- toOpenHab class GenerationOH error -- state empty");
						ok = false;
					}
				}
				else {
					System.err.println("internal error -- toOpenHab class GenerationOH error -- function empty");
					ok = false;
				}
			}
			else{
				System.err.println("internal error -- toOpenHab class GenerationOH error -- array of device dependencies empty");
				ok = false;
			}
		}
		else{
			System.err.println("internal error -- toOpenHab class GenerationOH error -- tableau des dépendances nul");
			ok =false;
		}
		if (!ok){
			feedback = "Je n'ai pas compris ce que signifie: "+'"'+query+'"'+".\nPouvez-vous reformuler s'il vous plait ?\n";
			System.out.println(feedback);
		}
		if (oh[0][1] != null && oh[0][0] != null){
			System.out.println("***\n"+oh[0][1]+"\n***");
			System.out.println("OK");
			return oh;
		}
		else{
			return null;
		}
	}

	/**
	 * Méthode pour formaliser en langage openHAB la donnée temporelle formulée par l'habitant en langage naturel libre
	 * @param time
	 * Donnée temporelle formulée par l'habitant en langage naturel libre et identifiée par l'analyse sémantique
	 * @return
	 * Donnée temporelle formalisée en langage openHAB
	 */
	public static String convertHour(String time){
		Pattern patternH;
		Matcher matcherH;
		Pattern patternHM;
		Matcher matcherHM;
		Pattern patternHMS;
		Matcher matcherHMS;

		String heure =null;
		String min =null;
		String sec =null;

		if(time.equals("midi"))
			time = "12h";
		else if(time.equals("minuit"))
			time = "0h";

		String patternHeure = "^(2[0-3]|[01]?[0-9])(\\s)*(:|h(eure)?s?)$"; // pattern heure
		String patternHeureMin = "^(2[0-3]|[01]?[0-9])(\\s)*(:|h(eure)?s?)([0-5]?[0-9])(\\s)*(:|m(n|in|inute)?s?)?$"; // pattern heure-minute
		String patternHeureMinS = "^(2[0-3]|[01]?[0-9])(\\s)*(:|h(eure)?s?)([0-5]?[0-9])(\\s)*(:|m(n|in|inute(s)?)?)?([0-5]?[0-9])(\\s)*(s(ec|econde(s)?)?)?$"; // pattern heure-minute-sec

		//expression heure de type HH
		patternH = Pattern.compile(patternHeure);
		matcherH = patternH.matcher(time); 
		boolean bH = matcherH.matches();

		//expression heure de type HHMM
		patternHM = Pattern.compile(patternHeureMin);
		matcherHM = patternHM.matcher(time); 
		boolean bHM = matcherHM.matches();

		//expression heure de type HHMMSS
		patternHMS = Pattern.compile(patternHeureMinS);
		matcherHMS = patternHMS.matcher(time); 
		boolean bHMS = matcherHMS.matches();


		if(bH) { // si recherche du pattern HH fructueuse
			heure = matcherH.group(1);
			min = "0";
			sec = "0";
		}
		else if(bHM){ // si recherche du pattern HHMM fructueuse
			heure = matcherHM.group(1);
			min = matcherHM.group(5);
			sec = "0";
		}
		else if(bHMS){ // si recherche du pattern HHMMSS fructueuse
			heure = matcherHMS.group(1);
			min = matcherHMS.group(5);
			sec = matcherHMS.group(10);
		}

		if (heure != null && min != null && sec != null)
			return sec+" "+min+" "+heure;
		else
			return null;
	}

	/**
	 * Getter retour utilisateur
	 * @return
	 * Retour utilisateur
	 */
	public static String getFeedback(){
		return feedback;
	}

	/**
	 * Getter code openHAB réussi
	 * @return
	 * Booléen réussite de la génération de code openHAB
	 */
	public static boolean getOk(){
		return ok;
	}

	/**
	 * Getter identification par l'analyse sémantique et reconnaissance par le système de la fonction
	 * @return
	 * Booléen reconnaissance de la fonction par le système
	 */
	public static boolean getErrorFunction(){
		return errorFunction;
	}

	/**
	 * Getter identification par l'analyse sémantique et reconnaissance par le système de l'appareil
	 * @return
	 * Booléen reconnaissance de l'appareil par le système
	 */
	public static boolean getErrorDevice(){
		return errorDevice;
	}

	/**
	 * Getter identification par l'analyse sémantique de l'appareil
	 * @return
	 * Booléen reconnaissance de l'appareil par l'analyse sémantique
	 */
	public static boolean getNoService(){
		return noDevice;
	}

	/**
	 * Getter identification par l'analyse sémantique de la fonction
	 * @return
	 * Booléen reconnaissance de la fonction par l'analyse sémantique
	 */
	public static boolean getNoFunction(){
		return noFunction;
	}

	/**
	 * Getter identification par l'analyse sémantique de la donnée temporelle
	 * @return
	 * Booléen reconnaissance de la donnée temporelle par l'analyse sémantique
	 */
	public static boolean getNoTime(){
		return noTime;
	}

	/**
	 * Getter identification par l'analyse sémantique et reconnaissance par le système de la donnée temporelle
	 * @return
	 * Booléen reconnaissance de la donnée temporelle par le système
	 */
	public static boolean getErrorTime(){
		return errorTime;
	}

	/**
	 * Getter formulation de la donnée temporelle par l'habitant en langage naturel
	 * @return
	 * Donnée temporelle formulée par l'habitant en langage naturel
	 */
	public static String getTempsUser(){
		return tempsUser;
	}

	/**
	 * Getter formulation de la fonction d'un appareil par l'habitant en langage naturel
	 * @return
	 * Fonction d'un appareil formulée par l'habitant en langage naturel
	 */
	public static String getFunctionUser(){
		return functionUser;
	}

	/**
	 * Getter formulation d'un appareil par l'habitant en langage naturel
	 * @return
	 * Appareil formulée par l'habitant en langage naturel
	 */
	public static String getServiceUser(){
		return serviceUser;
	}

	/**
	 * Getter appareil identifié ou non par le système
	 * @return
	 * Appareil identifié ou non par le système
	 */
	public static Device getDevice(){
		return device;
	}

	/**
	 * Getter type de règle non identifié
	 * @return
	 * Booléen type de règle non identifié
	 */
	public static boolean getIntentNone(){
		return bnone;
	}

	/**
	 * Getter type de règle d'action
	 * @return
	 * Booléen type de règle d'action
	 */
	public static boolean getIntentAction(){
		return baction;
	}

	/**
	 * Getter type de règle d'état
	 * @return
	 * Booléen type de règle d'état
	 */
	public static boolean getIntentEtat(){
		return betat;
	}

	/**
	 * Getter type de règle temporelle
	 * @return
	 * Booléen type de règle temporelle
	 */
	public static boolean getIntentTempo(){
		return btempo;
	}	
}