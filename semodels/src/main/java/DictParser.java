import utils.Form;
import utils.Lemma;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;

public class DictParser {

    public void parse(InputStream stream, ArrayList<Lemma> lemmas, Morph morph) throws XMLStreamException {
        // XML reader init
        XMLInputFactory streamFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = streamFactory.createXMLStreamReader(stream);

        System.out.println("=== Dictionary parsing ===");
        System.out.println("Start parsing....");

        // if false grammema tag describes lemma
        // if true  grammema tag describes form
        boolean grammemaState = false;

        Lemma lemma = null;

        Form form = null;
        String key = null;

        while (reader.hasNext()) {
            reader.next();

            int event_type = reader.getEventType();
            switch (event_type) {
                case XMLStreamConstants.START_ELEMENT -> {
                    switch (reader.getLocalName()) {
                        case "grammemes":
                            break;
                        case "restrictions":
                            break;
                        case "lemmata":
                            break;
                        case "lemma":
                            grammemaState = false;
                            lemma = new Lemma();
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                switch (reader.getAttributeLocalName(i)) {
                                    case "id" -> {
                                    }
                                    case "rev" -> lemma.rev = reader.getAttributeValue(i).trim();
                                    default -> System.out.println("Unknown attribute in <lemma>");
                                }
                            }
                            break;
                        case "l":
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                if ("t".equals(reader.getAttributeLocalName(i))) {
                                    assert lemma != null;
                                    lemma.word = reader.getAttributeValue(i).trim();
                                } else {
                                    System.out.println("Unknown attribute in <l>");
                                }
                            }
                            break;
                        case "g":
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                if ("v".equals(reader.getAttributeLocalName(i))) {
                                    if (!grammemaState) {
                                        // grammema of lemma
                                        assert lemma != null;
                                        lemma.grammemes.add(reader.getAttributeValue(i).trim());
                                    } else {
                                        // grammema of form
                                        assert form != null;
                                        form.grammemes.add(reader.getAttributeValue(i).trim());
                                    }
                                } else {
                                    System.out.println("Unknown attribute in <g>");
                                }
                            }
                            break;
                        case "f":
                            grammemaState = true;
                            form = new Form();
                            form.lemma = lemma;
                            for (int i = 0; i < reader.getAttributeCount(); i++) {
                                if ("t".equals(reader.getAttributeLocalName(i))) {
                                    key = reader.getAttributeValue(i);
                                } else {
                                    System.out.println("Unknown attribute in <f>");
                                }
                            }
                            break;
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    if (reader.getLocalName().equals("lemma")) {

                        grammemaState = true;
                        lemmas.add(lemma);
                        lemma = null;
                    }
                    if (reader.getLocalName().equals("f")) {
                        grammemaState = false;
                        morph.put(key, form);
                        form = null;
                    }
                }
                default -> {
                }
            }
        }

        reader.close();
        System.out.println("Finished parsing!");
    }
}
