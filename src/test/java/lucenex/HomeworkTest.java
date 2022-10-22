package lucenex;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


import static org.junit.Assert.*;

public class HomeworkTest {


    @Test
    public void view_all_index() throws IOException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve all documents
        Query query_all_index = new MatchAllDocsQuery();
        //perform the query
        runQuery(searcher, query_all_index);
    }

    @Test
    public void view_documents_with_term_in_name() throws IOException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the word "computer" in name
        TermQuery termQuery = new TermQuery(new Term("name", "computer"));
        //perform the query
        runQuery(searcher, termQuery);
    }

    @Test
    public void view_documents_with_term_in_content() throws IOException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the word "computer" in content
        TermQuery termQuery = new TermQuery(new Term("content", "computer"));
        //perform the query
        runQuery(searcher, termQuery);
    }

    @Test
    public void view_documents_with_phrasequery_in_name() throws IOException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the phrase "storia del computer" in name
        PhraseQuery phraseQuery = new PhraseQuery.Builder()
                .add(new Term("name", "storia"))
                .add(new Term("name", "del"))
                .add(new Term("name", "computer"))
                .build();
        //perform the query
        runQuery(searcher, phraseQuery);
    }

    @Test
    public void view_documents_with_No_phrasequery_in_name() throws IOException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the phrase "storia della cybersecurity" in name (No Match)
        PhraseQuery phraseQuery = new PhraseQuery.Builder()
                .add(new Term("name", "storia"))
                .add(new Term("name", "della"))
                .add(new Term("name", "cybersecurity"))
                .build();
        //perform the query
        runQuery(searcher, phraseQuery);
    }

    @Test
    public void view_documents_with_phrasequery_in_content() throws IOException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the phrase "proteggere i sistemi" in content
        PhraseQuery phraseQuery = new PhraseQuery.Builder()
                .add(new Term("content", "proteggere"))
                .add(new Term("content", "i"))
                .add(new Term("content", "sistemi"))
                .build();
        //perform the query
        runQuery(searcher, phraseQuery);
    }

    @Test
    public void view_documents_with_queryparser_in_name() throws IOException, ParseException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the words "computer della storia" in name dove "computer" e "storia" sono obbligatorie
        QueryParser queryParser = new QueryParser("name", new WhitespaceAnalyzer());
        Query query = queryParser.parse("+computer della +storia");
        //perform the query
        runQuery(searcher, query);
    }

    @Test
    public void view_documents_with_queryparser_in_name_withoutWordMatch() throws IOException, ParseException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the words "progetto" in name
        QueryParser queryParser = new QueryParser("name", new WhitespaceAnalyzer());
        Query query = queryParser.parse("progetto");
        //perform the query
        runQuery(searcher, query);
    }

    @Test
    public void view_documents_phrasequery_with_queryparser_in_name() throws IOException, ParseException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the phrase "prassi da proteggere" in content
        QueryParser queryParser = new QueryParser("content", new WhitespaceAnalyzer());
        Query query = queryParser.parse("\"prassi di proteggere\"");
        //perform the query
        runQuery(searcher, query);
    }

    @Test
    public void view_documents_with_booleanquery_in_name() throws IOException, ParseException {
        //setting directory and searcher
        IndexSearcher searcher = setting_path_query();
        //retrieve documents contain the words "computer della storia" in name
        QueryParser queryParser = new QueryParser("name", new WhitespaceAnalyzer());
        Query query = queryParser.parse("+computer della +storia");
        //retrieve documents contain the words "proteggere i sistemi" in content
        PhraseQuery phraseQuery = new PhraseQuery.Builder()
                .add(new Term("content", "proteggere"))
                .add(new Term("content", "i"))
                .add(new Term("content", "sistemi"))
                .build();
        //combine two query
        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(new BooleanClause(query, BooleanClause.Occur.SHOULD))
                .add(new BooleanClause(phraseQuery, BooleanClause.Occur.SHOULD))
                .build();
        //perform the query
        runQuery(searcher, booleanQuery);
    }

    //Finish test

    
    //Auxiliary Function Here

    public IndexSearcher setting_path_query() throws IOException {
        Path path = Paths.get("howework_index/idx1");
        Directory directory = FSDirectory.open(path);

        //prepare  to read the index
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

    private static void runQuery(IndexSearcher searcher, Query query) throws IOException {
        runQuery(searcher, query, false);
    }

    private static void runQuery(IndexSearcher searcher, Query query, boolean explain) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        if (hits.scoreDocs.length > 0){
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                ScoreDoc scoreDoc = hits.scoreDocs[i];
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("doc"+scoreDoc.doc);
                System.out.println("   " + "Name:"+ doc.get("name") + " (" + scoreDoc.score +")");
                System.out.println("   " + "Content:"+ doc.get("content") + " (" + scoreDoc.score +")");
                if (explain) {
                    Explanation explanation = searcher.explain(query, scoreDoc.doc);
                    System.out.println(explanation);
                }
            }
        }
        else{
            System.out.println("XXXXXXXXXXXXXXXXXXXXXX\nDocuments not found!\nXXXXXXXXXXXXXXXXXXXXXX\n");
        }

    }


}