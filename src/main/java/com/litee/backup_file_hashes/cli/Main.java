package com.litee.backup_file_hashes.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.litee.backup_file_hashes.FileMetadataCalculator;
import com.litee.backup_file_hashes.cache.FileMetadataCalculatorWithCacheImpl;
import com.litee.backup_file_hashes.commands.BackupCommand;
import com.litee.backup_file_hashes.commands.RestoreCommand;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        JCommander jCommander = new JCommander();
        BackupCommandArguments backupCommandArguments = new BackupCommandArguments();
        RestoreCommandArguments restoreCommandArguments = new RestoreCommandArguments();
        jCommander.addCommand(backupCommandArguments);
        jCommander.addCommand(restoreCommandArguments);
        jCommander.parse(args);
        if (jCommander.getParsedCommand().equals("backup")) {
            final FileMetadataCalculator fileMetadataCalculator = new FileMetadataCalculatorWithCacheImpl(backupCommandArguments.cacheDir);
            BackupCommand command = new BackupCommand(fileMetadataCalculator);
            command.process(backupCommandArguments.inputDir, backupCommandArguments.outputSnapshot);
        }
        else if (jCommander.getParsedCommand().equals("restore")) {
            RestoreCommand command = new RestoreCommand();
            command.process(restoreCommandArguments.inputDir, restoreCommandArguments.inputSnapshot, restoreCommandArguments.outputRootDir);
        }
        System.out.println("Done!");
        System.exit(0);
    }

    @Parameters(commandNames = "backup")
    public static class BackupCommandArguments {
        @Parameter(names = "-inputDir", required = true, variableArity = true)
        private List<String> inputDir;
        @Parameter(names = "-outputSnapshot", required = true)
        private String outputSnapshot;
        @Parameter(names = "-cacheDir")
        private String cacheDir;
    }

    @Parameters(commandNames = "restore")
    public static class RestoreCommandArguments {
        @Parameter(names = "-inputDir", variableArity = true)
        private List<String> inputDir;
        @Parameter(names = "-inputSnapshot")
        private String inputSnapshot;
        @Parameter(names = "-outputRootDir")
        private String outputRootDir;
    }

    @Parameters(commandNames = "diff")
    public static class DiffCommandArguments {
        @Parameter(names = "-input", arity = 2)
        private List<String> input;
        @Parameter(names = "-output")
        private String output;
    }
}
