package com.hackclub.clubs.slack;

import com.hackclub.clubs.models.SlackInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.users.profile.UsersProfileGetRequest;
import com.slack.api.methods.response.users.profile.UsersProfileGetResponse;
import com.slack.api.model.User;
import com.hackclub.common.file.Cache;
import java.util.Map;
import java.util.Optional;

/**
 * Integrates with Slack API
 */
public class SlackUtils {
    private final static String githubUrlKey = "Xf0DMHFDQA"; // Note - this is not any sort of secret, just a uid on GH
    public static Optional<SlackInfo> getSlackInfo(String slackUserId, String slackToken) {
        Optional<SlackInfo> slackData = getCachedSlackInfo(slackUserId);
        if (slackData.isPresent()) {
            return slackData;
        }

        Slack slack = Slack.getInstance();
        Optional<SlackInfo> ret = Optional.empty();
        boolean success = false;
        while(!success) {
            try {
                UsersProfileGetResponse response = slack.methods(slackToken).usersProfileGet(UsersProfileGetRequest.builder()
                        .user(slackUserId).build());

                if (response.getProfile() == null) {
                    ret = Optional.empty();
                    success = true;
                } else {
                    Map<String, User.Profile.Field> fields = response.getProfile().getFields();

                    SlackInfo slackInfo = new SlackInfo();
                    if (fields.containsKey(githubUrlKey)) {
                        String githubUrl = fields.get(githubUrlKey).getValue();
                        String[] urlParts = githubUrl.split("/");
                        String githubUsername = urlParts[urlParts.length - 1];

                        slackInfo.setGithubUrl(githubUrl);
                        slackInfo.setGithubUsername(githubUsername);
                    }
                    ret = Optional.of(slackInfo);
                    success = true;
                }
            } catch (SlackApiException e) {
                if (e.getResponse().code() == 429) {
                    handleError(e, false,500);
                    ret = Optional.empty();
                } else {
                    handleError(e, true, 0);
                    ret = Optional.empty();
                }
            }
            catch (Throwable t) {
                handleError(t, true,0);
                ret = Optional.empty();
            }
        }

        cacheSlackData(slackUserId, ret);
        return ret;
    }

    private static void handleError(Throwable t, boolean log, long msToWait) {
        if (log) {
            System.out.println("warning - issue");
            t.printStackTrace();
        }
        try {
            // We'll put a sleep here to back off a bit, errors here usually relate to rate limits
            if (msToWait > 0) {
                Thread.sleep(msToWait);
            }
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

    private static void cacheSlackData(String slackUserId, Optional<SlackInfo> ret) {
        try {
            if (ret.isPresent()) {
                Cache.save(slackUserId, new ObjectMapper().writeValueAsString(ret.get()));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static Optional<SlackInfo> getCachedSlackInfo(String slackUserId) {
        Optional<String> slackData = Cache.load(slackUserId);
        if (slackData.isPresent()) {
            try {
                return Optional.of(new ObjectMapper().readValue(slackData.get(), SlackInfo.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
