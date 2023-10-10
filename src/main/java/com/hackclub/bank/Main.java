package com.hackclub.bank;

import com.hackclub.bank.commands.GenerateProfilesCommand;
import com.hackclub.bank.commands.UploadCsvCommand;
import com.hackclub.common.elasticsearch.InitIndexCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "bank-chronicle",
        mixinStandardHelpOptions = true,
        version = "bank-chronicle 1.0",
        description = "Data conflation and indexing tool",
        subcommands = { GenerateProfilesCommand.class, InitIndexCommand.class, UploadCsvCommand.class })
public class Main {
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}