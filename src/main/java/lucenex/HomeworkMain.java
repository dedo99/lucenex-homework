package lucenex;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.*;
import java.util.Map;

public class HomeworkMain {

    public static void main(String[] args) throws IOException, ParseException {
        File file = new File("documents");
        //viene creata una mappa con chiave il nome del file e
        //come valore il contenuto di tale documento
        Map<String, String> mappa = new FileManager().findAllFilesInFolder(file);
        //effettuiamo l'indicizzazione dei mappa (quindi dei documenti)
        IndexManager indexManager = new IndexManager();
        indexManager.write_index(mappa);
        //effettuiamo query da console
        boolean run = true;
        while (run){
            run = QueryManager.query_from_console();
        }
    }


}
