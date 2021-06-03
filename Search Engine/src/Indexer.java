import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Indexer {
    private static ArrayList<String> listOfWords;
    private static int countOfWords;
    private static String content;
    private static String title;
    private static int tCountOfWords;
    private static String Description;
    public static void main(String[] args) throws SQLException, IOException {
        tCountOfWords=0;
        System.out.println("Started Indexing");
        IndexerDB.open();
        String URL;
        int count=0;
        long startTime = System.currentTimeMillis();
        while ((URL = IndexerDB.getNonIndexedURL()) != null) {
           System.out.println("Started Parsing "+URL);
           parsePAGE(URL);
           System.out.println("Finished Parsing "+URL);
           if (countOfWords > 0) {
               System.out.printf("started indexing page %d%n",++count);
               HashMap<String, Double> TF = IndexerDB.calcTF(listOfWords, countOfWords);
               IndexerDB.indexWords(TF, URL);
               System.out.printf("finished indexing page %d%n", count);
           }
           IndexerDB.updateURL(URL, title,Description);
        }
        long endTime = System.currentTimeMillis();
        System.out.printf("Finished Indexing %d words at %d %n",tCountOfWords,endTime-startTime);
        IndexerDB.close();
    }

    private static void parsePAGE(String url) throws IOException {
        try {
                countOfWords = 0;
                Document document = Jsoup.connect(url)
                        .followRedirects(false)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                StringBuilder titles = new StringBuilder();
                Elements elements = document.select("h1,title");
                for (Element title : elements) {
                    titles.append(title.text());
                }
                title = titles.toString();
                content = Extract.escapeMetaCharacters(document.wholeText());
                if(content.isEmpty()){
                    return;
                }
                listOfWords = Extract.splitSentence(content);
                listOfWords = Extract.removeStoppingWords(listOfWords);
                countOfWords += listOfWords.size();
                tCountOfWords += countOfWords;
                Description=document.select("meta[name=description]").get(0)
                        .attr("content");
        }catch(Exception e){
                System.out.println("error occurred in parse page");
        }
    }
    public static boolean isArabic(String url) throws IOException {

        try {
            Document doc = Jsoup.connect(url)
                    .followRedirects(false)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();

            assert doc != null;
            Element taglang = doc.select("html").first();
            if (!taglang.attr("lang").isEmpty() &&
                    taglang.attr("lang").toLowerCase().contains("ar"))
                return true;
            else {
                Elements metas = doc.select("meta");
                for (Element meta : metas) {
                    if (meta.attr("property").contains("locale") &&
                            meta.attr("content").toLowerCase().contains("ar"))
                        return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("error occurred in parse page");
        }
        return false;
    }
}