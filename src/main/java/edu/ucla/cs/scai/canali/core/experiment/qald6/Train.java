package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.io.FileReader;

import com.google.gson.Gson;

public class Train {

	public static void main(String[] args) throws Exception {
		String lang = "en";
		String trainPath = "/home/gaetangate/Dev/nlp2sparql-data/qald6/train/qald-6-train-multilingual.json";
		FileReader reader = new FileReader(trainPath);
		Gson gson = new Gson();
		QALD6 qald6 = gson.fromJson(reader, QALD6.class);
		// System.out.println(qald6.dataset.id);

		int count = 0;
		Questions[] qsList = qald6.questions;
		for (Questions qs : qsList) {
			if (qs.onlydbo.equals("true")) {
				count++;
				System.out.print(qs.id + " ");
				for (Question q : qs.question) {
					if (q.language.equals(lang))
						System.out.println(q.string);
				}
			}
		}
		System.out.println(count);

	}

}
