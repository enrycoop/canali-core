package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.io.FileReader;

import com.google.gson.Gson;

public class Train {

	public static void main(String[] args) throws Exception {
		String lang = "en";
		String trainPath = "/home/lucia/nlp2sparql-data/qald6/train/qald-6-train-multilingual.json";
		FileReader reader = new FileReader(trainPath);
		Gson gson = new Gson();
		QALD6Model qald6 = gson.fromJson(reader, QALD6Model.class);
		// System.out.println(qald6.dataset.id);

		int count = 0;
		Questions[] qsList = qald6.questions;
		for (Questions qs : qsList) {
			//if (qs.onlydbo.equals("true")) {
				count++;
				System.out.print(qs.id + " ");
				for (Question q : qs.question) {
					if (q.language.equals(lang))
						System.out.println(q.string);
				}
			//}
                        for (Answers ans : qs.answers){
                           Head head = ans.head;
                           String[] vars = head.vars;
                           if (vars != null) 
                                for (String v : vars){
                                    System.out.println("v: " + v);
                                }
                           Results res = ans.results;
                           if (res != null){
                                Bindings[] bind = res.bindings;
                                for (Bindings b : bind) {
                                    Var v = b.var;
                                    System.out.println("type " + v.type);
                                }
                           }
                        }
		}
		System.out.println(count);

	}

}
