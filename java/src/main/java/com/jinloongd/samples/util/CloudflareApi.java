package com.jinloongd.samples.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author JinLoong.Du
 */
public class CloudflareApi {

    private static final Logger logger = LoggerFactory.getLogger(CloudflareApi.class);

    private static final String BASE_URL = "https://api.cloudflare.com/client/v4";

    private String accountId;

    private final OkHttpClient okHttpClient;

    public CloudflareApi(String accountId, String xAuthEmail, String xAuthKey) {
        this.accountId = accountId;
        this.okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    // 添加公共请求头
                    Request.Builder builder = chain.request()
                            .newBuilder()
                            .addHeader("X-Auth-Email", xAuthEmail)
                            .addHeader("X-Auth-Key", xAuthKey);
                    return chain.proceed(builder.build());
                })
                .build();
    }

    public long getKvSize(String namespaceId) throws IOException {
        String url = String.format(BASE_URL + "/accounts/%s/storage/kv/namespaces/%s/keys", accountId, namespaceId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String cursor = "";
        long totalCount = 0L;
        do {
            urlBuilder.addQueryParameter("cursor", cursor);
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.code() == 200) {
                    String json = response.body().string();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode tree = objectMapper.readTree(json);
                    boolean success = tree.findValue("success").asBoolean();
                    if (success) {
                        int count = tree.findValue("result_info").findValue("count").asInt();
                        totalCount += count;
                        cursor = tree.findValue("result_info").findValue("cursor").asText();
                    } else {
                        logger.error("Fetch {} occurs error: {}, {}", url, response.code(), json);
                    }
                }
            }
        } while (!"".equals(cursor));
        return totalCount;
    }

    public boolean putToKv(String namespaceId, String key, String value) throws IOException {
        String url = String.format(BASE_URL + "/accounts/%s/storage/kv/namespaces/%s/bulk", accountId, namespaceId);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("key", key);
        objectNode.put("value", value);
        arrayNode.add(objectNode);
        RequestBody body = RequestBody.create(mapper.writeValueAsString(arrayNode), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                String json = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode tree = objectMapper.readTree(json);
                return tree.findValue("success").asBoolean();
            } else {
                logger.error("Fetch {} occurs error: {}, {}", url, response.code(), response.body().string());
                return false;
            }
        }
    }

    public boolean doesKeyExist(String namespaceId, String key) throws IOException {
        String url = String.format(BASE_URL + "/accounts/%s/storage/kv/namespaces/%s/values/%s", accountId, namespaceId, key);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.code() == 200;
        }
    }
}
