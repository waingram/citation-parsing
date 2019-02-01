package edu.vt.cs.etd;

import com.google.common.collect.ImmutableSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NeuralParsCitEvaluation {

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
            String npcHome = "/Users/waingram/Projects/Neural-ParsCit";
            String npcPath = "neural_parscit_parsed_citations.txt";

            String pathCitations = "/Users/waingram/Desktop/sample17-ran100-v2/sample17-ran100-v2.citestr_combined.txt";
            String pathResult = "results/npc_analysis.txt";

            Path citationsFile = Paths.get(pathCitations);
            Path npcFile = Paths.get(npcPath);

            List<String> npcLines = Files.lines(npcFile).collect(Collectors.toList());
            Iterator<String> npcIterator = npcLines.iterator();

            Path resultsFile = Paths.get(pathResult);
            if (Files.exists(resultsFile)) {
                Files.delete(resultsFile);
            }

            int count = 0;
            int index = 0;
            List<String> references = Files.lines(citationsFile).collect(Collectors.toList());
            for (String reference : references) {

                if (index == 2) index++;

                List<String> referenceTokens = Arrays.asList(reference.split("\\s+"));
                System.out.println(reference);

                Path annPath = Paths.get(citationsFile.getParent().toString(),
                        citationsFile.getFileName().toString()
                                .replace("combined", String.format("%02d", index++))
                                .replace(".txt", ".ann"));

                List<String> groundTruth = new ArrayList<>();
                Files.lines(annPath).forEach(groundTruth::add);
                groundTruth.sort(Comparator.comparing(l -> Integer.parseInt(l.split("\\t")[1].split("\\s")[1])));

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

                            String[] npcTokens = npcIterator.next().split("\t");
                            String predictedValue = npcTokens[0];
                            String predictedLabel = npcTokens[1];

                            System.out.printf("%s\t%s\t%s\t%s\t%s%n",
                                    referenceToken, trueValue, predictedValue, trueLabel, predictedLabel);

                            if (!FIELDS.contains(trueLabel)) continue;

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

    /**
     * Tokenize the input text into a list of tokens/words using a Lucene analyzer.
     *
     * @param text     An input text.
     * @param analyzer An Analyzer.
     * @return A list of tokenized words.
     */
    public static List<String> tokenize(String text, Analyzer analyzer) throws IOException {
        List<String> tokens = new ArrayList<>();
        if (text == null) return tokens;

        TokenStream ts = analyzer.tokenStream("", new StringReader(text));
        CharTermAttribute attr = ts.getAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            tokens.add(attr.toString());
        }
        ts.end();
        ts.close();
        return tokens;
    }

    private static void writeValue(Path resultsFile, Map<String, String> groundTruth, String predictedValue, String groundTruthValueKey)
            throws IOException {

        if (groundTruth.get(groundTruthValueKey) == null || groundTruth.get(groundTruthValueKey).isEmpty()) return;
        if (predictedValue == null) predictedValue = "";

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