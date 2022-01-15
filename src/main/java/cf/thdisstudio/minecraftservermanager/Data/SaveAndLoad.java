package cf.thdisstudio.minecraftservermanager.Data;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.json.JSONObject;

import java.io.*;

public class SaveAndLoad {
    public static void newServer(String name, String description, String version, String type, double Ram, File location) throws IOException {
        File conf = new File(location.getAbsolutePath()+"\\YSTMSM.json");
        conf.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(conf));
        bufferedWriter.write("{" +
                "\"Name\": \""+name+"\"," +
                "\"Description\": \""+description+"\"," +
                "\"Version\": \""+version+"\"," +
                "\"Type\": \""+type+"\"," +
                "\"Ram\": \""+Ram+"\"," +
                "\"Custom-VM-Option\": \"\"," +
                "\"Online\": {" +
                "\"isUsing\": false," +
                "\"Port\": 25575," +
                "\"Password\": \"ReallyGoodPassword\"," +
                "\"Permissions\": [\"View Logs\"]" +
                "}" +
                "}");
        bufferedWriter.flush();
    }

    public static String[] loadServer(File location) throws IOException {
        File conf = new File(location.getAbsolutePath() + "\\YSTMSM.json");
        if (!conf.exists())
            return null;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(conf));
        StringBuilder fullJson = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            fullJson.append(line);
        }
        try {
            JSONObject jsonObject = new JSONObject(fullJson.toString());
            return new String[]{jsonObject.getString("Name"), jsonObject.getString("Description"), jsonObject.getString("Version"), jsonObject.getString("Type"),
                    jsonObject.getString("Ram"), jsonObject.getString("Custom-VM-Option"), String.valueOf(jsonObject.getJSONObject("Online"))};
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("오류 - 서버 파일을 불러오는데 실패하였습니다.");
                alert.setHeaderText("서버 파일을 불러오는데 실패하였습니다");
                alert.setContentText("[중요 - 이 오류는 서버 파일에 문제가 생긴것으로 제보하지 마시길 바랍니다.]\n\n\n" +
                        "메시지: " + e.getLocalizedMessage()+"\n\n" +
                        "서버 위치: "+location);

                alert.showAndWait();
            });
            return null;
        }
    }
}
