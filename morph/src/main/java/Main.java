import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        ArrayList<Lemma> lemmas = new ArrayList<>();
        Morph morph = new Morph();

        try (InputStream stream = new FileInputStream("src/main/resources/dict.opcorpora.xml")) {
            DictParser parser = new DictParser();

            parser.parse(stream, lemmas, morph);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        ArrayList<String> tokens = new ArrayList<>();

        FileReader fr = null;
        fr = new FileReader("src/main/resources/bible.txt");
        StreamTokenizer st = new StreamTokenizer(fr);
        st.lowerCaseMode(true);

        while (st.nextToken() != StreamTokenizer.TT_EOF) {
            if (st.ttype == StreamTokenizer.TT_WORD) {
                tokens.add(st.sval);
            }
        }

        morph.processCorpora(tokens);
        String result = morph.printRecords();

        File outputFile = new File("src/main/resources/output.txt");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
