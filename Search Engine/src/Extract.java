import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import opennlp.tools.stemmer.PorterStemmer;

public class Extract {

    //use hashset instead of arraylist because of the performance of contain
    public static HashSet<String>stoppingWords;

    private static void getStoppingWords(){
            stoppingWords=new HashSet<>();
            try{
            File f = new File("src/StoppingWords.txt");
            Scanner s = new Scanner(f);
            while (s.hasNextLine()){
                String data = s.nextLine();
               // System.out.println(data);
                stoppingWords.add(data);
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static ArrayList<String> removeStoppingWords(ArrayList<String> listOfWords) throws FileNotFoundException {
        getStoppingWords();
        ArrayList<String>newListOfWords=new ArrayList<>();
        for (String Word : listOfWords) {
            if (!stoppingWords.contains(Word))
                newListOfWords.add(Word);
        }
        return newListOfWords;
    }
    public static ArrayList<String> splitSentence(String sentence){
        sentence=sentence.toLowerCase();
        String[]  s = sentence.split(" ");
        return new ArrayList<String>(Arrays.asList(s).subList(0, s.length));
    }
    public static String stemS(String string){
        PorterStemmer s = new PorterStemmer();
        return  s.stem(string);
    }
    public static String escapeMetaCharacters(String inputString) {
        inputString=inputString.replaceAll("(?m)^[ \t]*\r?\n", "");
        inputString=inputString.replaceAll("[^A-Za-z ]","");
        inputString=inputString.replaceAll("\\s+", " ");
        return inputString.trim();
    }
    public static HashMap<String,Double> calcTF(ArrayList<String> listOfWords, int countOfWords){

        HashMap<String,Double> TF = new HashMap<>();
        if(countOfWords!=0) {
            for (String word : listOfWords) {
                String sword=Extract.stemS(word);
                if (TF.containsKey(sword)) {
                    TF.put(sword, TF.get(sword) + 1.0);
                }
                else {
                    TF.put(sword, 1.0);
                }
            }
            //Normalize TF
           // int count=0;
            for (Map.Entry<String, Double> entry : TF.entrySet()) {
                entry.setValue(entry.getValue() / countOfWords);
            }
        }
        return TF;
    }
}
