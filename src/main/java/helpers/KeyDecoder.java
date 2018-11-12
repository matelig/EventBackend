package helpers;

import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class KeyDecoder {

    public static KeyDecoder shared = new KeyDecoder();

    private KeyDecoder() { }

    public String decode(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        if (token == null) {
            return null;
        }
        String[] tokenParts = token.split("\\.");
        Base64 base64Url = new Base64(true);
        String header = new String(base64Url.decode(tokenParts[1])); //decode token part
        JSONObject jsonObject = new JSONObject(header);
        return jsonObject.getString("sub");
    }
}
