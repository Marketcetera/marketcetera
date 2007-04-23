require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class ReportWithToFromDatesTest < MarketceteraTestBase
  def setup
    @suffix = 'smb'
    @params = {"date_smb"=>{"from(1i)"=>"2002", "to(1i)"=>"2007", "from(2i)"=>"4", "to(2i)"=>"4", "from(3i)"=>"23", 
                "to(3i)"=>"23"}, "m_symbol"=>{"root"=>"GOOG"}, "action"=>"by_symbol", "controller"=>"marks", "suffix"=>@suffix}
  end
  
  def test_initialize
    r = ReportWithToFromDates.new(@params, @suffix)
    assert r.valid?
    assert_equal Date.new(2002, 4, 23), r.from_date.as_date
    assert_equal Date.new(2007, 4, 23), r.to_date.as_date
  end
  
  # invalid from date
  def test_invalid
    params = {"date_smb"=>{"from(1i)"=>"2002", "to(1i)"=>"2007", "from(2i)"=>"4", "to(2i)"=>"4", "from(3i)"=>"32", 
                "to(3i)"=>"23"}, "m_symbol"=>{"root"=>"GOOG"}, "action"=>"by_symbol", "controller"=>"marks", "suffix"=>@suffix}
    r = ReportWithToFromDates.new(params, @suffix)
    assert !r.valid?
    assert_equal Date.new(2007, 4, 23), r.to_date.as_date
    assert_equal 1, r.errors.length
    assert_not_nil r.errors[:from_date]
  end
  
  # don't send any params in - dates should come out as invalid
  def test_params_empty
    r = ReportWithToFromDates.new({})
    assert !r.valid?
    assert_equal 2, r.errors.length
    assert_not_nil r.errors[:from_date]
    assert_not_nil r.errors[:to_date]
  
    # with suffix
    r = ReportWithToFromDates.new({:suffix => "sfx"})
    assert !r.valid?
    assert_equal 2, r.errors.length
    assert_not_nil r.errors[:from_date]
    assert_not_nil r.errors[:to_date]
  end
end