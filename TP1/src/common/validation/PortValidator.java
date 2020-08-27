package common.validation;



class PortValidator implements IValidator
{
    public String question() {return "Choisissez un port d'écoute entre 5000 et 5050: ";}

    public boolean validate(String port_raw) {
        /**
         * Validate the port
         * @param port_raw The string to be checked
         * @return true valid port number, false invalid port number
         */
        int port;
        try {
            port = Integer.parseInt(port_raw);
        } catch (NumberFormatException error) {
            return false;
        }
        return port >= 5000 && port <= 5050;
    }
}
