import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class Crawler {
    public ArrayList<String> URLs;

    public String escapeMetaCharacters(String inputString) {
        final String[] metaCharacters = {"\"", "'", "\\", "^", "$", "{", "}", "[", "]", "(", ")",
                ".", "*", "+", "?", "|", "<", ">", "-", "&", "%"};
        for (String metaCharacter : metaCharacters) {
            if (inputString.contains(metaCharacter)) {
                inputString = inputString.replace(metaCharacter, " ");
            }
        }
        return inputString.replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    public Crawler() throws SQLException {
        URLs = new ArrayList<>();
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        ResultSet result = conn.createStatement().executeQuery("SELECT * FROM uncrawledurls");
        while (result.next()) {
            URLs.add(result.getString("URL"));
        }
        result.close();
        // Seed List
        URLs.add("https://www.msn.com/");
        URLs.add("https://www.yahoo.com/");
        URLs.add("https://www.reddit.com/");
        URLs.add("https://en.wikipedia.org/wiki/Main_Page");
        URLs.add("https://www.geeksforgeeks.org/");
        URLs.add("https://www.imdb.com/");
        URLs.add("https://www.spotify.com/eg-en/");
        URLs.add("https://edition.cnn.com/");
        URLs.add("https://www.gamespot.com/");
        URLs.add("https://www.skysports.com/");
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
        StringBuilder titles = new StringBuilder();
        Elements elements = doc.select("h1,title");
        for (Element title : elements) {
            titles.append(title.text());
        }
        System.out.println("INSERT INTO searchengine.`crawledurls`(`URL`, `titles`, `CrawlDate`, `paragraphs`)" +
                " VALUES (\"" + url + "\",\"" + escapeMetaCharacters(titles.toString()) + "\",\""
                + new Date(System.currentTimeMillis()) + "\",\"" + escapeMetaCharacters(doc.body().text()) + "\")");
        conn.createStatement().executeUpdate("INSERT INTO searchengine.`crawledurls`(`URL`," +
                " `titles`, `CrawlDate`, `paragraphs`)" + " VALUES (\"" + url + "\",\"" +
                escapeMetaCharacters(titles.toString()) + "\",\"" + new Date(System.currentTimeMillis()) + "\",\""
                + escapeMetaCharacters(doc.wholeText()) + "\")");
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public String normalizeURL(String url) {
        URI temp = URI.create(url).normalize();

        String normalizedURL = null;
        if (temp.getScheme() != null)
            normalizedURL = temp.getScheme().toLowerCase() +"://";
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

        if (normalizedURL != null)
            return normalizedURL.replaceAll("^(http|https)://[0-9][0-9][0-9].*\r*", "/");
        else
            return "/";
    }


    public void AddUnCrawledURLs(String URL) throws SQLException {
        Connection conn = DBManager.getDBConnection();
        assert conn != null;
        URL = normalizeURL(URL);
        try {
            ResultSet result =
                    conn.createStatement().executeQuery("Select * FROM `uncrawledurls` where URL = \"" + URL + "\"");
            if (!result.next()) {
                conn.createStatement().executeUpdate("INSERT INTO `uncrawledurls`(`URL`) VALUES" +
                        " ('" + URL + "');");
                URLs.add(URL);
            }
        }
        catch(SQLException e)
        {
            System.out.println("Select * FROM `uncrawledurls` where URL = \"" + URL + "\"");
            System.out.println("INSERT INTO `uncrawledurls`(`URL`) VALUES" +
                    " ('" + URL + "');");
        }
    }

    public void crawl() throws IOException, SQLException {
        while (!URLs.isEmpty()) {
            String url = URLs.get(0);
            System.out.println("inside : " + url);
            try {
                Document doc = Jsoup.connect(url).get();
                RemoveCrawledURL(url);
                Elements elements = doc.select("a[href]");
                for (Element link : elements) {
                    System.out.println(link.attr("abs:href"));
                    AddUnCrawledURLs(link.attr("abs:href"));
                }
                AddCrawledURLData(doc, url);
            }
            catch(IllegalArgumentException | HttpStatusException e)
            {
                System.out.println("Malformed URL: "+url);
                RemoveCrawledURL(url);
            }
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        Crawler c = new Crawler();
        c.crawl();
    }

}
