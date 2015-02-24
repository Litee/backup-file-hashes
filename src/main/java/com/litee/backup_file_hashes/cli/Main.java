package com.litee.backup_file_hashes.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.litee.backup_file_hashes.FileMetadataCalculator;
import com.litee.backup_file_hashes.cache.FileMetadataCalculatorWithCacheImpl;
import com.litee.backup_file_hashes.commands.BackupCommand;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        JCommander jCommander = new JCommander();
        BackupCommandArguments commandBackup = new BackupCommandArguments();
        jCommander.addCommand(commandBackup);
        jCommander.parse(args);
        if (jCommander.getParsedCommand().equals("backup")) {
            final FileMetadataCalculator fileMetadataCalculator = new FileMetadataCalculatorWithCacheImpl(commandBackup.cacheDir);
            BackupCommand fileSystemWalker = new BackupCommand(fileMetadataCalculator);
            fileSystemWalker.processRoot(commandBackup.input, commandBackup.output);
        }
        else if (jCommander.getParsedCommand().equals("diff")) {
            final FileMetadataCalculator fileMetadataCalculator = new FileMetadataCalculatorWithCacheImpl(commandBackup.cacheDir);
            BackupCommand fileSystemWalker = new BackupCommand(fileMetadataCalculator);
            fileSystemWalker.processRoot(commandBackup.input, commandBackup.output);
        }
        System.out.println("Done!");
        System.exit(0);
    }

    @Parameters(commandNames = "backup")
    public static class BackupCommandArguments {
        @Parameter(names = "-input", required = true, variableArity = true)
        private List<String> input;
        @Parameter(names = "-output", required = true)
        private String output;
        @Parameter(names = "-cacheDir")
        private String cacheDir;
    }

    @Parameters(commandNames = "diff")
    public static class DiffCommandArguments {
        @Parameter(names = "-input", arity = 2)
        private List<String> input;
        @Parameter(names = "-output")
        private String output;
    }
}
