package org.marketcetera.trade.pnl;

import org.springframework.stereotype.Service;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.UserID;

/**
 * Stub class temporarily created for Spring Boot 3 migration.
 * Original implementation required QueryDSL, which isn't compatible with Jakarta EE.
 * 
 * See TradePnlServiceImpl.java.disabled for the original implementation.
 */
@Service
public class TradePnlServiceImpl 
        implements TradePnlService
{
    public TradePnlServiceImpl()
    {
        SLF4JLoggerProxy.warn(this, "This is a stub implementation for Spring Boot 3 migration");
    }

    @Override
    public CollectionPageResponse<CurrentPosition> getCurrentPositions(UserID inUserID, PageRequest inPageRequest) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getCurrentPositions not implemented");
        return new CollectionPageResponse<>();
    }

    @Override
    public CollectionPageResponse<ProfitAndLoss> getProfitAndLoss(UserID inUserID, Instrument inInstrument, PageRequest inPageRequest) {
        SLF4JLoggerProxy.warn(this, "Stub implementation - getProfitAndLoss not implemented");
        return new CollectionPageResponse<>();
    }
}