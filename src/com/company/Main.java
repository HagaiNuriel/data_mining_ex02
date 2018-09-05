package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
	// write your code here
        ArticleCompressor.compressArticles("Resources");
        ArticleCompressor.createVectorFile("labeled_dataset.txt", "labeled_dataset_vectors.txt");
        ArticleCompressor.createVectorFile("unlabeled_dataset.txt", "unlabeled_dataset_vectors.txt");
    }
}
