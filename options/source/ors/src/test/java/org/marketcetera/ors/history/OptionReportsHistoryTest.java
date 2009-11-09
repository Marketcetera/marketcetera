package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.core.position.PositionKey;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/* $License$ */
/**
 * Tests {@link ReportHistoryServices} behavior for Options.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionReportsHistoryTest extends ReportHistoryTestBase<Option> {
    @Override
    public void instrument() throws Exception {
        Date before = new Date();
        Option option = getInstrument();
        super.instrument();
        Date after = new Date();
        assertThat(getOptionPositions(after, option.getSymbol()),
                   allOf(isOfSize(1),
                         hasEntry(pos(option),getExpectedPosition().setScale(SCALE))));
        assertThat(getOptionPositions(after,sActor,option.getSymbol()),
                   allOf(isOfSize(1),
                         hasEntry(pos(option),getExpectedActorPosition().setScale(SCALE))));
        assertThat(getOptionPositions(after,sExtraUser,option.getSymbol()),
                   allOf(isOfSize(1),
                         hasEntry(pos(option),getExpectedExtraPosition().setScale(SCALE))));

        assertThat(getOptionPositions(before,option.getSymbol()),isOfSize(0));
        assertThat(getOptionPositions(before,sActor,option.getSymbol()),isOfSize(0));
        assertThat(getOptionPositions(before,sExtraUser,option.getSymbol()),isOfSize(0));
    }

    @Override
    protected Option getInstrument() {
        return new Option("ubm", "20101010", BigDecimal.TEN, OptionType.Call);
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Option inInstrument) throws Exception {
        return getPosition(inDate, inInstrument);
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Option inInstrument, SimpleUser inUser) throws Exception {
        return getPosition(inDate, inInstrument, inUser);
    }

    @Override
    protected Map<PositionKey<Option>, BigDecimal> getInstrumentPositions(Date inDate) throws Exception {
        return getAllOptionPositions(inDate);
    }

    @Override
    protected Map<PositionKey<Option>, BigDecimal> getInstrumentPositions(Date inDate, SimpleUser inUser) throws Exception {
        return getAllOptionPositions(inDate, inUser);
    }
}
