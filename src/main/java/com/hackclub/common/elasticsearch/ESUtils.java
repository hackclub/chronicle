package com.hackclub.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.HashMap;

/**
 * Convenience/utility methods for interacting with an elasticsearch cluster
 */
public class ESUtils {
    public static void clearIndex(ElasticsearchClient esClient, String userIndex) throws IOException {
        System.out.printf("Deleting all documents in index %s\n", userIndex);
        Query query = QueryBuilders.matchAll().build()._toQuery();
        DeleteByQueryRequest dbyquery = DeleteByQueryRequest
                .of(fn -> fn.query(query).index(userIndex));

        DeleteByQueryResponse response = esClient.deleteByQuery(dbyquery);
        System.out.printf("Deleting index took %dms\n", response.took());
    }

    public static void deleteIndex(ElasticsearchClient esClient, String userIndex) throws IOException {
        DeleteIndexRequest dir = new DeleteIndexRequest.Builder().index(userIndex).build();
        DeleteIndexResponse response = esClient.indices().delete(dir);
    }

    public static void addIndex(ElasticsearchClient esClient, String userIndex) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(userIndex)
                .build();
        esClient.indices().create(request);
    }

    /**
     * Create a column name -> column index mapping
     * @param columns An array of strings that represent column names
     * @return A HashMap that associates column names with a corresponding index
     */
    @NotNull
    public static HashMap<String, Integer> getIndexMapping(String[] columns) {
        HashMap<String, Integer> columnIndices = new HashMap<>();
        int index = 0;
        for(String column : columns) {
            columnIndices.put(column, index);
            index++;
        }
        return columnIndices;
    }

    public static ElasticsearchClient createElasticsearchClient(String hostname, int port, String username, String password, String fingerprint) throws IOException {
        SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);
        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(username, password)
        );

        RestClient restClient = RestClient
                .builder(new HttpHost(hostname, port, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();

        // Create the transport and the API client
        return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }
}
