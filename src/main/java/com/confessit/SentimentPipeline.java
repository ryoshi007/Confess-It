package com.confessit;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.Properties;

public class SentimentPipeline {
    private static StanfordCoreNLP pipeline;

    /***
     * Initialize the sentiment tool in the Stanford CoreNLP pipeline.
     * Also initialize the tokenizer, dependency parser and sentence splitter needed to use the sentiment tool.
     * This method is used to customize the pipeline that will be used for sentiment analysis.
     */
    public void init() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    /***
     * Analyse the given sentences and give mark based on the sentiment of the strings.
     * @param content is the strings of the submitted post.
     * @return the sentiment score in the double type
     */
    public double estimateSentiment(String content) {

        //Passing content for processing based on the pipeline object
        Annotation annotation = pipeline.process(content);
        double totalSentimentScore = 0;

        //Obtain the number of sentences
        int length = annotation.get(CoreAnnotations.SentencesAnnotation.class).size();

        //Iterate over the annotation object to get a sentence-level CoreMap object on each iteration
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {

            //Obtain a Tree object containing the sentiment annotations used to determine the sentiment of the sentence
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

            //Pass Tree object to RNN for extracting the number coe of the predicted sentiment for the sentence
            //4 - Very positive, 3 - Positive, 2 - Neutral, 1 - Negative, 0 - Very negative
            totalSentimentScore += RNNCoreAnnotations.getPredictedClass(tree);
        }
        return totalSentimentScore/length;
    }
}
