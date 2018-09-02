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
    private static String path = "Resources/";
    private static Long lineCount = 0l;

    public static void compressArticles(String path) throws IOException {
        String fileName = "newFile.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        int currIndex = 0;
        Integer lineCount = 0;
        List<ArticleLine> articleLines = new ArrayList<>();
        try (Stream<Path> files = list(Paths.get(path))) {
            long uniqueFileCount = (files.count() - 1)/3;
            List<Path> allFiles = list(Paths.get(path)).collect(Collectors.toList());
            Collections.sort(allFiles, (a,b) -> a.getFileName().toString().compareToIgnoreCase(b.getFileName().toString()));
             //List<Path> allFiles = files.collect(Collectors.toList());

            for (long i=0; i<uniqueFileCount;i++)
            {
                articleLines.addAll(compressFileTrio(allFiles, currIndex));
                currIndex += 3;

            }

            for(ArticleLine line : articleLines){
                writer.append(line.getProminentTag() + line.getText());
                writer.newLine();
            }

            writer.close();

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
        FileInputStream firstFileStream = new FileInputStream("Resources/" + paths.get(index).getFileName().toString());
        FileInputStream secondFileStream = new FileInputStream("Resources/" + paths.get(index + 1).getFileName().toString());
        FileInputStream thirdFileStream = new FileInputStream("Resources/" + paths.get(index + 2).getFileName().toString());

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

//        for(e_tagType tagType : e_tagType.values()){
//            stopWordsList.add(tagType.name());
//        }

        //stopWordsList.add("###");

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
