require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'pnl_controller'

# Re-raise errors caught by the controller.
class PnlController; def rescue_action(e) raise e end; end

class PnlControllerTest < MarketceteraTestBase

  def setup
    @controller = PnlController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    directory = File.join(File.dirname(__FILE__), "../fixtures/pnl") 
    Fixtures.create_fixtures(directory, [:trades, :m_symbols, :journals, :equities, :accounts, :sub_accounts,
                                         :sub_account_types, :postings, :marks]) 
  end

  # no mark for GOOG on 4/11/2007
  def test_by_account_no_mark_present
    get :report, { :account=>{:nickname=>"TOLI"}, :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"11", "to(3i)"=>"11"}}
    assert_response :success
    assert_template 'missing_marks'
    assert_has_error_notice                   
  end

  # Marks on 4/25/2006 and 4/26/2006
  # Txn date: B 100 IBM 70.73 on 5/8/2006
  # Do P&L from 1/1/2006 to 5/8/2006
  # Should get an error calculating P&L since don't have a mark on 5/8/2006
  # this is bug #231
  def test_pnl_by_account_mark_missing_on_txn_date
    e = Equity.get_equity("IBM", true)
    m1 = Mark.new(:mark_value => BigDecimal.new("100"), :mark_date => Date.civil(2006,4,25))
    m2 = Mark.new(:mark_value => BigDecimal.new("101"), :mark_date => Date.civil(2006,4,26))
    t = create_test_trade(100, 77.30, Side::QF_SIDE_CODE[:buy], "BOB", Date.civil(2006, 5, 8), "IBM", "4.53", "ZAI")
    a = Account.new(:nickname => "BOB")

    get :report, { :account=>{:nickname=>"BOB"}, :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"5", "from(2i)"=>"1",
                                "from(3i)"=>"1", "to(3i)"=>"8"}}
    assert_response :success
    assert_template 'missing_marks'
    assert_has_error_notice
    assert_has_error_notice("Unable to calculate P&L because some marks are missing.")
  end

  # same as above, but we are already missing some marks for FRO for example
  def test_pnl_by_account_mark_missing_on_txn_date_aggregate
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"5", "from(2i)"=>"1",
                                "from(3i)"=>"1", "to(3i)"=>"8"}}
    assert_response :success
    assert_template 'missing_marks'
    assert_has_error_notice
    assert_equal 5, assigns(:missing_marks).length
    assert_has_error_notice("Unable to calculate P&L because some marks are missing.")
  end

  def test_pnl_by_account_no_data_at_all
      Account.delete(Account.find_by_nickname('TOLI'))
      Account.delete(Account.find_by_nickname('GRAHAM'))
      Trade.delete_all
      Equity.delete_all
      MSymbol.delete_all
      Mark.delete_all
      Posting.delete_all
      Journal.delete_all

      get :report, { :account=>{:nickname=>""}, :suffix => "acct",
                         :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                  "from(3i)"=>"11", "to(3i)"=>"11"}}
      assert_response :success
      assert_template 'pnl_aggregate'
      assert_no_tag :tag => 'div', :attributes => { :id => "error_notice" }
      assert_equal 0, assigns(:pnls).length
  end

  # should return unassigned account
  def test_unassigned_account
    get :report, { :suffix => "acct", :account => {:nickname => "[UNASSIGNED]"},
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'pnl_by_account'
    assert_not_nil assigns(:pnls)
    # should find 3 P&Ls for unassigned
    assert_equal 3, assigns(:pnls).length
    assert_equal "[UNASSIGNED]", assigns(:nickname)
    pnls = assigns(:pnls)
    assert_equal 3, pnls.length
    assert_nums_equal 0, pnls[0].profit_and_loss
    assert_equal "MSFT", pnls[0].tradeable.m_symbol_root
    assert_nums_equal 0, pnls[1].profit_and_loss
    assert_equal "IBM", pnls[1].tradeable.m_symbol_root
    assert_nums_equal 0, pnls[2].profit_and_loss
    assert_equal "GOOG", pnls[2].tradeable.m_symbol_root
  end

  def test_nonexistent_acct
    get :report, { :suffix => "acct", :account => {:nickname => "noSuchAcct"},
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'index'
    assert_nil assigns(:cashflows)
    assert_has_error_box
    assert_not_nil assigns(:report).errors[:account]
  end
    
  def test_by_account
    get :report, { :suffix => "acct", :account => {:nickname => "TOLI"},
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'pnl_by_account'
    assert_not_nil assigns(:pnls)
    assert_equal "TOLI", assigns(:nickname)
    # should find 3 P&Ls for TOLI
    assert_equal 4, assigns(:pnls).length
    pnls = assigns(:pnls)
    assert_nums_equal -669.0, pnls[0].profit_and_loss
    assert_equal "SUNW", pnls[0].tradeable.m_symbol_root
    assert_nums_equal 0, pnls[1].profit_and_loss
    assert_equal "MSFT", pnls[1].tradeable.m_symbol_root
    assert_nums_equal 0, pnls[2].profit_and_loss
    assert_equal "IBM", pnls[2].tradeable.m_symbol_root
    assert_nums_equal 0, pnls[3].profit_and_loss
    assert_equal "GOOG", pnls[3].tradeable.m_symbol_root

    # now verify the page coming back has the correct link - ie it's linking the symbol and account correctly
    # should look like this: <td><a href="/queries/trade_search?m_symbol_root=IBM&amp;all_dates=yes&amp;nickname=toli">IBM</a></td>
    assert_tag :tag => 'td', :child => {:tag => 'a', :content => "SUNW",
                                        :attributes => {:href => /queries\/trade_search\?.*m_symbol_root=SUNW/}}
    assert_tag :tag => 'td', :child => {:tag => 'a', :content => "SUNW",
                                        :attributes => {:href => /queries\/trade_search\?.*all_dates=yes/}}
    assert_tag :tag => 'td', :child => {:tag => 'a', :content => "SUNW",
                                        :attributes => {:href => /queries\/trade_search\?.*nickname=TOLI/}}

    # verify that account name comes up correctly
    assert_tag :tag => 'tr', :child => {:tag => "td", :content => "TOLI"}
  end
  
  def test_invalid_from_to_by_acct
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"31", "to(3i)"=>"31"}}
    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_equal 2, assigns(:report).errors.length
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:to_date]
  end
  
  def test_invalid_from_to_by_dates
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"31", "to(3i)"=>"31"}}
    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_equal 2, assigns(:report).errors.length
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:to_date]
  end

  # should find 3 cashflows across all 3 accounts
  def test_aggregate_across_all_accts
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'pnl_aggregate'
    assert_equal 0, assigns(:report).errors.length

    pnls = assigns(:pnls)
    assert_equal 3, pnls.length
    assert_equal "[UNASSIGNED]",pnls[0].account.nickname 
    assert_nums_equal 0, pnls[0].profit_and_loss.to_s
    assert_equal "TOLI",pnls[1].account.nickname 
    assert_nums_equal -669.0, pnls[1].profit_and_loss.to_s
    assert_equal "GRAHAM",pnls[2].account.nickname 
    assert_nums_equal 285.12, pnls[2].profit_and_loss.to_s

    # now verify the links are formatted correctly
    # sample: <td><a href="/pnl/report?to_date=2007-10-01&amp;from_date=2007-09-28&amp;nickname=toli">[toli]</a></td>
    assert_tag :tag => 'td', :child => {:tag => 'a', :content => /GRAHAM/,
                                        :attributes => { :href => /pnl\/report\?.*to_date=2007-04-19/} }
    assert_tag :tag => 'td', :child => {:tag => 'a', :content => /GRAHAM/,
                                        :attributes => { :href => /pnl\/report\?.*from_date=2007-04-17/} }
    assert_tag :tag => 'td', :child => {:tag => 'a', :content => /GRAHAM/,
                                        :attributes => { :href => /pnl\/report\?.*nickname=GRAHAM/} }
  end

  def test_aggregate_missing_mark
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"20"}}
    assert_response :success
    assert_template 'missing_marks'
    assert_has_error_notice
    assert_not_nil assigns(:missing_marks)
    assert_equal 5, assigns(:missing_marks).length, "should need 5 marks"
    assert_equal "SUNW", assigns(:missing_marks)[0].tradeable_m_symbol_root
    assert_equal "MSFT", assigns(:missing_marks)[1].tradeable_m_symbol_root
    assert_equal "IBM", assigns(:missing_marks)[2].tradeable_m_symbol_root
    assert_equal "GOOG", assigns(:missing_marks)[3].tradeable_m_symbol_root
    assert_equal "FRO", assigns(:missing_marks)[4].tradeable_m_symbol_root

    #all marks are from 4-20-2007
    assigns(:missing_marks).each {|m| assert_equal m.mark_date.to_s, Date.civil(2007, 4, 20).to_s}
  end

  def test_from_after_to_date
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2002", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"20"}}

    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_equal 1, assigns(:report).errors.length
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:from_date].match("is later than")
  end

  # verify the missing marks page has the 'edit' link that's linked to the create method of the marks controller
  def test_missing_marks_have_right_links
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"20"}}
    assert_response :success
    assert_template 'missing_marks'
    # verify we have a "create new" link and no other links
    assert_tag :tag => 'img', :attributes => {:alt=>'Create New', :src => '/images/icons/pencil.png?37'}
    assert_has_show_edit_delete_links(false, false, false)

    # now let's dissect the link - should look like this:
    # href="/marks/new_missing?tradeable_type=Equity&amp;tradeable_id=1&amp;mark_date=2007-12-04
    m = assigns(:missing_marks)[0]
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*tradeable_type=Equity/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*mark_date=#{m.mark_date}/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*tradeable_id=#{m.tradeable.id}/}
  end

  # Verify that if we have a P&L in a currency XXX and we don't have the XXX/base currency mark for
  # that date the link shows up and is correct
  # BaseCurrency is USD, create a ZAI/EUR trade, and make sure a EUR/USD mark request shows up
  def test_missing_forex_base_currency_mark
    trade = create_test_trade(100, 77.30, Side::QF_SIDE_CODE[:buy], "forex", Date.civil(2006, 5, 8), "ZAI/EUR", "4.53", "ZAI", SecurityTypeForex)
    zaieur = CurrencyPair.get_currency_pair("ZAI/EUR")

    # get p&l, should get missing forex mark on 5/8/2006
    get :report, { :suffix => "acct", :account => {:nickname => "forex"},
            :date_acct=>{"to(1i)"=>"2006", "from(1i)"=>"2006", "to(2i)"=>"8", "from(2i)"=>"4",
                     "from(3i)"=>"17", "to(3i)"=>"20"}}

    assert_response :success
    assert_template 'missing_marks'
    m = assigns(:missing_marks)[0]
    assert_equal "2006-08-20", m.mark_date.to_s

    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*tradeable_type=CurrencyPair/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*mark_date=#{m.mark_date}/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*tradeable_id=#{m.tradeable.id}/}

    # now create that mark and verify we get a zai/usd mark request
    m = ForexMark.create(:tradeable => zaieur, :mark_date => Date.civil(2006, 8,20), :mark_value => "1.234", :mark_type => Mark::MarkTypeClose)
    assert m.save
    get :report, { :suffix => "acct", :account => {:nickname => "forex"},
            :date_acct=>{"to(1i)"=>"2006", "from(1i)"=>"2006", "to(2i)"=>"8", "from(2i)"=>"4",
                     "from(3i)"=>"17", "to(3i)"=>"20"}}

    assert_response :success
    assert_template 'pnl_by_account'

    # verify have a link to create missing zai/usd mark
    assert_tag :tag => 'a', :content => /Create mark/, :attributes =>
            {:href => /marks\/new_missing\?.*tradeable_type=CurrencyPair/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*mark_date=2006-08-20/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*currency_pair=ZAIUSD/}

    # verify same works for aggregate
    get :report, { :suffix => "acct",
            :date_acct=>{"to(1i)"=>"2006", "from(1i)"=>"2006", "to(2i)"=>"8", "from(2i)"=>"4",
                     "from(3i)"=>"17", "to(3i)"=>"20"}}

    assert_response :success
    assert_template 'pnl_aggregate'

    # verify have a link to create missing zai/usd mark
    assert_tag :tag => 'a', :content => /Create mark/, :attributes =>
            {:href => /marks\/new_missing\?.*tradeable_type=CurrencyPair/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*mark_date=2006-08-20/}
    assert_tag :tag => 'a', :attributes =>
            {:href => /marks\/new_missing\?.*currency_pair=ZAIUSD/}
  end
end
