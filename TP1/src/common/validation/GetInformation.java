package common.validation;

import common.ConnectionHost;
import common.Credentials;

import java.util.ArrayList;
import java.util.Scanner;


public class GetInformation {
    Scanner scanner;
    ArrayList<IValidator> validators;
    Side side;

    public GetInformation(Side side)
    {
        this.side = side;
        scanner = new Scanner(System.in);

        validators = new ArrayList<>();

        if (side == Side.CLIENT) {
            validators.add(new IPAddressValidator()); // Only need ip address on the client side
        }
        validators.add(new PortValidator());

    }

    public Credentials getCredentials(){
        System.out.print("Utilisateur: ");
        String username = scanner.nextLine().trim();

        System.out.print("Mot de passe: ");
        String password = scanner.nextLine().trim();

        return new Credentials(username, password);
    }

    public ConnectionHost getConnectionHostInfo() {
        //  Demande à l’utilisateur d’entrer les informations du serveur

        ArrayList<String> ipAndPortList = new ArrayList<>();
        for (IValidator validator : validators) {
            boolean validated = false;
            String info = null;
            while (!validated) {
                System.out.print(validator.question());
                info = scanner.nextLine().trim();
                validated = validator.validate(info);
            }
            ipAndPortList.add(info);
        }

        if (side == Side.CLIENT) {
            return new ConnectionHost(ipAndPortList.get(0), Integer.parseInt(ipAndPortList.get(1)));
        } else {
            return new ConnectionHost("", Integer.parseInt(ipAndPortList.get(0)));
        }
    }

}
