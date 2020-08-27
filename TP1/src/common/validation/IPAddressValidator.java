package common.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IPAddressValidator implements IValidator
{
    public String question() {return "Entrer l'adresse IP du poste sur lequel s’exécute le serveur: ";}

    private Pattern pattern;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public IPAddressValidator(){
        pattern = Pattern.compile(IPADDRESS_PATTERN);
    }

    public boolean validate(String ip){
        /**
         * Validate ip address with regular expression
         * @param ip ip address for common.validation
         * @return true valid ip address, false invalid ip address
         */
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
