package di.uniba.it.nlpita.index.utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
