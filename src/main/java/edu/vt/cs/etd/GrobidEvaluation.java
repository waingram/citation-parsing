package edu.vt.cs.etd;

import com.google.common.collect.ImmutableSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.grobid.core.data.BiblioItem;
import org.grobid.core.engines.Engine;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.utilities.GrobidProperties;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GrobidEvaluation {

    private static final Set<String> EMPTYS = ImmutableSet.of("", ",", ".");
    private static final Set<String> FIELDS = ImmutableSet.of(
            "author",
            "booktitle",
            "date",
            "editor",
            "institution",
            "journal",
            "location",
            "note",
            "pages",
            "publisher",
            "title",
            "volume");

    public static void main(String[] args) {
        try {
            String pGrobidHome = "/Users/waingram/Projects/grobid/grobid-home";
            GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(Arrays.asList(pGrobidHome));
            GrobidProperties.getInstance(grobidHomeFinder);
            Engine engine = GrobidFactory.getInstance().createEngine();

            String pathFiles = "/Users/waingram/Desktop/sample17-ran100-v2"; // change it to your own files path
            String pathResult = "results/grobid_analysis.txt";

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
                List<String> referenceTokens = Arrays.asList(reference.split("(\\s+|:|\\(|\\))"));
                System.out.println(reference);

                Path annPath = Paths.get(txtPath.getParent().toString(),
                        txtPath.getFileName().toString().replace(".txt", ".ann"));
                if (!Files.exists(annPath))
                    continue;

                List<String> groundTruth = new ArrayList<>();
                Files.lines(annPath).forEach(groundTruth::add);
                groundTruth.sort(Comparator.comparing(l -> Integer.parseInt(l.split("\\t")[1].split("\\s")[1])));

                BiblioItem biblioItem = engine.processRawReference(reference, 1);

                List<String> predictedAuthors = biblioItem.getAuthors() != null ? Arrays.asList(biblioItem.getAuthors().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedBooktitle = biblioItem.getBookTitle() != null ? Arrays.asList(biblioItem.getBookTitle().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedDate = biblioItem.getPublicationDate() != null ? Arrays.asList(biblioItem.getPublicationDate().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedEditors = biblioItem.getEditors() != null ? Arrays.asList(biblioItem.getEditors().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedInstitution = biblioItem.getInstitution() != null ? Arrays.asList(biblioItem.getInstitution().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedJournal = biblioItem.getJournal() != null ? Arrays.asList(biblioItem.getJournal().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedLocation = biblioItem.getLocation() != null ? Arrays.asList(biblioItem.getLocation().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedNote = biblioItem.getNote() != null ? Arrays.asList(biblioItem.getNote().toLowerCase().replaceAll("[^\\sa-zA-Z0-9]", "").split("\\s")) : new ArrayList<>();
                List<String> predictedPages = biblioItem.getPageRange() != null ? Arrays.asList(biblioItem.getPageRange().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedPublisher = biblioItem.getPublisher() != null ? Arrays.asList(biblioItem.getPublisher().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                List<String> predictedTitle = biblioItem.getTitle() != null ? Arrays.asList(biblioItem.getTitle().toLowerCase().replaceAll("[^\\sa-zA-Z0-9]", "").split("\\s")) : new ArrayList<>();
                List<String> predictedVolume = biblioItem.getVolumeBlock() != null ? Arrays.asList(biblioItem.getVolumeBlock().replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();

                Iterator<String> referenceTokenIterator = referenceTokens.iterator();
                for (String line : groundTruth) {
                    String[] parts = line.split("\\t");
                    String trueLabel = parts[1].split("\\s")[0];
                    if (trueLabel.equals("authors")) trueLabel = "author";
                    if (trueLabel.equals("address")) trueLabel = "location";
                    if (trueLabel.equals("year")) trueLabel = "date";
                    String value = parts[2];
                    if (trueLabel.equals("year_extra")) continue;
                    String[] gtTokens = value.split("[\\s:\"“”]+");
                    for (String trueValue : gtTokens) {
                        if (referenceTokenIterator.hasNext()) {
                            String referenceToken = "";
                            while (referenceTokenIterator.hasNext()) {
                                referenceToken = referenceTokenIterator.next();
                                if (!EMPTYS.contains(referenceToken)) break;
                            }
                            if (EMPTYS.contains(referenceToken) && !referenceTokenIterator.hasNext()) break;
                            String predictedLabel;
                            System.out.printf("%s\t%s\t%s%n", referenceToken, trueValue, trueLabel);

                            if (!FIELDS.contains(trueLabel)) continue;

                            String rtStripped = referenceToken.replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase();

                            List<String> predictedSame = getPredictedString(biblioItem, trueLabel) != null ? Arrays.asList(getPredictedString(biblioItem, trueLabel).replaceAll("[^\\sa-zA-Z0-9]", "").toLowerCase().split("\\s")) : new ArrayList<>();
                            if (predictedSame.contains(rtStripped)) predictedLabel = trueLabel;
                            else if (predictedAuthors.contains(rtStripped)) predictedLabel = "author";
                            else if (predictedTitle.contains(rtStripped)) predictedLabel = "title";
                            else if (predictedBooktitle.contains(rtStripped)) predictedLabel = "booktitle";
                            else if (predictedDate.contains(rtStripped)) predictedLabel = "date";
                            else if (predictedEditors.contains(rtStripped)) predictedLabel = "editor";
                            else if (predictedInstitution.contains(rtStripped)) predictedLabel = "institution";
                            else if (predictedJournal.contains(rtStripped)) predictedLabel = "journal";
                            else if (predictedLocation.contains(rtStripped)) predictedLabel = "location";
                            else if (predictedNote.contains(rtStripped)) predictedLabel = "note";
                            else if (predictedPublisher.contains(rtStripped)) predictedLabel = "publisher";
                            else if (predictedVolume.contains(rtStripped)) predictedLabel = "volume";
                            else if (predictedPages.contains(rtStripped)) predictedLabel = "pages";
                            else predictedLabel = "X";

                            Files.write(resultsFile, (String.format("%s\t%s\t%s%s",
                                    referenceToken,
                                    trueLabel,
                                    predictedLabel,
                                    System.lineSeparator())).getBytes(UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);


                        } else {
                            break;
                        }
                    }
                }

                Files.write(resultsFile, (System.lineSeparator()).getBytes(UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

//                if (++count > 3) break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getPredictedString(BiblioItem biblioItem, String trueLabel) {
        if (trueLabel.equals("author")) return biblioItem.getAuthors();
        if (trueLabel.equals("booktitle")) return biblioItem.getBookTitle();
        if (trueLabel.equals("year")) return biblioItem.getPublicationDate();
        if (trueLabel.equals("editor")) return biblioItem.getEditors();
        if (trueLabel.equals("institution")) return biblioItem.getInstitution();
        if (trueLabel.equals("journal")) return biblioItem.getJournal();
        if (trueLabel.equals("location")) return biblioItem.getLocation();
        if (trueLabel.equals("note")) return biblioItem.getNote();
        if (trueLabel.equals("pages")) return biblioItem.getPageRange();
        if (trueLabel.equals("publisher")) return biblioItem.getPublisher();
        if (trueLabel.equals("title")) return biblioItem.getTitle();
        if (trueLabel.equals("volume")) return biblioItem.getVolumeBlock();
        return "";
    }

}