require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'trades_controller'

class CreateTradeWithDanglingSymbolTest < MarketceteraTestBase
  fixtures :currencies

  # test the use case where you try to create a trade
  # where the symbol exists but the equity does not
  # Use case example:
  # 1. Create equity GOOG
  # 2. edit GOOG to become SUNW
  # 3. try to create a trade for GOOG
  def test_create_trade_for_symbol_no_equity
    assert_nil MSymbol.find_by_root("IFLI")
    ifli = MSymbol.create(:root => "IFLI")
    assert_not_nil ifli.id
    theTrade = Trade.new(:quantity => 20, :price_per_share => 420.23, :side => Side::QF_SIDE_CODE[:buy])
    assert theTrade.create_equity_trade(theTrade.quantity, "IFLI", theTrade.price_per_share, 19.99,  "USD", 
                                        "beer", Date.today)
    assert theTrade.save
    
    assert_equal ifli, theTrade.tradeable.m_symbol    
  end

end