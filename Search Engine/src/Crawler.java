import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Crawler {
    public ArrayList<String> URLs;

    public Crawler() throws SQLException {
        URLs = new ArrayList<>();
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        ResultSet result = conn.createStatement().executeQuery("SELECT * FROM uncrawledurls");
        while (result.next()) {
            URLs.add(result.getString("id"));
        }
        result.close();
        URLs.add("https://www.msn.com/");
    }

    public void RemoveCrawledURL(String URL) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        conn.createStatement().executeUpdate("DELETE FROM `uncrawledurls` WHERE Url = '"+URLs.get(0)+"';");
        URLs.remove(0);
    }

    public void AddCrawledURLData(Document doc) throws SQLException {

    }

    public void AddUnCrawledURLs(String URL) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        conn.createStatement().executeUpdate("INSERT INTO `uncrawledurls`(`URL`) VALUES" +
                " ('"+URL+"');");
        URLs.add(URL);
    }

    public void crawl () throws IOException, SQLException {
        while(!URLs.isEmpty()) {
            Document doc = Jsoup.connect(URLs.get(0)).get();
            RemoveCrawledURL(URLs.get(0));
            Elements elements = doc.select("a[href]");
            for(Element link: elements){
                    System.out.println(link.attr("abs:href"));
                    AddUnCrawledURLs(link.attr("abs:href"));
            }
            AddCrawledURLData(doc);
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        Crawler c = new Crawler();
        c.crawl();
    }

}
