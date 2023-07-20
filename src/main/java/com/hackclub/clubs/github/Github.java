package com.hackclub.clubs.github;

import com.hackclub.clubs.models.GithubInfo;
import com.hackclub.clubs.models.SlackInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.hackclub.common.Utils;
import com.hackclub.common.file.Cache;
import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Integrates with Github GraphQL APIs to retrieve data relating to PRs, commits, and other metadata
 */
public class Github {
    private static String userQueryTemplate;
    static {
        try {
            userQueryTemplate = Utils.getResourceFileAsString("github_user_query.graphql");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<GithubInfo> getUserData(String username, String githubApiKey) {
        if (username == null) {
            //System.out.println("No username, skipping github data");
            return Optional.empty();
        }

        String cacheKey = username + "-github";
        Optional<GithubInfo> cachedGithubInfo = loadFromCache(cacheKey);
        if (cachedGithubInfo.isPresent()) {
            return cachedGithubInfo;
        }

        HttpResponse<String> response = null;
        try {
            String template = "{\"query\": \"%s\"}";
            String query = String.format(template, StringEscapeUtils.escapeJson(String.format(userQueryTemplate, username)));

            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/graphql"))
                    .timeout(Duration.ofMinutes(2))
                    .header("Authorization", "bearer " + githubApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(query))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Optional<GithubInfo> ghInfo = parseUserData(Utils.getPrettyJson(response.body()));
            cache(cacheKey, ghInfo);
            return ghInfo;
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Failed to load github user data");
            return Optional.empty();
        }
    }

    private static Optional<GithubInfo> parseUserData(String data) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        DocumentContext root = JsonPath.parse(data);

        try {
            String userInfoPath = "$.data.search.edges[0].node";
            HashMap<String, String> userData = root.read(userInfoPath);
            if (userData.isEmpty()) return Optional.empty();
        } catch (Throwable t) {
            // no users returned
            return Optional.empty();
        }

        String topRepositoriesPath = "$.data.search.edges[0].node.topRepositories.edges[*].node";
        List<HashMap<String, String>> topRepositories = root.read(topRepositoriesPath);

        String pullRequestPath = "$.data.search.edges[0].node.contributionsCollection.pullRequestContributions.nodes[*].pullRequest";
        List<HashMap<String, Object>> pullRequests = root.read(pullRequestPath);

        GithubInfo info = new GithubInfo();
        HashMap<String, Integer> prCountsByRepo = new HashMap<>();
        HashMap<String, Integer> prCountsByLanguage = new HashMap<>();
        HashMap<String, Integer> prCountsByOwner = new HashMap<>();
        pullRequests.forEach(pr -> {
            try {
                HashMap<String, Object> repoInfo = (HashMap<String, Object>) pr.get("repository");
                HashMap<String, Object> ownerInfo = (HashMap<String, Object>) repoInfo.get("owner");
                String ownerName = (String) ownerInfo.get("login");
                String repoName = (String) repoInfo.get("name");
                HashMap<String, Object> primaryLanguageInfo = (HashMap<String, Object>) repoInfo.getOrDefault("primaryLanguage", "unknown");
                String primaryLanguage = "unknown";
                if (primaryLanguageInfo != null) {
                    primaryLanguage = (String)primaryLanguageInfo.getOrDefault("name", "unknown");
                }
                incrementKeyword(prCountsByRepo, String.format("%s/%s", ownerName, repoName));
                incrementKeyword(prCountsByLanguage, primaryLanguage);
                incrementKeyword(prCountsByOwner, ownerName);
                //System.out.printf("owner: %s repo: %s language: %s\n", ownerName, repoName, primaryLanguage);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

        info.setPullRequestCountsByLanguage(prCountsByLanguage);
        info.setPullRequestCountsByOwner(prCountsByOwner);
        info.setPullRequestCountsByRepo(prCountsByRepo);

        return Optional.of(info);
    }

    private static void incrementKeyword(HashMap<String, Integer> countMapping, String key) {
        int currentCount = countMapping.getOrDefault(key, 0);
        countMapping.put(key, currentCount+1);
    }

    private static void cache(String key, Optional<GithubInfo> ret) {
        try {
            if (ret.isPresent()) {
                Cache.save(key, new ObjectMapper().writeValueAsString(ret.get()));
            } else {
                Cache.save(key, new ObjectMapper().writeValueAsString(""));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static Optional<GithubInfo> loadFromCache(String key) {
        Optional<String> githubData = Cache.load(key);
        if (githubData.isPresent()) {
            if (githubData.get().length() == 2) {
                return Optional.empty();
            }
            try {
                return Optional.of(new ObjectMapper().readValue(githubData.get(), GithubInfo.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

}
