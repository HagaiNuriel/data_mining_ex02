package com.company;
import com.company.ArticleLine.e_tagType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.list;

public class ArticleCompressor {

    private static final String whitespaceDelimeter = "\\s+";
    private static final String stopWordsFileName = "stopwords.txt";
    private static final String labeledFileName = "labeled_dataset.txt";
    private static final String unlabeledFileName = "unlabled_dataset.txt";
    private static final String labeledVectorsFileName = "labeled_dataset_vectors.txt";
    private static final String unlabeledVectorsFileName = "unlabeled_dataset_vectors.txt";
    private static String path = "Resources/";
    private static List<String> uniqueWords = new ArrayList<>();


    private static List<String> countUniqueWords(String fileName) throws Exception {
        HashMap<String, Boolean> words = new HashMap<>();
        FileInputStream firstFileStream = new FileInputStream(fileName);
        DataInputStream in = new DataInputStream(firstFileStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String currLine;

        while((currLine = br.readLine()) != null){
            for(String word : currLine.split("[-!,~\\s]+")){
                if(!words.containsKey(word)){
                    words.put(word, true);
                }
            }
        }

        br.close();

        return  new ArrayList<>(words.keySet());

    }

    public static void createVectorFile(String originalFileName, String newFileName) throws Exception {
        List<String> uniqueWords = countUniqueWords(originalFileName);
        HashMap<String,Integer> uniqueWordLinesCount = countLinesForUniqueWords(uniqueWords, originalFileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));

        BufferedReader br = new BufferedReader(new FileReader(originalFileName));
        ArticleLineVector currArticleLineVector;
        String currLine;
        Long linesCount = br.lines().count();
        br.close();
        BufferedReader br2 = new BufferedReader(new FileReader(originalFileName));

        while((currLine = br2.readLine()) != null){
            currArticleLineVector = createArticleLineVector(currLine);
            writeVectorLine(writer, uniqueWords, currArticleLineVector,
                    uniqueWordLinesCount, linesCount);
        }

        writer.close();
        br2.close();
    }

    private static void writeVectorLine(BufferedWriter writer, List<String> uniqueWords,
                                        ArticleLineVector articleLineVector, HashMap<String, Integer> uniquWordLinesCount, Long linesCount) throws IOException {
        Double tfResult, idfResult, finalResult;
        Boolean putComa = false;
        for (String word : uniqueWords) {
            tfResult = 0.5 + (0.5 * (articleLineVector.getWordCount(word) /
                    articleLineVector.getWordCount(articleLineVector.getProminentWord())));
            idfResult = Math.log((double)linesCount / (double)(1 + uniquWordLinesCount.get(word)));

            finalResult = tfResult * idfResult;

            if(putComa){
                writer.append(",");
            }else
                putComa = true;

            writer.append(finalResult.toString());
        }

        writer.newLine();
    }

    private static HashMap<String,Integer> countLinesForUniqueWords(List<String> uniqueWords, String originalFileName) throws IOException {
        HashMap<String, Integer> wordsLinesCount = new HashMap<>();
        FileInputStream firstFileStream = new FileInputStream(originalFileName);
        DataInputStream in = new DataInputStream(firstFileStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        ArrayList<String> lines = br.lines().collect(Collectors.toCollection(ArrayList::new));
        String currLine;
        Integer count;

        for (String word : uniqueWords) {
            for (String line : lines) {
                if(line.contains(word)){
                    count = wordsLinesCount.containsKey(word) ? wordsLinesCount.get(word) + 1 : 1;
                    wordsLinesCount.put(word, count);
                }
            }
        }

        br.close();
        return wordsLinesCount;
    }

    private static ArticleLineVector createArticleLineVector(String currLine) {
        ArticleLineVector vector = new ArticleLineVector();

        for(String word : currLine.split("[-!,~\\s]+")){
           vector.countWord(word);
        }

        return vector;
    }

    public static void compressArticles(String path) throws IOException {
        BufferedWriter labeledWriter = new BufferedWriter(new FileWriter(labeledFileName, false));
        BufferedWriter unlabeledWriter = new BufferedWriter(new FileWriter(unlabeledFileName, false));
        int currIndex = 0;
        int newLineFlag = -1;
        List<ArticleLine> articleLines = new ArrayList<>();
        try (Stream<Path> files = list(Paths.get(path))) {
            long uniqueFileCount = (files.count() - 1)/3;
            List<Path> allFiles = list(Paths.get(path)).collect(Collectors.toList());
            Collections.sort(allFiles, (a,b) -> a.getFileName()
                    .toString().compareToIgnoreCase(b.getFileName().toString()));

            for (long i=0; i < uniqueFileCount; i++)
            {
                articleLines.addAll(compressFileTrio(allFiles, currIndex));
                currIndex += 3;
            }

            for(ArticleLine line : articleLines){
                if(newLineFlag > -1){
                    labeledWriter.newLine();
                    unlabeledWriter.newLine();
                }else{
                    newLineFlag++;
                }

                labeledWriter.append(line.getProminentTag() + "\t" + line.getText().trim());
                unlabeledWriter.append(line.getText().trim());
            }

            labeledWriter.close();
            unlabeledWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static  boolean isTag(String word) {
      for (e_tagType c : e_tagType.values()) {
          if (c.name().equals(word)) {
              return true;
          }
      }

      return false;
    }


    private static Collection<? extends ArticleLine> compressFileTrio(List<Path> paths, Integer index) throws IOException {
        List<ArticleLine> articleLines = new ArrayList<>();
        ArticleLine articleLine;

        FileInputStream firstFileStream = new FileInputStream
                ( path + paths.get(index).getFileName().toString());
        FileInputStream secondFileStream = new FileInputStream
                (path + paths.get(index + 1).getFileName().toString());
        FileInputStream thirdFileStream = new FileInputStream
                (path + paths.get(index + 2).getFileName().toString());

        DataInputStream in1 = new DataInputStream(firstFileStream);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));

        DataInputStream in2 = new DataInputStream(secondFileStream);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

        DataInputStream in3 = new DataInputStream(thirdFileStream);
        BufferedReader br3 = new BufferedReader(new InputStreamReader(in3));
        String strLine1, strLine2, strLine3;

        while ((strLine1 = br1.readLine()) != null &&
                (strLine2 = br2.readLine()) != null &&
                (strLine3 = br3.readLine()) != null) {

            String first = strLine1.split(whitespaceDelimeter)[0];
            System.out.println("First word: " + first);
            articleLine = new ArticleLine();

            if (isTag(first)) {
                articleLine.addTag(e_tagType.valueOf(first));
                articleLine.addTag(e_tagType.valueOf(strLine2.split(whitespaceDelimeter)[0]));
                articleLine.addTag(e_tagType.valueOf(strLine3.split(whitespaceDelimeter)[0]));
                articleLine.setText(filterLine(strLine1));
                articleLine.setLastTag(e_tagType.valueOf(strLine3.split(whitespaceDelimeter)[0]));
                articleLines.add(articleLine);
            }
        }

        br1.close();
        br2.close();
        br3.close();

        return articleLines;
    }

    private static List<String> stopWords() throws IOException {
        FileInputStream stopWords = new FileInputStream(path + stopWordsFileName);
        String word;
        DataInputStream in = new DataInputStream(stopWords);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> stopWordsList = new ArrayList<>();

        while((word = br.readLine()) != null){
            stopWordsList.add(word);
            stopWordsList.add(word.substring(0, 1).toUpperCase() + word.substring(1));
        }

        return stopWordsList;
    }

    private static String filterLine(String strLine) throws IOException {
        List<String> stopWords = stopWords();

        for(String word : stopWords){
            if(strLine.contains(" " + word + " ")){
                strLine = strLine.replaceAll(" " + word + " ", " ");
            }

            if(strLine.contains("\t" + word + " ")){
                strLine = strLine.replaceAll("\\t" + word + " ", "\t");
            }

            if(strLine.contains(" " + word + "\n")){
                strLine = strLine.replaceAll(" " + word + "\\n", "\n");
            }
        }

        for(e_tagType tagType : e_tagType.values()){
            if(strLine.contains(tagType.name())){
                strLine = strLine.replaceAll(tagType.name(), "");
            }
        }

        return strLine;
    }


}
