require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class ProfitAndLossTest < MarketceteraTestBase
  fixtures  :currency_pairs
  
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
  
  def setup()
    directory = File.join(File.dirname(__FILE__), "../fixtures/pnl") 
    Fixtures.create_fixtures(directory, [:trades, :m_symbols, :journals, :equities, :accounts, :sub_accounts,
                                         :sub_account_types, :postings, :marks]) 

  end
  
  def test_equity_pnl_type_1
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,3)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 0, pnls.length
  end


  def test_equity_pnl_type_2
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,8)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)

    assert_equal 1, pnls.length
    assert_nums_equal 84.01, pnls[0].profit_and_loss
  end

  def test_equity_pnl_type_3
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,5)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)

    assert_equal 1, pnls.length
    assert_nums_equal 84.01, pnls[0].profit_and_loss
  end

  def test_equity_pnl_type_4
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,5)
    pnl_end_date = Date.civil(2007,11,10)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)

    assert_equal 1, pnls.length
    assert_nums_equal 20.02, pnls[0].profit_and_loss
  end

  def test_equity_pnl_type_5
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,6)
    pnl_end_date = Date.civil(2007,11,9)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)

    assert_equal 1, pnls.length
    assert_nums_equal 100, pnls[0].profit_and_loss
  end

  def test_equity_pnl_type_6
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,10)
    pnl_end_date = Date.civil(2007,11,11)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)

    assert_equal 1, pnls.length
    assert_nums_equal 36.01, pnls[0].profit_and_loss
  end

  def test_equity_pnl_type_7
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,9)
    pnl_end_date = Date.civil(2007,11,11)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)

    assert_equal 1, pnls.length
    assert_nums_equal 36.01, pnls[0].profit_and_loss
  end

  def test_equity_pnl_type_8
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,11)
    pnl_end_date = Date.civil(2007,11,13)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)

    assert_equal 0, pnls.length
  end

  def test_equity_pnl_type_9
    create_equity_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,15)

    the_equity = Equity.get_equity("CSCO", false)
    usd = Currency.find_by_alpha_code("USD")

    Mark.create(:mark_value => 20, :tradeable => the_equity, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    Mark.create(:mark_value => 21, :tradeable => the_equity, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)

    pnls = ProfitAndLoss.get_equity_pnl(pnl_start_date, pnl_end_date)
    assert_equal 1, pnls.length
    assert_nums_equal 20.02, pnls[0].profit_and_loss
  end

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

  def test_forex_pnl_type_2
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,6)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 2, pnls.length

    pnl_hash = {}
    pnls.each { |a_pnl| pnl_hash[a_pnl.currency.alpha_code] = a_pnl }
    assert_not_nil pnl_hash["USD"]
    assert_not_nil pnl_hash["EUR"]
    
    assert_nums_equal 41995.01, pnl_hash["USD"]    
    assert_nums_equal 0, pnl_hash["EUR"] 
  end

  def test_forex_pnl_type_3
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,5)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 2, pnls.length

    pnl_hash = {}
    pnls.each { |a_pnl| pnl_hash[a_pnl.currency.alpha_code] = a_pnl }
    assert_not_nil pnl_hash["USD"]
    assert_not_nil pnl_hash["EUR"]
    
    assert_nums_equal 0, pnl_hash["EUR"]    
    assert_nums_equal 41995.01, pnl_hash["USD"]    
  end

  def test_forex_pnl_type_4
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,5)
    pnl_end_date = Date.civil(2007,11,10)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 2, pnls.length

    pnl_hash = {}
    pnls.each { |a_pnl| pnl_hash[a_pnl.currency.alpha_code] = a_pnl }
    assert_not_nil pnl_hash["USD"]
    assert_not_nil pnl_hash["EUR"]
    
    assert_nums_equal 0, pnl_hash["EUR"] 
    assert_nums_equal 990.02, pnl_hash["USD"]  
  end

  def test_forex_pnl_type_5
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,6)
    pnl_end_date = Date.civil(2007,11,9)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 2, pnls.length

    pnl_hash = {}
    pnls.each { |a_pnl| pnl_hash[a_pnl.currency.alpha_code] = a_pnl }
    assert_not_nil pnl_hash["USD"]
    assert_not_nil pnl_hash["EUR"]
    
    assert_nums_equal 100000, pnl_hash["USD"]   
    assert_nums_equal 0, pnl_hash["EUR"] 
  end

  def test_forex_pnl_type_6
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,10)
    pnl_end_date = Date.civil(2007,11,15)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 2, pnls.length

    pnl_hash = {}
    pnls.each { |a_pnl| pnl_hash[a_pnl.currency.alpha_code] = a_pnl }
    assert_not_nil pnl_hash["USD"]
    assert_not_nil pnl_hash["EUR"]
    
    assert_nums_equal 0, pnl_hash["EUR"]   
    assert_nums_equal 58995.01, pnl_hash["USD"]    
  end

  def test_forex_pnl_type_7
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,9)
    pnl_end_date = Date.civil(2007,11,15)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 2, pnls.length

    pnl_hash = {}
    pnls.each { |a_pnl| pnl_hash[a_pnl.currency.alpha_code] = a_pnl }
    assert_not_nil pnl_hash["USD"]
    assert_not_nil pnl_hash["EUR"]
    
    assert_nums_equal 0, pnl_hash["EUR"]
    assert_nums_equal 58995.01, pnl_hash["USD"]  
  end

  def test_forex_pnl_type_8
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,11)
    pnl_end_date = Date.civil(2007,11,15)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 0, pnls.length
  end

  def test_forex_pnl_type_9
    create_forex_trades()

    pnl_start_date = Date.civil(2007,11,1)
    pnl_end_date = Date.civil(2007,11,15)

    the_pair = CurrencyPair.get_currency_pair("EUR/USD", false)
    usd = Currency.find_by_alpha_code("USD")

    ForexMark.create(:mark_value => 1.4, :tradeable => the_pair, :mark_date => pnl_start_date, :mark_type=>"C", :currency_id=>usd.id)
    ForexMark.create(:mark_value => 1.5, :tradeable => the_pair, :mark_date => pnl_end_date, :mark_type=>"C", :currency_id=>usd.id)
    
    pnls = ProfitAndLoss.get_forex_pnl(pnl_start_date, pnl_end_date)  
    assert_equal 2, pnls.length

    pnl_hash = {}
    pnls.each { |a_pnl| pnl_hash[a_pnl.currency.alpha_code] = a_pnl }
    assert_not_nil pnl_hash["USD"]
    assert_not_nil pnl_hash["EUR"]
    
    assert_nums_equal 0, pnl_hash["EUR"]
    assert_nums_equal 990.02, pnl_hash["USD"]
  end

  def create_forex_trades()
    create_forex_trade(1000000, 1.458, Side::QF_SIDE_CODE[:buy], "TOLI", Date.civil(2007, 11, 5), "EUR/USD", 4.99, "USD")
    create_forex_trade(1000000, 1.459, Side::QF_SIDE_CODE[:sell], "TOLI", Date.civil(2007, 11, 10), "EUR/USD", 4.99, "USD")
  end
  
  def test_missing_equity_marks() 
    results = {}
    missing = ProfitAndLoss.get_missing_equity_marks(Date.civil(2007,9,28), true)
    missing.each { |m| results[m.tradeable_m_symbol_root]= m}
    assert_equal 5, results.size
    assert_not_nil results["SUNW"]
    assert_not_nil results["MSFT"]
    assert_not_nil results["IBM"]
    assert_not_nil results["GOOG"]
    assert_not_nil results["FRO"]
  end
  
  def test_missing_equity_marks_for_account()
    results = {}
    missing = ProfitAndLoss.get_missing_equity_marks(Date.civil(2007,12,11), true, Account.find_by_nickname("GRAHAM"))
    missing.each { |m| results[m.tradeable_m_symbol_root]= m}
    assert_equal 1, results.size
    assert_not_nil results["FRO"]
    
  end
  
  def test_missing_forex_marks_for_account()
    create_test_trade(1000000, 1.4432, Side::QF_SIDE_CODE[:buy], "GRAHAM", Date.civil(2007,12,1),
      "ZAI/USD", 0, "USD", TradesHelper::SecurityTypeForex)

    results = {}
    missing = ProfitAndLoss.get_missing_forex_marks(Date.civil(2007,12,11), true, Account.find_by_nickname("GRAHAM"))
    missing.each { |m| results[m.tradeable_m_symbol_root]= m}
    assert_equal 1, results.size
    assert_not_nil results["ZAI/USD"]
    
  end

  # Helper function to create a trade
  def create_forex_trade(qty, rate, side, account, date, symbol, commission, cur)
      theTrade = ForexTrade.new(:quantity => qty, :price_per_share => rate, :side => side)
    theTrade.tradeable = CurrencyPair.get_currency_pair(symbol)
    assert theTrade.create_trade(theTrade.quantity, symbol, theTrade.price_per_share, 
                                        commission,  cur, account, date)
    theTrade.save
    return theTrade  
  end

end
