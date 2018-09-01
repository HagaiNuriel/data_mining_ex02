package com.company;
import com.company.ArticleLine.e_tagType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArticleCompressor {

    private static final String whitespaceDelimeter = "\\s+";
    private static String path = "Resources";

    public  static void compressArticles(String path){
        int currIndex = 0;
        List<ArticleLine> articleLines = new ArrayList<>();
        try (Stream<Path> files = Files.list(Paths.get(path))) {
            long uniqueFileCount = (files.count() - 1)/3;
             List<Path> allFiles = files.collect(Collectors.toList());

            for (long i=0; i<uniqueFileCount;i++)
            {
                articleLines.addAll(compressFileTrio(allFiles, currIndex));
                currIndex += 3;
            }

            //TODO: all lines are aggregated, create new files from articleLines
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
        FileInputStream firstFileStream = new FileInputStream(paths.get(index).getFileName().toString());
        FileInputStream secondFileStream = new FileInputStream(paths.get(index + 1).getFileName().toString());
        FileInputStream thirdFileStream = new FileInputStream(paths.get(index + 2).getFileName().toString());

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
                articleLines.add(articleLine);
            }
        }

        return articleLines;
    }

    private static String filterLine(String strLine) {
        StringBuilder stringBuilder = new StringBuilder();

        //TODO: filter line and remove unwanted words

        return stringBuilder.toString();
    }
}
