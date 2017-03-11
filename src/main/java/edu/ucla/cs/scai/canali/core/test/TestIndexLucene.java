package edu.ucla.cs.scai.canali.core.test;

import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

public class TestIndexLucene {

	public static void main(String[] args) throws Exception {

		String indexPath = "/home/gaetangate/Dev/nlp2sparql-data/dbpedia-processed/2015-10/index_onlydbo/lucene";

		Directory directory = new RAMDirectory();
		FSDirectory tempDirectory;
		tempDirectory = FSDirectory.open(Paths.get(indexPath));
		for (String file : tempDirectory.listAll()) {
			directory.copyFrom(tempDirectory, file, file, IOContext.DEFAULT);
		}

		IndexReader reader = DirectoryReader.open(directory);
		System.out.println("# docs = " + reader.maxDoc());
		for (int i = 0; i < reader.maxDoc(); i++) {

			Document doc = reader.document(i);
			System.out.println(doc);
			//String docId = doc.get("id");

		}
	}

}
