package com.litee.backup_file_hashes.commands;

import com.litee.backup_file_hashes.FileMetaData;
import com.litee.backup_file_hashes.FileMetadataCalculator;
import com.litee.backup_file_hashes.FileMetadataCalculatorImpl;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: Andrey
 * Date: 2015-02-23
 * Time: 12:39
 */
public class RestoreCommand {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private FileMetadataCalculator fileMetadataCalculator;

    public RestoreCommand() throws ParserConfigurationException {
        assert fileMetadataCalculator != null;
        this.fileMetadataCalculator = new FileMetadataCalculatorImpl();
    }

    public void process(List<String> inputDirs, String inputSnapshot, String outputRoot) throws TransformerException, IOException, ParserConfigurationException, SAXException {
        assert inputDirs != null;
        // Scan files
        HashMap<Long, List<Path>> files = new HashMap<>();
        inputDirs.stream().forEach(inputDir -> {
            File dir = new File(inputDir);
            if (dir.exists() && dir.isDirectory()) {
                try {
                    Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                            File file = path.toFile();
                            if (file.isFile()) {
                                long length = file.length();
                                List<Path> paths = files.get(length);
                                if (paths == null) {
                                    paths = new ArrayList<>(1);
                                }
                                paths.add(path);
                                files.put(length, paths);
                            }
                            return super.visitFile(path, basicFileAttributes);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("ERROR: Not found or not a directory: " + dir.getAbsolutePath());
            }
        });
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document;
        try (BZip2CompressorInputStream inputStream = new BZip2CompressorInputStream(new FileInputStream(inputSnapshot))) {
            document = docBuilder.parse(inputStream);
        }
        Element rootElement = document.getDocumentElement();
        processDirectory(new File(outputRoot), rootElement, files);
    }

    public void processDirectory(File dir, Element parentDirElement, HashMap<Long, List<Path>> files) {
        assert dir != null;
        System.out.println("DEBUG: Processing directory " + dir);
        for (Node childNode = parentDirElement.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (childNode instanceof Element) {
                Element childElement = (Element) childNode;
                String nodeName = childElement.getNodeName();
                File childFileSystemEntry = new File(dir, childElement.getAttribute("Name"));
                if (nodeName.equals("Directory")) {
                    childFileSystemEntry.mkdir();
                    processDirectory(childFileSystemEntry, childElement, files);
                }
                else if (nodeName.equals("File")) {
                    try {
                        processFile(childFileSystemEntry, childElement, files);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void processFile(File file, Element fileElement, HashMap<Long, List<Path>> files) throws Exception {
        long fileLength = Long.parseLong(fileElement.getAttribute("Size"));
        String fileTTH = fileElement.getAttribute("TTH");
        List<Path> paths = files.get(fileLength);
        if (paths != null) {
            for (Path path : paths) {
                FileMetaData fileMetaData = fileMetadataCalculator.processFile(path.toFile());
                if (fileMetaData.getTthAsBase32().equals(fileTTH)) {
                    Files.copy(path, file.toPath());
                }
            }
        }
    }

}
