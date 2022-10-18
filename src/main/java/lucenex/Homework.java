package lucenex;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Homework {

    public static void write_index(Map<String, String> map_documents) throws IOException {
        Path path = Paths.get("howework_index/idx1");
        Directory directory = FSDirectory.open(path);

        //vengono definiti gli analyzers per l'indice name e l'indice content
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        //analyzer per il campo name del documento
        CharArraySet stopWords = new CharArraySet(Arrays.asList(" ", "_", "L'", "del", "in", "dei", "di"), true);
        Analyzer analyzer_name = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .build();
        perFieldAnalyzers.put("name", analyzer_name);
        //analyzer per il campo content del documento
        Analyzer analyzer_content = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .addTokenFilter(StopFilterFactory.class)
                .build();
        perFieldAnalyzers.put("content", analyzer_content);
        //analyzer definitivo
        Analyzer analyzer = new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), perFieldAnalyzers);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Codec codec = new SimpleTextCodec();
        if (codec != null) {
            config.setCodec(codec);
        }

        IndexWriter writer = new IndexWriter(directory, config);

        //delete all documents stored before
        writer.deleteAll();

        //acquisizione istante di tempo iniziale
        long startTime = System.nanoTime();

        //create a documents to add to index
        for (String key : map_documents.keySet()){
            Document doc = new Document();
            doc.add(new TextField("name", key, Field.Store.YES));
            doc.add(new TextField("content", map_documents.get(key), Field.Store.YES));
            writer.addDocument(doc);
        }

        //persisto i dati sull'indice
        writer.commit();
        writer.close();

        //acquisizione istante di tempo finale
        long endTime = System.nanoTime();
        // ottiene la differenza tra i due valori di tempo
        long timeElapsed = endTime - startTime;
        //stampe
        System.out.println("Numero documenti indicizati: " + map_documents.size());
        System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);
    }


    public static void main(String[] args) throws IOException, ParseException {
        File file = new File("documents");
        //viene creata una mappa con chiave il nome del file e
        //come valore il contenuto di tale documento
        Map<String, String> mappa = new FileManager().findAllFilesInFolder(file);
        //effettuiamo l'indicizzazione dei mappa (quindi dei documenti)
        write_index(mappa);
        //effettuiamo query da console
        boolean run = true;
        while (run){
            run = QueryManager.query_from_console();
        }
    }


}
