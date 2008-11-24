require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class MarksBySymbolTest < MarketceteraTestBase

  def setup
    @suffix = 'smb'
    @params = {"date_smb"=>{"from(1i)"=>"2002", "to(1i)"=>"2007", "from(2i)"=>"4", "to(2i)"=>"4", "from(3i)"=>"23",
                "to(3i)"=>"23"}, "m_symbol"=>{"root"=>"GOOG"}, "action"=>"by_symbol", "controller"=>"marks", "suffix"=>@suffix}
  end

  def test_unknown_currency
    mbs = MarksBySymbol.new("XYZ", @params, @suffix)

    assert mbs.valid?

    mbs.unknown_currency_pair=false
    assert mbs.valid?

    mbs.unknown_currency_pair=true
    assert !mbs.valid?
    assert_not_nil mbs.errors[:currency_pair]
    assert_not_nil mbs.errors[:currency_pair].match("XYZ")
  end

  # validate that we still preserve the super behaviour
  def test_validation_super_behaviour_preserved
    # from date is inavalid: 2007-2-31
    params = {"date_smb"=>{"from(1i)"=>"2002", "to(1i)"=>"2007", "from(2i)"=>"2", "to(2i)"=>"4", "from(3i)"=>"31",
                "to(3i)"=>"23"}, "m_symbol"=>{"root"=>"GOOG"}, "action"=>"by_symbol", "controller"=>"marks", "suffix"=>@suffix}
    mbs = MarksBySymbol.new("XYZ", params, @suffix)
    mbs.unknown_currency_pair = true
    assert !mbs.valid?
    assert_not_nil mbs.errors[:from_date], "didn't error out on date"
    assert_not_nil mbs.errors[:currency_pair], "didn't error out on having invalid currency pair"
    assert_not_nil mbs.errors[:currency_pair].match("XYZ")
  end

end
