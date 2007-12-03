require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class ProfitAndLossTest < MarketceteraTestBase
  fixtures :currencies, :accounts, :sub_accounts, :sub_account_types, :currency_pairs
  
  # The following describes the PnL test cases (T1 and T2 are test cases)
  #                    T1             T2
  # Type 1:  |------|
  # Type 2:  |--------------|
  # Type 3:  |---------|
  # Type 4:            |--------------|
  # Type 5:                 |------|
  # Type 6:                           |-----------|
  # Type 7:                        |--------------|
  # Type 8:                                |------|
  # Type 9:  |------------------------------------|
  # Tests assume correct marks.
  
  
#  def test_equity_pnl_type_1
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,1)
#    pnl_end_date = Date.civil(2007,11,3)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#    
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)  
#    assert_equal 0, pnls.length
#  end
#
#
#  def test_equity_pnl_type_2
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,1)
#    pnl_end_date = Date.civil(2007,11,8)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#
#    assert_equal 1, pnls.length
#    assert_nums_equal 84.01, pnls[0].profit_and_loss
#  end
#
#  def test_equity_pnl_type_3
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,1)
#    pnl_end_date = Date.civil(2007,11,5)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#
#    assert_equal 1, pnls.length
#    assert_nums_equal 84.01, pnls[0].profit_and_loss
#  end
#
#  def test_equity_pnl_type_4
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,5)
#    pnl_end_date = Date.civil(2007,11,10)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#
#    assert_equal 1, pnls.length
#    assert_nums_equal 20.02, pnls[0].profit_and_loss
#  end
#
#  def test_equity_pnl_type_5
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,6)
#    pnl_end_date = Date.civil(2007,11,9)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#
#    assert_equal 1, pnls.length
#    assert_nums_equal 100, pnls[0].profit_and_loss
#  end
#
#  def test_equity_pnl_type_6
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,10)
#    pnl_end_date = Date.civil(2007,11,11)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#
#    assert_equal 1, pnls.length
#    assert_nums_equal 36.01, pnls[0].profit_and_loss
#  end
#
#  def test_equity_pnl_type_7
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,9)
#    pnl_end_date = Date.civil(2007,11,11)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#
#    assert_equal 1, pnls.length
#    assert_nums_equal 36.01, pnls[0].profit_and_loss
#  end
#
#  def test_equity_pnl_type_8
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,11)
#    pnl_end_date = Date.civil(2007,11,13)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#
#    assert_equal 0, pnls.length
#  end
#
#  def test_equity_pnl_type_9
#    create_equity_trades()
#
#    pnl_start_date = Date.civil(2007,11,1)
#    pnl_end_date = Date.civil(2007,11,15)
#
#    the_equity = Equity.get_equity("CSCO", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#
#    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
#    assert_equal 1, pnls.length
#    assert_nums_equal 20.02, pnls[0].profit_and_loss
#  end

  def create_equity_trades()
    create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], "TOLI", Date.civil(2007, 11, 5), "CSCO", 4.99, "USD")
    create_test_trade(100, 20.41, Side::QF_SIDE_CODE[:sell], "TOLI", Date.civil(2007, 11, 10), "CSCO", 4.99, "USD")
  end

  def test_forex_pnl_type_1
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,3)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 0, pnls.length
  end

#  def test_forex_pnl_type_9
#    create_forex_trades()
#
#    pnl_start_date = Date.civil(2007,11,1)
#    pnl_end_date = Date.civil(2007,11,15)
#
#    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
#    usd = Currency.find_by_alpha_code("USD")
#
#    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
#    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
#    
#    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
#    assert_equal 1, pnls.length
#    assert_nums_equal 20.02, pnls[0].profit_and_loss
#  end

  def create_forex_trades()
    create_forex_trade(1000000, 1.458, Side::QF_SIDE_CODE[:buy], "TOLI", Date.civil(2007, 11, 5), "EUR/USD", 4.99, "USD")
    create_forex_trade(1000000, 1.459, Side::QF_SIDE_CODE[:sell], "TOLI", Date.civil(2007, 11, 10), "EUR/USD", 4.99, "USD")
  end
  # Helper function to create a trade
  def create_forex_trade(qty, rate, side, account, date, symbol, commission, cur)
      theTrade = Trade.new(:quantity => qty, :price_per_share => rate, :side => side)
    theTrade.tradeable = CurrencyPair.get_currency_pair(symbol)
    assert theTrade.create_trade(theTrade.quantity, symbol, theTrade.price_per_share, 
                                        commission,  cur, account, date)
    theTrade.save
    return theTrade  
  end

end
