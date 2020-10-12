package printcalltree;



/* libraries */
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Character.getNumericValue;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
// for jframe stuff
import java.awt.Canvas;
import java.awt.Graphics;
import javax.swing.JFrame;

public class PrintCallTree {


    /* global variables */
    

    /* ****************************************************************************************  */
    /* methods */
    private static String removeCommentsFromString(String myString) {
        String returnString = new String();
        String returnStringFINAL = new String();
        boolean inComment = false;
        StringBuilder sb = new StringBuilder();
        for (int ii = 0; ii < myString.length() - 1; ii++) {
            if (inComment) {
                if ((myString.charAt(ii) == '*') && (myString.charAt(ii + 1) == '/')) {
                    inComment = false;
                }
            } else {
                if ((myString.charAt(ii) == '/') && (myString.charAt(ii + 1) == '*')) {
                    inComment = true;
                } else {
                    sb.append(myString.charAt(ii));
                }
            }
        }
        returnString = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        for (int jj = 0; jj < returnString.length(); jj++) {
            if ((returnString.charAt(jj) == '/') || (returnString.charAt(jj) == '*')) {

            } else {
                sb2.append(returnString.charAt(jj));
            }
        }
        returnStringFINAL = sb2.toString();
        return returnStringFINAL;
    }

    private static String removeParenthesisFromString(String myString) {
        String returnString = new String();
        boolean inParenthesis = false;
        int recursion = 0;
        StringBuilder sb = new StringBuilder();
        for (int ii = 0; ii < myString.length(); ii++) {
            if (inParenthesis) {
                if ((myString.charAt(ii) == ')') && (recursion == 1)) {
                    recursion--;
                    inParenthesis = false;
                    sb.append(myString.charAt(ii));
                } else {
                    if ((myString.charAt(ii) == ')')) {
                        recursion--;
                    } else {
                        if (((myString.charAt(ii) == '('))) {
                            recursion++;
                        }
                    }
                }
            } else {
                if ((myString.charAt(ii) == '(')) {
                    recursion++;
                    inParenthesis = true;
                    sb.append(myString.charAt(ii));
                } else {
                    sb.append(myString.charAt(ii));
                }
            }
        }
        returnString = sb.toString();
        return returnString;
    }

    private static String removeBracketsFromString(String myString) {
        String returnString = new String();
        boolean inBrackets = false;
        int recursion = 0;
        StringBuilder sb = new StringBuilder();
        for (int ii = 0; ii < myString.length(); ii++) {
            if (inBrackets) {
                if ((myString.charAt(ii) == '}') && (recursion == 1)) {
                    recursion--;
                    inBrackets = false;
                    sb.append(myString.charAt(ii));
                } else {
                    if ((myString.charAt(ii) == '}')) {
                        recursion--;
                    } else {
                        if ((myString.charAt(ii) == '{')) {
                            recursion++;
                        }
                    }
                }
            } else {
                if ((myString.charAt(ii) == '{')) {
                    recursion++;
                    inBrackets = true;
                    sb.append(myString.charAt(ii));
                } else {
                    sb.append(myString.charAt(ii));
                }
            }
        }
        returnString = sb.toString();
        return returnString;
    }

    private static String removeSpacesFromString(String myString) {
        String returnString = new String();
        StringBuilder sb = new StringBuilder();
        for (int ii = 0; ii < myString.length(); ii++) {
            if ((myString.charAt(ii) == ' ')) {
            } else {
                sb.append(myString.charAt(ii));
            }
        }
        returnString = sb.toString();
        return returnString;
    }

    private static List<String> readFile(String filename) {
        List<String> records = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
            return records;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    private static void filterListWithDotCAndDotHFiles(List<Path> list_in, List<Path> list_out) {
        int counter = 0;
        for (int i = 0; i < list_in.size(); i++) {
            if ((list_in.get(i).toString().charAt(list_in.get(i).toString().length() - 2) == '.')
                    && (list_in.get(i).toString().charAt(list_in.get(i).toString().length() - 1) == 'c')
                    || ((list_in.get(i).toString().charAt(list_in.get(i).toString().length() - 2) == '.')
                    && (list_in.get(i).toString().charAt(list_in.get(i).toString().length() - 1) == 'h'))) {
                //System.out.println(list_in.get(i));
                list_out.add(counter, list_in.get(i));
                counter++;
            }
        }
    }

    private static void getListOfFiles(String args, List<Path> list) throws IOException {
        String pathString = args;
        Files.walkFileTree(Paths.get(pathString), new HashSet<>(), 999, new FileVisitor<Path>() {
            int counter = 0;
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                //System.out.println("preVisitDirectory: " + dir);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                //System.out.println("visitFile: " + file);
                list.add(counter, file);
                counter++;
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                System.out.println("visitFileFailed: " + file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                //System.out.println("postVisitDirectory: " + dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /* main method */
    public static void main(String[] args) throws IOException {
        String path;	// a pop up window needs to come and choose there the folder of the scanned files
        path = "./sampleCode_IBC"; // for now this is edited
        List<Path> listOfFiles = new ArrayList<Path>();
        List<Path> CleanlistOfFiles = new ArrayList<Path>();
        getListOfFiles(path, listOfFiles);
        List<Integer> numberOfReferencesInListOfFiles = new ArrayList<Integer>();
        List<Integer> numberOfCallsInListOfFiles = new ArrayList<Integer>();
        List<List<Integer>> listOfCallersForListOfFiles = new ArrayList<List<Integer>>();
        filterListWithDotCAndDotHFiles(listOfFiles, CleanlistOfFiles);
        System.out.println("total number of files: " + listOfFiles.size() + "\ntotal .c&.h files: " + CleanlistOfFiles.size());
        for (int i = 0; i < CleanlistOfFiles.size(); i++) {
            System.out.println("file " + i + ": " + CleanlistOfFiles.get(i));
	}
        //make a list of file names only
        ArrayList<String> CleanListOfFileNames = new ArrayList<String>();
        for (int j = 0; j < CleanlistOfFiles.size(); j++) {
            StringBuilder SB_ForFileName = new StringBuilder();
            int ll = (CleanlistOfFiles.get(j).toString().length()) - 1;
            int mm = ll;
            while (((int) (CleanlistOfFiles.get(j).toString().charAt(mm)) != 92)) {
                SB_ForFileName.append(CleanlistOfFiles.get(j).toString().charAt(mm));
                mm--;
            }
            String FileName = new String();
            FileName = SB_ForFileName.reverse().toString();
            SB_ForFileName.delete(0, SB_ForFileName.length());
            CleanListOfFileNames.add(FileName);
        }
        System.out.println("total number of file names: " + CleanListOfFileNames.size());

        //make a list of Strings, where each string is the whole file (for every one of the previous list
        ArrayList<String> FileInOneLineOfString = new ArrayList<String>();
        for (int i = 0; i < CleanlistOfFiles.size(); i++) {
            List<String> FileAsListOfString = new ArrayList<String>();
            FileAsListOfString = readFile(CleanlistOfFiles.get(i).toString());
            String line = new String();
            for (int j = 0; j < FileAsListOfString.size(); j++) {
                line = line + FileAsListOfString.get(j);
            }
            FileInOneLineOfString.add(line);
        }
        
        System.out.println("total number of String lines: " + FileInOneLineOfString.size());
        for (int i = 0; i < FileInOneLineOfString.size(); i++) {
            System.out.println("length in chars of file " + CleanlistOfFiles.get(i).toString() + ":\n" + FileInOneLineOfString.get(i).length());
        }
        

        //make a second list (copy of the first) and clean it to read the declarations of calls
        //then for each declaration find the file that also calls it
        ArrayList<String> Cleaned_FileInOneLineOfString = new ArrayList<String>();
        for (int k = 0; k < FileInOneLineOfString.size(); k++) {
            String temp_fileString = new String();
            temp_fileString = FileInOneLineOfString.get(k);
            temp_fileString = removeCommentsFromString(temp_fileString);
            temp_fileString = removeBracketsFromString(temp_fileString);
            temp_fileString = removeParenthesisFromString(temp_fileString);
            //temp_fileString = removeSpacesFromString(temp_fileString);
            Cleaned_FileInOneLineOfString.add(temp_fileString);
        }

        //text file, should be opening in default text editor
        File reportFile = new File("./calls_" + path.substring(2, path.length()) + ".xml");
        //first check if Desktop is supported by Platform or not
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Fatal error: Desktop is not supported!");
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (reportFile.exists()) {
            desktop.open(reportFile);
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile, true));
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
        writer.append("<callReport>" + "\n");

        for (int v = 0; v < Cleaned_FileInOneLineOfString.size(); v++) {
            // counter for number of references
            int ref_counter = 0;
            
            writer.append("  <" + CleanListOfFileNames.get(v) + " path=\"" + CleanlistOfFiles.get(v) + "\" >" + "\n");
            
                System.out.println(CleanlistOfFiles.get(v));
            
            
                System.out.println(CleanListOfFileNames.get(v));
            
            
                System.out.println(Cleaned_FileInOneLineOfString.get(v));
            
            
                System.out.println(FileInOneLineOfString.get(v));
            
            String currentWord = new String();
            StringBuilder sb = new StringBuilder();
            int defFunctionsCounter = 0;
            for (int ii = 0; ii < Cleaned_FileInOneLineOfString.get(v).length() - 3; ii++) {
                if (((Cleaned_FileInOneLineOfString.get(v).charAt(ii) == '(') && (Cleaned_FileInOneLineOfString.get(v).substring(ii, ii + 4).equals("(){}")))
                        || ((Cleaned_FileInOneLineOfString.get(v).charAt(ii) == '(') && (Cleaned_FileInOneLineOfString.get(v).substring(ii, ii + 5).equals("( ){}")))
                        || ((Cleaned_FileInOneLineOfString.get(v).charAt(ii) == '(') && (Cleaned_FileInOneLineOfString.get(v).substring(ii, ii + 5).equals("() {}")))
                        || ((Cleaned_FileInOneLineOfString.get(v).charAt(ii) == '(') && (Cleaned_FileInOneLineOfString.get(v).substring(ii, ii + 5).equals("(){ }")))) {
                    defFunctionsCounter++;
                    int jj = ii - 1;
                    while ((Cleaned_FileInOneLineOfString.get(v).charAt(jj) == '_')
                            || ((Cleaned_FileInOneLineOfString.get(v).charAt(jj) >= 'A') && (Cleaned_FileInOneLineOfString.get(v).charAt(jj) <= 'Z'))
                            || ((Cleaned_FileInOneLineOfString.get(v).charAt(jj) >= 'a') && (Cleaned_FileInOneLineOfString.get(v).charAt(jj) <= 'z'))
                            || ((Cleaned_FileInOneLineOfString.get(v).charAt(jj) >= '0') && (Cleaned_FileInOneLineOfString.get(v).charAt(jj) <= '9'))) {
                        sb.append(Cleaned_FileInOneLineOfString.get(v).charAt(jj));
                        jj--;
                    }
                    currentWord = sb.reverse().toString();
                    writer.append("    <" + currentWord + ">" + "\n");
                    
                        System.out.println(currentWord);
                    
                    for (int ff = 0; ff < FileInOneLineOfString.size(); ff++) {
                        if ((ff != v)
                                && (CleanListOfFileNames.get(ff).subSequence(0, CleanListOfFileNames.get(ff).length() - 3).equals(CleanListOfFileNames.get(v).subSequence(0, CleanListOfFileNames.get(v).length() - 3)) == false)
                                && (FileInOneLineOfString.get(ff).contains(currentWord.subSequence(0, currentWord.length())))) {
                            
                                System.out.println("  " + v + " | " + ff + " | " + CleanlistOfFiles.get(ff));
                            
                            writer.append("      <" + CleanListOfFileNames.get(ff) + ">" + "\n");
                            writer.append("        <" + CleanlistOfFiles.get(ff) + ">" + "\n");
                            writer.append("      </" + CleanListOfFileNames.get(ff) + ">" + "\n");
                            // ff is the caller, v is the callee
                            //listOfCallersForListOfFiles.get(v).add(ff);
                            ref_counter++;
                        }
                    }
                    writer.append("    </" + currentWord + ">" + "\n");
                    sb.delete(0, sb.length()); // for some reason it does not need - 1 (???)
                    currentWord = "";
                }
            }
            
                System.out.println("Functions found: " + defFunctionsCounter);
            
            
                System.out.println("References found: " + ref_counter);
            
            numberOfReferencesInListOfFiles.add((int) v, (int) ref_counter);
            numberOfCallsInListOfFiles.add((int) v, (int) defFunctionsCounter);
            ref_counter = 0;
            defFunctionsCounter = 0;
            writer.append("  </" + CleanListOfFileNames.get(v) + ">" + "\n");
        }
        writer.append("</callReport>");
        writer.close();


    }

}