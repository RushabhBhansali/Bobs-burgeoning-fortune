package com.bob;


import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static com.bob.Utils.validateResponseAndParsePrice;
import static com.bob.Utils.validateInputAndParseQty;
import static com.bob.Utils.buildUri;
import static com.bob.Utils.validateResponse;


public class PrintFortune {

    private static final String CURRENCY = "EUR";
    private FileReader fileReader;
    private double portfolioValue;
    private HttpClient client;

    public PrintFortune(final FileReader fileReader, final HttpClient client) {
        this.fileReader = fileReader;
        this.client = client;
    }

    public void processFile() {
        final BufferedReader reader = new BufferedReader(fileReader);
        CompletableFuture.allOf(reader.lines()
                .map(this::processAsync)
                .toArray(CompletableFuture[]::new))
                .join();
        System.out.println(String.format("\n\nTotal Portfolio Value: %,.2f", portfolioValue));
    }

    private CompletableFuture<Void> processAsync(String line) {
        final String coin = line.substring(0,line.indexOf('='));
        try {
            final int qty = validateInputAndParseQty(line);
            return getAsync(buildUri(coin, CURRENCY), this.client)
                    .thenApply(res -> validateResponseAndParsePrice(res))
                    .thenAccept(price -> updatePortFolioValueAndPrintSingleAsset(coin, qty, qty*price));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }

    private void updatePortFolioValueAndPrintSingleAsset(String coin, int qty, double total) {
        updatePortfolioValue(total);
        System.out.println(String.format("%s: Qty:%d, Value:%,.2f %s", coin, qty, total, CURRENCY));
    }

    // using synchronized update of the portfolio value makes the access to variable thread safe.
    public synchronized void updatePortfolioValue(double value){
        this.portfolioValue += value;
    }

    public CompletableFuture<String> getAsync(String uri, HttpClient client) throws Exception {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> validateResponse(response, request))
                .thenApply(HttpResponse::body);
    }

}
