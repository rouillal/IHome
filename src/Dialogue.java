/**
 * Méthode pour générer le feedback
 * @author Lorrie Rouillaux
 */
public class Dialogue {

	public static String DialogueTempo (boolean ok, boolean noTime, boolean errorTime, boolean noService, boolean errorDevice, boolean noFunction, boolean errorFunction, String temps, String function, String service, String fonction, Device d){
		String feedback;
		if (ok){ //si génération de code openHAB réussie
			feedback = "Vous avez réussi à programmer un automatisme !\n";
			feedback += "A "+temps+", je dois "+function+" l'appareil "+service+".";
		}
		else{ //si erreur dans la génération de code openHAB
			if (!noTime && !errorTime){ //temps reconnu
				feedback ="J'ai bien reconnu que vous vouliez programmer un automatisme pour une donnée temporelle, à "+temps+".\n";
				if (noService){ //pas d'appareil reconnu dans LUIS
					if (noFunction){ //pas de fonction reconnue dans LUIS
						feedback += "Je n'ai pas reconnu l'appareil et la fonction à programmer.\n";
					}
					else if (errorFunction){ //erreur dans la fonction
						feedback += "Je n'ai pas reconnu d'appareil à programmer et que signifie "+function+" ?\n";
					}
					else { //fonction reconnue
						feedback +="Quel appareil voulez-vous "+fonction+" ?\n";
					}
				}
				else { //service reconnu par LUIS
					if (errorDevice){ //appareil non reconnu par le système
						if (noFunction){ //pas de fonction reconnue dans LUIS
							feedback +="Je n'ai pas reconnu l'appareil "+service+ " et je n'ai pas reconnu de fonction.\n";
						}
						else if (errorFunction){ //erreur dans la fonction
							feedback +="Que signifie "+service+" ?\nQue signifie "+function+ " ?\n";
						}
						else { //fonction reconnue par le système
							feedback +="Vous voulez "+fonction+" l'appareil "+service+", quel est l'appareil "+service+" ?\n";
						}
					}
					else { //appareil reconnu par le système
						if (noFunction){ //pas de fonction reconnue dans LUIS
							feedback +="Je n'ai pas compris ce que vous vouliez programmer sur l'appareil "+d.getNom()+".\n";
						}
						else if (errorFunction){ //erreur dans la fonction
							feedback +="Je n'ai pas compris ce que vous vouliez programmer sur l'appareil "+d.getNom()+".\nQue signifie "+function+" ?\n";
						}
					}
				}
			}
			else if(!noTime && errorTime){ //donnée temporelle non reconnue
				feedback = "J'ai bien reconnu que vous vouliez programmer un automatisme pour une donnée temporelle, mais je n'ai pas compris quand.\n";
				feedback += "Que signifie "+temps+" ?\n";
				if (noService){ //pas d'appareil reconnu dans LUIS
					if (noFunction){ //pas de fonction reconnue dans LUIS
						feedback +="Je n'ai pas reconnu l'appareil et la fonction à programmer.\n";
					}
					else if (errorFunction){ //erreur dans la fonction
						feedback += "Je n'ai pas reconnu d'appareil à programmer et que signifie "+function+" ?\n";
					}
					else { //fonction reconnue
						feedback += "Quel appareil voulez-vous "+fonction+" ?\n";
					}
				}
				else { //service reconnu par LUIS
					if (errorDevice){ //appareil non reconnu par le système
						if (noFunction){ //pas de fonction reconnue dans LUIS
							feedback += "Je n'ai pas reconnu l'appareil "+service+ " et je n'ai pas reconnu de fonction.\n";
						}
						else if (errorFunction){ //erreur dans la fonction
							feedback += "Que signifie "+service+" ?\nQue signifie "+function+ " ?\n";
						}
						else { //fonction reconnue par le système
							feedback +="Vous voulez "+fonction+" l'appareil "+service+", quel est l'appareil "+service+" ?\n";
						}
					}
					else { //appareil reconnu par le système
						if (noFunction){ //pas de fonction reconnue dans LUIS
							feedback += "Je n'ai pas compris ce que vous vouliez programmer sur l'appareil "+d.getNom()+".\n";
						}
						else if (errorFunction){ //erreur dans la fonction
							feedback += "Je n'ai pas compris ce que vous vouliez programmer sur l'appareil "+d.getNom()+".\nQue signifie "+function+" ?\n";
						}
						else{
							feedback += "J'ai bien compris que vous vouliez "+function+" l'appareil "+d.getNom()+".\n";
						}
					}
				}
			}
			else{ //pas de donnée temporelle
				feedback = "J'ai bien reconnu que vous vouliez programmer un automatisme pour une donnée temporelle, mais je n'ai pas compris quand.\n";
				if (noService){ //pas d'appareil reconnu dans LUIS
					if (noFunction){ //pas de fonction reconnue dans LUIS
						feedback += "Je n'ai pas reconnu l'appareil et la fonction à programmer.\n";
					}
					else if (errorFunction){ //erreur dans la fonction
						feedback += "Je n'ai pas reconnu d'appareil à programmer et que signifie "+function+" ?\n";
					}
					else { //fonction reconnue
						feedback += "Quel appareil voulez-vous "+fonction+" ?\n";
					}
				}
				else { //service reconnu par LUIS
					if (errorDevice){ //appareil non reconnu par le système
						if (noFunction){ //pas de fonction reconnue dans LUIS
							feedback += "Je n'ai pas reconnu l'appareil "+service+ " et je n'ai pas reconnu de fonction.\n";
						}
						else if (errorFunction){ //erreur dans la fonction
							feedback += "Que signifie "+service+" ?\nQue signifie "+function+ " ?\n";
						}
						else { //fonction reconnue par le système
							feedback += "Vous voulez "+fonction+" l'appareil "+service+", quel est l'appareil "+service+" ?\n";
						}
					}
					else { //appareil reconnu par le système
						if (noFunction){ //pas de fonction reconnue dans LUIS
							feedback += "Je n'ai pas compris ce que vous vouliez programmer sur l'appareil "+d.getNom()+".\n";
						}
						else if (errorFunction){ //erreur dans la fonction
							feedback += "Je n'ai pas compris ce que vous vouliez programmer sur l'appareil "+d.getNom()+".\nQue signifie "+function+" ?\n";
						}
						else{
							feedback += "J'ai bien compris que vous vouliez "+function+" l'appareil "+d.getNom()+".\n";
						}
					}
				}	
			}
			feedback += "Pouvez-vous reformuler s'il vous plait ?\n";
		}
		return feedback;
	}
}