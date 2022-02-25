package com.bob;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


public class UtilsTest {


    @org.junit.Test
    public void validateResponseAndParsePrice_valid() {
        final String input = "{\"EUR\":5000.34}";
        assertEquals(5000.34, Utils.validateResponseAndParsePrice(input), 0.00);
    }

    @org.junit.Test
    public void validateResponseAndParsePrice_inValid() {
        final String input = "{\"EUR\":ABD}";
        assertEquals(0, Utils.validateResponseAndParsePrice(input), 0.00);
    }

    @org.junit.Test
    public void validateInputAndParseQty_valid() throws Exception{
        final String input = "BTC=10";
        assertEquals(10, Utils.validateInputAndParseQty(input));
    }


    @org.junit.Test
    public void validateInputAndParseQty_InValid() throws Exception{
        final String input = "BTC=ABC";
        Assertions.assertThrows(Exception.class, () -> Utils.validateInputAndParseQty(input));
    }

    @org.junit.Test
    public void buildUri(){
        final String currency = "testCurrency";
        final String coin = "testCoin";
        final String actualUri = Utils.buildUri(coin, currency);
        final String expectedUri = "https://min-api.cryptocompare.com/data/price?fsym=testCoin&tsyms=testCurrency";
        assertEquals(expectedUri, actualUri);
    }

}
