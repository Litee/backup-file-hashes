package com.litee.backup_file_hashes.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.litee.backup_file_hashes.commands.BackupCommand;
import com.litee.backup_file_hashes.commands.RestoreCommand;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting...");
        JCommander jCommander = new JCommander();
        BackupCommandArguments backupCommandArguments = new BackupCommandArguments();
        RestoreCommandArguments restoreCommandArguments = new RestoreCommandArguments();
        jCommander.addCommand(backupCommandArguments);
        jCommander.addCommand(restoreCommandArguments);
        jCommander.parse(args);
        if (jCommander.getParsedCommand().equals("backup")) {
            BackupCommand command = new BackupCommand();
            command.process(backupCommandArguments);
        }
        else if (jCommander.getParsedCommand().equals("restore")) {
            RestoreCommand command = new RestoreCommand();
            command.process(restoreCommandArguments);
        }
        System.out.println("Done!");
        System.exit(0);
    }

    @Parameters(commandNames = "backup")
    public static class BackupCommandArguments {
        @Parameter(names = "-inputDir", required = true, variableArity = true)
        public List<String> inputDir;
        @Parameter(names = "-outputSnapshot", required = true)
        public String outputSnapshot;
        @Parameter(names = "-cacheDir")
        public String cacheDir;
    }

    @Parameters(commandNames = "restore")
    public static class RestoreCommandArguments {
        @Parameter(names = "-inputDir", required = true, variableArity = true)
        public List<String> inputDir;
        @Parameter(names = "-inputSnapshot", required = true)
        public String inputSnapshot;
        @Parameter(names = "-outputRootDir", required = true)
        public String outputRootDir;
        @Parameter(names = "-restoreEmptyDirs")
        public boolean restoreEmptyDirs;
    }

    @Parameters(commandNames = "diff")
    public static class DiffCommandArguments {
        @Parameter(names = "-input", arity = 2)
        public List<String> input;
        @Parameter(names = "-output")
        public String output;
    }
}
