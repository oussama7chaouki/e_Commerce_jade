package Ecom;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
//import jade.wrapper.ControllerException;

public class EcomContainer {
    private static String[] arguments = {
    	    "Product1",
    	    "Product2",
    	    "Product3"
        };
    private static int count =0;
	public static void main(String[] args) {
		Runtime rt=Runtime.instance();
		ProfileImpl pc=new ProfileImpl(false);
		pc.setParameter(ProfileImpl.MAIN_HOST,"localhost");
		AgentContainer container =rt.createAgentContainer(pc);
		boolean agentCreated = false;
        while (!agentCreated) {
            try {
                // Create unique agent names with the count variable
                AgentController agentController1 = container.createNewAgent("Ecom" + count++, Ecom.class.getName(), new Object[]{arguments});
                agentController1.start();
                agentCreated = true;
            } catch (jade.wrapper.StaleProxyException e) {
                // Handle the exception by incrementing the counter
                e.printStackTrace();
            }
        }
		System.out.println("d�marrage du container avec 2 agents ");

	}
}
