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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.util.iterator.ExtendedIterator;
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
        int count =0;
        for (File file : folder.listFiles()){
            FileInputStream fr = new FileInputStream(file);
            System.out.println("Reading file: "+file.getName());
            dbpediaModel.read(fr, "RDF/XML");
            System.out.println("Saving file on destination directory...");
            OntModel output = ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
            navigate(count,dbpediaModel);
        }
    }
    
    
    public int navigate (int count ,OntModel model){
        HashMap<Integer,LinkedList<Triple>> map = new HashMap<Integer,LinkedList<Triple>>();
        for (StmtIterator prop = model.listStatements();prop.hasNext();){
            Statement pr = prop.next();
            if(pr.asTriple().getPredicate().toString().endsWith("hasPhenomenon")){
                
                System.out.println(pr.asTriple().getObject().toString());
                count++;
                LinkedList<Triple> list = new LinkedList<Triple>();

                
                for(StmtIterator p = model.listStatements((Resource) pr.getObject(), null, (RDFNode)null);p.hasNext();){
                    Statement proper = p.next();
                    list.add(proper.asTriple());
                    System.out.println("---->"+proper.asTriple());
                 
                }
                map.put(count,list);
                break;
            }
//            model.write(System.out);
        }
        for(Integer i : map.keySet()){
            
            Resource nuova = model.createResource("POLY"+count);
            for(Triple s: map.get(i)){
                if(s.toString().contains("hasPhenomenonCoverage"))
                    nuova.addProperty(RDFS.label, filter(s.getObject().toString()));
                System.out.println(nuova+"->>>"+nuova.getProperty(RDFS.label));
            }
        }
        
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        return count;
    }
    
    public String filter(String s){
        return s.substring(0,s.indexOf("^^")).replaceAll("\"", "");
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
