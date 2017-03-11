/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.util.ArrayList;

/**
 *
 * @author lucia
 */
public interface QASystem {
    public ArrayList<String> getAnswer(String query) throws Exception;
}
