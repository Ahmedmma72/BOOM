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
    public static void updateURL(String URL,String title,String Description) throws SQLException {
        String sql = "UPDATE urls set indexed = true, titles = ?, paragraphs = ? WHERE URL = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, Description);
        ps.setString(3, URL);
        ps.executeUpdate();
    }
    public static String getNonIndexedURL() throws SQLException {
        String sql = "SELECT URL FROM urls WHERE indexed IS false LIMIT 1";
        ResultSet result = connection.createStatement().executeQuery(sql);
        if (result.next()) {
            return result.getString(1);
        }
        return null;
    }
    public static HashMap<String,Double> calcTF(ArrayList<String> listOfWords, int countOfWords){

        HashMap<String,Double> TF = new HashMap<>();
        if(countOfWords!=0) {
            for (String word : listOfWords) {
                String sword=Extract.stemS(word);
                if (TF.containsKey(sword))
                    TF.put(sword, TF.get(sword) + 1.0);
                else
                    TF.put(sword, 1.0);
            }
            //Normalize TF
            for (Entry<String, Double> entry : TF.entrySet()) {
                entry.setValue(entry.getValue() / countOfWords);
            }
        }
        return TF;
    }
    public static void indexWords(HashMap<String, Double> TF,String URL) throws SQLException {
        int URLid=URLid(URL);
        for (Entry<String, Double> entry : TF.entrySet()) {
        String sql = "INSERT ignore INTO Words(word,TF,URLID) VALUES (?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, entry.getKey());
        ps.setDouble(2, entry.getValue());
        ps.setInt(3, URLid);
        ps.executeUpdate();
        }
    }
   /*public static void indexWords(HashMap<String, Double> TF,String URL) throws SQLException {
       int URLid = URLid(URL);
       StringBuilder sql = new StringBuilder();
       for (Entry<String, Double> entry : TF.entrySet()) {
           sql.append("INSERT INTO Words(word,stem,TF,URLID) VALUES (\"").append(entry.getKey()).append("\",\"").append(Extract.stemS(entry.getKey())).append("\",").append(entry.getValue()).append(",").append(URLid).append(");");
       }
       System.out.println(sql.toString());
       connection.createStatement().executeUpdate(sql.toString());
   }*/
    private static int URLid(String URL) throws SQLException {
        String sql = "SELECT id FROM urls WHERE URL =  ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,URL);
        ResultSet result =  ps.executeQuery();
        if (result.next()) {
            return result.getInt(1);
        }
        return -1;
    }
}