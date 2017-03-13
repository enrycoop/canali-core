/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.nlpita.vectors.word2vec;

import di.uniba.it.nlpita.vectors.VectorStoreUtils;
import di.uniba.it.nlpita.vectors.VectorType;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author pierpaolo
 */
public class Word2VectTxt2Bin {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        String[] split;
        int dim = 0;
        if (reader.ready()) {
            split = reader.readLine().split("\\s+");
            dim = Integer.parseInt(split[1]);
        }
        DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(args[1])));
        outStream.writeUTF(VectorStoreUtils.createHeader(VectorType.REAL, dim, -1));
        while (reader.ready()) {
            split = reader.readLine().split("\\s+");
            outStream.writeUTF(split[0]);
            for (int i = 1; i < split.length; i++) {
                outStream.writeFloat(Float.parseFloat(split[i].replace(",", ".")));
            }
        }
        reader.close();
        outStream.close();
    }

}
