package com.hackclub.common.elasticsearch;


import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Base class for a picocli command that expects to sync data to (or query) an elasticsearch cluster
 */
public abstract class ESCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--eshost"}, description = "Hostname of elasticsearch cluster to use for indexing")
    protected String esHostname = "localhost";

    @CommandLine.Option(names = {"--esport"}, description = "The port of the elasticsearch cluster")
    protected Integer esPort = 9200;

    @CommandLine.Option(names = {"--esusername"}, description = "The username for the elasticsearch cluster")
    protected String esUsername = "unknown";

    @CommandLine.Option(names = {"--espassword"}, description = "The password for the elasticsearch cluster")
    protected String esPassword = "not_applicable";

    @CommandLine.Option(names = {"--esfingerprint"}, description = "The fingerprint for the elasticsearch cluster")
    protected String esFingerprint = "please_fill_me_in";

    @CommandLine.Option(names = {"--esindex"}, description = "The elasticsearch index that you'd like to write data to")
    protected String esIndex = "UNKNOWN";

    @CommandLine.Option(names = {"--google-geocoding-api-key"}, description = "Google geocoding API key")
    protected String googleGeocodingApiKey = "please_fill_me_in";

    @CommandLine.Option(names = {"--github-api-key"}, description = "Github API key")
    protected String githubApiKey = "please_fill_me_in";

    @CommandLine.Option(names = {"--slack-api-key"}, description = "Slack API key")
    protected String slackApiKey = "please_fill_me_in";
}
