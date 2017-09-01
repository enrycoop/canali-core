/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import di.uniba.it.nlpita.vectors.MemoryVectorReader;
import di.uniba.it.nlpita.vectors.Vector;
import di.uniba.it.nlpita.vectors.VectorFactory;
import di.uniba.it.nlpita.vectors.VectorReader;
import di.uniba.it.nlpita.vectors.VectorType;
import edu.ucla.cs.scai.canali.core.autocompleter.AutocompleteObject;
import edu.ucla.cs.scai.canali.core.autocompleter.AutocompleteService;
import edu.ucla.cs.scai.canali.core.autocompleter.AutocompleteW2VService;
import edu.ucla.cs.scai.canali.core.query.QueryService;
import edu.ucla.cs.scai.canali.core.query.ResultObject;
import edu.ucla.cs.scai.canali.core.query.ResultWrapper;
import edu.ucla.cs.scai.canali.core.translation.TranslationService;
import edu.ucla.cs.scai.canali.core.translation.TranslationWrapper;

/**
 *
 * @author Lucia Siciliani
 */
public class CanaliW2VQASystem implements QASystem {

	public static int W2V_TOPN = 10;

	class PropSim implements Comparable<PropSim> {

		String label;
		double similarity;

		public PropSim(String label, double similarity) {
			this.label = label;
			this.similarity = similarity;
		}

		public String getLabel() {
			return label;
		}

		public double getSimilarity() {
			return similarity;
		}

		@Override
		public int compareTo(PropSim o) {
			if (similarity < o.similarity)
				return 1;
			else if (similarity > o.similarity)
				return -1;
			else
				return 0;
		}

	}

	private static HashMap<String, Vector> prop2v;
	private String word2vecModelPath;
	private String propertyLabesPath;
	private VectorReader vr;

	public CanaliW2VQASystem(String word2vecModelPath, String propertyLabesPath) throws Exception {
		this.word2vecModelPath = word2vecModelPath;
		this.propertyLabesPath = propertyLabesPath;
		prop2v = dbpedia2v();
	}

	private HashMap<String, Vector> dbpedia2v() throws Exception {
		this.vr = new MemoryVectorReader(new File(word2vecModelPath));

		vr.init();
		HashMap<String, Vector> prop2v = new HashMap<String, Vector>();

		BufferedReader in = new BufferedReader(new FileReader(propertyLabesPath));
		String l = in.readLine();
		while (l != null) {
			Vector propVec = VectorFactory.createZeroVector(VectorType.REAL, vr.getDimension());
			String[] split = l.split("\t");
			if (split.length == 2) {
				String propLabel = split[1];
				//System.out.println(propLabel);
				String[] propLabelTerms = propLabel.split(" ");
				for (String propTerm : propLabelTerms) {
					Vector propTermVec = vr.getVector(propTerm);
					if (propTermVec != null)
						propVec.superpose(propTermVec, 1.0, null);
				}
				prop2v.put(propLabel, propVec);
			}
			l = in.readLine();
		}

		return prop2v;
	}

	private static String getCurrentState(ArrayList<AutocompleteObject> acceptedTokens) {
		if (acceptedTokens.isEmpty()) {
			return AutocompleteService.INITIAL_STATE_S0;
		}
		return acceptedTokens.get(acceptedTokens.size() - 1).state;
	}

	private static String getLastAcceptedProperty(ArrayList<AutocompleteObject> acceptedTokens) {
		int i = acceptedTokens.size() - 1;
		while (i > 0) {
			if ((!(acceptedTokens.get(i - 1)).state.equals(AutocompleteService.ACCEPT_PROPERTY_FOR_RANK_STATE_S9)) && (acceptedTokens.get(i).tokenType).equals(AutocompleteService.PROPERTY)) {
				return acceptedTokens.get(i).labels;
			}
			i--;
		}
		return null;
	}

	@SuppressWarnings("empty-statement")
	private static String[] getOpenVariables(ArrayList<AutocompleteObject> acceptedTokens, boolean onlyLast) {
		int i = acceptedTokens.size() - 1;
		String[] res = new String[3];
		res[0] = "";
		res[1] = "";
		res[2] = "";
		int contextVariablePosition = -1;

		while (i >= 0) {
			if (acceptedTokens.get(i).tokenType.equals(AutocompleteService.PROPERTY)) {
				if (contextVariablePosition == -1 || contextVariablePosition == i) {
					//System.out.println("label: " + acceptedTokens.get(i).labels);
					String[] ll = acceptedTokens.get(i).labels.split("\\|");
					for (int k = 0; k < ll.length; k++) {
						if (res[0] != null && res[0].length() > 0) { //!!! div da null
							res[0] += ",";
							res[1] += ",";
							res[2] += ",";
						}
						res[0] += ll[k];
						res[1] += acceptedTokens.get(i).text;
						res[2] += Integer.toString(i);
					}
					//      contextVariablePosition = acceptedTokens.get(i).relatedTokenPosition;
					if (onlyLast) {
						break;
					}
				}
			} else if (acceptedTokens.get(i).tokenType.equals(AutocompleteService.CLASS)) {
				if (contextVariablePosition == -1 || contextVariablePosition == i) {
					if (res[0].length() > 0) {
						res[0] += ",";
						res[1] += ",";
						res[2] += ",";
					}
					res[0] += acceptedTokens.get(i).labels;
					res[1] += acceptedTokens.get(i).text;
					res[2] += Integer.toString(i);
					if (acceptedTokens.get(i).relatedTokenPosition != null) // fix NullPointerException
					{
						contextVariablePosition = acceptedTokens.get(i).relatedTokenPosition;
					}
					if (onlyLast) {
						break;
					}
				}

			} else if (acceptedTokens.get(i).tokenType.equals(AutocompleteService.QUESTION_START) && acceptedTokens.get(i).labels.contains("has")) {
				if (res[0].length() > 0) {
					res[0] += ",";
					res[1] += ",";
					res[2] += ",";
				}
				res[0] += "http://www.w3.org/2002/07/owl#Thing";
				res[1] += acceptedTokens.get(i).text;
				res[2] += Integer.toString(i);
				contextVariablePosition = 0;
				if (onlyLast) {
					break;
				}
			}
			i--;

		}
		return res;
	}

	private static String getFinalPunctuation(ArrayList<AutocompleteObject> acceptedTokens) {
		if (acceptedTokens.isEmpty()) {
			return "?";
		}
		return acceptedTokens.get(0).finalPunctuation;
	}

	/*
	 * deprecated
	 */
	private String getMostSimilarProperty(String token, ArrayList<String> filter) throws Exception {

		Vector v = vr.getVector(token);
		System.out.println("r:" + token);

		ArrayList<PropSim> propSimList = new ArrayList<PropSim>();

		for (HashMap.Entry<String, Vector> entry : prop2v.entrySet()) {
			String propL = entry.getKey();
			if (!filter.contains(propL)) {
				Vector propV = entry.getValue();
				Double sim = v.measureOverlap(propV);
				propSimList.add(new PropSim(propL, sim));
			}
		}
		Collections.sort(propSimList);

		int count = 0;
		for (PropSim ps : propSimList) {
			if (count > W2V_TOPN)
				break;
			System.out.println(ps.getLabel() + " " + ps.getSimilarity());
			count++;
		}
		return propSimList.get(0).getLabel();

	}

	private ArrayList<String> getMostSimilarPropertyList(String token) throws Exception {
		ArrayList<String> similarProp = new ArrayList<String>();
		ArrayList<PropSim> propSimList = new ArrayList<PropSim>();
		Vector v = vr.getVector(token);
		if (v != null) {
			System.out.println("r:" + token);

			for (HashMap.Entry<String, Vector> entry : prop2v.entrySet()) {
				String propL = entry.getKey();
				Vector propV = entry.getValue();
				Double sim = v.measureOverlap(propV);
				//propSimList.add(new PropSim(propL, sim));
				propSimList.add(new PropSim(propL, sim / propL.split(" ").length));
			}
			Collections.sort(propSimList);

			int count = 0;
			for (PropSim ps : propSimList) {
				if (count > W2V_TOPN)
					break;
				System.out.println(ps.getLabel() + " " + ps.getSimilarity());
				similarProp.add(ps.getLabel());
				count++;
			}

		}

		return similarProp;

	}

	@Override
	public ArrayList<String> getAnswer(String query, String answerType) {
		System.out.println("Using CanaliW2V...");

		ArrayList<String> answers = new ArrayList<String>();

		String lastRemainder = null;
		HashMap<Integer, ArrayList<String>> filteredPropertyMap = new HashMap<Integer, ArrayList<String>>();
		HashMap<Integer, ArrayList<AutocompleteObject>> removedTokenMap = new HashMap<Integer, ArrayList<AutocompleteObject>>();

		try {

			String lastAcceptedProperty = null; //p
			String[] openVariablesUri = null; //ou //!!!
			Integer[] openVariablesPosition = null; //op  //!!!
			String currentState = "0"; //s
			String finalPunctuation = null; //f
			boolean disableContextRules = false; //crd
			boolean autoAcceptance = true; //aa
			boolean dateToNumber = false; //dtn
			boolean useKeywords = false; //k

			boolean isEmpty = false;

			removedTokenMap.put(0, new ArrayList<AutocompleteObject>());
			ArrayList<AutocompleteObject> acceptedTokens = new AutocompleteW2VService().getAutocompleResults(query, lastAcceptedProperty, openVariablesUri, openVariablesPosition, currentState, finalPunctuation, disableContextRules, autoAcceptance, dateToNumber, useKeywords, removedTokenMap.get(0));
			if (acceptedTokens == null) {
				isEmpty = true;
			} else {
				lastRemainder = acceptedTokens.get(acceptedTokens.size() - 1).remainder;
				removedTokenMap.get(0).add(acceptedTokens.get(0));
			}
			while (!query.equals(finalPunctuation) && !isEmpty) {
				//System.out.println("nel while");
				currentState = getCurrentState(acceptedTokens);
				if (acceptedTokens.size() > 0) {
					if ((acceptedTokens.get(acceptedTokens.size() - 1).state.equals(AutocompleteService.ACCEPT_OPERATOR_OR_DIRECT_OPERAND_STATE_S4) || acceptedTokens.get(acceptedTokens.size() - 1).state.equals(AutocompleteService.ACCEPT_DIRECT_OPERAND_STATE_S5)) && (acceptedTokens.get(acceptedTokens.size() - 1).labels.equals("year=") || acceptedTokens.get(acceptedTokens.size() - 1).labels.equals("month="))) {
						dateToNumber = true;
					}
					String lastAcceptedPropertyNew = getLastAcceptedProperty(acceptedTokens);
					if (lastAcceptedPropertyNew != null) {
						lastAcceptedProperty = lastAcceptedPropertyNew;
					}
					boolean propertyHaving = acceptedTokens.size() > 2 && acceptedTokens.get(acceptedTokens.size() - 1).state.equals(AutocompleteService.ACCEPT_PROPERTY_FOR_CONSTRAINT_STATE_S3) && acceptedTokens.get(acceptedTokens.size() - 2).state.equals(AutocompleteService.ACCEPT_OPERATOR_OR_DIRECT_OPERAND_STATE_S4);
					String[] openVariables = getOpenVariables(acceptedTokens, propertyHaving);
					if (openVariables[0] != null && !openVariables[0].equals("")) { //!!!
						//System.out.println("open variables [0]:" + openVariables[0]);

						openVariablesUri = openVariables[0].split(",");
						//System.out.println("open variables [2]:" + openVariables[2]);
						if (!openVariables[2].equals("")) {

							String[] intSplit = openVariables[2].split(",");
							openVariablesPosition = new Integer[intSplit.length];
							for (int i = 0; i < intSplit.length; i++) {
								openVariablesPosition[i] = Integer.parseInt(intSplit[i]);
							}

						}
					}
					finalPunctuation = getFinalPunctuation(acceptedTokens);
					//query = acceptedTokens.get(acceptedTokens.size() - 1).remainder; ///!!!
					query = lastRemainder;
				}
				System.out.println("QUERY #1 = " + query);
				if (removedTokenMap.get(acceptedTokens.size()) == null) {
					removedTokenMap.put(acceptedTokens.size(), new ArrayList<AutocompleteObject>());
				}
				ArrayList<AutocompleteObject> newTokens = new AutocompleteW2VService().getAutocompleResults(query, lastAcceptedProperty, openVariablesUri, openVariablesPosition, currentState, finalPunctuation, disableContextRules, autoAcceptance, dateToNumber, useKeywords, removedTokenMap.get(acceptedTokens.size()));

				if (newTokens != null && newTokens.size() > 0) {
					System.out.println("newTokens = " + newTokens.get(0).text);
					acceptedTokens.add(newTokens.get(0));
					lastRemainder = acceptedTokens.get(acceptedTokens.size() - 1).remainder;
					removedTokenMap.get(acceptedTokens.size() - 1).add(newTokens.get(0));
				} else {

					int currentTokenIndex = acceptedTokens.size();
					lastRemainder = acceptedTokens.get(acceptedTokens.size() - 1).remainder;
					String[] remainder = lastRemainder.split(" ");

					if (filteredPropertyMap.get(currentTokenIndex) == null) {
						filteredPropertyMap.put(currentTokenIndex, getMostSimilarPropertyList(remainder[0]));
					}
					if (!filteredPropertyMap.get(currentTokenIndex).isEmpty()) {
						String nearProperty = filteredPropertyMap.get(currentTokenIndex).get(0);
						filteredPropertyMap.get(currentTokenIndex).remove(0);
						lastRemainder = lastRemainder.replace(remainder[0], nearProperty);
					} else {
						lastRemainder = acceptedTokens.get(acceptedTokens.size() - 1).remainder;
						acceptedTokens.remove(currentTokenIndex - 1);
						filteredPropertyMap.put(currentTokenIndex, null);

					}

					/*
					 * TODO ricordarsi della condizione di empty
					 */
					if (acceptedTokens.isEmpty()) {
						isEmpty = true;
					}

				}

			}

			if (!isEmpty) {
				String endpoint = "default";
				int limit = 100000;
				boolean disableSubclass = true;
				TranslationWrapper tWrapper = new TranslationService().translateQuery(acceptedTokens, endpoint, limit, disableSubclass);
				System.out.println(tWrapper.getQuery());

				ResultWrapper rWrapper = new QueryService().answerQuery(acceptedTokens, endpoint, limit, disableSubclass);
				//System.out.println("+++rWrapper query = \n"+ rWrapper.);
				ArrayList<ResultObject> results = rWrapper.getResults();
				if (results != null && results.size() > 0) {
					for (ResultObject result : results) {
						// System.out.println(result.getL() + " " + result.getUe() + " " + result.getU() + " " + result.getId() + " " + result.getIde());
						if (result.getId() != null) {
							answers.add(result.getId());
						} else {
							answers.add(result.getL());
						}
					}
				} else {
					answers.add(QASystem.EMPTY_RESULT);
				}
			} else {
				answers.add(QASystem.EMPTY_RESULT);
			}
		} catch (Exception e) {
			answers.add(QASystem.EMPTY_RESULT);
			System.out.println(e);
			e.printStackTrace();
		}
		return answers;
	}
}
