require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class PnlByAccountTest < MarketceteraTestBase
  fixtures :accounts
  
  def setup
    @suffix = 'smb'
    @params = {"date_smb"=>{"from(1i)"=>"2002", "to(1i)"=>"2007", "from(2i)"=>"4", "to(2i)"=>"4", "from(3i)"=>"23", 
                "to(3i)"=>"23"}, "m_symbol"=>{"root"=>"GOOG"}}
  end
  
  def test_initialize
    pba = PnlByAccount.new("invalid", @params, @suffix)
    assert !pba.valid?
    assert_equal 1, pba.errors.length
    assert_not_nil pba.errors[:account]
    
    pba = PnlByAccount.new("toli money", @params, @suffix)
    assert pba.valid?
    pba.validate
    assert_equal 0, pba.errors.length
    assert_equal accounts(:toli_account), pba.account
  end
end