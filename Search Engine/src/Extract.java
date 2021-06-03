import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
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
    public static ArrayList<String> SplitSentence(String sentence){
        String[]  s = sentence.split(" ");
        return new ArrayList<String>(Arrays.asList(s).subList(0, s.length));
    }
    public static String stemS(String string){
        PorterStemmer s = new PorterStemmer();
        return  s.stem(string);
    }
    public static String escapeMetaCharacters(String inputString) {
        final String[] metaCharacters = {"\"", "'", "\\", "^", "$", "{", "}", "[", "]", "(", ")",
                ".", "*", "+", "?", "|", "<", ">", "-", "&", "%"};
        for (String metaCharacter : metaCharacters) {
            if (inputString.contains(metaCharacter)) {
                inputString = inputString.replace(metaCharacter, " ");
            }
        }
        for(int i=0;i<inputString.length();i++){
            int asc=inputString.charAt(i);
            if(asc > 255){
                inputString=inputString.replace(String.valueOf(inputString.charAt(i)),"");
            }
        }
        return inputString.replaceAll("(?m)^[ \t]*\r?\n", "");
    }

}
