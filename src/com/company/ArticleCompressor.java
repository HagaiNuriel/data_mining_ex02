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


    private static String path = "Resources";

    public  static void CompressArticles(String path){
        int currIndex = 0;
        List<ArticleLine> articleLines = new ArrayList<>();
        try (Stream<Path> files = Files.list(Paths.get(path))) {
            long uniquefileCount = (files.count() - 1)/3;
             List<Path> allFiles = files.collect(Collectors.toList());

            for (long i=0; i<uniquefileCount;i++)
            {
                articleLines.addAll(CompressFileTrio(allFiles.subList(currIndex, currIndex + 2)));

                currIndex += 3;
            }


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


    private static Collection<? extends ArticleLine> CompressFileTrio(List<Path> paths) throws IOException {
        List<ArticleLine> articleLines = new ArrayList<>();
        ArticleLine articleLine = new ArticleLine();
        FileInputStream firstFileStream = new FileInputStream( paths.get(0).getFileName().toString());
        FileInputStream seocondFileStream =  new FileInputStream( paths.get(1).getFileName().toString());
        FileInputStream thirdFileStream = new FileInputStream( paths.get(2).getFileName().toString());
        DataInputStream in = new DataInputStream(firstFileStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;

        while ((strLine = br.readLine()) != null)   {

            String[] delims = strLine.split(" ");
            String first = delims[0];
            System.out.println("First word: "+first);
            if(isTag(first)) {
                articleLine.addTag(e_tagType.valueOf(first));
                articleLine.setText(filterLine(strLine));
                articleLines.add(articleLine);

            }






        }

//        try{
//            // Open the file that is the first
//            // command line parameter
//            FileInputStream fstream = new FileInputStream("RokFile.txt");
//            // Get the object of DataInputStream
//            DataInputStream in = new DataInputStream(fstream);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            String strLine;
//            //Read File Line By Line
//            while ((strLine = br.readLine()) != null)   {
//
//                String[] delims = strLine.split(" ");
//                String first = delims[0];
//                System.out.println("First word: "+first);
//
//            }
//            //Close the input stream
//            in.close();
//        }catch (Exception e){//Catch exception if any
//            System.err.println("Error: " + e.getMessage());
//        }
    }

    private static String filterLine(String strLine) {
        StringBuilder stringBuilder = new StringBuilder();


    }


}


    public static  long CountFiles(String path){



        try (Stream<Path> files = Files.list(Paths.get(path))) {
            return files.count();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
