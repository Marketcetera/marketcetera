require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class ReportOnDateTest < MarketceteraTestBase
  def test_initialize
    params =  {"date"=>{"on(1i)"=>"2002", "on(2i)"=>"4", "on(3i)"=>"23"}, "suffix"=>"smb"}
    r = ReportOnDate.new(params)
    assert r.valid?
    assert_equal Date.new(2002, 4, 23), r.on_date.as_date

    # try single str
    params =  {"on_date"=>"2002-4-23"}
    r = ReportOnDate.new(params)
    assert r.valid?
    assert_equal Date.new(2002, 4, 23), r.on_date.as_date

    params =  {"date"=>{"on(1i)"=>"2002", "on(2i)"=>"4", "on(3i)"=>"23"}}
    r = ReportOnDate.new(params)
    assert r.valid?
    assert_equal Date.new(2002, 4, 23), r.on_date.as_date
  end
  
  # invalid from date
  def test_invalid
    params =  {"date"=>{"on(1i)"=>"2002", "on(2i)"=>"4", "on(3i)"=>"33"}}
    r = ReportOnDate.new(params)
    assert !r.valid?
    assert_equal 1, r.errors.length
    assert_not_nil r.errors[:on_date]
  end
end