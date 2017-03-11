/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.experiment.qald6;

import com.google.gson.Gson;
import java.io.FileReader;

/**
 *
 * @author lucia
 */
public class QALD6 {
    
    private QALD6Model qald6Model;
    
    public void JsontoQALD6(String jsonPath) throws Exception{
        FileReader reader = new FileReader(jsonPath);
        Gson gson = new Gson();
	qald6Model = gson.fromJson(reader, QALD6Model.class);
    }
    
    private void QALD6toJson() {
    
    }
    
    
}
