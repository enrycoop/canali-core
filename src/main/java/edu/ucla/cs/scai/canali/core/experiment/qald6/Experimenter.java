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

/**
 *
 * @author lucia
 */
class Result {

	private final String q;
	private final ArrayList<String> qaldAns;
	private final ArrayList<String> systAns;
	private final double[] metrics = new double[3];

	public Result(String q, ArrayList<String> qaldAns, ArrayList<String> systAns) {
		this.q = q;
		this.qaldAns = qaldAns;
		this.systAns = systAns;
		computeMetrics(qaldAns, systAns);
	}

	public double[] getMetrics() {
		return metrics;
	}

	private void computeMetrics(ArrayList<String> qaldAns, ArrayList<String> systAns) {
		if (qaldAns.size() == 0)
			System.out.println(qaldAns.size());
		int corrAns = 0;
		for (String ans : systAns) {
			if (qaldAns.contains(ans)) {
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
	}

}

public class Experimenter {

	public static void printMetricAvg(List<Result> results) {
		System.out.println("QA System - metrics average");
		System.out.println("# answered questions = " + results.size());

		double avgR = 0;
		double avgP = 0;
		double avgF = 0;
		for (int i = 0; i < results.size(); i++) {
			avgR += results.get(i).getMetrics()[0];
			avgP += results.get(i).getMetrics()[1];
			avgF += results.get(i).getMetrics()[2];
		}
		avgR /= results.size();
		avgP /= results.size();
		avgF /= results.size();

		System.out.println("Recall = " + avgR);
		System.out.println("Precision = " + avgP);
		System.out.println("F-measure = " + avgF);
	}

	public static void main(String[] args) throws Exception {

		//String testFilePath = "/home/lucia/nlp2sparql-data/qald6/submission1_integer_ids.json";
		String testFilePath = "/home/gaetangate/Dev/nlp2sparql-data/qald6/test/qald-6-test-multilingual_errata.json";

		// QASystem qas = new CanaliQASystem();
		QASystem qas = new DummyQASystem();

		String lang = "en";

		FileReader reader = new FileReader(testFilePath);
		Gson gson = new Gson();
		QALD6Model qald6 = gson.fromJson(reader, QALD6Model.class);

		ArrayList<Result> results = new ArrayList<Result>();

		Questions[] qsList = qald6.questions;

		/*
		 * For each query in test set we get the ground truth answers and system
		 * answers
		 */
		for (Questions qs : qsList) {
			if (qs.onlydbo.equals("true")) {
				System.out.println(qs.id + " " + qs.answertype);

				/*
				 * Ground truth Answers
				 */

				ArrayList<String> qaldAns = new ArrayList<String>();

				if (qs.answers.length == 0) {
					qaldAns.add("empty");
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
							qaldAns.add(Boolean.toString(ans.bool));
						}
					}
				}

				/*
				 * System answers
				 */
				String query = "";
				ArrayList<String> systAns = new ArrayList<String>();
				for (Question q : qs.question) {
					if (q.language.equals(lang)) {
						query = q.string;
						systAns = qas.getAnswer(q.string);
					}
				}

				Result res = new Result(query, qaldAns, systAns);
				//Result res = new Result(query, qaldAns, qaldAns);
				results.add(res);
			}
		}

		printMetricAvg(results);

	}
}
