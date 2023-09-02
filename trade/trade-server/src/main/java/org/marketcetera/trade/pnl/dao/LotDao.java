package org.marketcetera.trade.pnl.dao;

import java.math.BigDecimal;
import java.util.List;

import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.core.Preserve;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.pnl.Trade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Identifies a lot used to calculate profit and loss.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public interface LotDao
        extends JpaRepository<PersistentLot,Long>,QuerydslPredicateExecutor<PersistentLot>
{
    /**
     * Find the unallocated lots for the given attributes in FIFO order.
     *
     * @param inUser a <code>SixerUser</code> value
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @return a <code>List&lt;PersistentLot&gt;</code> value
     */
    @Query("select l from Lot l where user=?1 and symbol=?2 and securityType=?3 and (quantity-allocatedQuantity>0) order by effectiveDate,id")
    List<PersistentLot> findUnallocatedLots(PersistentUser inUser,
                                            String inSymbol,
                                            SecurityType inSecurityType);
    /**
     * Find the unallocated lots for the given attributes in FIFO order.
     *
     * @param inUser a <code>SixerUser</code> value
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inPageRequest a <code>Pageable</code> value
     * @return a <code>List&lt;PersistentLot&gt;</code> value
     */
    @Query("select l from Lot l where user=?1 and symbol=?2 and securityType=?3 and (quantity-allocatedQuantity>0) order by effectiveDate,id")
    List<PersistentLot> findUnallocatedLots(PersistentUser inUser,
                                            String inSymbol,
                                            SecurityType inSecurityType,
                                            Pageable inPageRequest);
    /**
     * Find the lots for the given attributes in FIFO order.
     *
     * @param inUser a <code>SixerUser</code> value
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @return a <code>List&lt;PersistentLot&gt;</code> value
     */
    List<PersistentLot> findByUserAndSymbolAndSecurityTypeOrderByEffectiveDateAscIdAsc(PersistentUser inUser,
                                                                                       String inSymbol,
                                                                                       SecurityType inSecurityType);
    /**
     * Find the weighted average values for the given attributes.
     *
     * @param inUser a <code>SixerUser</code> value
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @return a <code>LotCalculationResult</code> value
     */
    @Query("select new com.marketcetera.sixer.model.dao.LotCalculationResult(sum(l.quantity-l.allocatedQuantity),sum((l.quantity-l.allocatedQuantity)*l.basisPrice)) from Lot l where l.user=?1 and l.symbol=?2 and l.securityType=?3 and (l.quantity-l.allocatedQuantity>0)")
    LotCalculationResult findPositionWeightedAverage(PersistentUser inUser,
                                                     String inSymbol,
                                                     SecurityType inSecurityType);
    /**
     * Find the realized gain for the given user/instrument.
     *
     * @param inUser a <code>SixerUser</code> value
     * @param inSymbol a <code>String</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @return a <code>BigDecimal</code> value
     */
    @Query("select sum(l.baseGain) from Lot l where l.user=?1 and l.symbol=?2 and l.securityType=?3")
    BigDecimal findRealizedGain(PersistentUser inUser,
                                String inSymbol,
                                SecurityType inSecurityType);
    /**
     * Find the realized gain for the given trade.
     *
     * @param inTrade a <code>Trade</code> value
     * @return a <code>BigDecimal</code> value
     */
    @Query("select sum(l.gain) from Lot l where trade=?1")
    BigDecimal findPnlGain(Trade inTrade);
}
