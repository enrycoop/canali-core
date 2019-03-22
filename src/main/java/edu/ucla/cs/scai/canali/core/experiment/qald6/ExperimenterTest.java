/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.util.ArrayList;

public class ExperimenterTest {

    public static void main(String[] args) throws Exception {
        //String path = "/home/lucia/data/nlp2sparql_data/";
        String path = "";

        QASystem qas = new CanaliW2VQASystem(path + "/home/lucia/data/w2v/abstract_200_20.w2v.bin", path + "/home/lucia/data/seodwarf/index/supportFiles/property_labels");
        //System.setProperty("kb.index.dir", "/home/lucia/nlp2sparql-data/dbpedia-processed/2015-10/dbpedia-processed_onlydbo_mini_e/index/"); //!!!
        System.setProperty("kb.index.dir", path + "/home/lucia/data/seodwarf/index/processed/");

        
        String query = "What is the online resource of Cretan Sea ?";
        //String query = "Show me the Image having hasOnlineResource aaa";
        
        ArrayList<String> systAns = new ArrayList<String>();
        systAns = qas.getAnswer(query, null);

        for (String a : systAns) {
            System.out.println("System = " + a);
        }

    }
}
