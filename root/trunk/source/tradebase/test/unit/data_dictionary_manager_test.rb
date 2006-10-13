require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class DataDictionaryTradeTest < MarketceteraTestBase
  #module DataDictionaryMgr
  
  def test_data_mgr_creation
    begin 
      DataDictionaryMgr.new
      flunk "DataDictionaryMgr.new should've failed"
    rescue NoMethodError
    end
  end

  def test_get_value_name
    side = Quickfix::Side.new
    assert_equal "BUY", DataDictionaryMgr.get_value_name(side.getField, Quickfix::Side_BUY())
    assert_equal "SELL SHORT", DataDictionaryMgr.get_value_name(side.getField, Quickfix::Side_SELL_SHORT())

  	assert_nil DataDictionaryMgr.get_value_name( side, "no_such_field" )
  end
  
  def test_getFieldTag
	assert_equal( 54, DataDictionaryMgr.get_field_tag("Side") )
  	assert_equal 0,  DataDictionaryMgr.get_field_tag("no_such_field") 
  end

  def test_getFieldName
  		assert_equal( Side, DataDictionaryMgr.get_field_tag(Quickfix::Side.new.getField) )
  	assert_nil( DataDictionaryMgr.get_field_tag(98765) )
  end

end