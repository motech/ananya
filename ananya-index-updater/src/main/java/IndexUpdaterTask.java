import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public class IndexUpdaterTask implements ITask {
    private String server = "http://127.0.0.1:5984";
    private HttpUtil httpUtil;

    @Override
    public void Run() {
        httpUtil = new HttpUtil();
        String dbsUrl = String.format("%s/_all_dbs", server);
        JsonArray dbs = httpUtil.getJson(dbsUrl).getAsJsonArray();
        for (JsonElement db : dbs){
            String dbName = db.getAsString();
            IndexDbViews(dbName);
            CompactDbAndCleanViews(dbName);
        }
    }

    private void CompactDbAndCleanViews(String dbName) {
        String compactUrl = String.format("%s/%s/_compact", server, dbName);
        httpUtil.post(compactUrl);
        String cleanupUrl = String.format("%s/%s/_view_cleanup", server, dbName);
        httpUtil.post(cleanupUrl);
    }

    private void IndexDbViews(String dbName) {
        String dbUrl = String.format("%s/%s/_all_docs?startkey=%%22_design%%2F%%22&include_docs=true", server, dbName);
        JsonElement jsonElement = httpUtil.getJson(dbUrl);
        JsonArray rows = jsonElement.getAsJsonObject().get("rows").getAsJsonArray();
        for(JsonElement ele : rows){
            JsonObject doc = ele.getAsJsonObject().get("doc").getAsJsonObject();
            String document = doc.get("_id").getAsString();
            JsonElement views = doc.get("views");
            if(views != null){
                Set<Map.Entry<String,JsonElement>> entries = views.getAsJsonObject().entrySet();
                for(Map.Entry<String,JsonElement> entry : entries){
                    String view = entry.getKey();
                    String viewUrl = String.format("%s/%s/%s/_view/%s", server, dbName, document, view);
                    httpUtil.getJson(viewUrl);
                }
            }
        }
    }
}
