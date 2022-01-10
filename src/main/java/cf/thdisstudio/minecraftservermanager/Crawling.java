package cf.thdisstudio.minecraftservermanager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Crawling {

    static StringBuilder stringBuilder = new StringBuilder("{\"Bukkit\": [");

    public static void main(String[] args) throws IOException {
//        Document doc = Jsoup.connect("https://getbukkit.org/download/craftbukkit").get();
//        List<List<String>> lists = new ArrayList<>();
//        for(Element element : doc.select("div.download-pane")){
//            Document doc1 = Jsoup.connect(element.getElementsByClass("col-sm-4").get(0).getElementsByClass("btn btn-download").get(0).absUrl("href")).get();
//            stringBuilder.append("{\"version\": ").append("\"").append(element.getElementsByClass("col-sm-3").get(0).getElementsByTag("h2").text())
//                            .append("\"").append(", \"downloadLink\": ").append("\"")
//                    .append(doc1.getElementById("get-download").getElementsByClass("col-md-4").get(1).getElementsByTag("h2")
//                            .get(0).getElementsByTag("a").get(0).absUrl("href"))
//                    .append("\"}, ");
//        }
//        stringBuilder.append("]}");
//        System.out.println(stringBuilder.toString());
    }
}
