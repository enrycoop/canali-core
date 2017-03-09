package edu.ucla.cs.scai.canali.core.experiment.qald6;

public class QALD6 {

	public Dataset dataset;
	public Questions[] questions;
}

class Dataset {
	public String id;
}

class Questions {
	public String id;
	public String answertype;
	public String aggregation;
	public String onlydbo;
	public String hybrid;
	public Question[] question;
	public Query query;
}

class Question {
	public String language;
	public String string;
	public String keywords;
	
	
}

class Query {
	public String sparql;
	public String pseudo;
	public String schemaless;
	
}

