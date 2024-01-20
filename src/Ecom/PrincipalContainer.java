package Ecom;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class PrincipalContainer {

    public static void main(String[] args) {
        try {
            Runtime runtime = Runtime.instance();
            Properties properties = new ExtendedProperties();
            properties.setProperty("gui", "true");
            ProfileImpl profileImpl = new ProfileImpl(properties);
            AgentContainer mainContainer = runtime.createMainContainer(profileImpl);
            mainContainer.start();
            System.out.println("Le conteneur principal est démarré!");
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}
