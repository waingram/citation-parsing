package edu.vt.cs.etd;

import org.jdom.Element;
import pl.edu.icm.cermine.ContentExtractor;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CermineExtractor {

    public static void main(String[] args) {
        try {
            String pathFiles = "/Volumes/GoogleDrive/My Drive/Projects/neural-parscit/etd-samples/2017vtetds"; // change it to your own files path
            String pathAnnotations = "/Users/waingram/Desktop";

            List<Path> paths = Files.walk(Paths.get(pathFiles))
                    .filter(path -> path.toString().endsWith(".pdf"))
                    .filter(path -> !path.toString().matches(".*_support_[0-9].pdf"))
                    .filter(path -> !path.toString().matches(".*_[aA]pproval_201[78].pdf"))
                    .filter(path -> !path.toString().endsWith("permission.pdf"))
                    .filter(path -> !path.toString().endsWith("_Letter.pdf"))
                    .collect(Collectors.toList());

            for (Path path : paths) {
                System.out.println(path);
                InputStream inputStream = new FileInputStream(path.toString());

                ContentExtractor extractor = new ContentExtractor();
                extractor.setPDF(inputStream);
                List<BibEntry> citations = extractor.getReferences();

                int count = 0;
                for (BibEntry citation: citations) {
                    String targetName = String.format("%s_%d", path.getParent().getFileName().toString(), count);
                    Path rootDir = Paths.get(pathAnnotations);
//                    Path citationFile = Paths.get(String.valueOf(path.getParent().getParent().toAbsolutePath()),"annotations", targetName + ".txt");

                    Path citationFile = Paths.get(String.valueOf(rootDir.toAbsolutePath()),"citations", targetName + ".txt");
                    Path bibtexFile = Paths.get(String.valueOf(rootDir.toAbsolutePath()),"citations", targetName + ".bib");

                    Files.createDirectories(citationFile.getParent());
                    Files.createDirectories(bibtexFile.getParent());

                    String citationText = citation.getText();
                    String bibtexText = citation.toBibTeX();

                    Files.write(citationFile, citationText.getBytes());
                    Files.write(bibtexFile, bibtexText.getBytes());

                    System.out.println(citationText);
                    System.out.println(bibtexText);

//                    System.out.println(citation.toBibTeX());
//                    for (BibEntryFieldType field : citation.getFieldKeys()) {
//                        System.out.println(field);
//                    }

                    count++;
                }
                System.out.println();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
