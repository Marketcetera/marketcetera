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
  
end