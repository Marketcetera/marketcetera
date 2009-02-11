require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class VDateTest < MarketceteraTestBase
  def test_get_date_from_params
    assert_equal Date.civil(2006, 7, 11), 
      VDate.get_date_from_params({"position"=>{"as_of(1i)"=>"2006", "as_of(2i)"=>"7", "as_of(3i)"=>"11"}}, 
                          "position", "as_of", "as_of_date").as_date
    assert_equal Date.civil(2006, 7, 11).to_s, 
                 VDate.get_date_from_params({ "as_of_date"=>"2006-7-11"}, "position", "as_of", "as_of_date").as_date.to_s
      
    assert_nil VDate.get_date_from_params({}, "position", "as_of", "as_of_date")
    assert_nil VDate.get_date_from_params({"position" => {} }, "position", "as_of", "as_of_date")
    
    # try specifying the object_name but not the full date
    assert_nil VDate.get_date_from_params({"position"=>{"unrelated"=>"data"}}, "position", "as_of", "as_of_date")
  end
  
  def test_validity
    assert !VDate.new(2007, 4, 31).valid?
  end
  
  def test_error_message
    begin
      vdate = VDate.new(2007, 4, 31)
      str = vdate.as_date    
      fail("should not be able to parse the date")
    rescue Exception => ex
      assert_equal "Invalid date: 2007-4-31", ex.message
    end
  end

  def test_to_s
      assert_equal "2007-4-33", VDate.new(2007,4,33).to_s
      assert_equal "2007-4-33", VDate.new("2007-4-33").to_s
  end

  def test_erro_message_single_str
    begin
      vdate = VDate.new("2007-4-31", nil, nil)
      str = vdate.as_date    
      fail("should not be able to parse the date")
    rescue Exception => ex
      assert_equal "Invalid date: 2007-4-31", ex.message
    end
  end
end