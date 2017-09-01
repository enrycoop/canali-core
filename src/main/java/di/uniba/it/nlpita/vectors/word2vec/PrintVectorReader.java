/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.nlpita.vectors.word2vec;

import di.uniba.it.nlpita.vectors.MemoryVectorReader;
import di.uniba.it.nlpita.vectors.ObjectVector;
import di.uniba.it.nlpita.vectors.Vector;
import di.uniba.it.nlpita.vectors.VectorReader;
import di.uniba.it.nlpita.vectors.utils.SpaceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class PrintVectorReader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
        	String modelPath = "/home/gaetangate/Dev/nlp2sparql-data/abstract_200_20.w2v.bin";
        	//String modelPath = "/home/gaetangate/Dev/word2vec/models/wiki_regexp/enwiki_20161220_skip_300.bin";
        	VectorReader vr = new MemoryVectorReader(new File(modelPath));
            vr.init();
            Iterator<String> words = vr.getKeys();
            while(words.hasNext()) {
            	System.out.println(words.next());
            }

            

        } catch (IOException ex) {
            Logger.getLogger(PrintVectorReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
