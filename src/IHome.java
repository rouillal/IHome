import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JMenuItem;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Interface utilisateur Java Swing pour la saisie de programme en langage naturel libre et la résolution d'ambiguïtés via le dialogue de désambiguïsation
 * @author Lorrie Rouillaux
 */
@SuppressWarnings("serial")
public class IHome extends JFrame {
	private static IHome frame;
	private JPanel contentPane;
	private JTextField tfRgle;
	private JMenuBar menuBar;
	private static JTextArea taFeedback;
	private static JLabel lbFeedback;
	private static JPanel pnDialog;
	private static JButton btnReformuler;
	private JMenu mnMenu;
	private JMenuItem mntmQuitter;
	private JPanel panel_2;
	private JButton btnProposerUneNouvelle;
	private JMenuItem mntmNouvelleRgle;
	
	private static Services S; //liste des appareils de l'habitat
	private static String query = null; // programme exprimé en langage naturel libre par l'habitant
	private static String newquery = null; //programme en langage naturel reformulé
	private static boolean okTime; //attester que l'utilisateur a bien reformulé la donnée temporelle
	private static boolean okFunction; //attester que l'utilisateur a bien reformulé la fonction de l'appareil
	private static boolean okDevice; //attester que l'utilisateur a bien reformulé l'appareil
	private static Device newDevice = null; //appareil reformulé
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new IHome();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public IHome() {
		S = new Services(); //liste de tous les appareils de l'habitat
		Device.createDevice(S); //création des appareils de l'habitat
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);

		mntmQuitter = new JMenuItem("Quitter");
		mntmQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		mntmNouvelleRgle = new JMenuItem("Nouvelle règle");
		mntmNouvelleRgle.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				tfRgle.enable(true);
				tfRgle.setText("");
				lbFeedback.setText("");
				taFeedback.setText("");
				btnProposerUneNouvelle.setVisible(false);
				btnReformuler.setVisible(false);
			}
		});
		mnMenu.add(mntmNouvelleRgle);
		mnMenu.add(mntmQuitter);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLabel lblNewLabel = new JLabel("Veuillez entrer une règle pour votre habitat :");
		contentPane.add(lblNewLabel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		tfRgle = new JTextField();
		panel_1.add(tfRgle);
		tfRgle.setColumns(10);

		pnDialog = new JPanel();
		panel.add(pnDialog, BorderLayout.CENTER);
		pnDialog.setLayout(new BorderLayout(0, 0));

		taFeedback = new JTextArea();
		taFeedback.setWrapStyleWord(true);
		taFeedback.setForeground(Color.BLACK);
		taFeedback.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		taFeedback.setBackground(SystemColor.window);
		taFeedback.setLineWrap(true);
		taFeedback.setEditable(false);
		pnDialog.add(taFeedback, BorderLayout.CENTER);

		lbFeedback = new JLabel("");
		pnDialog.add(lbFeedback, BorderLayout.NORTH);

		panel_2 = new JPanel();
		pnDialog.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnReformuler = new JButton("Reformuler");
		btnReformuler.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (GenerationOH.getIntentTempo()){ //dialogue de désambiguïsation pour une règle temporelle
					btnReformuler.setVisible(false);
					okTime = false;
					okFunction = false;
					okDevice = false;
					if (!GenerationOH.getNoFunction() && !GenerationOH.getNoService() && !GenerationOH.getNoTime()){ //si pas de manque de données
						if (GenerationOH.getErrorTime()){ //erreur temporelle
							JPanel panel = new JPanel();
							panel.add(new JLabel("Que signifie "+'"'+GenerationOH.getTempsUser()+'"'+" ?"));
							final JComboBox<Integer> jcbHeure = new JComboBox<Integer>();
							for (int i=0; i<24; i++){
								jcbHeure.addItem(i);
							}
							panel.add(jcbHeure);
							jcbHeure.setSelectedIndex(12);
							panel.add(new JLabel("Heure"));
							final JComboBox<Integer> jcbMin = new JComboBox<Integer>();
							for (int i=0; i<60; i++){
								jcbMin.addItem(i);
							}
							panel.add(jcbMin);
							panel.add(new JLabel("Min"));
							final JComboBox<Integer> jcbSec = new JComboBox<Integer>();
							for (int i=0; i<60; i++){
								jcbSec.addItem(i);
							}
							panel.add(jcbSec);
							panel.add(new JLabel("Sec"));
							
							int result = JOptionPane.showConfirmDialog(null, panel, "Reformulation de la donnée temporelle", JOptionPane.OK_CANCEL_OPTION);
							if (result == JOptionPane.OK_OPTION) {
								okTime = true;
								String newH = jcbHeure.getSelectedItem().toString();
								String newM = jcbMin.getSelectedItem().toString();
								String newS = jcbSec.getSelectedItem().toString();
								String newTime = newH+"h"+newM+"m"+newS;
								newquery = newquery.replace(GenerationOH.getTempsUser(), newTime); //remplacement de la donnée temporelle non comprise par le système par celle reformulée par l'habitant
							}
							else if (result == JOptionPane.CANCEL_OPTION){
								okTime = false;
								lbFeedback.setForeground(Color.RED);
								lbFeedback.setText("Erreur...");
								taFeedback.setText(GenerationOH.getFeedback());
								btnReformuler.setVisible(true);
								btnProposerUneNouvelle.setVisible(true);
							}	
						}
						if (GenerationOH.getErrorDevice() && (okTime || !GenerationOH.getErrorTime())){ //erreur appareil
							final JComboBox<String> jcbDevice = new JComboBox<String>();
							for (Device d: S.getServices()){
								jcbDevice.addItem(d.getNom()); //remplir la JComboBox avec tous les appareils de l'habitat
							}
							JPanel panel = new JPanel();
							panel.add(new JLabel("Quel est l'appareil "+'"'+GenerationOH.getServiceUser()+'"'+" ?"));
							panel.add(jcbDevice);
							int result = JOptionPane.showConfirmDialog(null, panel, "Reformulation de l'appareil", JOptionPane.OK_CANCEL_OPTION);
							if (result == JOptionPane.OK_OPTION) {
								okDevice = true;
								String newD = jcbDevice.getSelectedItem().toString(); //récupérer le nom de l'appareil reformulé
								for (Device d: S.getServices()){
									if (d.getNom().equals(newD))
										newDevice = d; //récupérer l'appareil correspondant à ce nom
								}
								newquery = newquery.replace(GenerationOH.getServiceUser(), newD); //remplacer le nom de l'appareil non compris par le système par le nom de l'appareil reformulé
								try{
									BufferedWriter bw1 = new BufferedWriter(new FileWriter(
											"Synonymes/Appareils/"+newDevice.getNom()+".txt", true)); //apprentissage de l'appareil non compris au départ par le système en l'inscrivant dans le dictionnaire des synonymes
									bw1.write("\n"+GenerationOH.getServiceUser());
									bw1.close();
								}catch (Exception e1){
									e1.toString();
								}
							}
							else if (result == JOptionPane.CANCEL_OPTION){
								okDevice = false;
							}	
						}
						if (GenerationOH.getErrorFunction() && (okTime || !GenerationOH.getErrorTime()) && (okDevice || !GenerationOH.getErrorDevice())){ //erreur fonction
							final JComboBox<String> jcbFunction = new JComboBox<String>();
							Device d = GenerationOH.getDevice();
							if (d == null)
								d = newDevice; //reprise de l'appareil reformulé s'il n'a pas été compris par le système avant
							for (String f: d.getFonctions()){
								jcbFunction.addItem(f); //remplissage de la JComboBox avec les fonctions disponibles pour l'appareil dont on souhaite effectuer une action
							}
							JPanel panel = new JPanel();
							panel.add(new JLabel("Que signifie "+'"'+GenerationOH.getFunctionUser()+'"'+" pour l'appareil "+d.getNom()+" ?"));
							panel.add(jcbFunction);
							int result = JOptionPane.showConfirmDialog(null, panel, "Reformulation de la fonction", JOptionPane.OK_CANCEL_OPTION);
							if (result == JOptionPane.OK_OPTION) {
								okFunction = true;
								String newFunction = jcbFunction.getSelectedItem().toString(); //récupération de la fonction reformulée
								newquery = TreeTagger.toLemma(newquery).replace(GenerationOH.getFunctionUser(), newFunction); //remplacement de la fonction non comprise par le système par la fonction reformulée
								if (newFunction.equals("éteindre"))
									newFunction = "eteindre";
								try{
									BufferedWriter bw2 = new BufferedWriter(new FileWriter(
											"Synonymes/Fonctions/"+newFunction+".txt", true)); //apprentissage de la fonction non comprise par le système au départ en la rentrant dans le dictionnaire des synonymes
									bw2.write("\n"+GenerationOH.getFunctionUser());
									bw2.close();
								}catch (Exception e2){
									e2.toString();
								}
							}
							else if (result == JOptionPane.CANCEL_OPTION){
								okFunction = false;
							}	
						}
					}
					if ( (okTime && GenerationOH.getErrorTime()) || (okFunction && GenerationOH.getErrorFunction()) || (okDevice && GenerationOH.getErrorDevice())){
						System.out.println(newquery);
						tfRgle.setText(newquery);
						Traitement(newquery); //relancement de la chaine de traitement sur le programme en langage naturel libre reformulé
					}
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				lbFeedback.setForeground(Color.BLUE);
				lbFeedback.setText("Reformulation en cours...");
				taFeedback.setText("");
			}
		});
		panel_2.add(btnReformuler);

		btnProposerUneNouvelle = new JButton("Nouvelle règle");
		btnProposerUneNouvelle.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent e) {
				tfRgle.enable(true);
				tfRgle.setText("");
				lbFeedback.setText("");
				taFeedback.setText("");
				btnProposerUneNouvelle.setVisible(false);
				btnReformuler.setVisible(false);
			}
		});
		btnProposerUneNouvelle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_2.add(btnProposerUneNouvelle);
		btnReformuler.setVisible(false);
		btnProposerUneNouvelle.setVisible(false);

		JButton btnValider = new JButton("Valider");
		btnValider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (query != null){
					Traitement(query); //traitement du programme exprimé en langage naturel libre par l'habitant
				}
			}
			@SuppressWarnings("deprecation")
			@Override
			public void mousePressed(MouseEvent e) {
				lbFeedback.setText("");
				taFeedback.setText("");
				if (!tfRgle.getText().equals("")){
					query = tfRgle.getText();
					taFeedback.setText("En traitement...");
					tfRgle.enable(false);
				}
			}
		});
		panel_1.add(btnValider);
	}

	/**
	 * Lancement de la chaine de traitement sur le programme exprimé en langage naturel libre par l'habitant
	 * @param query
	 * programme exprimé en langage naturel libre par l'habitant
	 */
	private void Traitement(String query){
		newquery = query;
		String oh[][] = new String [1][2]; //code openHAB avec le nom de la règle dans la première case et le corps de la règle dans la seconde
		//analyse lexicale - lemmatisation (par TreeTagger)
		query = TreeTagger.toLemma(query);
		System.out.println("***\nRequête lemmatisée: "+query+"\n***");
		//Normalisation (recherche de motifs d'expression et synonymes dans dicitonnaire)
		String queryNormalise = Normalisation.normalisation(query);
		System.out.println("***\nRequête normalisée: "+queryNormalise+"\n***");
		//création du fichier contenant le programme exprimé en langage naturel par l'habitant
		String queryBonsai = Bonsai.preTraitement(queryNormalise);
		System.out.println(queryBonsai);
		creerFichier(queryBonsai, "bonsai/query.txt");
		//analyse syntaxique - appel du script.sh pour créer les dépendances (par Bonsaï)
		Bonsai.callScriptBonsai();
		String dep[][] = Bonsai.parseBonsai(); //récupération du tableau des relations de dépendances
		//analyse sémantique - connexion au web service Luis et création du fichier json (par LUIS)
		Luis.query(queryNormalise);
		//generation de code openHAB après parsing du fichier JSon resultat de l'analyse sémantique
		oh = GenerationOH.toOpenHab(query, Luis.jsonParsing(), S, dep);
		if (GenerationOH.getOk()){
			lbFeedback.setForeground(Color.GREEN);
			lbFeedback.setText("Réussite !");
			taFeedback.setText(GenerationOH.getFeedback()); //affichage du feedback généré
			//création du fichier contenant la règle openHAB
			if (oh != null){
				creerFichier(oh[0][1], "rulesOpenHAB/"+oh[0][0]+".rules"); //création du fichier contenant la règle openHAB
				btnProposerUneNouvelle.setVisible(true);
				btnReformuler.setVisible(false);
			}
		}
		else{
			lbFeedback.setForeground(Color.RED);
			lbFeedback.setText("Erreur...");
			taFeedback.setText(GenerationOH.getFeedback()); //affichage du feedback généré
			if (GenerationOH.getIntentNone() || GenerationOH.getIntentAction() || GenerationOH.getIntentEtat()){ //si erreur dans intent None, Action ou Etat (pas encore de reformulation possible)
				btnReformuler.setVisible(false);
				btnProposerUneNouvelle.setVisible(true);
			}
			else if (GenerationOH.getIntentTempo() && !GenerationOH.getNoFunction() && !GenerationOH.getNoService() && !GenerationOH.getNoTime()){ //si erreur(s) dans intent RegleTempo sans données manquantes
				btnProposerUneNouvelle.setVisible(true);
				btnReformuler.setVisible(true); //initiation du dialogue de désambiguïsation
			}
			else{
				btnReformuler.setVisible(false);
				btnProposerUneNouvelle.setVisible(true);
			}

		}
	}
	
	/**
	 * Méthode affichage des différentes étapes de traitement pendant l'exécution
	 * @param msg
	 * Message de l'étape en cours d'exécution à afficher
	 */
	public static void log(String msg) {
		System.out.print("log>\t" + msg);
	}
	
	/**
	 * Méthode de création d'un fichier
	 * @param chaine
	 * Chaine de caractères à écrire dans le fichier
	 * @param chemin
	 * Chemin du fichier
	 */
	public static void creerFichier(String chaine, String chemin){
		try{
			IHome.log("Création du fichier...   ");
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(chemin)));
			writer.print(chaine);
			writer.close();
			System.out.println("OK");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("KO");
			System.exit(1);
		}
	}
}