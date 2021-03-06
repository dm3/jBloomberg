/*
 * Copyright (C) 2012 - present by Yann Le Tallec.
 * Please see distribution for license.
 */
package com.assylias.jbloomberg;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class IntradayTickResultParserTest {

    private static final ZonedDateTime NOW = ZonedDateTime.now();
    private final String INVALID_SECURITY = "XXX";
    private DefaultBloombergSession session = null;

    @BeforeClass(groups = "requires-bloomberg")
    public void beforeClass() throws BloombergException {
        session = new DefaultBloombergSession();
        session.start();
    }

    @AfterClass(groups = "requires-bloomberg")
    public void afterClass() throws BloombergException {
        if (session != null) {
            session.stop();
        }
    }

    @Test(groups = "requires-bloomberg")
    public void testParse_InvalidSecurity() throws Exception {
        RequestBuilder<IntradayTickData> builder = new IntradayTickRequestBuilder(INVALID_SECURITY, NOW.minusDays(5), NOW);
        IntradayTickData data = session.submit(builder).get(5, TimeUnit.MINUTES);
        assertTrue(data.hasErrors());
        assertFalse(data.getSecurityErrors().isEmpty());
        assertTrue(data.getFieldErrors().isEmpty());
        assertTrue(data.isEmpty());
    }

    @Test(groups = "unit", expectedExceptions = UnsupportedOperationException.class)
    public void testParse_CantAddFieldError() throws Exception {
        IntradayTickResultParser parser = new IntradayTickResultParser(INVALID_SECURITY);
        parser.addFieldError("");
    }

    @Test(groups = "requires-bloomberg")
    public void testParse_OK() throws Exception {
        RequestBuilder<IntradayTickData> builder = new IntradayTickRequestBuilder("SPX Index", NOW.minusDays(5), NOW)
                .includeBicMicCodes()
                .includeBrokerCodes()
                .includeConditionCodes()
                .includeExchangeCodes()
                .includeRpsCodes()
                .includeNonPlottableEvents();
        IntradayTickData data = session.submit(builder).get(1, TimeUnit.MINUTES);
        assertFalse(data.hasErrors());
        assertTrue(data.getSecurityErrors().isEmpty());
        assertTrue(data.getFieldErrors().isEmpty());
        assertFalse(data.isEmpty());
    }
}
