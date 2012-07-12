require File.dirname(__FILE__) + '/../test_helper'
require 'quickfix_ruby'
require 'quickfix_fields'

class MarketceteraTestBase < Test::Unit::TestCase
  include TradesHelper
  fixtures :currencies, :sub_account_types, :currency_pairs
  
  def logger
    Logger.new(STDOUT)
  end  
  
  def test_default
    SubAccountType.new
    assert_equal 7, SubAccountType.find(:all).length
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.preloaded.length
    assert SubAccountType.preloaded.length > 0, "didn't load preloaded sub-accounts"
  end
  
  def assert_errors
    assert_tag error_message_field
  end
  
  def assert_no_errors
    assert_no_tag error_message_field
  end
  
  def error_message_field
    {:tag => "div", :attributes => { :class => "fieldWithErrors" }}
  end
  
  def assert_has_error_box
    assert_tag :tag => "div", :attributes => { :class => "errorExplanation" }
  end
  
  # verifies the red line with an error notice shows up (ex: No bla bla was found...)
  def assert_has_error_notice(content = nil)
    if(content.nil?)
      assert_tag :tag => "div", :attributes => { :id => "error_notice" }
    else
      assert_tag :tag => "div", :attributes => { :id => "error_notice" }, :content => content  
    end
  end
  
  # verifies trade has the right total price + commissions
  # Looks at all the postings and makes sure the numbers are the same as passed in
  def verify_trade_prices(trade, total_price, total_commission)
    sti = trade.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    assert_nums_equal total_price, sti.quantity
    assert_nums_equal -sti.quantity, 
        trade.journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], sti.pair_id).quantity, 
        "cash portion of STI is incorrect"
    
    comm = trade.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
    assert_nums_equal total_commission, comm.quantity
    assert_nums_equal -comm.quantity, 
        trade.journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], comm.pair_id).quantity, 
        "cash portion of commission is incorrect"   
  end

  # Verfies a particular action does not exist in the controller
  def assert_no_controller_action(isPost, action, params = {})
    begin
      if(isPost) 
        post action, params
      else 
        get action
      end
    rescue ActionController::UnknownAction => err
      assert_equal "No action responded to "+action.to_s, err.message
    
    end 
  end
  
  # Verify that show/edit/delete links are present
  # config/environments/test.rb has 37 hard-coded as an asset-id
  def assert_has_show_edit_delete_links(hasShow, hasEdit, hasDelete)
    if(hasShow)
      assert_tag :tag => 'img', :attributes => { :alt=>'Show', :src => '/images/icons/show.png?37'}
    else 
      assert_no_tag :tag => 'img', :attributes => { :alt=>'Show', :src => '/images/icons/show.png?37'}
    end
    if(hasEdit)
      assert_tag :tag => 'img', :attributes => {:alt=>'Edit', :src => '/images/icons/pencil.png?37'}
    else
      assert_no_tag :tag => 'img', :attributes => {:alt=>'Edit', :src => '/images/icons/pencil.png?37'}
    end
    if(hasDelete)
      assert_tag :tag => 'img', :attributes => { :alt=>'Destroy', :src => '/images/icons/cancel.png?37'}
    else
      assert_no_tag :tag => 'img', :attributes => { :alt=>'Destroy', :src => '/images/icons/cancel.png?37'}
    end
  end
  
  # compare exceptions
  def assert_exception(exception, message=nil, &block)
    begin
      yield
    rescue exception => e
      assert_equal message,e.message
    end
  end
  
   
  
  # Helper function to create a trade
  def create_test_trade(qty, price, side, account, date, symbol, commission, cur, security_type = TradesHelper::SecurityTypeEquity)
    if(security_type == TradesHelper::SecurityTypeEquity)
      theTrade = Trade.new(:quantity => qty, :price_per_share => price, :side => side)
      theTrade.tradeable = Equity.get_equity(symbol)
    else
      if (security_type == TradesHelper::SecurityTypeForex)
        theTrade = ForexTrade.new(:quantity => qty, :price_per_share => price, :side => side)
        theTrade.tradeable = CurrencyPair.get_currency_pair(symbol)
      else
        raise "Creating trade of unknown type: #{security_type}"
      end
    end
    assert theTrade.create_trade(theTrade.quantity, symbol, theTrade.price_per_share, 
                                        commission,  cur, account, date)
    theTrade.save
    return theTrade  
  end

  # Compare two numbers (float, bigDecimal, strings, etc) with a given tolerance
  def assert_nums_equal(expected, actual, message=nil, tolerance=BigDecimal.new("0.000001"))
     full_message = build_message(message, <<EOT, expected, actual)
<?> expected but was
<?>.
EOT
    expected = BigDecimal.new(expected.to_s)
    actual =   BigDecimal.new(actual.to_s)
    assert_block(full_message) { (expected - actual).abs < tolerance }
  end  
end