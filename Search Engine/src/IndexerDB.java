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
    public static void indexWords(HashMap<String, Double> TF,String URL) throws SQLException {
         int URLid=URLid(URL);
         String sql = "INSERT ignore INTO Words(stem,TF,URLID) VALUES "+helper(TF.size());
         PreparedStatement ps = connection.prepareStatement(sql);
         int counter=1;
         for (Entry<String, Double> entry : TF.entrySet()) {
             ps.setString(counter, entry.getKey());
             ps.setDouble(counter+1, entry.getValue());
             ps.setInt(counter+2, URLid);
             counter+=3;
         }
         ps.executeUpdate();
    }
    public static String helper(int size){
        StringBuilder s=new StringBuilder();
        s.append("(?,?,?)");
        for (int i=0;i<size-1;i++){
            s.append(",(?,?,?)");
        }
        return s.toString();
    }
     /*
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
     */
}
