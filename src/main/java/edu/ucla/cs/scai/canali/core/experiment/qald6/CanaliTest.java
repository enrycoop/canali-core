/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author lucia
 */
public class CanaliTest {

    public static void main(String[] args) throws Exception {
        String lang = "en";
        String trainPath = "/home/lucia/nlp2sparql-data/qald6/test/qald-6-test-multilingual.json";
        String canaliPath = "/home/lucia/nlp2sparql-data/qald6/test/submission1_integer_ids.json";

        FileReader reader = new FileReader(trainPath);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        QALD6Model qald6 = gson.fromJson(reader, QALD6Model.class);

        reader = new FileReader(canaliPath);
        QALD6Model canali = gson.fromJson(reader, QALD6Model.class);

        QALD6Model newModel = new QALD6Model();

        newModel.dataset = qald6.dataset;
        System.out.println(newModel.dataset.id);

        Questions[] qsListQald = qald6.questions;
        Questions[] qsListCanali = canali.questions;
        //Questions[] qsListNew = new Questions[qsListQald.length];
        ArrayList<Questions> aListNew = new ArrayList<Questions>();
        
        int j = 0;
        for (int i = 0; i < qsListQald.length; i++) {
            if (qsListQald[i].onlydbo.equals("true")) {
                Questions qs = new Questions();
                qs.id = qsListQald[i].id;
                qs.answertype = qsListQald[i].answertype;
                qs.aggregation = qsListQald[i].aggregation;
                qs.onlydbo = qsListQald[i].onlydbo;
                qs.hybrid = qsListQald[i].hybrid;
                
                qs.question = qsListCanali[i].question;
                qs.query = qsListCanali[i].query;
                qs.answers = qsListCanali[i].answers;
                
                //qsListNew[j] = qs;
                aListNew.add(qs);
                j++;
            }
        }
       Questions[] qsListNew =  new Questions[aListNew.size()];
       qsListNew = aListNew.toArray(qsListNew);
       newModel.questions = qsListNew;
       PrintWriter out = new PrintWriter(new FileOutputStream("/home/lucia/nlp2sparql-data/qald6/test/qald-6-test-multilingual_rephrase.json", false), true);
       out.println(gson.toJson(newModel));
    }
}
