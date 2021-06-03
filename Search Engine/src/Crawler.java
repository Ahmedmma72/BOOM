import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Crawler {
    public ArrayList<String> URLs;
    public static int countChar(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c)
                count++;
        }
        return count;
    }
    public String normalizeURL(String url) {
        URI temp = URI.create(url).normalize();

        String normalizedURL = null;
        if(temp.getScheme() != null && !temp.getScheme().startsWith("http"))
            return "https://www.google.com/";
        if (temp.getScheme() != null)
            normalizedURL = temp.getScheme().toLowerCase() + "://";
        if (temp.getHost() != null)
            normalizedURL += temp.getHost().toLowerCase();

        String path = temp.getPath();
        if (path != null) {
            if (path.endsWith("/index.html"))
                path = path.replace("/index.html", "/");
            if (path.startsWith("/index") || path.startsWith("/default") || path.equals(""))
                path = "/";
            normalizedURL += path;
        }

        if (temp.getQuery() != null && !temp.getQuery().isEmpty())
            normalizedURL += "?" + temp.getQuery().toLowerCase();

        if (normalizedURL != null && !normalizedURL.endsWith("/"))
            normalizedURL += "/";

        if (normalizedURL != null)
            return normalizedURL.replaceAll("^(http|https)://[0-9][0-9][0-9].*\r*", "/");
        else
            return "https://www.google.com/";
    }
    public void AddURL(String URL) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        URL = normalizeURL(URL);
        if (countChar(URL, '/') > 3)
            return;
        try {
            ResultSet result =
                    conn.createStatement().executeQuery("Select * FROM searchengine.urls where URL = \"" + URL + "\"");
            if (!result.next()) {
                conn.createStatement().executeUpdate("INSERT INTO searchengine.urls (`URL`) VALUES" +
                        " ('" + URL + "');");
                URLs.add(URL);
            }
        } catch (SQLException e) {
            System.out.println("Select * FROM searchengine.urls where URL = \"" + URL + "\"");
            System.out.println("INSERT INTO searchengine.urls (`URL`) VALUES" +
                    " ('" + URL + "');");
        }
    }
    public void UpdateDate(String URL) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        conn.createStatement().executeUpdate("UPDATE searchengine.urls SET crawldate = current_date() WHERE url = '" + URLs.get(0) + "';");
        URLs.remove(0);
    }
    public void crawl() throws IOException, SQLException {
        while (true) {
            while (!URLs.isEmpty()) {
                String url = URLs.get(0);
                System.out.println("inside : " + url);
                try {
                    Document doc = Jsoup.connect(url)
                            .followRedirects(false)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("http://www.google.com")
                            .get();
                    Elements elements = doc.select("a[href]");
                    for (Element link : elements) {
                        System.out.println(link.attr("abs:href"));
                        AddURL(link.attr("abs:href"));
                    }
                    UpdateDate(url);
//                    AddCrawledURLData(doc, url);
                } catch (IllegalArgumentException | HttpStatusException
                        | SocketTimeoutException | UnknownHostException e) {
                    System.out.println("Malformed URL: " + url);
                    UpdateDate(url);
                }
            }
        }
    }
    public Crawler() throws SQLException {
        URLs = new ArrayList<>();
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        ResultSet result = conn.createStatement().executeQuery("SELECT * FROM searchengine.urls where crawldate is NULL;");
        while (result.next()) {
            URLs.add(result.getString("URL"));
        }
        result.close();
    }

    public static void main(String[] args) throws SQLException, IOException {
        Crawler c = new Crawler();
        c.crawl();
    }
}
