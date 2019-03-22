/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucia
 */
public class SeoDwarfDemo {

    public static void main(String[] args) {

        try {
            File file = new File("./question.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                System.out.println(st);

                System.out.println("STRING:" + st);
                QASystem qas = new CanaliW2VQASystem("seodwarf_res/abstract_200_20.w2v.bin", "seodwarf_res/index/supportFiles/property_labels");
                //System.setProperty("kb.index.dir", "/home/lucia/nlp2sparql-data/dbpedia-processed/2015-10/dbpedia-processed_onlydbo_mini_e/index/"); //!!!
                System.setProperty("kb.index.dir", "./seodwarf_res/index/processed/");

                ArrayList<String> systAns = new ArrayList<String>();
                systAns = qas.getAnswer(st, null);

                for (String a : systAns) {
                    System.out.println("System = " + a);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeoDwarfDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SeoDwarfDemo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SeoDwarfDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void printQuery(String query) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./sparql_query.txt"));

            writer.write(query);
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(SeoDwarfDemo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
