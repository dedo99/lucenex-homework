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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class QueryManager {

    public static boolean query_from_console() throws IOException, ParseException {
        System.out.println("---------TO EXIT THE PROCESS WRITE q IN THE TERMINAL---------");
        //Istanzia scanner con standard input
        Scanner scan = new Scanner(System.in);
        //Chiede di introdurre nome e cognome
        System.out.print("Insert your section query here (name or content)> ");
        String section = scan.nextLine(); //il programma adesso prende TUTTO il testo introdotto fino alla pressione di ENTER
        //Chiede di introdurre la città
        System.out.print("Insert your query here> ");
        String words = scan.nextLine();

        //controllo terminazione programma
        if(section.equals("q") || words.equals("q")){
            return false;
        }

        Query query;
        //controllo se è una Phrasequery
        if(words.startsWith("'") && words.endsWith("'")){
            query = execute_phrasequery(section, words);
        }//altrimenti viene effettuata una query parser
        else {
            query = execute_queryparser(section, words);
        }

        //esecuzione query
        runQuery(query);
        return true;
    }

    private static Query execute_queryparser(String section, String words) throws ParseException {
        QueryParser queryParser = new QueryParser(section, new
                WhitespaceAnalyzer());
        Query query = queryParser.parse(words);
        return query;
    }

    private static PhraseQuery execute_phrasequery(String section, String words){
        //rimozione degli apici dalla query
        words = words.substring(1, words.length()-1);
        System.out.println(words);

        //divido la stringa in parole utilizzate per la query
        String[] splits = words.split(" ");

        //creazione di una PhraseQuery Builder
        PhraseQuery.Builder builder = new PhraseQuery.Builder();

        //This regEx splits the String on the WhiteSpaces
        for(String splits2: splits) {
            builder.add(new Term(section, splits2));
        }

        //creazione di una PhraseQuery
        PhraseQuery phraseQuery = builder.build();
        return phraseQuery;
    }

    public static void runQuery(Query query) throws IOException {
        Path path = Paths.get("howework_index/idx1");
        Directory directory = FSDirectory.open(path);

        //prepare  to read the index
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        //perform the query
        runQuery(searcher, query);
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
