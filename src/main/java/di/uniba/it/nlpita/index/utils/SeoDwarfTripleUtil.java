package di.uniba.it.nlpita.index.utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.graph.Graph;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author enrico coppolecchia
 */
public class SeoDwarfTripleUtil {
    String sourcePath;
    String outputPath;
    OntModel dbpediaModel = ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
    public SeoDwarfTripleUtil(String sourceDir, String destDir){
        sourcePath = sourceDir;
        outputPath = destDir;
    }
    public void updateTriples() throws FileNotFoundException{
        File folder = new File(sourcePath);
        
        HashSet<String> classes = new HashSet<>();
        for (File file : folder.listFiles()){
            FileInputStream fr = new FileInputStream(file);
            System.out.println("Reading file: "+file.getName());
            dbpediaModel.read(fr, "RDF/XML");
            System.out.println("Saving file on destination directory...");
            OntModel output = ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
            navigate(dbpediaModel);
        }
        
    }
    
    
    public ArrayList navigate (OntModel model){
        
        ArrayList list = new ArrayList();
        int count = 0;
        StmtIterator stmts = dbpediaModel.listStatements();
        while (stmts.hasNext()) {
            Statement stmt = stmts.next();  
            list.add(stmt);
            
            if(stmt.asTriple().toString().contains("hasPhenomenon")){
                System.out.println(stmt+"\n\n\n\n");
                list.add(stmt);
                count++;
            }
        }
        return list;
    }
    public static void main(String[] args) {
        try{
            SeoDwarfTripleUtil s = new SeoDwarfTripleUtil("C:\\Users\\enric\\Desktop\\2018-07\\", "C:\\Users\\enric\\Desktop\\new\\");
            s.updateTriples();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
