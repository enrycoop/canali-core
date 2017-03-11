/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.canali.core.index.utils2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author lucia
 */
public class DBpedia201510Index {

    String sourcePath;
    String outputPath;
    OntModel dbpediaModel = ModelFactory.createOntologyModel(ProfileRegistry.OWL_DL_LANG);
    
    /**
     * 
     * @param sourceDir Source files path
     * @param outputDir Output files path
     * @throws java.io.FileNotFoundException
     */
    public DBpedia201510Index(String sourceDir, String outputDir) throws FileNotFoundException {
        System.out.println("SOURCE DIR: " + sourceDir);
        this.sourcePath = sourceDir;
        this.outputPath = outputDir;
        
        dbpediaModel.read(new FileReader(sourcePath + "dbpedia_2015-10.owl"), "RDF/XML");
        System.out.println("dbpedia_2015-10.owl loaded successfully...");
    }
    
    /**
     * 
     * @return 
     */
    public HashSet<String> createClassParentsFile(){
        System.out.println("Creating class parents file...");
        HashSet<String> classes = new HashSet<>();
        
        try {
            System.out.println("Saving class hierarchy in: \n" + outputPath + "supportFiles/class_parents");
            
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/class_parents", false), true);
            
            StmtIterator stmts = dbpediaModel.listStatements(null, RDFS.subClassOf, (RDFNode) null);
            while (stmts.hasNext()){
                Statement stmt = stmts.next();
                out.println(stmt.getSubject() + "\t" + stmt.getObject());
                classes.add(stmt.getSubject().toString());
            }
            out.close();        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        }
        return classes;
    }
    
    /**
     * 
     * @param classes
     * @throws UnsupportedEncodingException 
     */
    public void createClassLabelsFile(HashSet<String> classes) throws UnsupportedEncodingException{
        System.out.println("Creating class labels file...");
        try {
            System.out.println("Saving class labels in: \n" + outputPath + "supportFiles/class_labels");
            
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/class_labels", false), true);
            
            ExtendedIterator<OntClass> ontClasses = dbpediaModel.listClasses();
            while (ontClasses.hasNext()){
                OntClass cls = ontClasses.next();
                if(classes.contains(cls.getURI())){
                    out.println(cls.getURI() + "\t" + URLDecoder.decode(StringEscapeUtils.unescapeJava(cls.getLabel("en")), "UTF-8"));
                }
            }
            out.close();        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @return 
     * @throws java.io.IOException
     */
    public HashSet<String> createPropertyLabelsFile() throws IOException{
    System.out.println("Creating property labels file...");
    HashSet<String> properties = new HashSet<>();
    
        try {
            System.out.println("Saving property labels in: \n" + outputPath + "supportFiles/property_labels");
            
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/property_labels", false), true);
            
            ExtendedIterator<OntProperty> props = dbpediaModel.listAllOntProperties(); //modified from CANaLI
            while (props.hasNext()){
                OntProperty prop = props.next();
                if (prop.getURI().startsWith("http://dbpedia.org/ontology")){
                    out.println(prop.getURI() + "\t" + StringEscapeUtils.unescapeJava(prop.getLabel("en")));
                    properties.add(prop.getURI());
                }
            }
            
           /* BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/infobox_property_definitions_en.ttl"));
            Pattern pattern = Pattern.compile("(\\s*)<(.*)> <http://www.w3.org/2000/01/rdf-schema#label> \"(.*)\"");
            
            String l = in.readLine();
            //int n = 0;
            while (l != null && n < 1000){
                Matcher match = pattern.matcher(l);
                if (match.find()){
                    String aUri = match.group(2);
                    String label = StringEscapeUtils.unescapeJava(match.group(3));
                    if (aUri.startsWith("http://dbpedia.org/property")) {
                        out.println(aUri + "\t" + label);
                        properties.add(aUri);
                    }
                }
                l = in.readLine();
                //n++;
            }*/
            out.close();          
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }
    
    /**
     * 
     * @return 
     */
    public HashSet<String> createEntityLabelsFile(){
    System.out.println("Creating entity labels file...");
    HashSet<String> entities = new HashSet<>();
    
        try {
            System.out.println("Saving entity labels in: \n" + outputPath + "supportFiles/entity_labels");
            
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/entity_labels", false), true);
            BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/labels_en.ttl"));
            Pattern pattern = Pattern.compile("(\\s*)<(.*)> <http://www.w3.org/2000/01/rdf-schema#label> \"(.*)\"");
            
            String l = in.readLine();
            //int n = 0; //!!! to remove
            while (l != null /*&& n< 1000*/){
                //System.out.println(l);
                Matcher match = pattern.matcher(l);
                if (match.find()){
                    String eUri = match.group(2);
                    String eLabel = StringEscapeUtils.unescapeJava(match.group(3));
                    if (eUri.startsWith("http://dbpedia.org/resource")){
                        //System.out.println(eUri + "\t" + eLabel);
                        out.println(eUri + "\t" + eLabel);
                        entities.add(eUri);
                    }
                }
                l = in.readLine();
                //n++;
            }
            out.close();        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entities;
    }
    
    
    public void createEntityClassesFile(HashSet<String> entities,HashSet<String> classes){
        System.out.println("Creating entity classes file...");
        try {
            System.out.println("Saving entity labels in: \n" + outputPath + "supportFiles/entity_classes");
            
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/entity_classes", false), true);
            BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/instance_types_en.ttl"));
            Pattern pattern = Pattern.compile("<(.*)> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <(.*)>");
            
            String l = in.readLine();
            //int n = 0; //!!! to remove
            while (l != null /*&& n < 1000*/){
                //System.out.println(l);
                Matcher match = pattern.matcher(l);
                if (match.find()){
                    String eUri = match.group(1);
                    String cUri = match.group(2);
                    if (entities.contains(eUri) && classes.contains(cUri)){
                        out.println(eUri + "\t" + cUri);
                    }
                }
                l = in.readLine();
                //n++;
            }
            out.close();        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     */
    public void createBasicTypesLiteralTypesFile(){
        System.out.println("Creating basic types literal file...");
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/basic_types_literal_types", false), true);
            out.println("http://dbpedia.org/datatype/centimetre\tDouble");
            out.println("http://dbpedia.org/datatype/cubicCentimetre\tDouble");
            out.println("http://dbpedia.org/datatype/cubicKilometre\tDouble");
            out.println("http://dbpedia.org/datatype/cubicMetre\tDouble");
            out.println("http://dbpedia.org/datatype/cubicMetrePerSecond\tDouble");
            out.println("http://dbpedia.org/datatype/day\tDouble");
            out.println("http://dbpedia.org/datatype/gramPerKilometre\tDouble");
            out.println("http://dbpedia.org/datatype/hour\tDouble");
            out.println("http://dbpedia.org/datatype/inhabitantsPerSquareKilometre\tDouble");
            out.println("http://dbpedia.org/datatype/kelvin\tDouble");
            out.println("http://dbpedia.org/datatype/kilogram\tDouble");
            out.println("http://dbpedia.org/datatype/kilogramPerCubicMetre\tDouble");
            out.println("http://dbpedia.org/datatype/kilometre\tDouble");
            out.println("http://dbpedia.org/datatype/kilometrePerHour\tDouble");
            out.println("http://dbpedia.org/datatype/kilometrePerSecond\tDouble");
            out.println("http://dbpedia.org/datatype/kilowatt\tDouble");
            out.println("http://dbpedia.org/datatype/litre\tDouble");
            out.println("http://dbpedia.org/datatype/megabyte\tDouble");
            out.println("http://dbpedia.org/datatype/metre\tDouble");
            out.println("http://dbpedia.org/datatype/millimetre\tDouble");
            out.println("http://dbpedia.org/datatype/minute\tDouble");
            out.println("http://dbpedia.org/datatype/newtonMetre\tDouble");
            out.println("http://dbpedia.org/datatype/second\tDouble");
            out.println("http://dbpedia.org/datatype/squareKilometre\tDouble");
            out.println("http://dbpedia.org/datatype/squareMetre\tDouble");
            out.println("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\tString");
            out.println("http://dbpedia.org/datatype/usDollar\tDouble");
            out.println("http://dbpedia.org/datatype/euro\tDouble");
            out.println("http://dbpedia.org/datatype/poundSterling\tDouble");
            out.println("http://dbpedia.org/datatype/swedishKrona\tDouble");
            out.println("http://dbpedia.org/datatype/philippinePeso\tDouble");
            out.println("http://dbpedia.org/datatype/singaporeDollar\tDouble");
            out.println("http://dbpedia.org/datatype/indianRupee\tDouble");
            out.println("http://dbpedia.org/datatype/mauritianRupee\tDouble");
            out.println("http://dbpedia.org/datatype/canadianDollar\tDouble");
            out.println("http://dbpedia.org/datatype/hongKongDollar\tDouble");
            out.println("http://dbpedia.org/datatype/zambianKwacha\tDouble");
            out.println("http://dbpedia.org/datatype/moroccanDirham\tDouble");
            out.println("http://dbpedia.org/datatype/ghanaianCedi\tDouble");
            out.println("http://dbpedia.org/datatype/peruvianNuevoSol\tDouble");
            out.println("http://dbpedia.org/datatype/thaiBaht\tDouble");
            out.println("http://dbpedia.org/datatype/nicaraguanCórdoba\tDouble");
            out.println("http://dbpedia.org/datatype/malaysianRinggit\tDouble");
            out.println("http://dbpedia.org/datatype/unitedArabEmiratesDirham\tDouble");
            out.println("http://dbpedia.org/datatype/ethiopianBirr\tDouble");
            out.println("http://dbpedia.org/datatype/egyptianPound\tDouble");
            out.println("http://dbpedia.org/datatype/tanzanianShilling\tDouble");
            out.println("http://dbpedia.org/datatype/azerbaijaniManat\tDouble");
            out.println("http://dbpedia.org/datatype/indonesianRupiah\tDouble");
            out.println("http://dbpedia.org/datatype/botswanaPula\tDouble");
            out.println("http://dbpedia.org/datatype/bangladeshiTaka\tDouble");
            out.println("http://dbpedia.org/datatype/czechKoruna\tDouble");
            out.println("http://dbpedia.org/datatype/belizeDollar\tDouble");
            out.println("http://dbpedia.org/datatype/ukrainianHryvnia\tDouble");
            out.println("http://dbpedia.org/datatype/bulgarianLev\tDouble");
            out.println("http://dbpedia.org/datatype/icelandKrona\tDouble");
            out.println("http://dbpedia.org/datatype/sriLankanRupee\tDouble");
            out.println("http://dbpedia.org/datatype/armenianDram\tDouble");
            out.println("http://dbpedia.org/datatype/pakistaniRupee\tDouble");
            out.println("http://dbpedia.org/datatype/southAfricanRand\tDouble");
            out.println("http://dbpedia.org/datatype/romanianNewLeu\tDouble");
            out.println("http://dbpedia.org/datatype/colombianPeso\tDouble");
            out.println("http://dbpedia.org/datatype/russianRouble\tDouble");
            out.println("http://dbpedia.org/datatype/algerianDinar\tDouble");
            out.println("http://dbpedia.org/datatype/sierraLeoneanLeone\tDouble");
            out.println("http://dbpedia.org/datatype/netherlandsAntilleanGuilder\tDouble");
            out.println("http://dbpedia.org/datatype/nigerianNaira\tDouble");
            out.println("http://dbpedia.org/datatype/hungarianForint\tDouble");
            out.println("http://dbpedia.org/datatype/estonianKroon\tDouble");
            out.println("http://dbpedia.org/datatype/georgianLari\tDouble");
            out.println("http://dbpedia.org/datatype/gambianDalasi\tDouble");
            out.println("http://dbpedia.org/datatype/gibraltarPound\tDouble");
            out.println("http://dbpedia.org/datatype/kuwaitiDinar\tDouble");
            out.println("http://dbpedia.org/datatype/brazilianReal\tDouble");
            out.println("http://dbpedia.org/datatype/maldivianRufiyaa\tDouble");
            out.println("http://dbpedia.org/datatype/jordanianDinar\tDouble");
            out.println("http://dbpedia.org/datatype/israeliNewSheqel\tDouble");
            out.println("http://dbpedia.org/datatype/saudiRiyal\tDouble");
            out.println("http://dbpedia.org/datatype/serbianDinar\tDouble");
            out.println("http://dbpedia.org/datatype/iranianRial\tDouble");
            out.println("http://dbpedia.org/datatype/omaniRial\tDouble");
            out.println("http://dbpedia.org/datatype/nepaleseRupee\tDouble");
            out.println("http://dbpedia.org/datatype/argentinePeso\tDouble");
            out.println("http://dbpedia.org/datatype/honduranLempira\tDouble");
            out.println("http://dbpedia.org/datatype/papuaNewGuineanKina\tDouble");
            out.println("http://dbpedia.org/datatype/qatariRial\tDouble");
            out.println("http://dbpedia.org/datatype/moldovanLeu\tDouble");
            out.println("http://dbpedia.org/datatype/bosniaAndHerzegovinaConvertibleMarks\tDouble");
            out.println("http://dbpedia.org/datatype/kazakhstaniTenge\tDouble");
            out.println("http://dbpedia.org/datatype/malawianKwacha\tDouble");
            out.println("http://dbpedia.org/datatype/newTaiwanDollar\tDouble");
            out.println("http://dbpedia.org/datatype/chileanPeso\tDouble");
            out.println("http://dbpedia.org/datatype/southKoreanWon\tDouble");
            out.println("http://dbpedia.org/datatype/bahrainiDinar\tDouble");
            out.println("http://dbpedia.org/datatype/latvianLats\tDouble");
            out.println("http://dbpedia.org/datatype/jamaicanDollar\tDouble");
            out.println("http://dbpedia.org/datatype/namibianDollar\tDouble");
            out.println("http://dbpedia.org/datatype/latvianLats\tDouble");
            out.println("http://dbpedia.org/datatype/turkishLira\tDouble");
            out.println("http://dbpedia.org/datatype/danishKrone\tDouble");
            out.println("http://dbpedia.org/datatype/norwegianKrone\tDouble");
            out.println("http://dbpedia.org/datatype/kenyanShilling\tDouble");
            out.println("http://dbpedia.org/datatype/renminbi\tDouble");
            out.println("http://dbpedia.org/datatype/polishZłoty\tDouble");
            out.println("http://dbpedia.org/datatype/ugandaShilling\tDouble");
            out.println("http://dbpedia.org/datatype/japaneseYen\tDouble");
            out.println("http://dbpedia.org/datatype/newZealandDollar\tDouble");
            out.println("http://dbpedia.org/datatype/rwandaFranc\tDouble");
            out.println("http://dbpedia.org/datatype/swissFranc\tDouble");
            out.println("http://dbpedia.org/datatype/australianDollar\tDouble");
            out.println("http://dbpedia.org/datatype/mexicanPeso\tDouble");
            out.println("http://dbpedia.org/datatype/lithuanianLitas\tDouble");
            out.println("http://dbpedia.org/datatype/croatianKuna\tDouble");
            out.println("http://dbpedia.org/datatype/engineConfiguration\tString");
            out.println("http://dbpedia.org/datatype/fuelType\tString");
            out.println("http://dbpedia.org/datatype/valvetrain\tString");
            out.println("http://dbpedia.org/datatype/rod\tDouble");
            out.println("http://dbpedia.org/datatype/degreeRankine\tDouble");
            out.println("http://dbpedia.org/datatype/stone\tDouble");
            out.println("http://dbpedia.org/datatype/perCent\tDouble");
            out.println("http://dbpedia.org/datatype/gram\tDouble");
            out.println("http://dbpedia.org/datatype/pond\tDouble");
            out.println("http://dbpedia.org/datatype/inch\tDouble");
            out.println("http://dbpedia.org/datatype/pound\tDouble");
            out.println("http://dbpedia.org/datatype/megahertz\tDouble");
            out.println("http://dbpedia.org/datatype/gramPerCubicCentimetre\tDouble");
            out.println("http://dbpedia.org/datatype/micrometre\tDouble");
            out.println("http://dbpedia.org/datatype/tonne\tDouble");
            out.println("http://dbpedia.org/datatype/squareFoot\tDouble");
            out.println("http://dbpedia.org/datatype/nanometre\tDouble");
            out.println("http://dbpedia.org/datatype/foot\tDouble");
            out.println("http://dbpedia.org/datatype/gigalitre\tDouble");
            out.println("http://dbpedia.org/datatype/acre\tDouble");
            out.println("http://dbpedia.org/datatype/horsepower\tDouble");
            out.println("http://dbpedia.org/datatype/milePerHour\tDouble");
            out.println("http://dbpedia.org/datatype/mile\tDouble");
            out.println("http://dbpedia.org/datatype/nautialMile\tDouble");
            out.println("http://dbpedia.org/datatype/footPerMinute\tDouble");
            out.println("http://dbpedia.org/datatype/metrePerSecond\tDouble");
            out.println("http://dbpedia.org/datatype/ampere\tDouble");
            out.println("http://dbpedia.org/datatype/degreeCelsius\tDouble");
            out.println("http://dbpedia.org/datatype/astronomicalUnit\tDouble");
            out.println("http://dbpedia.org/datatype/millibar\tDouble");
            out.println("http://dbpedia.org/datatype/milligram\tDouble");
            out.println("http://dbpedia.org/datatype/byte\tDouble");
            out.println("http://dbpedia.org/datatype/degreeFahrenheit\tDouble");
            out.println("http://dbpedia.org/datatype/decimetre\tDouble");
            out.println("http://dbpedia.org/datatype/watt\tDouble");
            out.println("http://dbpedia.org/datatype/knot\tDouble");
            out.println("http://dbpedia.org/datatype/perMil\tDouble");
            out.println("http://dbpedia.org/datatype/megawatt\tDouble");
            out.println("http://dbpedia.org/datatype/kilowattHour\tDouble");
            out.println("http://dbpedia.org/datatype/kilopascal\tDouble");
            out.println("http://dbpedia.org/datatype/kilobyte\tDouble");
            out.println("http://dbpedia.org/datatype/pferdestaerke\tDouble");
            out.println("http://dbpedia.org/datatype/footPerSecond\tDouble");
            out.println("http://dbpedia.org/datatype/gigawattHour\tDouble");
            out.println("http://dbpedia.org/datatype/bit\tDouble");
            out.println("http://dbpedia.org/datatype/myanmaKyat\tDouble");
            out.println("http://dbpedia.org/datatype/tonganPaanga\tDouble");
            out.println("http://dbpedia.org/datatype/millilitre\tDouble");
            out.println("http://dbpedia.org/datatype/nanosecond\tDouble");
            out.println("http://dbpedia.org/datatype/bar\tDouble");
            out.println("http://dbpedia.org/datatype/gigabyte\tDouble");
            out.println("http://dbpedia.org/datatype/bahamianDollar\tDouble");
            out.println("http://dbpedia.org/datatype/volt\tDouble");
            out.println("http://dbpedia.org/datatype/kilolightYear\tDouble");
            out.println("http://dbpedia.org/datatype/pascal\tDouble");
            out.println("http://dbpedia.org/datatype/gigametre\tDouble");
            out.println("http://dbpedia.org/datatype/terabyte\tDouble");
            out.println("http://dbpedia.org/datatype/kilogramForce\tDouble");
            out.println("http://dbpedia.org/datatype/hectare\tDouble");
            out.println("http://dbpedia.org/datatype/megalitre\tDouble");
            out.println("http://dbpedia.org/datatype/gigawatt\tDouble");
            out.println("http://dbpedia.org/datatype/terawattHour\tDouble");
            out.println("http://dbpedia.org/datatype/kilohertz\tDouble");
            out.println("http://dbpedia.org/datatype/hertz\tDouble");
            out.println("http://dbpedia.org/datatype/newton\tDouble");
            out.println("http://dbpedia.org/datatype/meganewton\tDouble");
            out.println("http://dbpedia.org/datatype/lightYear\tDouble");
            out.println("http://dbpedia.org/datatype/megabit\tDouble");
            out.println("http://dbpedia.org/datatype/cubicInch\tDouble");
            out.println("http://dbpedia.org/datatype/squareMile\tDouble");
            out.println("http://dbpedia.org/datatype/cubicFeetPerSecond\tDouble");
            out.println("http://dbpedia.org/datatype/joule\tDouble");
            out.println("http://dbpedia.org/datatype/seychellesRupee\tDouble");
            out.println("http://dbpedia.org/datatype/yard\tDouble");
            out.println("http://dbpedia.org/datatype/squareCentimetre\tDouble");
            out.println("http://dbpedia.org/datatype/microlitre\tDouble");
            out.println("http://dbpedia.org/datatype/calorie\tDouble");
            out.println("http://dbpedia.org/datatype/cubicHectometre\tDouble");
            out.println("http://dbpedia.org/datatype/brakeHorsepower\tDouble");
            out.println("http://dbpedia.org/datatype/gramPerMillilitre\tDouble");
            out.println("http://dbpedia.org/datatype/milliwatt\tDouble");
            out.println("http://dbpedia.org/datatype/poundPerSquareInch\tDouble");
            out.println("http://dbpedia.org/datatype/hectolitre\tDouble");
            out.println("http://dbpedia.org/datatype/millipond\tDouble");
            out.println("http://dbpedia.org/datatype/saintHelenaPound\tDouble");
            out.println("http://dbpedia.org/datatype/ounce\tDouble");
            out.println("http://dbpedia.org/datatype/bermudianDollar\tDouble");
            out.println("http://dbpedia.org/datatype/kilocalorie\tDouble");
            out.println("http://dbpedia.org/datatype/hectopascal\tDouble");
            out.println("http://dbpedia.org/datatype/millihertz\tDouble");
            out.println("http://dbpedia.org/datatype/kilovolt\tDouble");
            out.println("http://dbpedia.org/datatype/kilojoule\tDouble");
            out.println("http://dbpedia.org/datatype/grain\tDouble");
            out.println("http://dbpedia.org/datatype/albanianLek\tDouble");
            out.println("http://dbpedia.org/datatype/milliampere\tDouble");
            out.println("http://dbpedia.org/datatype/cubicMillimetre\tDouble");
            out.println("http://dbpedia.org/datatype/usGallon\tDouble");
            out.println("http://dbpedia.org/datatype/cubicFoot\tDouble");
            out.println("http://dbpedia.org/datatype/gigahertz\tDouble");
            out.println("http://dbpedia.org/datatype/centilitre\tDouble");
            out.println("http://dbpedia.org/datatype/decilitre\tDouble");
            out.println("http://dbpedia.org/datatype/tonneForce\tDouble");
            out.println("http://dbpedia.org/datatype/sãoToméAndPríncipeDobra\tDouble");
            out.println("http://dbpedia.org/datatype/eritreanNakfa\tDouble");
            out.println("http://dbpedia.org/datatype/megapond\tDouble");
            out.println("http://dbpedia.org/datatype/megapascal\tDouble");
            out.println("http://dbpedia.org/datatype/giganewton\tDouble");
            out.println("http://dbpedia.org/datatype/megavolt\tDouble");
            out.println("http://dbpedia.org/datatype/kilopond\tDouble");
            out.println("http://dbpedia.org/datatype/bolivianBoliviano\tDouble");
            out.println("http://dbpedia.org/datatype/mongolianTögrög\tDouble");
            out.println("http://dbpedia.org/datatype/furlong\tDouble");
            out.println("http://dbpedia.org/datatype/belarussianRuble\tDouble");
            out.println("http://dbpedia.org/datatype/lebanesePound\tDouble");
            out.println("http://dbpedia.org/datatype/laoKip\tDouble");
            out.println("http://dbpedia.org/datatype/guineaFranc\tDouble");
            out.println("http://dbpedia.org/datatype/gramForce\tDouble");
            out.println("http://dbpedia.org/datatype/poundFoot\tDouble");
            out.println("http://dbpedia.org/datatype/cubicYard\tDouble");
            out.println("http://dbpedia.org/datatype/sudanesePound\tDouble");
            out.println("http://dbpedia.org/datatype/somaliShilling\tDouble");
            out.println("http://dbpedia.org/datatype/syrianPound\tDouble");
            out.println("http://dbpedia.org/datatype/megawattHour\tDouble");
            out.println("http://dbpedia.org/datatype/dominicanPeso\tDouble");
            out.println("http://dbpedia.org/datatype/caymanIslandsDollar\tDouble");
            out.println("http://dbpedia.org/datatype/microsecond\tDouble");
            out.println("http://dbpedia.org/datatype/capeVerdeEscudo\tDouble");
            out.println("http://dbpedia.org/datatype/imperialGallon\tDouble");
            out.println("http://dbpedia.org/datatype/squareHectometre\tDouble");
            out.println("http://dbpedia.org/datatype/squareDecimetre\tDouble");
            out.println("http://dbpedia.org/datatype/kilolitre\tDouble");
            out.println("http://dbpedia.org/datatype/hectometre\tDouble");
            out.println("http://dbpedia.org/datatype/standardAtmosphere\tDouble");
            out.println("http://dbpedia.org/datatype/zimbabweanDollar\tDouble");
            out.println("http://dbpedia.org/datatype/congoleseFranc\tDouble");
            out.println("http://dbpedia.org/datatype/arubanGuilder\tDouble");
            out.println("http://dbpedia.org/datatype/kilometresPerLitre\tDouble");
            out.println("http://dbpedia.org/datatype/cubicDecimetre\tDouble");
            out.println("http://dbpedia.org/datatype/venezuelanBolívar\tDouble");
            out.println("http://dbpedia.org/datatype/wattHour\tDouble");
            out.println("http://dbpedia.org/datatype/trinidadAndTobagoDollar\tDouble");
            out.println("http://dbpedia.org/datatype/erg\tDouble");
            out.println("http://dbpedia.org/datatype/fijiDollar\tDouble");
            out.println("http://dbpedia.org/datatype/malagasyAriary\tDouble");
            out.println("http://dbpedia.org/datatype/bhutaneseNgultrum\tDouble");
            out.println("http://dbpedia.org/datatype/kiloampere\tDouble");
            out.println("http://dbpedia.org/datatype/squareMillimetre\tDouble");
            out.println("http://dbpedia.org/datatype/imperialBarrelOil\tDouble");
            out.println("http://dbpedia.org/datatype/macanesePataca\tDouble");
            out.println("http://dbpedia.org/datatype/samoanTala\tDouble");
            out.println("http://dbpedia.org/datatype/slovakKoruna\tDouble");
            out.println("http://dbpedia.org/datatype/inhabitantsPerSquareMile\tDouble");
            out.println("http://dbpedia.org/datatype/tunisianDinar\tDouble");
            out.println("http://dbpedia.org/datatype/cambodianRiel\tDouble");
            out.println("http://dbpedia.org/datatype/kilobit\tDouble");
            out.println("http://dbpedia.org/datatype/afghanAfghani\tDouble");
            out.println("http://dbpedia.org/datatype/barbadosDollar\tDouble");
            out.println("http://dbpedia.org/datatype/kyrgyzstaniSom\tDouble");
            out.println("http://dbpedia.org/datatype/footPound\tDouble");
            out.println("http://dbpedia.org/datatype/angolanKwanza\tDouble");
            out.println("http://dbpedia.org/datatype/costaRicanColon\tDouble");
            out.println("http://dbpedia.org/datatype/panamanianBalboa\tDouble");
            out.println("http://dbpedia.org/datatype/macedonianDenar\tDouble");
            out.println("http://dbpedia.org/datatype/chain\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#gMonthDay\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#anyURI\tString");
            out.println("http://www.w3.org/2001/XMLSchema#boolean\tBoolean");
            out.println("http://www.w3.org/2001/XMLSchema#date\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#dateTime\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#double\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#float\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#gYear\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#gYearMonth\tDate");
            out.println("http://www.w3.org/2001/XMLSchema#integer\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#int\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#nonNegativeInteger\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#positiveInteger\tDouble");
            out.println("http://www.w3.org/2001/XMLSchema#string\tString");
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @throws java.io.FileNotFoundException
     */
    public void createTripleFile() throws FileNotFoundException, IOException{
        System.out.println("Creating triples file...");
        
            System.out.println("Saving triples in: \n" + outputPath + "supportFiles/triples");
            //int n = 0; //!!! to remove
            
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath + "supportFiles/triples", false), true);
            BufferedReader in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/mappingbased_literals_en.ttl"));
            String l = in.readLine();
            while (l != null /*&& n < 1000*/){
                out.println(l);
                l = in.readLine();
                //n++;
            }
            in.close();
            
            //!!!
//            in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/infobox_properties_unredirected_en.ttl"));
//            l = in.readLine();
//            while (l != null /*&& n < 2000*/){
//                out.println(l);
//                l = in.readLine();
//                //n++;
//            }
//            in.close();
            
            in = new BufferedReader(new FileReader(sourcePath + "core-i18n/en/mappingbased_objects_en.ttl"));
            l = in.readLine();
            while (l != null /*&& n < 3000*/){
                out.println(l);
                l = in.readLine();
                //n++;
            }
            in.close();            
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            //String directory = "/home/lucia/nlp2sparql/dbpedia/2015-10/core-i18n/en";
            DBpedia201510Index dbpedia = new DBpedia201510Index(args[0], args[1]);
            HashSet<String> classes = dbpedia.createClassParentsFile();
            dbpedia.createClassLabelsFile(classes);
            
                dbpedia.createPropertyLabelsFile();
            
            HashSet<String> entities = dbpedia.createEntityLabelsFile();
            dbpedia.createEntityClassesFile(entities, classes);
            dbpedia.createBasicTypesLiteralTypesFile();
            dbpedia.createTripleFile();
            
            System.out.println("Ended at " + new Date());
            long time = System.currentTimeMillis() - start;
            long sec = time / 1000;
            System.out.println("The process took " + (sec / 60) + "'" + (sec % 60) + "." + (time % 1000) + "\"");
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DBpedia201510Index.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    //
}
