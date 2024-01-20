package Ecom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Ecom extends Agent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int offreindex = -1;
	private int offrequantite = -1;

    private List<String> offres = new ArrayList<>();
    Random rand = new Random();


    @SuppressWarnings("serial")
	@Override
    public void setup() {
        this.populateOffres();
        this.registreAHinDF();
        this.addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)));
                ACLMessage message = this.myAgent.receive(messageTemplate);
                if (message != null) {
                    switch (message.getPerformative()) {
                        case ACLMessage.CFP:
                            String[] partsmessage = message.getContent().split(",");
                        	if (partsmessage[0].equals("Demande des offres disponible")) {
                        	    String productsearch=searchProduct(partsmessage[1]);
                        	    offrequantite=Integer.parseInt(partsmessage[2]);
                        	    if (productsearch != null) {
                        	        ACLMessage reply = message.createReply();
                        	        reply.setPerformative(ACLMessage.PROPOSE);
                        	        reply.setContent(productsearch);
                        	        this.myAgent.send(reply);
                        	    }
                        	}
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage reply = message.createReply();
                            if(offreindex != -1 && offrequantite != -1)
                            	subtractQuantity(offreindex,offrequantite);
                            offreindex=-1;
                            offrequantite=-1;
                            reply.setPerformative(ACLMessage.CONFIRM);
                            this.myAgent.send(reply);
                            break;
                        case ACLMessage.REJECT_PROPOSAL:
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

    public void populateOffres() {
        Object[] arguments = this.getArguments();
        String[] args=(String[])arguments[0];
        for (int i = 0; i < args.length; i++) {
            // Generate random integers in range 0 to 999
            int prix = rand.nextInt(1000);
            int quantite = rand.nextInt(100);
            int delai = rand.nextInt(10);
            String product = args[i];

            offres.add(product + ", " + prix + ", " + quantite + ", " + delai);
        }
    }

    public void registreAHinDF() {
        DFAgentDescription description = new DFAgentDescription();
        description.setName(getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("Commerce");
        serviceDescription.setName("Commerce en Ligne");
        description.addServices(serviceDescription);
        try {
            DFService.register(this, description);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
    public String searchProduct(String product) {
    	for (int i = 0; i < offres.size(); i++) {
            String offer = offres.get(i);
            if (offer.startsWith(product + ",")) {
            	offreindex = i;
                return offer;
            }
        }
    	offreindex = -1; // Reset index if product is not found
        return null; // Product not found
    }
    private void subtractQuantity(int index, int quantityToSubtract) {
        if (index >= 0 && index < offres.size()) {
            String offer = offres.get(index);
            String[] parts = offer.split(", ");
            if (parts.length >= 3) {
                int oldQuantity = Integer.parseInt(parts[2]);
                int newQuantity = Math.max(0, oldQuantity - quantityToSubtract);
                parts[2] = String.valueOf(newQuantity);
                offres.set(index, String.join(", ", parts));
            }
        }
    }
    }
