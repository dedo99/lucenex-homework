package lucenex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private Map<String, String> map_documents;

    public FileManager(){
        this.map_documents = new HashMap<>();
    }

    public Map<String, String> findAllFilesInFolder(File folder) throws IOException {
        for (File file : folder.listFiles()) {
            //l'if viene eseguito se il file è un documento e non una cartella
            if (!file.isDirectory()) {
                getNameAndContentFile(file, this.map_documents);
            } else {
                findAllFilesInFolder(file);
            }
        }
        return this.map_documents;
    }

    public static void getNameAndContentFile(File file, Map<String,String> map_documents) throws IOException {
        String name = file.getName();
        String content = "";
        //rimuove l'estensione .txt
        name = name.substring(0, name.length()-4);
        //lettura del contenuto del file
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String line = reader.readLine();
        content = line;
        while(line!=null) {
            line = reader.readLine();
            content = content + line;
        }
        //rimuove l'ultimo inserimento che corrisponde a null
        content = content.substring(0, content.length()-4);
        //mappa che contiene come chiave il nome del documento e
        //come valore il suo contenuto
        map_documents.put(name, content);
    }

}
