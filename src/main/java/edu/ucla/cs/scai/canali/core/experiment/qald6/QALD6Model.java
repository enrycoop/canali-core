package edu.ucla.cs.scai.canali.core.experiment.qald6;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.FileReader;

public class QALD6Model {

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
        public Answers[] answers;
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

class Answers {
        public Head head;
        public Results results;
        //public String boolean;
}

class Head {
        public String[] link;
        public String[] vars;
}


class Results {
        public String distinct;
        public String ordered;
        public Bindings[] bindings;
}

class Bindings {
    
        @SerializedName(value="var", alternate={"uri", "num", "date", "c", "string", "height", "number", "year", "n", "x", "carbs", "ni", "name", "\"callret-0\""})
        public Var var;
}

class Var {
        public String type;
        public String datatype;
        public String Value;
}
