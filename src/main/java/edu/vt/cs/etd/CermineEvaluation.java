package edu.vt.cs.etd;

import pl.edu.icm.cermine.bibref.CRFBibReferenceParser;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static pl.edu.icm.cermine.bibref.model.BibEntryFieldType.*;

public class CermineEvaluation {

    public static void main(String[] args) {
        try {

            String pathFiles = "/Volumes/GoogleDrive/My Drive/Projects/neural-parscit/etd-samples/sample17-ran100-v2"; // change it to your own files path
            String pathResult = "results/cermine_analysis.txt";

            List<Path> paths = Files.walk(Paths.get(pathFiles))
                    .filter(path -> path.toString().endsWith(".txt"))
                    .filter(path -> !path.toString().endsWith("_02.txt"))
                    .collect(Collectors.toList());
            Path resultsFile = Paths.get(pathResult);
            if (Files.exists(resultsFile)) {
                Files.delete(resultsFile);
            }

            int count = 0;
            for (Path txtPath : paths) {
                String reference = new String(Files.readAllBytes(txtPath), "UTF-8");
                System.out.println(reference);

                Path annPath = Paths.get(txtPath.getParent().toString(),
                        txtPath.getFileName().toString().replace(".txt", ".ann"));

//                Map<String, String> groundTruth = Files.lines(annPath)
//                        .map(s -> s.split("\\t"))
//                        .collect(Collectors.toMap(parts -> parts[1].split("\\s")[0], parts -> parts[2], (a, b) -> b));

                Map<String, String> groundTruth = new HashMap<>();
                Files.lines(annPath).forEach(line -> {
                    String[] parts = line.split("\\t");
                    String key = parts[1].split("\\s")[0];
                    String value = parts[2];
                    groundTruth.put(key, value);
                });


                CRFBibReferenceParser referenceParser = CRFBibReferenceParser.getInstance();
                BibEntry bibEntry = referenceParser.parseBibReference(reference);

                String authors = null;
                String booktitle = null;
                String date = null;
                String editors = null;
                String institution = null;
                String journal = null;
                String location = null;
                String note = null;
                String pages = null;
                String publisher = null;
                String title = null;
                String volume = null;

                if (bibEntry.getAllFieldValues(AUTHOR) != null &&  bibEntry.getAllFieldValues(AUTHOR).size() > 0) {
                    authors = String.join(", ", bibEntry.getAllFieldValues(AUTHOR));
                }

                if (bibEntry.getAllFieldValues(BOOKTITLE) != null &&  bibEntry.getAllFieldValues(BOOKTITLE).size() > 0) {
                    booktitle = String.join(", ", bibEntry.getAllFieldValues(BOOKTITLE));
                }

                if (bibEntry.getAllFieldValues(YEAR) != null &&  bibEntry.getAllFieldValues(YEAR).size() > 0) {
                    date = String.join(", ", bibEntry.getAllFieldValues(YEAR));
                }

                if (bibEntry.getAllFieldValues(EDITOR) != null &&  bibEntry.getAllFieldValues(EDITOR).size() > 0) {
                    editors = String.join(", ", bibEntry.getAllFieldValues(EDITOR));
                }

                if (bibEntry.getAllFieldValues(INSTITUTION) != null &&  bibEntry.getAllFieldValues(INSTITUTION).size() > 0) {
                    institution = String.join(", ", bibEntry.getAllFieldValues(INSTITUTION));
                }

                if (bibEntry.getAllFieldValues(JOURNAL) != null &&  bibEntry.getAllFieldValues(JOURNAL).size() > 0) {
                    journal = String.join(", ", bibEntry.getAllFieldValues(JOURNAL));
                }

                if (bibEntry.getAllFieldValues(LOCATION) != null &&  bibEntry.getAllFieldValues(LOCATION).size() > 0) {
                    location = String.join(", ", bibEntry.getAllFieldValues(LOCATION));
                }

                if (bibEntry.getAllFieldValues(NOTE) != null &&  bibEntry.getAllFieldValues(NOTE).size() > 0) {
                    note = String.join(", ", bibEntry.getAllFieldValues(NOTE));
                }

                if (bibEntry.getAllFieldValues(PAGES) != null &&  bibEntry.getAllFieldValues(PAGES).size() > 0) {
                    pages = String.join(", ", bibEntry.getAllFieldValues(PAGES));
                }

                if (bibEntry.getAllFieldValues(PUBLISHER) != null &&  bibEntry.getAllFieldValues(PUBLISHER).size() > 0) {
                    pages = String.join(", ", bibEntry.getAllFieldValues(PUBLISHER));
                }

                if (bibEntry.getAllFieldValues(TITLE) != null &&  bibEntry.getAllFieldValues(TITLE).size() > 0) {
                    title = String.join(", ", bibEntry.getAllFieldValues(TITLE));
                }

                if (bibEntry.getAllFieldValues(VOLUME) != null &&  bibEntry.getAllFieldValues(VOLUME).size() > 0) {
                    volume = String.join(", ", bibEntry.getAllFieldValues(VOLUME));
                }


                if ((authors != null && !authors.trim().isEmpty()) || groundTruth.containsKey("authors")) {
                    writeValue(resultsFile, groundTruth, authors, "authors");
                }

                if ((booktitle != null && !booktitle.trim().isEmpty()) || groundTruth.containsKey("booktitle")) {
                    writeValue(resultsFile, groundTruth, booktitle, "booktitle");
                }

                if ((date != null && !date.trim().isEmpty()) || groundTruth.containsKey("year")) {
                    writeValue(resultsFile, groundTruth, date, "year");
                }

                if ((editors != null && !editors.trim().isEmpty()) || groundTruth.containsKey("editor")) {
                    writeValue(resultsFile, groundTruth, editors, "editor");
                }

                if ((institution != null && !institution.trim().isEmpty()) || groundTruth.containsKey("school")) {
                    writeValue(resultsFile, groundTruth, institution, "school");
                }

                if ((journal != null && !journal.trim().isEmpty()) || groundTruth.containsKey("journal")) {
                    writeValue(resultsFile, groundTruth, journal, "journal");
                }

                if ((location != null && !location.trim().isEmpty()) || groundTruth.containsKey("address")) {
                    writeValue(resultsFile, groundTruth, location, "address");
                }

                if ((note != null && !note.trim().isEmpty()) || groundTruth.containsKey("note")) {
                    writeValue(resultsFile, groundTruth, note, "note");
                }

                if ((pages != null && !pages.trim().isEmpty()) || groundTruth.containsKey("pages")) {
                    writeValue(resultsFile, groundTruth, pages, "pages");
                }

                if ((publisher != null && !publisher.trim().isEmpty()) || groundTruth.containsKey("publisher")) {
                    writeValue(resultsFile, groundTruth, publisher, "publisher");
                }

                if ((title != null && !title.trim().isEmpty()) || groundTruth.containsKey("title")) {
                    writeValue(resultsFile, groundTruth, title, "title");
                }

                if ((volume != null && !volume.trim().isEmpty()) || groundTruth.containsKey("volume")) {
                    writeValue(resultsFile, groundTruth, volume, "volume");
                }

                Files.write(resultsFile, (System.lineSeparator()).getBytes(UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

//                if (++count > 3) break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeValue(Path resultsFile, Map<String, String> groundTruth, String predictedValue, String groundTruthValueKey)
            throws IOException {

        if (groundTruth.get(groundTruthValueKey) == null || groundTruth.get(groundTruthValueKey).isEmpty()) return;
        if (predictedValue == null) predictedValue = "null";

        String[] gtvs = groundTruth.get(groundTruthValueKey).split("\\s");
        String[] pvs = predictedValue.split("\\s");

        for (int i = 0; i < gtvs.length; i++) {
            String pv = pvs.length <= i ? "null" : pvs[i];
            Files.write(resultsFile, (String.format("%s\t%s\t%s%s",
                    gtvs[i],
                    pv,
                    groundTruthValueKey,
                    System.lineSeparator())).getBytes(UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }
}