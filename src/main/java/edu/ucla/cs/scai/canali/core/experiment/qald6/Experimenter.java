/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author lucia
 */
class Result {

    private final String q;
    private final ArrayList<String> qaldAns;
    private final ArrayList<String> systAns;
    private final Integer[] metrics = new Integer[3];

    public Result(String q, ArrayList<String> qaldAns, ArrayList<String> systAns) {
        this.q = q;
        this.qaldAns = qaldAns;
        this.systAns = systAns;
        computeMetrics(qaldAns, systAns);
    }

    private void computeMetrics(ArrayList<String> qaldAns, ArrayList<String> systAns) {
        int corrAns = 0;
        for (String ans : systAns) {
            if (qaldAns.contains(ans)) {
                corrAns++;
            }
        }
        this.metrics[0] = corrAns / qaldAns.size();
        this.metrics[1] = corrAns / systAns.size();
        this.metrics[2] = (2 * this.metrics[0] * this.metrics[1]) / (this.metrics[0] + this.metrics[1]);
    }
}

public class Experimenter {

    public static void main(String[] args) throws Exception {
        String lang = "en";
        FileReader reader = new FileReader("/home/lucia/nlp2sparql-data/qald6/submission1_integer_ids.json");
        Gson gson = new Gson();
        QALD6Model qald6 = gson.fromJson(reader, QALD6Model.class);
        ArrayList<Result> results = new ArrayList<>();
        
        Questions[] qsList = qald6.questions;
        for (Questions qs : qsList) {
            if (qs.onlydbo.equals("true")) {
                System.out.print(qs.id + " ");
                String query = "";
                ArrayList<String> systAns = new ArrayList<String>();
                for (Question q : qs.question) {
                    if (q.language.equals(lang)) {
                        query = q.string;
                        QAsystem qas = new CanaliQAsystem();
                        systAns = qas.getAnswer(q.string);

                    }
                }
                ArrayList<String> qaldAns = new ArrayList<String>();
                for (Answers ans : qs.answers) {
                    Results res = ans.results;
                    if (res != null) {
                        Bindings[] bind = res.bindings;
                        for (Bindings b : bind) {
                            Var v = b.var;
                            qaldAns.add(v.Value);
                        }
                    }
                }
                Result res = new Result(query, qaldAns, systAns);
                results.add(res);
            }
        }

    }

}
