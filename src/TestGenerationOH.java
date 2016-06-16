import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests unitaires JUnit <br>
 * Quand il est 20h30 alors allume le  ventilo<br>
 * éteins la lumière à midi<br>
 * A 9h30 alors allume le  ventilo<br>
 * éteins la lampe quand il est minuit<br>
 * Met en route le ventilo à 20H30<br>
 * Quand j' allume la tv, éteins la lumière<br>
 * Quand j' éteins le ventilateur, éteins la télé<br>
 * Eteins la télé quand j'allume la lumière<br>
 * Allume le ventilo lorsque j'allume la lumière<br>
 * la lumière doit être éteinte quand j'allume la télé<br>
 * la lumière doit être éteinte lorsque j'allume la télé<br>
 * quand j'arrêtes le ventilo alors met en route la tv<br>
 * si je cesse de faire fonctionner la télévision alors il faut que tu fasses marcher la lampe<br>
 * si je fais fonctionner la télévision, le ventilateur doit fonctionner<br>
 * Si je fais fonctionner la télé alors ne pas mettre en fonctionnement la lampe<br>
 * Quand la lampe est allumée allume le ventilo<br>
 * éteins le ventilo si la lampe est allumée<br>
 * il faut éteindre la lumière quand la télévision est allumée<br>
 * la lumière doit être éteinte quand la télévision est allumée<br>
 * la lumière doit être éteinte quand la télévision est en marche<br>
 * quand la tv fonctionne éteins la lumière<br>
 * quand la tv est déconnectée allume la lumière<br>
 * quand la tv ne marche pas met en route le ventilateur<br>
 * quand la tv n'est pas en route met en route le ventilateur<br>
 * il faut que tu fasses marcher la lumière quand la tv ne fonctionne pas<br>
 * quand la tv est en fonctionnement il faut que tu cesses de faire fonctionner le ventilo<br>
 * allume la lumière quand la tv est déconnectée<br>
 * il faut démarrer le ventilo si la télé n'est pas en marche<br>
 * si la tv n'est pas éteinte alors éteins la lumière<br>
 * si la tv n'est pas éteinte alors n'allume pas la lumière<br>
 * ne mets pas en route la tv quand le ventilo n'est pas arrêté<br>
 *
 * @author Lorrie Rouillaux
 */
public class TestGenerationOH {
	
	public String modeleTest (String query){
		String oh[][] = new String [1][2];
		Services S = new Services(); //liste des appareils de l'habitat
		Device.createDevice(S); //création des appareils
		//analyse lexicale (TreeTagger)
		query = TreeTagger.toLemma(query);
		System.out.println("***\nRequête lemmatisée: "+query+"\n***");
		//Normalisation
		String queryNormalise = Normalisation.normalisation(query);
		System.out.println("***\nRequête normalisée: "+queryNormalise+"\n***");
		//création du fichier requête
		String queryBonsai = Bonsai.preTraitement(queryNormalise);
		System.out.println(queryBonsai);
		IHome.creerFichier(queryBonsai, "bonsai/query.txt");
		//analyse syntaxique (appel du script.sh pour créer les dépendances) Bonsai
		Bonsai.callScriptBonsai();
		String dep[][] = Bonsai.parseBonsai();
		//analyse sémantique (connexion au web service Luis et création du fichier json)
		Luis.query(queryNormalise);
		//generation de code openHAB après parsing du fichier json
		oh = GenerationOH.toOpenHab(query, Luis.jsonParsing(), S, dep);
		if (oh != null)
			return oh[0][1];
		else
			return null;
	}

	@Test
	public void testRegleTemporelle1() {
		String query= "Quand il est 20h30 alors allume le  ventilo";
		String res = modeleTest(query);
		String resAttendu = "rule "+"ventilateurON03020"+"\nwhen\n\tTime cron "+'"'+"0 30 20 * * ?"+'"'+"\nthen\n\tsendCommand(ventilateur,ON)\nend";
		assertEquals(res, resAttendu);
	}
	
	@Test
	public void testRegleTemporelle2() {
		String query= "éteins la lumière à midi";
		String res = modeleTest(query);
		String resAttendu = "rule "+"lampeOFF0012"+"\nwhen\n\tTime cron "+'"'+"0 0 12 * * ?"+'"'+"\nthen\n\tsendCommand(lampe,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleTemporelle3() {
		String query= "A 9h30 alors allume le  ventilo";
		String res = modeleTest(query);
		String resAttendu = "rule "+"ventilateurON0309"+"\nwhen\n\tTime cron "+'"'+"0 30 9 * * ?"+'"'+"\nthen\n\tsendCommand(ventilateur,ON)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleTemporelle4() {
		String query= "éteins la lampe quand il est minuit";
		String res = modeleTest(query);
		String resAttendu = "rule "+"lampeOFF000"+"\nwhen\n\tTime cron "+'"'+"0 0 0 * * ?"+'"'+"\nthen\n\tsendCommand(lampe,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleTemporelle5() {
		String query= "Met en route le ventilo à 20H30"; 
		String res = modeleTest(query);
		String resAttendu = "rule "+"ventilateurON03020"+"\nwhen\n\tTime cron "+'"'+"0 30 20 * * ?"+'"'+"\nthen\n\tsendCommand(ventilateur,ON)\nend";
		assertEquals(res, resAttendu);
	}
	
	@Test
	public void testRegleAction1() {
		String query = "Quand j' allume la tv, éteins la lumière";
		String res = modeleTest(query);
		String resAttendu = "rule "+"lampeOFFwhentvON"+"\nwhen\n\tItem tv changed to ON\nthen\n\tsendCommand(lampe,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction2() {
		String query = "Quand j' éteins le ventilateur, éteins la télé";
		String res = modeleTest(query);
		String resAttendu = "rule "+"tvOFFwhenventilateurOFF"+"\nwhen\n\tItem ventilateur changed to OFF\nthen\n\tsendCommand(tv,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction3() {
		String query = "Eteins la télé quand j'allume la lumière";
		String res = modeleTest(query);
		String resAttendu = "rule "+"tvOFFwhenlampeON"+"\nwhen\n\tItem lampe changed to ON\nthen\n\tsendCommand(tv,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction4() {
		String query = "Allume le ventilo lorsque j'allume la lumière";
		String res = modeleTest(query);
		String resAttendu = "rule "+"ventilateurONwhenlampeON"+"\nwhen\n\tItem lampe changed to ON\nthen\n\tsendCommand(ventilateur,ON)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction5() {
		String query = "la lumière doit être éteinte quand j'allume la télé";
		String res = modeleTest(query);
		String resAttendu = "rule "+"lampeOFFwhentvON"+"\nwhen\n\tItem tv changed to ON\nthen\n\tsendCommand(lampe,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction6() {
		String query = "la lumière doit être éteinte lorsque j'allume la télé ";
		String res = modeleTest(query);
		String resAttendu = "rule "+"lampeOFFwhentvON"+"\nwhen\n\tItem tv changed to ON\nthen\n\tsendCommand(lampe,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction7() {
		String query = "quand j'arrêtes le ventilo alors met en route la tv";
		String res = modeleTest(query);
		String resAttendu = "rule "+"tvONwhenventilateurOFF"+"\nwhen\n\tItem ventilateur changed to OFF\nthen\n\tsendCommand(tv,ON)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction8() {
		String query = "si je cesse de faire fonctionner la télévision alors il faut que tu fasses marcher la lampe";
		String res = modeleTest(query);
		String resAttendu = "rule "+"lampeONwhentvOFF"+"\nwhen\n\tItem tv changed to OFF\nthen\n\tsendCommand(lampe,ON)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction10() {
		String query = "si je fais fonctionner la télévision, le ventilateur doit fonctionner";
		String res = modeleTest(query);
		String resAttendu = "rule "+"ventilateurONwhentvON"+"\nwhen\n\tItem tv changed to ON\nthen\n\tsendCommand(ventilateur,ON)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleAction11() {
		String query = "Si je fais fonctionner la télé alors ne pas mettre en fonctionnement la lampe";
		String res = modeleTest(query);
		String resAttendu = "rule "+"lampeOFFwhentvON"+"\nwhen\n\tItem tv changed to ON\nthen\n\tsendCommand(lampe,OFF)\nend";
		assertEquals(res, resAttendu);	
	}
	
	@Test
	public void testRegleEtat1() {
		String query = "Quand la lampe est allumée allume le ventilo";
		String res = modeleTest(query);
		String resAttendu = "rule "+"ventilateurONwhenEtatlampeON"+"\nwhen\n\tItem lampe received update ON\nthen\n\tsendCommand(ventilateur,ON)\nend";
		assertEquals(res, resAttendu);	
	}
	
		@Test
		public void testRegleEtat2() {
			String query = "éteins le ventilo si la lampe est allumée";
			String res = modeleTest(query);
			String resAttendu = "rule "+"ventilateurOFFwhenEtatlampeON"+"\nwhen\n\tItem lampe received update ON\nthen\n\tsendCommand(ventilateur,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat3() {
			String query = "il faut éteindre la lumière quand la télévision est allumée";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeOFFwhenEtattvON"+"\nwhen\n\tItem tv received update ON\nthen\n\tsendCommand(lampe,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat4() {
			String query = "la lumière doit être éteinte quand la télévision est allumée";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeOFFwhenEtattvON"+"\nwhen\n\tItem tv received update ON\nthen\n\tsendCommand(lampe,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat5() {
			String query = "la lumière doit être éteinte quand la télévision est en marche";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeOFFwhenEtattvON"+"\nwhen\n\tItem tv received update ON\nthen\n\tsendCommand(lampe,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat6() {
			String query = "quand la tv fonctionne éteins la lumière";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeOFFwhenEtattvON"+"\nwhen\n\tItem tv received update ON\nthen\n\tsendCommand(lampe,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat7() {
			String query = "quand la tv est déconnectée allume la lumière";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeONwhenEtattvOFF"+"\nwhen\n\tItem tv received update OFF\nthen\n\tsendCommand(lampe,ON)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat8() {
			String query = "quand la tv ne marche pas met en route le ventilateur";
			String res = modeleTest(query);
			String resAttendu = "rule "+"ventilateurONwhenEtattvOFF"+"\nwhen\n\tItem tv received update OFF\nthen\n\tsendCommand(ventilateur,ON)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat9() {
			String query = "quand la tv n'est pas en route met en route le ventilateur";
			String res = modeleTest(query);
			String resAttendu = "rule "+"ventilateurONwhenEtattvOFF"+"\nwhen\n\tItem tv received update OFF\nthen\n\tsendCommand(ventilateur,ON)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat10() {
			String query = "il faut que tu fasses marcher la lumière quand la tv ne fonctionne pas";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeONwhenEtattvOFF"+"\nwhen\n\tItem tv received update OFF\nthen\n\tsendCommand(lampe,ON)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat11() {
			String query = "quand la tv est en fonctionnement il faut que tu cesses de faire fonctionner le ventilo";
			String res = modeleTest(query);
			String resAttendu = "rule "+"ventilateurOFFwhenEtattvON"+"\nwhen\n\tItem tv received update ON\nthen\n\tsendCommand(ventilateur,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat12() {
			String query = "allume la lumière quand la tv est déconnectée";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeONwhenEtattvOFF"+"\nwhen\n\tItem tv received update OFF\nthen\n\tsendCommand(lampe,ON)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat13() {
			String query = "il faut démarrer le ventilo si la télé n'est pas en marche";
			String res = modeleTest(query);
			String resAttendu = "rule "+"ventilateurONwhenEtattvOFF"+"\nwhen\n\tItem tv received update OFF\nthen\n\tsendCommand(ventilateur,ON)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat14() {
			String query = "si la tv n'est pas éteinte alors éteins la lumière";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeOFFwhenEtattvON"+"\nwhen\n\tItem tv received update ON\nthen\n\tsendCommand(lampe,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat15() {
			String query = "si la tv n'est pas éteinte alors n'allume pas la lumière";
			String res = modeleTest(query);
			String resAttendu = "rule "+"lampeOFFwhenEtattvON"+"\nwhen\n\tItem tv received update ON\nthen\n\tsendCommand(lampe,OFF)\nend";
			assertEquals(res, resAttendu);	
		}
		
		@Test
		public void testRegleEtat16() {
			String query = "ne mets pas en route la tv quand le ventilo n'est pas arrêté";
			String res = modeleTest(query);
			String resAttendu = "rule "+"tvOFFwhenEtatventilateurON"+"\nwhen\n\tItem ventilateur received update ON\nthen\n\tsendCommand(tv,OFF)\nend";
			assertEquals(res, resAttendu);	
			
		}
}