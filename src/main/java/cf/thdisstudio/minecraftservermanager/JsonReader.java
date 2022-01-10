package cf.thdisstudio.minecraftservermanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {
    public JsonReader() {
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();

        int cp;
        while((cp = rd.read()) != -1) {
            sb.append((char)cp);
        }

        return sb.toString();
    }

    public static JSONObject ReadFromUrl(String url) throws IOException, JSONException {
        InputStream is = (new URL(url)).openStream();

        JSONObject var5;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            var5 = json;
        } finally {
            is.close();
        }

        return var5;
    }

    public static JSONObject ReadFromString(String toconvert) {
        return new JSONObject(toconvert);
    }

    public static JSONObject ReadFromChar(char toconvert) {
        return new JSONObject(String.valueOf(toconvert));
    }
}
