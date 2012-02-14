import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpUtil {

    public JsonElement getJson(String url) {
        JsonParser parser = new JsonParser();
        return parser.parse(get(url));
    }

    public String get(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        int statusCode;
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String source = convertStreamToString(instream);
                    instream.close();
                    return source;
                }
            }
        }
        catch (Exception  ex) {
            throw new RuntimeException(String.format("Exception while executing get for url: %s. Error Message : %s", url, ex.getMessage()));
        }
        throw new RuntimeException(String.format("Get call for url: %s returned with %s status",url,statusCode ));
    }

    public void post(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        int statusCode;
        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
            statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){
                    return;
            }
        }
        catch (Exception  ex) {
            throw new RuntimeException(String.format("Exception while executing post for url: %s. Error Message : %s", url, ex.getMessage()));
        }
        throw new RuntimeException(String.format("Post call for url: %s returned with %s status",url,statusCode ));
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
