/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.util.ArrayList;

public class ExperimenterTest {

	public static void main(String[] args) throws Exception {

		QASystem qas = new CanaliW2VQASystem();
		System.setProperty("kb.index.dir", "/home/lucia/nlp2sparql-data/dbpedia-processed/2015-10/dbpedia-processed_onlydbo_mini_e/index/");

		/*
		 * System answers
		 */
		//String query = "What is known for of Elon Musk?";
		//String query = "What is the active years end date of Boris Becker?";
		//String query = "What is the number of children of Jacques Cousteau?";
		//String query = "What is the count of movies directed by Park Chan-wook?";
		String query = "What is the birth of Alain Connes?";
		//String query = "Is there a award of Aki Kaurismäki equal to Grand Prix (Cannes Film Festival) ?";
		ArrayList<String> systAns = new ArrayList<String>();
		systAns = qas.getAnswer(query, null);

		for (String a : systAns) {
			System.out.println("System = " + a);
		}

	}
}
