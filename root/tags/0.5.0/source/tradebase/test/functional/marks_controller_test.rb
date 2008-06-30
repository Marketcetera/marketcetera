require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'marks_controller'

# Re-raise errors caught by the controller.
class MarksController; def rescue_action(e) raise e end; end

class MarksControllerTest < MarketceteraTestBase
  fixtures :marks, :equities, :m_symbols, :currency_pairs
  include TradesHelper

  def setup
    @controller = MarksController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    @googEq     = equities(:GOOG)
    @zaiusd_4_13= marks(:zaiusd_4_13)
    @sunw_4_12  = marks(:sunw_4_12)
  end

  def test_index
    get :index
    assert_response :success
    assert_template 'index'
  end

  def test_by_symbol_no_symbol
    get :by_symbol

    assert_response :success
    assert_template 'index'

    assert_nil assigns(:marks)
    assert_has_error_box
    assert_not_nil assigns(:report).errors[:symbol]
  end

  # both from and to dates should be Date.today
  def test_by_symbol_no_from_to_dates
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, :security_type => TradesHelper::SecurityTypeEquity}

    assert_response :success
    assert_template 'index'

    assert_nil assigns(:marks)
    assert_has_error_box
    
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:to_date]
  end

  def test_by_symbol_invalid_dates
    # test when dates are invalid
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, :security_type => TradesHelper::SecurityTypeEquity,
                     :date_ => {"to(1i)"=>"2008", "to(2i)"=>"4", "to(3i)"=>"32", 
                               "from(1i)"=>"2008", "from(2i)"=>"4", "from(3i)"=>"32"}}

    assert_response :success
    assert_template 'index'
    assert_nil assigns(:marks)
    assert_has_error_box
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:to_date]
    
    # now try a when from date is later than to_date
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, :security_type => TradesHelper::SecurityTypeEquity,
                     :date_ => {"to(1i)"=>"2006", "to(2i)"=>"10", "to(3i)"=>"20", 
                               "from(1i)"=>"2008", "from(2i)"=>"4", "from(3i)"=>"11"}}
    assert_response :success               
    assert_nil assigns(:marks)
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:from_date].match("is later than")
    assert_has_error_box
  end
  
    def test_by_symbol_with_dates
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, :security_type => TradesHelper::SecurityTypeEquity,
                     :date_ => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20", 
                               "from(1i)"=>"2006", "from(2i)"=>"10", "from(3i)"=>"20"}}

    assert_response :success
    assert_template 'list_by_symbol'
    assert_not_nil assigns(:marks)
    assert_nil flash[:error]
    
    assert_equal Date.new(2006, 10, 20).to_s, assigns(:from_date).to_s
    assert_equal Date.new(2008, 10, 20).to_s, assigns(:to_date).to_s
    
    # should find 3 GOOG entries
    assert_equal 3, assigns(:marks).length
    assert_has_show_edit_delete_links(true, true, true)
    
    # now try a date in the past, should get 0
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, :security_type => TradesHelper::SecurityTypeEquity,
                     :date_ => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20", 
                               "from(1i)"=>"2008", "from(2i)"=>"10", "from(3i)"=>"20"}}
    assert_response :success               
    assert_equal 0, assigns(:marks).length
    assert_tag :tag => 'div', :attributes => {:id => "error_notice"}
  end

  # both from and to dates should be Date.today
  def test_by_symbol_no_marks_found
    get :by_symbol, {:m_symbol => {:root => "DNE"}, :security_type => TradesHelper::SecurityTypeEquity,
                     :date_ => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20", 
                           "from(1i)"=>"2008", "from(2i)"=>"10", "from(3i)"=>"20"}}
    assert_response :success
    assert_template 'list_by_symbol'

    assert_not_nil assigns(:marks)
    assert_has_error_notice
    
    # should find no entries
    assert_equal 0, assigns(:marks).length
  end

  def test_by_symbol_forex
    assert_not_nil CurrencyPair.get_currency_pair("ZAI/USD", false)
    get :by_symbol, {:m_symbol => {:root => "ZAI/USD"}, :security_type => TradesHelper::SecurityTypeForex,
                     :date_ => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20",
                               "from(1i)"=>"2006", "from(2i)"=>"10", "from(3i)"=>"20"}}

    assert_response :success
    assert_template 'list_by_symbol'
    assert_not_nil assigns(:marks)
    assert_nil flash[:error]

    # should find 1 ZAI/USD entry
    assert_equal 1, assigns(:marks).length
    assert_equal assigns(:marks)[0].tradeable_m_symbol_root, "ZAI/USD"
    assert_has_show_edit_delete_links(true, true, true)
  end

  # 2007/4/13 should have 3 marks: goog, sunw, beer, zai/usd
  def test_on_date
    get :on_date, :date => {"on(1i)"=>"2007", "on(2i)"=>"4", "on(3i)"=>"13"}
    
    assert_response :success
    assert_template 'list_on_date'

    assert_not_nil assigns(:marks)
    assert_nil flash[:error]
    
    # should find 3 entries
    assert_equal 4, assigns(:marks).length
    assert_equal marks(:goog_4_13), assigns(:marks)[0]
    assert_equal marks(:sunw_4_13), assigns(:marks)[1]
    assert_equal marks(:beer_4_13), assigns(:marks)[2]
    assert_equal marks(:zaiusd_4_13), assigns(:marks)[3]
    assert_equal assigns(:marks)[3].class.to_s, "ForexMark"
    assert_has_show_edit_delete_links(true, true, true)
  end

  def test_on_date_none_found
    get :on_date, :date => {"on(1i)"=>"2006", "on(2i)"=>"4", "on(3i)"=>"13"}
    
    assert_response :success
    assert_template 'list_on_date'

    assert_not_nil assigns(:marks)
    assert_has_error_notice
    
    # should find 0 entries
    assert_equal 0, assigns(:marks).length
    assert_equal Date.new(2006, 4, 13), assigns(:on_date)
  end

  def test_on_date_no_args
    get :on_date
    
    assert_response :success
    assert_template 'index'

    assert_nil assigns(:marks)
    assert_has_error_box
    assert_not_nil assigns(:report).errors[:on_date]
  end

  def test_show
    get :show, :id => @sunw_4_12

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:mark)
    assert assigns(:mark).valid?
  end

  def test_by_symbol_inavalid_currency
    get :by_symbol, {:m_symbol => {:root => "DNE"}, :security_type => TradesHelper::SecurityTypeForex,
                     :date_ => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20",
                           "from(1i)"=>"2008", "from(2i)"=>"10", "from(3i)"=>"20"}}
    assert_response :success
    assert_template 'index'

    assert_nil assigns(:marks)
    assert_has_error_box
    assert_not_nil assigns(:report).errors[:currency_pair]
    assert_not_nil assigns(:report).errors[:currency_pair].match("DNE")
  end

  def test_by_symbol_currency_not_found
    get :by_symbol, {:m_symbol => {:root => "XYZ/BSD"}, :security_type => TradesHelper::SecurityTypeForex,
                     :date_ => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20",
                           "from(1i)"=>"2008", "from(2i)"=>"10", "from(3i)"=>"20"}}
    assert_response :success
    assert_template 'list_by_symbol'

    assert_not_nil assigns(:marks)
    assert_has_error_notice
    assert_equal assigns(:marks).length, 0
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:mark)
  end

  def test_create
    num_marks = Mark.count

    post :create, { :mark => {:mark_value => "10.20", :mark_date => Date.today}, :m_symbol =>{ :root =>"fred"},
                    :security_type => TradesHelper::SecurityTypeEquity}

    assert_response :redirect
    assert_redirected_to :action => 'by_symbol'


    assert_nil assigns(:report) # shouldn't have any errors on redirection'
    assert_no_tag :tag => 'div', :attributes => {:class => "errorExplanation"}
    assert_equal num_marks + 1, Mark.count
  end

  # creates a mark for currency that doesn't exist upfront - but underlying currencies do exist
  def test_create_forex
    num_marks = Mark.count

    post :create, { :mark => {:mark_value => "1.234", :mark_date => Date.today}, :m_symbol =>{ :root =>"USD/ZAI"},
            :security_type => TradesHelper::SecurityTypeForex }

    assert_response :redirect
    assert_redirected_to :action => 'by_symbol'


    assert_nil assigns(:report) # shouldn't have any errors on redirection'
    assert_no_tag :tag => 'div', :attributes => {:class => "errorExplanation"}
    assert_equal num_marks + 1, Mark.count
  end

  def test_create_already_exists
    sunw4_12 = marks(:sunw_4_12)
    num_marks = Mark.count

    post :create, { :mark => {:mark_value => "10.20", :mark_date => sunw4_12.mark_date}, 
                    :m_symbol =>{ :root => sunw4_12.tradeable.m_symbol.root} }

    assert_response :success
    assert :template => 'create'
    assert :action => 'new'
    assert_has_error_box
    assert_equal num_marks, Mark.count
  end
  
  def test_create_future_date
    post :create, { :mark => {:mark_value => "10.20", :mark_date => Date.today+10},
                    :security_type => TradesHelper::SecurityTypeEquity, 
                    :m_symbol =>{ :root => @sunw_4_12.tradeable.m_symbol.root} }
    assert_response :success
    assert_has_error_box
    assert_equal 1, assigns(:mark).errors.length
    assert_not_nil assigns(:mark).errors[:mark_date]   
    assert_equal "should not be in the future.", assigns(:mark).errors[:mark_date]             
  end

  # the currency pair can't be created b/c underlying currencie's don't exist
  def test_create_forex_unknown_currency_pair
    num_marks = Mark.count

    post :create, { :mark => {:mark_value => "1.234", :mark_date => Date.today}, :m_symbol =>{ :root =>"XYZ/BOB"},
            :security_type => TradesHelper::SecurityTypeForex }

    assert :template => 'create'
    assert :action => 'new'
    assert_has_error_box

    assert_nil assigns(:report)
    assert_not_nil assigns(:mark).errors[:symbol]
    assert_not_nil assigns(:mark).errors[:symbol].match("XYZ/BOB")
    assert_equal num_marks, Mark.count
  end

  # USD/ZAI doesn't exist, but we should be able to create mark for it since we have hte underlying currencies
  def test_create_currency_pair_illegal
    num_marks = Mark.count

    post :create, { :mark => {:mark_value => "1.234", :mark_date => Date.today}, :m_symbol =>{ :root =>"ZAI"},
            :security_type => TradesHelper::SecurityTypeForex }

    assert :action => 'new'
    assert :template => 'create'

    assert_nil assigns(:report)
    assert_not_nil assigns(:mark).errors[:symbol]
    assert_not_nil assigns(:mark).errors[:symbol].match("ZAI")
    assert_equal num_marks, Mark.count
  end

  def test_edit
    get :edit, :id => @sunw_4_12

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:mark)
    assert assigns(:mark).valid?
  end

  def test_update_no_edits
    post :update, :id => @sunw_4_12

    assert_response :redirect
    assert_redirected_to :action => 'show', :id => @sunw_4_12
    assert "Mark was successfully updated.", flash[:notice] 
  end

  def test_destroy
    assert_nothing_raised {
      Mark.find(@sunw_4_12)
    }

    post :destroy, :id => @sunw_4_12
    assert_response :redirect
    assert_redirected_to :action => 'index'

    assert_no_errors
    assert_equal "Mark was successfully deleted.", flash[:notice]

    assert_raise(ActiveRecord::RecordNotFound) {
      Mark.find(@sunw_4_12)
    }
  end

  def test_new_has_security_type_radio_buttons
    get :new
    assert_template 'new'
    assert_tag :tag => 'input', :attributes => {:class => 'radio'}
  end

  def test_edit_page_security_type_disabled
    get :edit, :id => @sunw_4_12
    assert_response :success
    assert_template 'edit'
    # shouldn't have any radio buttons
    assert_no_tag :tag => 'input', :attributes => {:class => 'radio'}
    assert_tag :tag => 'div', :attributes => {:id => "asset_type"}, :content => "Equity"

    get :edit, :id => @zaiusd_4_13
    assert_response :success
    assert_template 'edit'
    # shouldn't have any radio buttons
    assert_no_tag :tag => 'input', :attributes => {:class => 'radio'}
    assert_tag :tag => 'div', :attributes => {:id => 'asset_type'}, :content => 'CurrencyPair'
  end

  # verify that passing the info to new_missing action correctly renders the mark
  def test_new_missing_equity
    get :new_missing,  {"mark_date"=>"2007-07-11", "tradeable_type"=>"Equity", "tradeable_id"=> @googEq.id}
    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:mark)
    m = assigns(:mark)
    assert_equal m.mark_date, Date.civil(2007, 7, 11)
    assert_equal m.tradeable_m_symbol_root, @googEq.m_symbol_root
    assert_equal "Equity", m.tradeable_type

    # make sure equity shows up as selected security type and forex is not checked as well
    assert_tag :tag => 'input', :attributes => {:checked => "checked", :type => "radio", :id => 'security_type_e'}
    assert_no_tag :tag => 'input', :attributes => {:checked => "checked", :type => "radio", :id => 'security_type_f'}
  end

  def test_new_missing_forex
    get :new_missing,  {"mark_date"=>"2007-07-11", :tradeable_type =>"CurrencyPair", "tradeable_id"=> @zaiusd_4_13.tradeable_id}
    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:mark)
    m = assigns(:mark)
    assert_equal m.mark_date, Date.civil(2007, 7, 11)
    assert_equal m.tradeable_m_symbol_root, @zaiusd_4_13.tradeable_m_symbol_root
    assert_equal "CurrencyPair", m.tradeable_type

    # make sure forex shows up as selected security type and equity is not checked as well
    assert_tag :tag => 'input', :attributes => {:checked => "checked", :type => "radio", :id => 'security_type_f'}
    assert_no_tag :tag => 'input', :attributes => {:checked => "checked", :type => "radio", :id => 'security_type_e'}
  end
end
