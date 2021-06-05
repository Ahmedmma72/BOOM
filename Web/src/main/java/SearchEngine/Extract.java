package SearchEngine;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import opennlp.tools.stemmer.PorterStemmer;

public class Extract {
 public static HashSet<String> stoppingWords;

 public Extract() {
 }

 private static void getStoppingWords() {
     stoppingWords = new HashSet();

     try {
         File f = new File("src/StoppingWords.txt");
         Scanner s = new Scanner(f);

         while(s.hasNextLine()) {
             String data = s.nextLine();
             stoppingWords.add(data);
         }

         s.close();
     } catch (FileNotFoundException var3) {
         System.out.println("An error occurred.");
         var3.printStackTrace();
     }

 }

 public static ArrayList<String> removeStoppingWords(ArrayList<String> listOfWords) throws FileNotFoundException {
     getStoppingWords();
     ArrayList<String> newListOfWords = new ArrayList();
     Iterator var2 = listOfWords.iterator();

     while(var2.hasNext()) {
         String Word = (String)var2.next();
         if (!stoppingWords.contains(Word)) {
             newListOfWords.add(Word);
         }
     }

     return newListOfWords;
 }

 public static ArrayList<String> SplitSentence(String sentence) {
     String[] s = sentence.split(" ");
     return new ArrayList(Arrays.asList(s).subList(0, s.length));
 }

 public static String stemS(String string) {
     PorterStemmer s = new PorterStemmer();
     return s.stem(string);
 }

 public static String escapeMetaCharacters(String inputString) {
     String[] metaCharacters = new String[]{"\"", "'", "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&", "%", "_", "!", ":", ";", "~", "`", "/"};
     String[] var2 = metaCharacters;
     int var3 = metaCharacters.length;

     for(int var4 = 0; var4 < var3; ++var4) {
         String metaCharacter = var2[var4];
         if (inputString.contains(metaCharacter)) {
             inputString = inputString.replace(metaCharacter, "");
         }
     }

     for(int i = 0; i < inputString.length(); ++i) {
         int asc = inputString.charAt(i);
         if (asc > 255) {
             inputString = inputString.replace(String.valueOf(inputString.charAt(i)), "");
         }
     }

     return inputString.replaceAll("(?m)^[ \t]*\r?\n", "");
 }
}
