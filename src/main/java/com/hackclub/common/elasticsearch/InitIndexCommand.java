package com.hackclub.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import picocli.CommandLine;
import static com.hackclub.common.elasticsearch.ESUtils.addIndex;
import static com.hackclub.common.elasticsearch.ESUtils.deleteIndex;

/**
 * A picocli command that completely deletes and re-adds the associated ES index.
 */
@CommandLine.Command(name = "init")
public class InitIndexCommand extends ESCommand {
    @Override
    public Integer call() throws Exception {
        ElasticsearchClient esClient = ESUtils.createElasticsearchClient(esHostname, esPort, esUsername, esPassword, esFingerprint);
        deleteIndex(esClient, esIndex);
        addIndex(esClient, esIndex);

        // TODO: I believe we need to add geopoint mappings to the index after recreation for things to work correctly!
        return 0;
    }
}
