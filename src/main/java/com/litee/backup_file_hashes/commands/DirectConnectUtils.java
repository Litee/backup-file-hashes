package com.litee.backup_file_hashes.commands;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * User: Andrey
 * Date: 2015-03-30
 * Time: 23:58
 */
public class DirectConnectUtils {
    public static final String DEFAULT_CID = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    public static Document createDefaultDocument(String cid) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.newDocument();
        document.setXmlStandalone(true);
        Element rootElement = document.createElement("FileListing");
        rootElement.setAttribute("Version", "1");
        rootElement.setAttribute("CID", cid);
        rootElement.setAttribute("Base", "/");
        rootElement.setAttribute("Generator", "HashBasedRecovery");
        document.appendChild(rootElement);
        return document;
    }

    public static void saveDocument(Document document, String outputFile) throws IOException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        try (BZip2CompressorOutputStream outputStream = new BZip2CompressorOutputStream(new FileOutputStream(outputFile))) {
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
        }
    }
}
