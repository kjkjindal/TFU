package app.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 12/20/16
 */
public final class XMLSaver {

    /**
     * Creates a Document object containing the XML data from a Collection of
     * MusicalElements
     *
     * @param c Collection of MusicalElements to be converted
     * @return Document of XML data
     */
    public static Document documentFromComposition(Collection<Saveable> c){
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .newDocument();

            Element rootElement = doc.createElement("experiment");
            doc.appendChild(rootElement);

            for (Saveable element : c) {
                rootElement.appendChild(element.getXML(doc));
            }
            return doc;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Cannot construct document builder", e);
        }
    }

    /**
     * Converts the Collection of MusicalElements into a String of XML data and returns it
     *
     * @param c Collection of MusicalElements
     * @return the String of XML composition data
     * @throws RuntimeException if the collection cannot be transformed to an XML string
     */
    public static String xmlFromComposition(Collection<Saveable> c) {
        return xmlFromDocument(documentFromComposition(c));
    }

    /**
     * Creates an XML representation of a Collection of MusicalElements and writes it to
     * a File
     *
     * @param c Collection of MusicalElements to convert and save
     * @param f the File into which to write the XML data
     * @throws IOException any source-result transformation error
     */
    public static void saveComposition(Collection<Saveable> c, File f)
            throws IOException {
        saveDocument(f, documentFromComposition(c));
    }

    /**
     * Converts the Document of XML data into a String and returns it
     *
     * @param doc Document of XML to turn into a String
     * @return String of XML data
     * @throws RuntimeException if the document cannot be transformed to an XML string
     */
    public static String xmlFromDocument(Document doc) {
        try {
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformSource(source, result);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Conversion to XML failed", e);
        }
    }

    /**
     * Saves the Document of XML data into a File
     *
     * @param file File to save to
     * @param doc Document to save to File
     * @throws IOException any source-result transformation error
     */
    public static void saveDocument(File file, Document doc) throws IOException {
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformSource(source, result);
    }

    /**
     * Transforms the XML suorce into an appropriate format and returns it
     *
     * @param s the XML source
     * @param r the transformed XML result
     * @throws IOException any source-result transformation error
     */
    private static void transformSource(Source s, Result r) throws IOException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(s, r);
        } catch (TransformerException e) {
            throw new IOException("Cannot save to file", e);
        }
    }

}
