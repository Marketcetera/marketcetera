require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'dividends_controller'

# Re-raise errors caught by the controller.
class DividendsController; def rescue_action(e) raise e end; end

class DividendsControllerTest < MarketceteraTestBase
  fixtures :dividends, :currencies, :m_symbols, :equities
  include ApplicationHelper
  
  def setup
    @controller = DividendsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_index
    get :index
    assert_response :success
    assert_template 'list'
  end

  def test_list
    get :list

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:dividends)
    assert_has_show_edit_delete_links(true, true, true)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:dividend)
    assert assigns(:dividend).valid?
    
    assert_select "body h1", /[a-zA-Z ]*TOLI/
    assert_tag  :tag => 'div', :attributes => {:id => 'description', :class => "data view_data"}, :content=> 'toli dividend'
    assert_tag  :tag => "div", :attributes => {:id => "amount"}, :content=> "20.0"
    assert_tag  :tag => "div", :attributes => {:id => "currency"}, :content=>'USD'
    
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:dividend)
  end

  def test_create_no_args
    num_dividends = Dividend.count

    post :create, :dividend => {}
  
    assert_template 'new'
    
    assert_tag  :input, :attributes => { :id => 'currency_alpha_code', :value => 'USD' }
    
    assert_equal 2, assigns(:dividend).errors.length, "number of validation errors"
    assert_not_nil assigns(:dividend).errors[:symbol]
    assert_not_nil assigns(:dividend).errors[:amount]
    assert_equal num_dividends, Dividend.count
  end
  
  # almost same as above, but we simulate just clicking new and pressing enter
  # on the webpage - ie specifying dates, sending nulls for symbol/amount/etc
  def test_create_no_entered_args
    num_dividends = Dividend.count

    post :create, :params => { :m_symbol => {:root=>""}, :currency =>{:alpha_code =>""}, 
            :dividend => {"status"=>"", "payable_date(1i)"=>"2006", "announce_date(1i)"=>"2006", "announce_date(2i)"=>"10", 
            "payable_date(2i)"=>"10", "ex_date(1i)"=>"2006", "payable_date(3i)"=>"24", "announce_date(3i)"=>"24", 
            "ex_date(2i)"=>"10", "ex_date(3i)"=>"24", "description"=>"", "amount"=>""} }

    assert_template 'new'
    
    assert_tag  :input, :attributes => { :id => 'currency_alpha_code', :value => 'USD' }
    
    assert_equal 2, assigns(:dividend).errors.length, "number of validation errors"
    assert_not_nil assigns(:dividend).errors[:symbol]
    assert_not_nil assigns(:dividend).errors[:amount]
    assert_equal num_dividends, Dividend.count
  end
  
  def test_create_no_symbol
    num_dividends = Dividend.count

    post :create, { :m_symbol =>{ :root=>""}, :currency =>{ :alpha_code =>""}, 
         :dividend => {:status =>"", :description=>"", :amount =>"23",
         "payable_date(1i)"=>"2006", "payable_date(2i)"=>"10", "payable_date(3i)"=>"24", 
         "announce_date(1i)"=>"2006", "announce_date(2i)"=>"10", "announce_date(3i)"=>"24", 
          "ex_date(1i)"=>"2006", "ex_date(2i)"=>"10", "ex_date(3i)"=>"24"}}

    assert_template 'new'
    
    assert_tag  :input, :attributes => { :id => 'currency_alpha_code', :value => 'USD' }
    assert_equal 1, assigns(:dividend).errors.length, 
      "number of validation errors: "+collect_errors_into_string(assigns(:dividend).errors)
    assert_not_nil assigns(:dividend).errors[:symbol]
    assert_tag :input, :attributes => { :id => 'dividend_amount', :value => '23'}
    assert_equal num_dividends, Dividend.count
  end

  def test_create_no_amount
    num_dividends = Dividend.count

    post :create, {"m_symbol"=>{"root"=>"abc"}, 
         :dividend =>{"status"=>"", "payable_date(1i)"=>"2006", "announce_date(1i)"=>"2006", "announce_date(2i)"=>"10", 
                       "payable_date(2i)"=>"10", "ex_date(1i)"=>"2006", "payable_date(3i)"=>"24", "announce_date(3i)"=>"24", 
                       "ex_date(2i)"=>"10", "ex_date(3i)"=>"24", "description"=>"", "amount"=>""}, 
         "currency"=>{"alpha_code"=>"ZAI"}}

    assert_template 'new'
    
    assert_tag  :input, :attributes => { :id => 'currency_alpha_code', :value => 'ZAI' }
    assert_equal 1, assigns(:dividend).errors.length, "number of validation errors"
    assert_not_nil assigns(:dividend).errors[:amount]
    assert_tag :input, :attributes => { :id => 'currency_alpha_code', :value => 'ZAI'}
    assert_tag :input, :attributes => { :id => 'm_symbol_root', :value => 'abc'}
    assert_equal num_dividends, Dividend.count
  end

  def test_create_invalid_currency_fails
    num_dividends = Dividend.count
    assert_nil Currency.find_by_alpha_code("XXX")
    
    post :create, {"m_symbol"=>{"root"=>"abc"}, 
         :dividend =>{:status=>"", :amount => "23", "payable_date(1i)"=>"2006", "announce_date(1i)"=>"2006", "announce_date(2i)"=>"10", 
                       "payable_date(2i)"=>"10", "ex_date(1i)"=>"2006", "payable_date(3i)"=>"24", "announce_date(3i)"=>"24", 
                       "ex_date(2i)"=>"10", "ex_date(3i)"=>"24", "description"=>""}, 
         "currency"=>{"alpha_code"=>"XXX"}}

    assert_template 'new'
    assert_has_error_box
    
    assert_equal 1, assigns(:dividend).errors.length, "number of validation errors: " + 
                                                      collect_errors_into_string(assigns(:dividend).errors)
    assert_not_nil assigns(:dividend).errors[:currency]
    assert_equal num_dividends, Dividend.count
  end

  def test_create_successful
    num_dividends = Dividend.count

    post :create, { :m_symbol =>{ :root=>"IFLI"}, :currency =>{ :alpha_code =>"ZAI"}, 
         :dividend => {:status =>"E", :description=>"ifli rocks", :amount =>"23",
         "payable_date(1i)"=>"2006", "payable_date(2i)"=>"7", "payable_date(3i)"=>"8", 
         "announce_date(1i)"=>"2006", "announce_date(2i)"=>"9", "announce_date(3i)"=>"10", 
          "ex_date(1i)"=>"2006", "ex_date(2i)"=>"11", "ex_date(3i)"=>"12"}}

    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_dividends + 1, Dividend.count
    ifli = Dividend.find(:all)[Dividend.count-1]
    
    verify_dividend_value(ifli, "IFLI", "ZAI", 23, Date.civil(2006, 9, 10), Date.civil(2006, 11, 12),  
                          Date.civil(2006, 7, 8), "E", "ifli rocks")
  end

  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:dividend)
    assert assigns(:dividend).valid?
  end

  def test_edit_currency_value_shows
    get :edit, :id => dividends(:sunw_div).id
    
    assert_response :success
    assert_template 'edit'

    assert_tag  :input, :attributes => { :id => 'currency_alpha_code', :value => 'ZAI' }
  end

  def test_update_no_actual_edits
    origDiv = dividends(:toli_div)
    post :update, { :id =>  origDiv.id, 
                    :m_symbol =>{ :root=> origDiv.equity_m_symbol_root }, 
                    :currency =>{ :alpha_code => origDiv.currency_alpha_code}, 
                    :dividend => origDiv.attributes }

    assert_response :redirect
    assert_redirected_to :action => 'show', :id => origDiv.id
    
    # verify all parts of dividend
    compare_dividends(origDiv, Dividend.find(origDiv.id))
  end

  def test_update_amount
    origDiv = dividends(:toli_div)
    post :update, { :id =>  origDiv.id, 
                    :m_symbol =>{ :root=> origDiv.equity_m_symbol_root }, 
                    :currency =>{ :alpha_code => origDiv.currency_alpha_code}, 
                    :dividend => origDiv.attributes.merge({ "amount" => "0.49" }) }

    assert_response :redirect
    assert_redirected_to :action => 'show', :id => origDiv.id
    
    # verify all parts of dividend
    assert_nums_equal 0.49, Dividend.find(origDiv.id).amount
  end

  def test_update_everything
    origDiv = dividends(:toli_div)
    post :update, { :id =>  origDiv.id, 
                    :m_symbol =>{ :root=> "ABC" }, 
                    :currency =>{ :alpha_code => "ZAI"}, 
                    :dividend => origDiv.attributes.merge({ "amount" => "0.49" , 
                         "status" =>"E", "description"=>"ifli rocks", 
                         "payable_date(1i)"=>"2006", "payable_date(2i)"=>"7", "payable_date(3i)"=>"8", 
                         "announce_date(1i)"=>"2006", "announce_date(2i)"=>"9", "announce_date(3i)"=>"10", 
                         "ex_date(1i)"=>"2006", "ex_date(2i)"=>"11", "ex_date(3i)"=>"12"}) }

    assert_response :redirect
    assert_redirected_to :action => 'show', :id => origDiv.id
    
    # verify all parts of dividend
    verify_dividend_value(Dividend.find(origDiv.id), "ABC", "ZAI", 0.49, Date.civil(2006, 9, 10), Date.civil(2006, 11, 12),  
                          Date.civil(2006, 7, 8), "E", "ifli rocks")
  end

  def test_destroy
    assert_not_nil Dividend.find(1)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      Dividend.find(1)
    }
  end
  
  def compare_dividends(origDiv, newDiv)
    verify_dividend_value(newDiv, origDiv.equity_m_symbol_root, origDiv.currency_alpha_code, origDiv.amount, 
        origDiv.announce_date, origDiv.ex_date, origDiv.payable_date, origDiv.status, origDiv.description)
  end
  
  def verify_dividend_value(div, symbol, currency, amount, announceDate, exDate, payableDate, status, description)
    assert_equal symbol, div.equity_m_symbol_root, "symbol"
    assert_equal status, div.status, "status"
    assert_equal description, div.description, "description"
    assert_equal currency, div.currency_alpha_code, "currency"
    assert_equal payableDate, div.payable_date, "payable date"
    assert_equal announceDate, div.announce_date, "announce date"
    assert_equal exDate, div.ex_date, "ex date"
    assert_nums_equal amount, div.amount, "amount"
  end
end
