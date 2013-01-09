require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class MarkTest < MarketceteraTestBase
  fixtures :marks, :equities, :m_symbols

  def setup
    @ifli = Equity.get_equity("ifli")
  end 
  
  def test_modification_existing
    mark = Mark.find(1)
    oldVal = mark.mark_value
    mark.mark_value = "123.45"
    mark.save
    
    assert_equal 123.45, mark.mark_value
    assert_not_equal oldVal, mark.mark_value
  end
  
  def test_negative_amount_validation
    mark = Mark.new(:tradeable => @ifli, :mark_value => "-10.23", :mark_date => Date.today)
    assert_equal 0, mark.errors.length
    mark.save  
    assert_equal 1, mark.errors.length, "accepted a negative amount: #{mark.pretty_print_errors}"
    assert_equal "should be a zero or positive value.", mark.errors[:mark_value]
  end
  
  def test_validation
    mark = Mark.new
    assert !mark.valid?
    # we get 2 errors for value
    assert_equal 4, mark.errors.length, "doesn't contain value and date and symbol validation"
    assert_not_nil mark.errors[:symbol]
    assert_not_nil mark.errors[:mark_value]
    assert_not_nil mark.errors[:mark_date]
    
    mark.tradeable = @ifli
    assert !mark.valid?
    assert_equal 3, mark.errors.length
    assert_nil mark.errors[:symbol]
    assert_not_nil mark.errors[:mark_value]  # 2 errors
    assert_not_nil mark.errors[:mark_date]
    
    mark.mark_value = -23
    assert !mark.valid?
    assert_equal 2, mark.errors.length
    assert_nil mark.errors[:symbol]
    assert_not_nil mark.errors[:mark_value]
    assert_not_nil mark.errors[:mark_date]

    mark.mark_value = 23
    assert !mark.valid?
    assert_equal 1, mark.errors.length
    assert_not_nil mark.errors[:mark_date]

    mark.mark_date = Date.today + 10
    assert !mark.valid?, "mark created in future"
    assert_equal 1, mark.errors.length
    assert_not_nil mark.errors[:mark_date]

    mark.mark_date = Date.today-1
    assert mark.valid?
    assert_nil mark.errors[:mark_date]
  end
  
  def test_symbol_presense_validation
    mark = Mark.new
    mark.tradeable = Equity.new
    assert !mark.valid?
    assert_not_nil mark.errors[:symbol]
    
    # test validation fails on blank equity root
    blankE = Equity.get_equity('')
    mark.tradeable = blankE
    assert !mark.valid?
    assert_not_nil mark.errors[:symbol]
  end
  
  def test_numericality_validation
    mark = Mark.new
    mark.tradeable = @ifli
    mark.mark_value = "abcd"
    mark.mark_date = Date.today
    
    mark.save
    assert_equal 1, mark.errors.length, "accepted non-numeric"
  end
  
  def test_numericality_validation_string_number
    mark = Mark.new
    mark.tradeable = @ifli
    mark.mark_value = "20"
    mark.mark_date = Date.today
    
    mark.save
    assert_equal 0, mark.errors.length, "didn't accept string number"
  end
  
  def test_zero_amount
    mark = Mark.new
    mark.tradeable = @ifli
    mark.mark_value = 0
    mark.mark_date = Date.today
    
    mark.save
    assert_equal 0, mark.errors.length, "didn't accept zero"
  end
  def test_non_integer
    mark = Mark.new()
    mark.tradeable = @ifli
    mark.mark_value = 23.42
    mark.mark_date = Date.today
    
    mark.save
    assert_equal 0, mark.errors.length, "didn't accept non-integer"
  end
  
  def test_tradeable_m_symbol_root
    mark = Mark.find(:first)
    assert_not_nil mark.tradeable_m_symbol_root
    assert_equal mark.tradeable.m_symbol.root, mark.tradeable_m_symbol_root
    
    # null
    mark = Mark.new
    assert_nil mark.tradeable_m_symbol_root 
    
    mark.tradeable = Equity.new
    assert_nil mark.tradeable_m_symbol_root 
    
    mark.tradeable = @ifli
    assert_not_nil mark.tradeable_m_symbol_root
    
    @ifli.m_symbol = nil
    assert_nil mark.tradeable_m_symbol_root 
  end
  
  # test duplicate marks on same day
  def test_marks_on_same_date
    mark = Mark.find(:first)
    newMark = Mark.new(:tradeable => mark.tradeable, :mark_date => mark.mark_date)
    newMark.save
    assert_not_nil newMark.errors[:mark_date]
    assert_equal "Already have a mark on that date. Please update an existing mark instead.", newMark.errors[:mark_date]
  end

  def test_pretty_print_errors
    mark = Mark.new(:tradeable => @ifli, :mark_date => (Date.today + 2))
    mark.save
    assert !mark.valid?
    assert_equal 3, mark.errors.length, mark.errors.inspect
    assert_equal "{Value should be a number. Date should not be in the future. }", mark.pretty_print_errors
  end

end
