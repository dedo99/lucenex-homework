package lucenex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class SamplesExamples {
	
	
	
	public static void write_index() throws IOException {
		Path path = Paths.get("target_src/idx1");
		Directory directory = FSDirectory.open(path);
		IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig());
		
		//delete all documents stored before
		writer.deleteAll();
		
		//create a documents to add to index
		Document doc1 = new Document();
        doc1.add(new TextField("titolo", "Come diventare un ingegnere dei dati, Data Engineer?", Store.YES));
        doc1.add(new TextField("contenuto", "Sembra che oggigiorno tutti vogliano diventare un Data Scientist  ...", Store.YES));
        doc1.add(new StringField("data", "12 ottobre 2016", Store.YES));

        Document doc2 = new Document();
        doc2.add(new TextField("titolo", "Curriculum Ingegneria dei Dati - Sezione di Informatica e Automazione", Store.YES));
        doc2.add(new TextField("contenuto", "Curriculum. Ingegneria dei Dati. Laurea Magistrale in Ingegneria Informatica ...", Store.YES));

		writer.addDocument(doc1);
		writer.addDocument(doc2);
		writer.commit();		
		writer.close();
	}
	
	
	public static void view_all_index() throws IOException {
		Path path = Paths.get("target_src/idx1");
		Directory directory = FSDirectory.open(path);
		
		//prepare  to read the index
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		//retrieve all documents
		Query query_all_index = new MatchAllDocsQuery();
		//perform the query
		runQuery(searcher, query_all_index);
	}
	
	
    public static void search_query_one() throws IOException, ParseException {
    	Path path = Paths.get("target/idx1");
    	Directory directory = FSDirectory.open(path);
    	
    	//to prepare the query
    	IndexReader reader = DirectoryReader.open(directory);
    	IndexSearcher searcher = new IndexSearcher(reader);
    	
    	QueryParser parser = new QueryParser("titolo", new WhitespaceAnalyzer());
    	Query query = parser.parse("+ingegnere dei +dati,");
    	
    	//to perform the query
    	runQuery(searcher, query);
    }
	
    
	private static void runQuery(IndexSearcher searcher, Query query) throws IOException {
        runQuery(searcher, query, false);
    }

	
    private static void runQuery(IndexSearcher searcher, Query query, boolean explain) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc"+scoreDoc.doc);
            System.out.println("   " + "Titolo:"+ doc.get("titolo") + " (" + scoreDoc.score +")");
            System.out.println("   " + "Contenuto:"+ doc.get("contenuto") + " (" + scoreDoc.score +")");
            if (explain) {
                Explanation explanation = searcher.explain(query, scoreDoc.doc);
                System.out.println(explanation);
            }
        }
    }
    
	
	
	public static void main(String[] args) throws IOException, ParseException {
		write_index();
//		view_all_index();
		search_query_one();
	}
	
}
