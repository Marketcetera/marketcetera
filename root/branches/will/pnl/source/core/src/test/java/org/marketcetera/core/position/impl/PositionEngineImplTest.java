package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import org.junit.Test;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.Side;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;

public class PositionEngineImplTest {

    private abstract class PositionEngineTestTemplate implements Runnable {

        EventList<ReportHolder> reports = new BasicEventList<ReportHolder>();
        Factory factory = Factory.getInstance();
        long tradeCounter = 0;

        @Override
        public void run() {
            initReports();
            PositionEngine engine = PositionEngineFactory.createFromReportHolders(reports);
            EventList<PositionRow> positions = engine.getFlatData().getPositions();
            positions.addListEventListener(new ExpectedListChanges<PositionRow>("Positions",
                    getExpectedPositionListChanges()));
            validatePositions(positions);
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

        protected void addTrade(String account, String symbol, Side side, String quantity,
                String price, long sequence) {
            add(new MockExecutionReport(account, symbol, side, price, quantity, sequence,
                    OrderStatus.Filled));
        }

        protected void addTrade(String account, String symbol, Side side, String quantity,
                String price) {
            addTrade(account, symbol, side, quantity, price, ++tradeCounter);
        }

        private void add(ReportBase report) {
            reports.add(new ReportHolder(report));
        }

        protected void assertPosition(PositionRow position, String symbol, String account,
                String amount) {
            assertThat(position.getSymbol(), is(symbol));
            assertThat(position.getAccount(), is(account));
            assertThat(position.getPositionMetrics().getPosition(), comparesEqualTo(amount));
        }

    }

    @Test
    public void simpleInit() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("personal", "METC", Side.Buy, "1000", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1000");
            }
        }.run();
    }

    @Test
    public void complexInit() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("personal", "METC", Side.Buy, "1000", "1");
                addTrade("personal", "METC", Side.Buy, "104", "1");
                addTrade("work", "METC", Side.Buy, "70", "1");
                addTrade("personal", "GOOG", Side.Buy, "45.5", "1");
                addTrade("work", "YHOO", Side.Buy, "20", "1");
                addTrade("work", "YHOO", Side.Sell, "6", "1");
                addTrade("work", "ABC", Side.Sell, "100", "1");
                addTrade("work", "ABC", Side.Buy, "20", "1");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(5));
                assertPosition(positions.get(0), "ABC", "work", "-80");
                assertPosition(positions.get(1), "GOOG", "personal", "45.5");
                assertPosition(positions.get(2), "METC", "personal", "1104");
                assertPosition(positions.get(3), "METC", "work", "70");
                assertPosition(positions.get(4), "YHOO", "work", "14");
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
                addTrade("personal", "METC", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1000");
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
                addTrade("personal", "METC", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1000");
                addTrade("personal", "METC", Side.Buy, "104", "1");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1104");
                addTrade("work", "METC", Side.Buy, "70", "1");
                assertThat(positions.size(), is(2));
                assertPosition(positions.get(0), "METC", "personal", "1104");
                assertPosition(positions.get(1), "METC", "work", "70");
                addTrade("personal", "GOOG", Side.Buy, "45.5", "1");
                assertThat(positions.size(), is(3));
                assertPosition(positions.get(0), "GOOG", "personal", "45.5");
                assertPosition(positions.get(1), "METC", "personal", "1104");
                assertPosition(positions.get(2), "METC", "work", "70");
                addTrade("work", "YHOO", Side.Buy, "20", "1");
                assertThat(positions.size(), is(4));
                assertPosition(positions.get(0), "GOOG", "personal", "45.5");
                assertPosition(positions.get(1), "METC", "personal", "1104");
                assertPosition(positions.get(2), "METC", "work", "70");
                assertPosition(positions.get(3), "YHOO", "work", "20");
                addTrade("work", "YHOO", Side.Sell, "6", "1");
                assertThat(positions.size(), is(4));
                assertPosition(positions.get(0), "GOOG", "personal", "45.5");
                assertPosition(positions.get(1), "METC", "personal", "1104");
                assertPosition(positions.get(2), "METC", "work", "70");
                assertPosition(positions.get(3), "YHOO", "work", "14");
                addTrade("work", "ABC", Side.Sell, "100", "1");
                assertThat(positions.size(), is(5));
                assertPosition(positions.get(0), "ABC", "work", "-100");
                assertPosition(positions.get(1), "GOOG", "personal", "45.5");
                assertPosition(positions.get(2), "METC", "personal", "1104");
                assertPosition(positions.get(3), "METC", "work", "70");
                assertPosition(positions.get(4), "YHOO", "work", "14");
                addTrade("work", "ABC", Side.Buy, "20", "1");
                assertThat(positions.size(), is(5));
                assertPosition(positions.get(0), "ABC", "work", "-80");
                assertPosition(positions.get(1), "GOOG", "personal", "45.5");
                assertPosition(positions.get(2), "METC", "personal", "1104");
                assertPosition(positions.get(3), "METC", "work", "70");
                assertPosition(positions.get(4), "YHOO", "work", "14");
            }
        }.run();
    }

    @Test
    public void unknowns() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade(null, "METC", Side.Buy, "1000", "500");
                addTrade(null, "METC", Side.Buy, "1000", "500");
                addTrade(null, "METC", Side.Sell, "200", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", null, "1800");
            }
        }.run();
    }

    @Test
    public void clear() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("personal", "METC", Side.Buy, "1000", "500");
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.DELETE, 0, ListEvent.INSERT, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                clearReports();
                assertThat(positions.size(), is(0));
                addTrade("personal", "METC", Side.Buy, "2000", "500");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "2000");
            }
        }.run();
    }

    @Test
    public void complexClear() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("personal", "METC", Side.Buy, "1000", "500");
                addTrade("personal", "METC", Side.Buy, "100", "500");
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 0, ListEvent.DELETE, 0, ListEvent.INSERT, 0,
                        ListEvent.UPDATE, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "1100");
                addTrade("personal", "METC", Side.Buy, "200", "500");
                assertPosition(positions.get(0), "METC", "personal", "1300");
                clearReports();
                assertThat(positions.size(), is(0));
                addTrade("personal", "METC", Side.Buy, "2000", "500");
                addTrade("personal", "METC", Side.Buy, "2000", "500");
                assertThat(positions.size(), is(1));
                assertPosition(positions.get(0), "METC", "personal", "4000");
            }
        }.run();
    }

    @Test
    public void validation() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade("personal", "", Side.Buy, "1000", "500");
                addTrade("personal", "METC", Side.Buy, "1000", "0");
                addTrade("personal", "METC", Side.Buy, "0", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(0));
            }
        }.run();
    }
}
