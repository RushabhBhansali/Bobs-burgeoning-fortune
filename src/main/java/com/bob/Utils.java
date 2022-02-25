package com.bob;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String RESPONSE_PATTERN = "\\{\\\"[a-zA-Z]+\\\":(\\d+.*\\d*)}";
    private static final String INPUT_LINE_PATTERN = "[A-Z]+=(\\d+)";

    public static CompletableFuture<String> getAsync(String uri, HttpClient client) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> validateResponse(response, request))
                .thenApply(HttpResponse::body);
    }

    public static double validateResponseAndParsePrice(final String response) {
        Pattern pattern = Pattern.compile(RESPONSE_PATTERN);
        Matcher matcher = pattern.matcher(response);
        if(matcher.matches()) {
            return Double.parseDouble(matcher.group(1));
        } else {
            System.err.println(String.format("Invalid response = [%s]. \n\n price can't be parsed for the asset. " +
                    "It Will be recorded as 0.", response));
            return 0;
        }
    }

    public static int validateInputAndParseQty(final String line) throws Exception{
        Pattern pattern = Pattern.compile(INPUT_LINE_PATTERN);
        Matcher matcher = pattern.matcher(line);
        if(matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new Exception(String.format("Invalid Input:[%s]. Skipping this line", line));
        }
    }

    public static String buildUri(final String coin, final String currency) {
        return String.format("https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=%s",
                coin,
                currency);
    }

    public static HttpResponse<String> validateResponse(HttpResponse<String> response, HttpRequest request) {
        final int statusCode = response.statusCode();
        if(statusCode < 200 || statusCode >= 300) {
            System.err.println(String.format("Invalid Status code %d returned from API.", statusCode));
            System.err.println(String.format("request = [%s]\n response=[%s]", request, response));
        }
        return response;
    }
}
