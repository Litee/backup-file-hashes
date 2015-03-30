package com.litee.backup_file_hashes.commands.diff;

import com.litee.backup_file_hashes.cli.Main;
import com.litee.backup_file_hashes.commands.DirectConnectUtils;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: Andrey
 * Date: 2015-02-28
 * Time: 09:54
 */
public class DiffCommand {
    public void process(Main.DiffCommandArguments args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        assert args.newSnapshot != null;
        assert args.oldSnapshot != null;
        assert args.outputSnapshot != null;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Element newRootElement;
        try (BZip2CompressorInputStream inputStream = new BZip2CompressorInputStream(new FileInputStream(args.newSnapshot))) {
            newRootElement = docBuilder.parse(inputStream).getDocumentElement();
        }
        Element oldRootElement;
        try (BZip2CompressorInputStream inputStream = new BZip2CompressorInputStream(new FileInputStream(args.oldSnapshot))) {
            oldRootElement = docBuilder.parse(inputStream).getDocumentElement();
        }
        Document outputDocument = DirectConnectUtils.createDefaultDocument(DirectConnectUtils.DEFAULT_CID);
        String outputFile = args.outputSnapshot == null ? "Me.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.xml.bz2" : args.outputSnapshot;

        Element addedElement = outputDocument.createElement("Directory");
        addedElement.setAttribute("Name", "Added");
        if (processElement(newRootElement, oldRootElement, addedElement) == DiffResult.DIFF_FOUND) {
            outputDocument.getDocumentElement().appendChild(addedElement);
        }
        Element removedElement = outputDocument.createElement("Directory");
        removedElement.setAttribute("Name", "Removed");
        if (processElement(oldRootElement, newRootElement, removedElement) == DiffResult.DIFF_FOUND) {
            outputDocument.getDocumentElement().appendChild(removedElement);
        }
        DirectConnectUtils.saveDocument(outputDocument, outputFile);
    }

    private DiffResult processElement(Element inputParentDirElement, Element excludeParentDirElement, Element outputParentDirElement) {
        for (Node inputChildNode = inputParentDirElement.getFirstChild(); inputChildNode != null; inputChildNode = inputChildNode.getNextSibling()) {
            if (inputChildNode instanceof Element) {
                Element inputChildElement = (Element) inputChildNode;
                String nodeName = inputChildElement.getNodeName();
                String fileSystemItemName = inputChildElement.getAttribute("Name");
                if (nodeName.equals("Directory")) {
                    Element excludeChildElement = null;
                    for (Node excludeChildNode = excludeParentDirElement.getFirstChild(); excludeChildNode != null; excludeChildNode = excludeChildNode.getNextSibling()) {
                        if (excludeChildNode instanceof Element) {
                            Element excludeChildElementTmp = (Element) excludeChildNode;
                            if (excludeChildElementTmp.getAttribute("Name").equals(fileSystemItemName)) {
                                excludeChildElement = excludeChildElementTmp;
                            }
                        }
                    }
                    if (excludeChildElement == null) {
                        outputParentDirElement.appendChild(outputParentDirElement.getOwnerDocument().importNode(inputChildElement, true));
                        return DiffResult.DIFF_FOUND;
                    } else {
                        Element outputChildElement = outputParentDirElement.getOwnerDocument().createElement("Directory");
                        outputChildElement.setAttribute("Name", fileSystemItemName);
                        if (processElement(inputChildElement, excludeChildElement, outputChildElement) == DiffResult.DIFF_FOUND) {
                            outputParentDirElement.appendChild(outputChildElement);
                            return DiffResult.DIFF_FOUND;
                        }
                    }
                } else if (nodeName.equals("File")) {
                    Element excludeChildElement = null;
                    for (Node excludeChildNode = excludeParentDirElement.getFirstChild(); excludeChildNode != null; excludeChildNode = excludeChildNode.getNextSibling()) {
                        if (excludeChildNode instanceof Element) {
                            Element excludeChildElementTmp = (Element) excludeChildNode;
                            if (excludeChildElementTmp.getAttribute("Name").equals(fileSystemItemName) &&
                                    excludeChildElementTmp.getAttribute("Length").equals(inputChildElement.getAttribute("Length")) &&
                                    excludeChildElementTmp.getAttribute("TTH").equals(inputChildElement.getAttribute("TTH"))) {
                                excludeChildElement = excludeChildElementTmp;
                            }
                        }
                    }
                    if (excludeChildElement == null) {
                        outputParentDirElement.appendChild(outputParentDirElement.getOwnerDocument().importNode(inputChildElement, true));
                        return DiffResult.DIFF_FOUND;
                    }
                }
            }
        }
        return DiffResult.NO_DIFF;
    }
}
