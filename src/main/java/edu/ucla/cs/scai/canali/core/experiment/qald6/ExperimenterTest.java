/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.util.ArrayList;

public class ExperimenterTest {

	public static void main(String[] args) throws Exception {

		QASystem qas = new CanaliQASystem();
		System.setProperty("kb.index.dir", "/home/gaetangate/Dev/nlp2sparql-data/dbpedia-processed/2015-10/index_onlydbo/");

		/*
		 * System answers
		 */
		String query = "Who has field Computer graphics and award Academy Awards?";
		ArrayList<String> systAns = new ArrayList<String>();
		systAns = qas.getAnswer(query);

		for (String a : systAns) {
			System.out.println("System = " + a);
		}

	}
}
