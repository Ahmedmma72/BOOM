import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
public class IndexerDB {
    static private Connection connection;
    public static void open() {
        connection=DBManager.getDBConnection();
    }
    public static void close() throws SQLException {
        DBManager.close();
    }
    public static void updateURL(String URL,String title,String content) throws SQLException {
        String sql = "UPDATE CrawledURLs set indexed = true, titles = ?, paragraphs = ? WHERE URL = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, content);
        ps.setString(3, URL);
        ps.executeUpdate();
    }
    public static String getNonIndexedURL() throws SQLException {
        String sql = "SELECT URL FROM CrawledURLs WHERE indexed IS false LIMIT 1";
        ResultSet result = connection.createStatement().executeQuery(sql);
        if (result.next()) {
            return result.getString(1);
        }
        return null;
    }
    public static HashMap<String,Double> calcTF(ArrayList<String> listOfWords, int countOfWords){
        HashMap<String,Double> TF = new HashMap<>();
        for (String word: listOfWords) {
            if(TF.containsKey(word))
                TF.put(word,TF.get(word)+1.0);
            else
                TF.put(word, 1.0);
        }
        //Normalize TF
        for (Entry<String, Double> entry : TF.entrySet()) {
            entry.setValue(entry.getValue()/countOfWords);
        }
        return TF;
    }
    public static void indexWords(HashMap<String, Double> TF,String URL) throws SQLException {
        int URLid=URLid(URL);
        for (Entry<String, Double> entry : TF.entrySet()) {
        String sql = "INSERT INTO Words(word,stem,TF,URLID) VALUES (?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, entry.getKey());
        ps.setString(2, Extract.stemS(entry.getKey()));
        ps.setDouble(3, entry.getValue());
        ps.setInt(4, URLid);
        ps.executeUpdate();
        }

    }
    private static int URLid(String URL) throws SQLException {
        String sql = "SELECT id FROM CrawledURLs WHERE URL =  ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,URL);
        ResultSet result =  ps.executeQuery();
        if (result.next()) {
            return result.getInt(1);
        }
        return -1;
    }
}
