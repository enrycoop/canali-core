/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import edu.ucla.cs.scai.canali.core.index.TokenIndex;

/**
 *
 * @author lucia
 */
class Result {

	private final String q;
	private final ArrayList<String> qaldAns;
	private final ArrayList<String> systAns;
	private final double[] metrics = new double[4];

	public Result(String q, ArrayList<String> qaldAns, ArrayList<String> systAns) {
		this.q = q;
		this.qaldAns = qaldAns;
		this.systAns = systAns;
		computeMetrics(qaldAns, systAns);
		System.out.println(metrics[0] + " " + metrics[1] + " " + metrics[2] + " " + metrics[3]);
	}

	public double[] getMetrics() {
		return metrics;
	}

	private void computeMetrics(ArrayList<String> qaldAns, ArrayList<String> systAns) {
		if (qaldAns.size() == 0)
			System.out.println(qaldAns.size());
		int corrAns = 0;
		for (String ans : systAns) {
			for (String qa : qaldAns) {
				if (qa.equals(ans))
					corrAns++;
			}
		}
		this.metrics[0] = corrAns / (double) qaldAns.size(); // Recall
		this.metrics[1] = corrAns / (double) systAns.size(); // Precision
		if (this.metrics[0] == 0 && this.metrics[1] == 0) {
			this.metrics[2] = 0d;
		} else {
			this.metrics[2] = (2 * this.metrics[0] * this.metrics[1]) / (this.metrics[0] + this.metrics[1]); // F-measure
		}
		this.metrics[3] = corrAns;
	}

}

public class Experimenter {

	public static void printMetricAvg(List<Result> results) {
		System.out.println("QA System - metrics average");
		System.out.println("# answered questions = " + results.size());

		double avgR = 0.0;
		double avgP = 0.0;
		double avgF = 0.0;
		int count = 0;
		for (int i = 0; i < results.size(); i++) {
			avgR += results.get(i).getMetrics()[0];
			avgP += results.get(i).getMetrics()[1];
			avgF += results.get(i).getMetrics()[2];
			count += results.get(i).getMetrics()[3];
		}
		avgR /= (double)results.size();
		avgP /= (double)results.size();
		avgF /= (double)results.size();

		System.out.println("Count = " + count);
		System.out.println("Recall = " + avgR);
		System.out.println("Precision = " + avgP);
		System.out.println("F-measure = " + avgF);
	}

	public static void main(String[] args) throws Exception {

		System.setProperty("kb.index.dir", "/home/gaetangate/Dev/nlp2sparql-data/dbpedia-processed/2015-10/index_onlydbo/");
		

		QASystem qas = new DummyQASystem();
		
		//new TokenIndex();
		//QASystem qas = new CanaliQASystem();

		//String testFilePath = "/home/lucia/nlp2sparql-data/qald6/submission1_integer_ids.json";
		String testFilePath = "/home/gaetangate/Dev/nlp2sparql-data/qald6/test/qald-6-test-multilingual_rephrase.json";
		//String testFilePath = "/home/gaetangate/Dev/nlp2sparql-data/qald6/test/qald-6-test-multilingual_errata.json";

		String lang = "en";

		FileReader reader = new FileReader(testFilePath);
		Gson gson = new Gson();
		QALD6Model qald6 = gson.fromJson(reader, QALD6Model.class);

		ArrayList<Result> results = new ArrayList<Result>();

		Questions[] qsList = qald6.questions;
		System.out.println(qsList.length);
		/*
		 * For each query in test set we get the ground truth answers and system
		 * answers
		 */
		for (Questions qs : qsList) {
			if (qs.onlydbo.equals("true")) {
				System.out.print(qs.id + " " + qs.answertype);

				/*
				 * System answers
				 */
				String query = "";
				ArrayList<String> systAns = new ArrayList<String>();
				for (Question q : qs.question) {
					if (q.language.equals(lang)) {
						query = q.string;
						System.out.println(" " + query);
						try {
							systAns = qas.getAnswer(q.string, qs.answertype);
						} catch (Exception e) {
							System.out.println(e);

						}
						if (systAns.size() == 0)
							System.out.println("WARNING - NO SYSTEM RESULTS");
					}
				}

				for (String a : systAns) {
					System.out.println("System = " + a);
				}

				/*
				 * Ground truth Answers
				 */

				ArrayList<String> qaldAns = new ArrayList<String>();

				if (qs.answers.length == 0) {
					qaldAns.add(QASystem.EMPTY_RESULT);
				} else {
					for (Answers ans : qs.answers) {
						if (!qs.answertype.equals("boolean")) {
							Results res = ans.results;
							if (res != null) {
								Bindings[] bind = res.bindings;
								for (Bindings b : bind) {
									Var v = b.var;
									qaldAns.add(v.value);
								}
							}
						} else {
							qaldAns.add(ans.bool);
						}
					}
				}

				for (String a : qaldAns) {
					System.out.println("Ground Truth = " + a);
				}

				Result res = new Result(query, qaldAns, systAns);
				//Result res = new Result(query, qaldAns, qaldAns);
				results.add(res);
			}
			System.out.println();
		}

		printMetricAvg(results);

	}
}
