package lucenex;

import org.apache.lucene.analysis.Analyzer;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class IndexManager {

    public void write_index(Map<String, String> map_documents) throws IOException {
        Path path = Paths.get("howework_index/idx1");
        Directory directory = FSDirectory.open(path);

        //vengono definiti gli analyzers per l'indice name e l'indice content
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();

        // Per inserire le stop words di default dell'ItalianAnalyzer() nel CustomAnalyzer
        // salvo la lista in un file che verrà utilizzato successivamente
        String stopWords = ItalianAnalyzer.getDefaultStopSet().toString();
        stopWords = stopWords.substring(0, stopWords.length()-1);
        saveArrayToFile("stopWords.txt", stopWords.split(","));

        //analyzer per il campo name del documento
        Analyzer analyzer_name = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .build();
        perFieldAnalyzers.put("name", analyzer_name);

        // è necessario definire la cartella
        // da cui prendere i file nei parametri (in questo caso le stop words)
        Path resources = Paths.get("");

        //analyzer per il campo content del documento
        Analyzer analyzer_content = CustomAnalyzer.builder(resources)
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .addTokenFilter(StopFilterFactory.NAME, "ignoreCase", "false", "words", "stopWords.txt", "format", "wordset")
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
        System.out.println("Number of documents indexed: " + map_documents.size());
        System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);
    }

    // funzione per il salvataggio di un array su file
    private static void saveArrayToFile(String filename, String[] x) throws IOException {
        BufferedWriter outputWriter;
        outputWriter = new BufferedWriter(new FileWriter(filename));
        for (String s : x) {
            outputWriter.write(s.substring(1));
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
        System.out.println("File " + filename + " created.");
    }
}
