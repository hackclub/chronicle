package com.hackclub.clubs;

import com.hackclub.clubs.commands.GenerateProfilesCommand;
import com.hackclub.common.elasticsearch.InitIndexCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "clubs-chronicle",
        mixinStandardHelpOptions = true,
        version = "clubs-chronicle 1.0",
        description = "Data conflation and indexing tool",
        subcommands = { GenerateProfilesCommand.class, InitIndexCommand.class })
public class Main {
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}