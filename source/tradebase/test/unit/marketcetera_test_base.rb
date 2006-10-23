require File.dirname(__FILE__) + '/../test_helper'
require 'quickfix_ruby'
require 'quickfix_fields'

class MarketceteraTestBase < Test::Unit::TestCase
  fixtures :currencies, :sub_account_types
  
  def logger
    Logger.new(STDOUT)
  end  
  
  def test_default
    SubAccountType.new
    assert_equal SubAccountType.find_all.length, 7
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.preloaded.length
    assert SubAccountType.preloaded.length > 0
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