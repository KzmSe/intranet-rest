package az.gov.adra.util;

import javax.xml.bind.DatatypeConverter;

public class ResourceUtil {

    public static String convertToString(String hex) {
        return new String(DatatypeConverter.parseHexBinary(hex));
    }

}
