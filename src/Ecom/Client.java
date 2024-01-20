package Ecom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;

@SuppressWarnings("serial")
public class Client extends GuiAgent {

    private ClientContainer gui;
    GuiEvent guiEventGlobal = null;
    private int nombreAH = 0;
    private DFAgentDescription[] result = null;
    private Map<String, String> offreAChercher = null;
    private Map<String, String> meilleurChoix = null;
    private List<String> offres = new ArrayList<>();
    private List<AID> envoyeurs = new ArrayList<>();

    protected void setup() {
        gui = (ClientContainer) getArguments()[0];
        gui.setAgentinterface(this);
        this.registreClientinDF();




        this.addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                    MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE), MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
                    ACLMessage message = this.myAgent.receive(messageTemplate);
                    if (message != null) {
                        switch (message.getPerformative()) {
                            case ACLMessage.PROPOSE:
                                offres.add(message.getContent());
                                envoyeurs.add(message.getSender());
                                if (offres.size() == nombreAH) {
                                    AID meilleurAID = getMeilleurAID();
                                    for (AID agentID : envoyeurs) {
                                        ACLMessage messageCFP = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                        if (agentID == meilleurAID) {
                                            messageCFP = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                        }
                                        messageCFP.addReceiver(agentID);
                                        this.myAgent.send(messageCFP);
                                    }
                                    if (meilleurAID == null) {
                                    	showresult("");
                                        offreAChercher = null;
                                        offres = new ArrayList<>();
                                        envoyeurs = new ArrayList<>();
                                        meilleurChoix = null;
                                    }
                                }
                                break;
                            case ACLMessage.CONFIRM:
                                showresult(meilleurChoix.get("prix"));
                                
                                offreAChercher = null;
                                offres = new ArrayList<>();
                                envoyeurs = new ArrayList<>();
                                meilleurChoix = null;
                                break;
                        }
                    } else {
                        this.block();
                    }
                }
        });




        System.out.println("Agent est démarré: " + this.getAID().getName());
    }

    @Override
    public void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("Agent est fini: " + this.getAID().getName());
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
        	String product=guiEvent.getParameter(0).toString();
        	String quantite=guiEvent.getParameter(1).toString();
        	String delai=guiEvent.getParameter(2).toString();
            offreAChercher = new HashMap<>();
			  offreAChercher.put("Produit", product);
			  offreAChercher.put("quantite", quantite);
			  offreAChercher.put("delai", delai);
		        this.result = this.getEcomListFromDF();
		        this.nombreAH = this.result.length;
			  sendCFPMessage(result,product,quantite);
			 
        }
    }



    public void sendRequestMessage(Map<String, String> offreCherche) {
        ACLMessage messageREQUEST = new ACLMessage(ACLMessage.REQUEST);

        try {
            messageREQUEST.setContent(offreCherche.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < this.result.length; i++) {
            messageREQUEST.addReceiver(this.result[i].getName());
        }

        this.send(messageREQUEST);
    }
	
    public AID getMeilleurAID() {
        int meilleurIndex = -1;
        int min=100000;

        for (int i = 0; i < offres.size(); i++) {
        	if(offres.get(i)==null)
        		continue;
            Map<String, String> offre = Utils.stringToHashMap(offres.get(i));
            System.out.println(offre);
            System.out.println(offreAChercher);


            if (Integer.parseInt(offre.get("quantite")) >= Integer.parseInt(offreAChercher.get("quantite"))) {
            	if(min>Integer.parseInt(offre.get("prix"))) {
            		min=Integer.parseInt(offre.get("prix"));
                meilleurIndex = i;
                meilleurChoix = offre;
            	}
            }
        }

        return (meilleurIndex == -1) ? null : envoyeurs.get(meilleurIndex);
    }


	 


    public DFAgentDescription[] getEcomListFromDF() {
        DFAgentDescription AHDescription = new DFAgentDescription();
        ServiceDescription AHServiceDescription = new ServiceDescription();
        AHServiceDescription.setType("Commerce");
        AHServiceDescription.setName("Commerce en Ligne");
        AHDescription.addServices(AHServiceDescription);
        DFAgentDescription[] result = null;
        try {
            result = DFService.search(this, AHDescription);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return result;
    }
    public void registreClientinDF() {
        DFAgentDescription description = new DFAgentDescription();
        description.setName(getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("client");
        serviceDescription.setName("client");
        description.addServices(serviceDescription);
        try {
            DFService.register(this, description);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
    public void sendCFPMessage(DFAgentDescription[] result,String produit,String quantite) {
        ACLMessage messageCFP = new ACLMessage(ACLMessage.CFP);
        messageCFP.setContent("Demande des offres disponible,"+produit+","+quantite);
        for (DFAgentDescription description : result) {
            messageCFP.addReceiver(description.getName());
        }
        this.send(messageCFP);
    }
    public void showresult(String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (content.length() == 0) {
                    gui.show("Pas d'offre trouve!");
                } else {
                    gui.show("Meilleur offre trouve avec un prix de: " + content + "DH");
                }
            }
        });
    }
}
