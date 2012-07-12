require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class DataDictionaryTradeTest < MarketceteraTestBase
  #module DataDictionaryMgr
  
  def test_data_mgr_creation
    assert_raise(NoMethodError) { DataDictionaryMgr.new}
  end

  def test_get_value_name
    side = Quickfix::Side.new
    assert_equal "BUY", DataDictionaryMgr.get_value_name(side, Quickfix::Side_BUY())
    assert_equal "SELL SHORT", DataDictionaryMgr.get_value_name(side, Quickfix::Side_SELL_SHORT())

  	assert_nil DataDictionaryMgr.get_value_name( side, "no_such_field" )
  end
  
  def test_getFieldTag
	assert_equal( 54, DataDictionaryMgr.get_field_tag("Side") )
  	assert_nil DataDictionaryMgr.get_field_tag("no_such_field") 
  end

  def test_getFieldName
    assert_equal( "Side", DataDictionaryMgr.get_field_name(Quickfix::Side.new) )
  end
  
  def test_direct
    d = Quickfix::DataDictionary.new("lib/FIX42.xml")
    assert_equal "BUY", d.getValueName(Quickfix::Side.new.getField, Quickfix::Side_BUY())
  
  end

end