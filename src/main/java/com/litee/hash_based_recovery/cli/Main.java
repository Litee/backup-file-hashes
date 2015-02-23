package com.litee.hash_based_recovery.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.litee.hash_based_recovery.FileMetadataCalculator;
import com.litee.hash_based_recovery.cache.FileMetadataCalculatorWithCacheImpl;
import com.litee.hash_based_recovery.output.dc.DirectConnectFileSystemWalker;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        JCommander jCommander = new JCommander();
        CommandBackup commandBackup = new CommandBackup();
        jCommander.addCommand(commandBackup);
        jCommander.parse(args);
        if (jCommander.getParsedCommand().equals("backup")) {
            final FileMetadataCalculator fileMetadataCalculator = new FileMetadataCalculatorWithCacheImpl(commandBackup.cacheDir);
            DirectConnectFileSystemWalker fileSystemWalker = new DirectConnectFileSystemWalker(fileMetadataCalculator);
            fileSystemWalker.processRoot(commandBackup.input, commandBackup.output);
            System.out.println("Done!");
            System.exit(0);
        }
    }

    @Parameters(commandNames = "backup")
    public static class CommandBackup {
        @Parameter(names = "-input")
        private List<String> input;
        @Parameter(names = "-output")
        private String output;
        @Parameter(names = "-cacheDir")
        private String cacheDir;
    }
}
