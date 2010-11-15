require File.dirname(__FILE__) + '/../test_helper'
require 'bigdecimal'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class ApplicationHelperTest < MarketceteraTestBase
  include ApplicationHelper
  
  def test_contract_string
    assert_nil contract_string(nil, 5)
    assert_equal "", contract_string("", 5)
    assert_equal "tolik", contract_string("tolik", 10)
    assert_equal "tolik", contract_string("tolik", 5)
    assert_equal "t...k", contract_string("tolik", 4)
    assert_equal "al...ba", contract_string("alibaba", 5)
    assert_equal "zapo...hets", contract_string("zaporozhets", 10)  
  end
  
  def test_shorten_string 
    assert_nil shorten_string(nil, 5)
    assert_equal "[UNASSIGNED]", shorten_string("[UNASSIGNED]")
    assert_equal "0123456...", shorten_string("01234567890", 10)
    assert_equal "v lesu ...", shorten_string("v lesu rodilas elochka", 10)
  end
  
  def test_make_spaces_hard()
    assert_nil make_spaces_hard(nil)
    assert_equal "", make_spaces_hard("")
    assert_equal "bob", make_spaces_hard("bob")
    assert_equal "bob&nbsp;barker", make_spaces_hard("bob barker")
    assert_equal "bob&nbsp;barker&nbsp;louis", make_spaces_hard("bob barker louis")
  end
  
  # If (should I say when) this test fails, it is likely because
  # the ActiveRecord::Base.sanitize_sql method has disappeared or 
  # changed its contract.  In that case, we will need to come up
  # with a new way to do what it does.
  def test_sanitize_sql_accessor()
    result = ActiveRecord::Base.sanitize_sql_accessor(["select * from foo where bar = ?", 7])
    assert_equal "select * from foo where bar = 7", result
  end
  
end