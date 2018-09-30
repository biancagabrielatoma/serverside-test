import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;

public class Launch {

    public static void main (String[] args) {

        JSONObject results = ProductRetriever.buildSainsburyProductsJSON();
        System.out.println(JsonWriter.formatJson(String.valueOf(results)));
    }
}
