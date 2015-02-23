package com.litee.hash_based_recovery.output.dc;

import com.litee.hash_based_recovery.FileMetaData;
import com.litee.hash_based_recovery.FileMetadataCalculator;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: Andrey
 * Date: 2015-02-23
 * Time: 12:39
 */
public class DirectConnectFileSystemWalker {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Document document;
    private FileMetadataCalculator fileMetadataCalculator;

    public DirectConnectFileSystemWalker(FileMetadataCalculator fileMetadataCalculator) throws ParserConfigurationException {
        assert fileMetadataCalculator != null;
        this.fileMetadataCalculator = fileMetadataCalculator;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        document = docBuilder.newDocument();
    }

    public void processRoot(List<String> dirPaths, String outputFile) throws TransformerException, IOException {
        assert dirPaths != null;
        assert document != null;
        Element rootElement = document.createElement("FileListing");
        rootElement.setAttribute("Version", "1");
        rootElement.setAttribute("CID", "FLXGVEOUMVOMU");
        rootElement.setAttribute("Base", "/");
        rootElement.setAttribute("Generator", "HashBasedRecovery");
        document.appendChild(rootElement);
        for (String dirPath : dirPaths) {
            File dir = new File(dirPath);
            if (dir.exists() && dir.isDirectory()) {
                processDirectory(dir, rootElement);
            }
            else {
                // TODO
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        DOMSource source = new DOMSource(document);
        try (BZip2CompressorOutputStream outputStream = new BZip2CompressorOutputStream(new FileOutputStream(outputFile))) {
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
        }
    }

    public void processDirectory(File dir, Element parentElement) {
        assert dir != null;
        Element childElement = document.createElement("Directory");
        childElement.setAttribute("Name", dir.getName());
        parentElement.appendChild(childElement);
        assert dir.isDirectory();
        File[] dirs = dir.listFiles(File::isDirectory);
        Arrays.sort(dirs);
        for (File childDir : dirs) {
            processDirectory(childDir, childElement);
        }
        File[] files = dir.listFiles(File::isFile);
        Arrays.sort(files);
        Map<File, Future<FileMetaData>> futures = new LinkedHashMap<>();
        for (File file : files) {
            futures.put(file, executor.submit(() -> fileMetadataCalculator.processFile(file)));
        }
        for (Map.Entry<File, Future<FileMetaData>> fileFutureEntry : futures.entrySet()) {
            try {
                processFile(fileFutureEntry.getKey(), childElement, fileFutureEntry.getValue().get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void processFile(File file, Element parentElement, FileMetaData fileMetaData) {
        Element childElement = document.createElement("File");
        childElement.setAttribute("Name", file.getName());
        childElement.setAttribute("Size", String.valueOf(fileMetaData.getFileLength()));
        childElement.setAttribute("TTH", fileMetaData.getTthAsBase32());
        parentElement.appendChild(childElement);
    }

}