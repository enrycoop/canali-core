/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.nlpita.index.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author lucia
 */
public class SeoDwarfIndex {

    String sourcePath;
    String outputPath;
    OntModel dbpediaModel = ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);

    /**
     *
     * @param sourceDir Source files path
     * @param outputDir Output files path
     * @throws java.io.FileNotFoundException
     */
    public SeoDwarfIndex(String sourceDir, String outputDir) throws FileNotFoundException {
        System.out.println("SOURCE DIR: " + sourceDir);
        this.sourcePath = sourceDir;
        this.outputPath = outputDir;
        FileReader fr = new FileReader(sourcePath + "SeoDwarf_v1.0_no_sweet_import.owl");
        dbpediaModel.read(fr, "RDF/XML");
        System.out.println("SeoDwarf_v1.0_no_sweet_import.owl loaded successfully...");
    }

    /**
     *
     * @return
     */
    public HashSet<String> createClassParentsFile() {
        System.out.println("Creating class parents file...");
        HashSet<String> classes = new HashSet<>();

        try {
            System.out.println("Saving class hierarchy in: \n" + outputPath + "supportFiles/class_parents");

            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/class_parents", false), true);

            StmtIterator stmts = dbpediaModel.listStatements(null, RDFS.subClassOf, (RDFNode) null);
            while (stmts.hasNext()) {
                Statement stmt = stmts.next();
                out.println(stmt.getSubject() + "\t" + stmt.getObject());
                
                classes.add(stmt.getSubject().toString());
            }
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        return classes;
    }

    /**
     *
     * @param classes
     * @throws UnsupportedEncodingException
     */
    public void createClassLabelsFile(HashSet<String> classes) throws UnsupportedEncodingException {
        System.out.println("Creating class labels file...");
        try {
            System.out.println("Saving class labels in: \n" + outputPath+"indexes.rdf");

            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath+"indexes.rdf", false), true);

            ExtendedIterator<OntClass> ontClasses = dbpediaModel.listClasses();
            while (ontClasses.hasNext()) {
                OntClass cls = ontClasses.next();
                if (classes.contains(cls.getURI())) {
                    
                    String label = null;
                    if((label=parseURIFromLabels(cls))!=null)
                        out.println(label);
                    else
                        if((label=parseURIFromString(cls))!=null)
                            out.println(label);
                }
            }
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //-------------------------------------------------------------------------------
    public String parseURIFromLabels(OntClass cls) {

        // out.println(cls.getURI() + "\t" + URLDecoder.decode(StringEscapeUtils.unescapeJava(cls.getLabel("en")), "UTF-8"));
        if (cls.getURI().contains("seodwarf")) 
            return cls.getURI() + "\t" + splitOnUpperCase(cls.getURI().split("#")[1]);
        if(cls.getURI().contains("sweet2.3/"))
            return cls.getURI() + "\t" + splitOnUpperCase(cls.getURI().split("sweet2.3/")[1]); //!!! mettere label per tutte le classi !!!
        if(cls.getURI().contains("/v1.0#"))
            return cls.getURI() + "\t" + splitOnUpperCase(cls.getURI().split("/v1.0#")[1]); 
        
        return null;
    }
    //-------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------
    public String parseURIFromString(OntClass cls) {
        //inserire metodo estrazione label basato sulla stringa dell'URI, questo metodo è alternativo a quello subito sopra

        String s = cls.getURI();
        String[] div = s.split("#");
        if(div.length==1)
           div = s.split("/");
        return cls.getURI() + "\t" + splitOnUpperCase(div[div.length-1]);
    }
    //-------------------------------------------------------------------------------
    
    public String splitOnUpperCase(String s){
        String[] r = s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        String temp = "";
        for (String l:r){
            temp+=l.toLowerCase()+" ";
        }
        return temp.trim();
    }
    /**
     *
     * @return @throws java.io.IOException
     */
    public HashSet<String> createPropertyLabelsFile() throws IOException {
        System.out.println("Creating property labels file...");
        HashSet<String> properties = new HashSet<>();

        try {
            System.out.println("Saving property labels in: \n" + outputPath + "supportFiles/property_labels");

            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/property_labels", false), true);

            ExtendedIterator<OntProperty> props = dbpediaModel.listAllOntProperties(); //modified from CANaLI
            while (props.hasNext()) {
                OntProperty prop = props.next();
                //if (prop.getURI().startsWith("http://dbpedia.org/ontology")){
                //out.println(prop.getURI() + "\t" + StringEscapeUtils.unescapeJava(prop.getLabel("en")));
                if (prop.getURI().contains("#")) {
                    out.println(prop.getURI() + "\t" + prop.getURI().split("#")[1]);
                    properties.add(prop.getURI());
                }
                //}
            }

            out.println("https://sweet.jpl.nasa.gov/sweet2.3/hasStartTime\thasStartTime");
            properties.add("https://sweet.jpl.nasa.gov/sweet2.3/hasStartTime");
            out.println("https://sweet.jpl.nasa.gov/sweet2.3/hasEndTime\thasEndTime");
            properties.add("https://sweet.jpl.nasa.gov/sweet2.3/hasEndTime");
            out.println("http://strdf.di.uoa.gr/ontology/hasGeometry\thasGeometry");
            properties.add("http://strdf.di.uoa.gr/ontology/hasGeometry");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }

    /**
     *
     * @return
     */
    public HashSet<String> createEntityLabelsFile() {
        System.out.println("Creating entity labels file...");
        HashSet<String> entities = new HashSet<>();

        try {
            System.out.println("Saving entity labels in: \n" + outputPath + "supportFiles/entity_labels");

            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/entity_labels", false), true);

            Model model = ModelFactory.createDefaultModel();
            model.read(sourcePath + "Data_Turb_0_mod.rdf");

            StmtIterator it = model.listStatements();
            Statement stmt;
            while (it.hasNext()) {
                stmt = it.next();
                Property p = stmt.getPredicate();
                if (p.equals(RDFS.label)) {
                    String subj = stmt.getSubject().toString().replace("seo:", "http://seodwarf.eu/ontology/v1.0#");
                    out.println(subj + "\t" + stmt.getObject().toString());
                    //out.println(stmt.getSubject() + "\t" + stmt.getObject().toString());
                    System.out.println("*Inserting:" + stmt.getSubject().toString());
                    entities.add(stmt.getSubject().toString());
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entities;
    }

    public void createEntityClassesFile(HashSet<String> entities, HashSet<String> classes) {
        System.out.println("Creating entity classes file...");
        try {
            System.out.println("Saving entity labels in: \n" + outputPath + "supportFiles/entity_classes");

            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/entity_classes", false), true);

            Model model = ModelFactory.createDefaultModel();
            model.read(sourcePath + "Data_Turb_0_mod.rdf");

            StmtIterator it = model.listStatements();
            Statement stmt;
            while (it.hasNext()) {
                stmt = it.next();
                Resource subject = stmt.getSubject();
                Property p = stmt.getPredicate();
                RDFNode object = stmt.getObject();

                //System.out.println("looking for:" + object.toString());

                if (entities.contains(subject.toString()) && p.equals(RDF.type) && classes.contains(object.toString())) {
                    out.println(stmt.getSubject() + "\t" + stmt.getObject().toString());
                }

            }

//            BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/instance_types_en.ttl"));
//            Pattern pattern = Pattern.compile("<(.*)> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <(.*)>");
//
//            String l = in.readLine();
//            //int n = 0; //!!! to remove
//            while (l != null /*&& n < 1000*/) {
//                //System.out.println(l);
//                Matcher match = pattern.matcher(l);
//                if (match.find()) {
//                    String eUri = match.group(1);
//                    String cUri = match.group(2);
//                    if (entities.contains(eUri) && classes.contains(cUri)) {
//                        out.println(eUri + "\t" + cUri);
//                    }
//                }
//                l = in.readLine();
//                //n++;
//            }
//            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void createBasicTypesLiteralTypesFile() {
        System.out.println("Creating basic types literal file...");
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/basic_types_literal_types", false), true);
            out.println("http://dbpedia.org/datatype/centimetre\tDouble");
            out.println("http://dbpedia.org/datatype/cubicCentimetre\tDouble");
            out.println("http://dbpedia.org/datatype/cubicKilometre\tDouble");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @throws java.io.FileNotFoundException
     */
    public void createTripleFile() throws FileNotFoundException, IOException {
        System.out.println("Creating triples file...");

        System.out.println("Saving triples in: \n" + outputPath + "supportFiles/triples");

        PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/triples", false), true);

        Model model = ModelFactory.createDefaultModel();
        model.read(sourcePath + "Data_Turb_0_mod.rdf");

        StmtIterator it = model.listStatements();
        Statement stmt;
        while (it.hasNext()) {
            stmt = it.next();
            Resource s = stmt.getSubject();
            String subj = s.toString().replace("seo:", "http://seodwarf.eu/ontology/v1.0#");
            Property p = stmt.getPredicate();
            RDFNode o = stmt.getObject();
            String o_temp = o.toString().replaceAll("POLYGON\\(\\(\n          		", "POLYGON((");
            System.out.println("REPLACED::" + o.toString().replaceAll("POLYGON\\(\\(\n          		", "POLYGON(("));
            System.out.println("Triple:" + s.toString() + "\t" + p.toString() + "\t" + o_temp);
            out.println(subj + "\t" + p.toString() + "\t" + o_temp);
            //out.println(s.toString() + "\t" + p.toString() + "\t" + o_temp);
        }
//        BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/mappingbased_literals_en.ttl"));
//        String l = in.readLine();
//        //int n = 0; //!!!
//        while (l != null /*&& n < 1000*/) { //!!!
//            out.println(l);
//            l = in.readLine();
//            //n++; //!!!
//        }
//        in.close();
//
////            in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/infobox_properties_unredirected_en.ttl"));
////            l = in.readLine();
////            while (l != null){
////                out.println(l);
////                l = in.readLine();
////            }
////            in.close();
//        in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/mappingbased_objects_en.ttl"));
//        l = in.readLine();
//        while (l != null /*&& n < 2000*/) { //!!!
//            out.println(l);
//            l = in.readLine();
//            //n++; //!!!
//        }
//        in.close();
        }
        /**
         * @param args the command line arguments
         */
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            //String directory = "/home/lucia/nlp2sparql/dbpedia/2015-10/core-i18n/en";
            //SeoDwarfIndex dbpedia = new SeoDwarfIndex(args[0], args[1]);
            SeoDwarfIndex dbpedia = new SeoDwarfIndex("C:/Users/enric/Desktop/Progetto IIA/2018-07/", "C:/Users/enric/Desktop/Progetto IIA/index/");

            HashSet<String> classes = dbpedia.createClassParentsFile();

            dbpedia.createClassLabelsFile(classes);

//            dbpedia.createPropertyLabelsFile();

  //          HashSet<String> entities = dbpedia.createEntityLabelsFile();

   //         dbpedia.createEntityClassesFile(entities, classes);

            //dbpedia.createBasicTypesLiteralTypesFile();
 //           dbpedia.createTripleFile();

            System.out.println("Ended at " + new Date());
            long time = System.currentTimeMillis() - start;
            long sec = time / 1000;
            System.out.println("The process took " + (sec / 60) + "'" + (sec % 60) + "." + (time % 1000) + "\"");

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SeoDwarfIndex.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //
}
