package edu.ucla.cs.scai.canali.core.experiment.qald6;

import java.util.ArrayList;

public class DummyQASystem implements QASystem {

	@Override
	public ArrayList<String> getAnswer(String query) throws Exception {
		ArrayList<String> answer = new ArrayList<String>();
		answer.add("1997-08-31");
		return answer;
	}

}
