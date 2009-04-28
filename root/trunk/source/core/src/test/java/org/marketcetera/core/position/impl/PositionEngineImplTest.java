package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.ImmutablePositionSupport;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.Side;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Tests {@link PositionEngineImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PositionEngineImplTest {

    private abstract class PositionEngineTestTemplate implements Runnable {

        EventList<ReportHolder> reports = new BasicEventList<ReportHolder>();
        Factory factory = Factory.getInstance();
        long tradeCounter = 0;

        @Override
        public void run() {
            initReports();
            PositionEngine engine = PositionEngineFactory.createFromReportHolders(reports,
                    new ImmutablePositionSupport(getIncomingPositions()),
                    mock(MarketDataSupport.class));
            EventList<PositionRow> positions = getPositionData(engine);
            positions.addListEventListener(new ExpectedListChanges<PositionRow>("Positions",
                    getExpectedPositionListChanges()));
            validatePositions(positions);
        }

        protected Map<? extends PositionKey, BigDecimal> getIncomingPositions() {
            return Maps.newHashMap();
        }

        protected EventList<PositionRow> getPositionData(PositionEngine engine) {
            return engine.getFlatData().getPositions();
        }

        protected void initReports() {
            // default is no-op
        }

        protected void validatePositions(EventList<PositionRow> positions) {
            // default is no-op
        }

        protected int[] getExpectedPositionListChanges() {
            return new int[] {};
        }

        protected void clearReports() {
            reports.clear();
        }

        protected void addReport(ReportBase report) {
            add(report);
        }

        protected void addTrade(String symbol, String account, long traderId, Side side,
                String quantity, String price, long sequence) {
            add(new MockExecutionReport(account, symbol, traderId, side, price, quantity, sequence,
                    OrderStatus.Filled));
        }

        protected void addTrade(String symbol, String account, Side side, String quantity,
                String price) {
            addTrade(symbol, account, "1", side, quantity, price);
        }

        protected void addTrade(String symbol, String account, String traderId, Side side,
                String quantity, String price) {
            addTrade(symbol, account, Long.valueOf(traderId), side, quantity, price, ++tradeCounter);
        }

        private void add(ReportBase report) {
            reports.add(new ReportHolder(report));
        }

        protected void assertPosition(PositionRow position, String symbol, String account,
                String traderId, String amount) {
            assertThat(position.getSymbol(), is(symbol));
            assertThat(position.getAccount(), is(account));
            assertThat(position.getTraderId(), is(traderId));
            assertThat(position.getPositionMetrics().getPosition(), comparesEqualTo(amount));
        }

        protected void assertPositions(EventList<PositionRow> positions, String[]... values) {
            assertThat(positions.size(), is(values.length));
            for (int i = 0; i < values.length; i++) {
                String[] strings = values[i];
                assertPosition(positions.get(i), strings[0], strings[1], strings[2], strings[3]);
            }
        }

    }

    @Test
    public void simpleInit() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("METC", "personal", Side.Buy, "1000", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "1000");
            }
        }.run();
    }

    @Test
    public void complexInit() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("METC", "personal", Side.Buy, "1000", "1");
                addTrade("METC", "personal", Side.Buy, "104", "1");
                addTrade("METC", "work", Side.Buy, "70", "1");
                addTrade("GOOG", "personal", Side.Buy, "45.5", "1");
                addTrade("YHOO", "work", Side.Buy, "20", "1");
                addTrade("YHOO", "work", Side.Sell, "6", "1");
                addTrade("ABC", "work", Side.Sell, "100", "1");
                addTrade("ABC", "work", Side.Buy, "20", "1");
                addTrade("ABC", "work", "2", Side.Buy, "20", "1");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(6));
                assertPosition(positions.get(0), "ABC", "work", "1", "-80");
                assertPosition(positions.get(1), "GOOG", "personal", "1", "45.5");
                assertPosition(positions.get(2), "METC", "personal", "1", "1104");
                assertPosition(positions.get(3), "METC", "work", "1", "70");
                assertPosition(positions.get(4), "YHOO", "work", "1", "14");
                assertPosition(positions.get(5), "ABC", "work", "2", "20");
            }
        }.run();
    }

    @Test
    public void simpleLiveUpdate() {
        new PositionEngineTestTemplate() {

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.INSERT, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                addTrade("METC", "personal", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "1000");
            }
        }.run();
    }

    @Test
    public void complexLiveUpdate() {
        new PositionEngineTestTemplate() {

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.INSERT, 0, ListEvent.UPDATE, 0, ListEvent.INSERT, 1,
                        ListEvent.INSERT, 0, ListEvent.INSERT, 3, ListEvent.UPDATE, 3,
                        ListEvent.INSERT, 0, ListEvent.UPDATE, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                addTrade("METC", "personal", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "1000");
                addTrade("METC", "personal", Side.Buy, "104", "1");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "1104");
                addTrade("METC", "work", Side.Buy, "70", "1");
                assertThat(positions.size(), is(2));
                assertPosition(positions.get(0), "METC", "personal", "1", "1104");
                assertPosition(positions.get(1), "METC", "work", "1", "70");
                addTrade("GOOG", "personal", Side.Buy, "45.5", "1");
                assertThat(positions.size(), is(3));
                assertPosition(positions.get(0), "GOOG", "personal", "1", "45.5");
                assertPosition(positions.get(1), "METC", "personal", "1", "1104");
                assertPosition(positions.get(2), "METC", "work", "1", "70");
                addTrade("YHOO", "work", Side.Buy, "20", "1");
                assertThat(positions.size(), is(4));
                assertPosition(positions.get(0), "GOOG", "personal", "1", "45.5");
                assertPosition(positions.get(1), "METC", "personal", "1", "1104");
                assertPosition(positions.get(2), "METC", "work", "1", "70");
                assertPosition(positions.get(3), "YHOO", "work", "1", "20");
                addTrade("YHOO", "work", Side.Sell, "6", "1");
                assertThat(positions.size(), is(4));
                assertPosition(positions.get(0), "GOOG", "personal", "1", "45.5");
                assertPosition(positions.get(1), "METC", "personal", "1", "1104");
                assertPosition(positions.get(2), "METC", "work", "1", "70");
                assertPosition(positions.get(3), "YHOO", "work", "1", "14");
                addTrade("ABC", "work", Side.Sell, "100", "1");
                assertThat(positions.size(), is(5));
                assertPosition(positions.get(0), "ABC", "work", "1", "-100");
                assertPosition(positions.get(1), "GOOG", "personal", "1", "45.5");
                assertPosition(positions.get(2), "METC", "personal", "1", "1104");
                assertPosition(positions.get(3), "METC", "work", "1", "70");
                assertPosition(positions.get(4), "YHOO", "work", "1", "14");
                addTrade("ABC", "work", Side.Buy, "20", "1");
                assertThat(positions.size(), is(5));
                assertPosition(positions.get(0), "ABC", "work", "1", "-80");
                assertPosition(positions.get(1), "GOOG", "personal", "1", "45.5");
                assertPosition(positions.get(2), "METC", "personal", "1", "1104");
                assertPosition(positions.get(3), "METC", "work", "1", "70");
                assertPosition(positions.get(4), "YHOO", "work", "1", "14");
            }
        }.run();
    }

    @Test
    public void unknowns() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("METC", null, Side.Buy, "1000", "500");
                addTrade("METC", null, Side.Buy, "1000", "500");
                addTrade("METC", null, Side.Sell, "200", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", null, "1", "1800");
            }
        }.run();
    }

    @Test
    @Ignore // clearing not currently supported
    public void clear() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("METC", "personal", Side.Buy, "1000", "500");
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.DELETE, 0, ListEvent.INSERT, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                clearReports();
                assertThat(positions.size(), is(0));
                addTrade("METC", "personal", Side.Buy, "2000", "500");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "2000");
            }
        }.run();
    }

    @Test
    @Ignore // clearing not currently supported
    public void complexClear() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("METC", "personal", Side.Buy, "1000", "500");
                addTrade("METC", "personal", Side.Buy, "100", "500");
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 0, ListEvent.DELETE, 0, ListEvent.INSERT, 0,
                        ListEvent.UPDATE, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "1100");
                addTrade("METC", "personal", Side.Buy, "200", "500");
                assertPosition(positions.get(0), "METC", "personal", "1", "1300");
                clearReports();
                assertThat(positions.size(), is(0));
                addTrade("METC", "personal", Side.Buy, "2000", "500");
                addTrade("METC", "personal", Side.Buy, "2000", "500");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "4000");
            }
        }.run();
    }

    @Test
    public void validation() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("", "personal", Side.Buy, "1000", "500");
                addTrade("METC", "personal", Side.Buy, "1000", "0");
                addTrade("METC", "personal", Side.Buy, "0", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(0));
            }
        }.run();
    }

    @Test
    public void grouping() {
        new PositionEngineTestTemplate() {

            @Override
            protected EventList<PositionRow> getPositionData(PositionEngine engine) {
                return engine.getGroupedData(Grouping.Symbol, Grouping.Account).getPositions();
            }

            @Override
            protected void initReports() {
                addTrade("METC", "personal", Side.Buy, "1000", "500");
                addTrade("METC", "personal", Side.Buy, "100", "500");
                addTrade("METC", "business", Side.Buy, "1050", "500");
                addTrade("METC", "business", Side.Buy, "100", "500");
                addTrade("IBM", "business", Side.Buy, "300", "500");
                addTrade("IBM", "personal", Side.Buy, "200", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                // root
                assertPositions(positions, new String[] { "IBM", "business", "1", "500" },
                        new String[] { "METC", "business", "1", "2250" });

                // first level
                PositionRow ibm = positions.get(0);
                assertPositions(ibm.getChildren(), new String[] { "IBM", "business", "1", "300" },
                        new String[] { "IBM", "personal", "1", "200" });
                PositionRow metc = positions.get(1);
                assertPositions(metc.getChildren(),
                        new String[] { "METC", "business", "1", "1150" }, new String[] { "METC",
                                "personal", "1", "1100" });

                // second level
                PositionRow metcBusiness = metc.getChildren().get(0);
                assertPositions(metcBusiness.getChildren(), new String[] { "METC", "business", "1",
                        "1150" });
                PositionRow metcPersonal = metc.getChildren().get(1);
                assertPositions(metcPersonal.getChildren(), new String[] { "METC", "personal", "1",
                        "1100" });
                PositionRow ibmBusiness = ibm.getChildren().get(0);
                assertPositions(ibmBusiness.getChildren(), new String[] { "IBM", "business", "1",
                        "300" });
                PositionRow ibmPersonal = ibm.getChildren().get(1);
                assertPositions(ibmPersonal.getChildren(), new String[] { "IBM", "personal", "1",
                        "200" });

                // some new trades
                addTrade("IBM", "business", Side.Sell, "45", "500");
                addTrade("GOOG", "abc", Side.Sell, "100", "500");
                addTrade("METC", "personal", Side.Sell, "90.3", "500");

                // root
                assertPositions(positions, new String[] { "GOOG", "abc", "1", "-100" },
                        new String[] { "IBM", "business", "1", "455" }, new String[] { "METC",
                                "business", "1", "2159.7" });

                // first level
                PositionRow goog = positions.get(0);
                assertPositions(goog.getChildren(), new String[] { "GOOG", "abc", "1", "-100" });
                ibm = positions.get(1);
                assertPositions(ibm.getChildren(), new String[] { "IBM", "business", "1", "255" },
                        new String[] { "IBM", "personal", "1", "200" });
                metc = positions.get(2);
                assertPositions(metc.getChildren(),
                        new String[] { "METC", "business", "1", "1150" }, new String[] { "METC",
                                "personal", "1", "1009.7" });

                // second level
                metcBusiness = metc.getChildren().get(0);
                assertPositions(metcBusiness.getChildren(), new String[] { "METC", "business", "1",
                        "1150" });
                metcPersonal = metc.getChildren().get(1);
                assertPositions(metcPersonal.getChildren(), new String[] { "METC", "personal", "1",
                        "1009.7" });
                ibmBusiness = ibm.getChildren().get(0);
                assertPositions(ibmBusiness.getChildren(), new String[] { "IBM", "business", "1",
                        "255" });
                ibmPersonal = ibm.getChildren().get(1);
                assertPositions(ibmPersonal.getChildren(), new String[] { "IBM", "personal", "1",
                        "200" });
                PositionRow googAbc = goog.getChildren().get(0);
                assertPositions(googAbc.getChildren(), new String[] { "GOOG", "abc", "1", "-100" });
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 0, ListEvent.INSERT, 0, ListEvent.UPDATE, 2 };
            }

        }.run();
    }

    @Test
    public void grouping2() {
        new PositionEngineTestTemplate() {

            @Override
            protected EventList<PositionRow> getPositionData(PositionEngine engine) {
                return engine.getGroupedData(Grouping.Trader, Grouping.Account).getPositions();
            }

            @Override
            protected void initReports() {
                addTrade("METC", "personal", "1", Side.Buy, "1000", "500");
                addTrade("METC", "personal", "2", Side.Buy, "100", "500");
                addTrade("IBM", "personal", "2", Side.Sell, "200", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                // root
                assertPositions(positions, new String[] { "METC", "personal", "1", "1000" },
                        new String[] { "IBM", "personal", "2", "-100" });

                // first level
                PositionRow t1 = positions.get(0);
                assertPositions(t1.getChildren(), new String[] { "METC", "personal", "1", "1000" });
                PositionRow t2 = positions.get(1);
                assertPositions(t2.getChildren(), new String[] { "IBM", "personal", "2", "-100" });

                // second level
                PositionRow t1Personal = t1.getChildren().get(0);
                assertPositions(t1Personal.getChildren(), new String[] { "METC", "personal", "1",
                        "1000" });
                PositionRow t2Personal = t2.getChildren().get(0);
                assertPositions(t2Personal.getChildren(), new String[] { "IBM", "personal", "2",
                        "-200" }, new String[] { "METC", "personal", "2", "100" });

                // some new trades
                addTrade("IBM", "business", "2", Side.Sell, "45", "500");
                addTrade("GOOG", "abc", "1", Side.Sell, "100", "500");

                // root
                assertPositions(positions, new String[] { "METC", "personal", "1", "900" },
                        new String[] { "IBM", "personal", "2", "-145" });

                // first level
                t1 = positions.get(0);
                assertPositions(t1.getChildren(), new String[] { "GOOG", "abc", "1", "-100" },
                        new String[] { "METC", "personal", "1", "1000" });
                t2 = positions.get(1);
                assertPositions(t2.getChildren(), new String[] { "IBM", "business", "2", "-45" },
                        new String[] { "IBM", "personal", "2", "-100" });

                // second level
                PositionRow t1Abc = t1.getChildren().get(0);
                assertPositions(t1Abc.getChildren(), new String[] { "GOOG", "abc", "1", "-100" });
                t1Personal = t1.getChildren().get(1);
                assertPositions(t1Personal.getChildren(), new String[] { "METC", "personal", "1",
                        "1000" });
                PositionRow t2Business = t2.getChildren().get(0);
                assertPositions(t2Business.getChildren(), new String[] { "IBM", "business", "2",
                        "-45" });
                t2Personal = t2.getChildren().get(1);
                assertPositions(t2Personal.getChildren(), new String[] { "IBM", "personal", "2",
                        "-200" }, new String[] { "METC", "personal", "2", "100" });
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 1, ListEvent.UPDATE, 0 };
            }

        }.run();
    }

    @Test
    public void incomingPosition() {
        new PositionEngineTestTemplate() {

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 0 };
            }

            @Override
            protected Map<? extends PositionKey, BigDecimal> getIncomingPositions() {
                return ImmutableMap.of(new PositionKeyImpl("METC", "personal", "1"),
                        new BigDecimal("100"));
            }
            
            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "100");
                addTrade("METC", "personal", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1", "1100");
            }
        }.run();
    }
}
