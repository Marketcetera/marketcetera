require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'create_trades_controller'

# Re-raise errors caught by the controller.
class CreateTradesController; def rescue_action(e) raise e end; end
  
class CreateTradesControllerTest < MarketceteraTestBase
  include MessageLogsHelper
  fixtures :messages_log

  def setup
    @controller = CreateTradesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_create_one_trade
    # testing the goog_20 message
    msg = MessageLog.find(20)
    assert_equal false, msg.processed
    trade = @controller.create_one_trade(20)
    msg.reload
    assert_equal true, msg.processed
    
    # verify the trade
    assert_not_nil trade
    assert_not_nil trade.id
    
    qfMsg = Quickfix::Message.new(msg.text)
    assert_equal "GOOG", getStringFieldValueIfPresent(qfMsg, Quickfix::Symbol.new)
    logger.debug(trade.to_s)
    assert_equal getStringFieldValueIfPresent(qfMsg, Quickfix::Symbol.new), trade.tradeable_m_symbol_root
    
  end
end
