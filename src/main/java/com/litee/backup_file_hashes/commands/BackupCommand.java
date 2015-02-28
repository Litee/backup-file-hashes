package com.litee.backup_file_hashes.commands;

import com.litee.backup_file_hashes.FileMetaData;
import com.litee.backup_file_hashes.FileMetadataCalculator;
import com.litee.backup_file_hashes.FileMetadataCalculatorImpl;
import com.litee.backup_file_hashes.cache.FileMetadataCalculatorWithCacheImpl;
import com.litee.backup_file_hashes.cli.Main;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
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
public class BackupCommand {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void process(Main.BackupCommandArguments args) throws Exception {
        assert args.inputDir != null;
        FileMetadataCalculator fileMetadataCalculator;
        if (args.cacheDir == null) {
            fileMetadataCalculator = new FileMetadataCalculatorImpl();
        }
        else {
            fileMetadataCalculator = new FileMetadataCalculatorWithCacheImpl(args.cacheDir);
        }
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.newDocument();
        document.setXmlStandalone(true);
        List<String> dirPaths = args.inputDir;
        String outputFile = args.outputSnapshot == null ? "Me.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.xml.bz2" : args.outputSnapshot;
        Element rootElement = document.createElement("FileListing");
        rootElement.setAttribute("Version", "1");
        rootElement.setAttribute("CID", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        rootElement.setAttribute("Base", "/");
        rootElement.setAttribute("Generator", "HashBasedRecovery");
        document.appendChild(rootElement);
        for (String dirPath : dirPaths) {
            File dir = new File(dirPath);
            if (dir.exists() && dir.isDirectory()) {
                processDirectory(dir, fileMetadataCalculator, document, rootElement);
            }
            else {
                System.out.println("ERROR: Not found or not a directory: " + dir.getAbsolutePath());
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        try (BZip2CompressorOutputStream outputStream = new BZip2CompressorOutputStream(new FileOutputStream(outputFile))) {
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
        }
    }

    public void processDirectory(File dir, FileMetadataCalculator fileMetadataCalculator, Document document, Element parentElement) {
        assert dir != null;
        Element childElement = document.createElement("Directory");
        childElement.setAttribute("Name", dir.getName());
        parentElement.appendChild(childElement);
        assert dir.isDirectory();
        File[] dirs = dir.listFiles(File::isDirectory);
        if (dirs != null) {
            Arrays.sort(dirs);
            for (File childDir : dirs) {
                processDirectory(childDir, fileMetadataCalculator, document, childElement);
            }
        }
        File[] files = dir.listFiles(File::isFile);
        if (files != null) {
            Arrays.sort(files);
            Map<File, Future<FileMetaData>> futures = new LinkedHashMap<>();
            for (File file : files) {
                futures.put(file, executor.submit(() -> fileMetadataCalculator.processFile(file)));
            }
            for (Map.Entry<File, Future<FileMetaData>> fileFutureEntry : futures.entrySet()) {
                try {
                    processFile(fileFutureEntry.getKey(), document, childElement, fileFutureEntry.getValue().get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void processFile(File file, Document document, Element parentElement, FileMetaData fileMetaData) {
        Element childElement = document.createElement("File");
        childElement.setAttribute("Name", file.getName());
        childElement.setAttribute("Size", String.valueOf(fileMetaData.getFileLength()));
        childElement.setAttribute("TTH", fileMetaData.getTthAsBase32());
        parentElement.appendChild(childElement);
    }

}
