require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'create_trades_controller'

# Re-raise errors caught by the controller.
class CreateTradesController; def rescue_action(e) raise e end; end
  
class CreateTradesControllerTest < MarketceteraTestBase
  include MessageLogsHelper
  fixtures :messages_log, :currencies, :m_symbols, :equities, :sub_accounts, :sub_account_types

  def setup
    @controller = CreateTradesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_create_one_trade
    # testing the goog_20 message
    msg = MessageLog.find(20)
    assert_equal false, msg.processed
  
    qfMsgOrig = Quickfix::Message.new(msg.text)
    # the message doesn't have an account on it, let's set it.
    qfMsgOrig.setField(Quickfix::Account.new.getField, "funny money")
    # set the commission
    qfMsgOrig.setField(Quickfix::Commission.new.getField, "14.95")
    msg.text = qfMsgOrig.to_s
    msg.save
    
    trade = @controller.create_one_trade(20)
    msg.reload
    assert_equal true, msg.processed
    
    # verify the trade
    assert_not_nil trade
    assert_not_nil trade.id
    
    qfMsg = Quickfix::Message.new(msg.text)
    assert_equal "GOOG", getStringFieldValueIfPresent(qfMsg, Quickfix::Symbol.new)
    assert_equal 14.95.to_s, trade.total_commission.to_s, "total commission not present"
    assert_equal getStringFieldValueIfPresent(qfMsg, Quickfix::Symbol.new), trade.tradeable_m_symbol_root

    # need to verify accounts, etc
    acct = Account.find_by_nickname("funny money")
    assert_not_nil acct, "account was not created"
    assert_equal trade.account, acct
    assert_equal SubAccountType::DESCRIPTIONS.length, trade.account.sub_accounts.length

    # verify the original FIX msg got preserved
    assert_not_nil trade.imported_fix_msg
    assert_equal msg.text, trade.imported_fix_msg

    # now try creating this again
    assert_nil @controller.create_one_trade(20)
  end
  
  def test_create_trades
    assert_equal 0, Trade.find(:all).length
    # setup 3 trades to be created: 20, 21, 23
    post :create_trades, :trades => { 20 => "1", 21 => "1", 23 => "1", 24 => 0}  
  
    assert_equal "Created 3 trades", flash[:notice]
    assert_equal 3, Trade.count
    assert_redirected_to :controller => 'trades', :action => 'list'
  end
  
  # Verify that we don't process the same trade twice, and that we keep count of only processed ones
  def test_create_trades_already_processed
    # process some trades
    test_create_trades

    assert_equal 3, Trade.find(:all).length
    # setup 3 trades to be created: 20, 21, 23
    post :create_trades, :trades => { 20 => "1", 21 => "1", 23 => "1", 24 => 0, 27 => "1"}  
  
    assert_equal "Created 1 trades", flash[:notice]
    assert_equal 4, Trade.count
    assert_redirected_to :controller => 'trades', :action => 'list'
  end
end
