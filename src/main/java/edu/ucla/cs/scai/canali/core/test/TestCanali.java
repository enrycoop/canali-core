/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.test;

import edu.ucla.cs.scai.canali.core.autocompleter.AutocompleteObject;
import edu.ucla.cs.scai.canali.core.autocompleter.AutocompleteService;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author lucia
 */
public class TestCanali {

    private String getLastAcceptedProperty(ArrayList<AutocompleteObject> acceptedTokens){
        int i =  acceptedTokens.size() - 1;
        while (i > 0) {            
            if ( (!(acceptedTokens.get(i - 1)).state.equals(AutocompleteService.ACCEPT_PROPERTY_FOR_RANK_STATE_S9)) &&
                (acceptedTokens.get(i).tokenType).equals(AutocompleteService.PROPERTY)) {
                return acceptedTokens.get(i).labels;
            }
            i--;
        }
        return null;
    }
    
    @SuppressWarnings("empty-statement")
    public static void main(String... args) throws Exception {

        String query = "What is the birth date of Alain Connes?"; //q
        String lastAcceptedProperty = null;         //p
        String[] openVariablesUri = null;          //ou
        Integer[] openVariablesPosition = null;     //op 
        String currentState = "0";                  //s
        String finalPunctuation = null;             //f
        boolean disableContextRules = false;        //crd
        boolean autoAcceptance = true;              //aa
        boolean dateToNumber = false;               //dtn
        boolean useKeywords = false;                //k
        
        ArrayList<AutocompleteObject> res = new AutocompleteService().getAutocompleResults(query, lastAcceptedProperty,
                openVariablesUri, openVariablesPosition, currentState, finalPunctuation, disableContextRules, autoAcceptance, dateToNumber, useKeywords);

        ArrayList<AutocompleteObject> acceptedToken = new ArrayList<AutocompleteObject>();
        

       
        
        
        
        do {
            System.out.println("==========testCanali");
            for (int i = 0; i < res.size(); i++) {
                AutocompleteObject r = res.get(i);
                System.out.println("============================================");
                System.out.println("res[" + i + "]");
                System.out.println("text:" + r.text);
                System.out.println("restrictedText:" + r.restrictedText);
                System.out.println("state:" + r.state);
                System.out.println("labels:" + r.labels);
                System.out.println("tokenType:" + r.tokenType);
                System.out.println("finalPunctuation:" + r.finalPunctuation);
                System.out.println("relatedTokenPosition:" + r.relatedTokenPosition);
                System.out.println("isPrefix:" + r.isPrefix);
                System.out.println("mustBeAccepted:" + r.mustBeAccepted);
                System.out.println("similarity:" + r.similarity);
                System.out.println("prefixSimilarity:" + r.prefixSimilarity);
                System.out.println("remainder:" + r.remainder);
                System.out.println("keywords:" + Arrays.toString(r.keywords));
                System.out.println("============================================");
            }
           
//           res = new AutocompleteService().getAutocompleResults(res.get(res.size() - 1).remainder,
//                                                                res.get(res.size() - 1).labels,
//
//                                                                
//                                                                );
            AutocompleteObject last = res.get(res.size() - 1);

            String[] ou = new String[]{last.labels};
            Integer[] op = new Integer[]{last.relatedTokenPosition};
            res = new AutocompleteService().getAutocompleResults(
                    last.remainder,
                    last.labels,
                    ou,
                    op,
                    last.state,
                    last.finalPunctuation,
                    false,
                    true,
                    false,
                    false);

            
//                    (openVariablesUri == null || openVariablesUri.length() == 0) ? null : openVariablesUri.split(","), 
//                    op,
//                    state,
//                    finalPunctuation, 
//                    contextRulesDisabled != null && contextRulesDisabled.toLowerCase().equals("true"),
//                    autoAcceptance != null && autoAcceptance.toLowerCase().equals("true"),
//                    "true".equals(dateToNumber),
//                    useKeywords.booleanValue());

        } while (!res.get(res.size() - 1).state.equals("f"));
    }
}
