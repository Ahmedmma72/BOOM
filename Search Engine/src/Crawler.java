import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class Crawler {
    public ArrayList<String> URLs;

    public String escapeMetaCharacters(String inputString) {
        final String[] metaCharacters = {"\"","'","\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&", "%"};

        for (String metaCharacter : metaCharacters) {
            if (inputString.contains(metaCharacter)) {
                inputString = inputString.replace(metaCharacter, " ");
            }
        }
        return inputString;
    }

    public Crawler() throws SQLException {
        URLs = new ArrayList<>();
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        ResultSet result = conn.createStatement().executeQuery("SELECT * FROM uncrawledurls");
        while (result.next()) {
            URLs.add(result.getString("id"));
        }
        result.close();
        // Seed List
        URLs.add("https://www.msn.com/");
        URLs.add("https://www.yahoo.com/");
        URLs.add("https://www.reddit.com/");
    }

    public void RemoveCrawledURL(String URL) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        conn.createStatement().executeUpdate("DELETE FROM `uncrawledurls` WHERE Url = '" + URLs.get(0) + "';");
        URLs.remove(0);
    }

    public void AddCrawledURLData(Document doc, String url) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        System.out.println("INSERT INTO `crawledurls`(`URL`, `Title`, `CrawlDate`," +
                " `Paragraph`) VALUES (\"" + url + "\",\"" + escapeMetaCharacters(doc.select("title").text()) + "\",\""
                + new Date(System.currentTimeMillis()) + "\",\"" + escapeMetaCharacters(doc.select("p").text()) + "\")");
        conn.createStatement().executeUpdate("INSERT INTO `crawledurls`(`URL`, `Title`, `CrawlDate`," +
                " `Paragraph`) VALUES (\"" + url + "\",\"" + escapeMetaCharacters(doc.select("title").text()) + "\",\""
                + new Date(System.currentTimeMillis()) + "\",\"" + escapeMetaCharacters(doc.select("p").text()) + "\")");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void AddUnCrawledURLs(String URL) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        conn.createStatement().executeUpdate("INSERT INTO `uncrawledurls`(`URL`) VALUES" +
                " ('" + URL + "');");
        URLs.add(URL);
    }

    public void crawl() throws IOException, SQLException {
        while (!URLs.isEmpty()) {
            String url = URLs.get(0);
            Document doc = Jsoup.connect(url).get();
            RemoveCrawledURL(url);
            Elements elements = doc.select("a[href]");
            for (Element link : elements) {
                System.out.println(link.attr("abs:href"));
                AddUnCrawledURLs(link.attr("abs:href"));
            }
            AddCrawledURLData(doc, url);
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        Crawler c = new Crawler();
        c.crawl();
    }

}
