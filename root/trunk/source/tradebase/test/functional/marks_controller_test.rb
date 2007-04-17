require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'marks_controller'

# Re-raise errors caught by the controller.
class MarksController; def rescue_action(e) raise e end; end

class MarksControllerTest < MarketceteraTestBase
  fixtures :marks, :equities, :m_symbols

  def setup
    @controller = MarksController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    @googEq     = equities(:GOOG)
    @sunw_4_12  = marks(:sunw_4_12)
  end

  def test_index
    get :index
    assert_response :success
    assert_template 'index'
  end

  def test_by_symbol_no_symbol
    get :by_symbol

    assert_response :redirect
    assert_redirected_to :action => 'index', :controller => 'marks'

    assert_nil assigns(:marks)
    assert_not_nil flash[:error]
  end

  # both from and to dates should be Date.today
  def test_by_symbol_no_from_to_dates
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}}

    assert_response :success
    assert_template 'list_by_symbol'

    assert_not_nil assigns(:marks)
    assert_nil flash[:error]
    
    assert_nil assigns(:from_date)
    assert_equal Date.today, assigns(:to_date)
    
    # should find 3 GOOG entries
    assert_equal 3, assigns(:marks).length
  end

  def test_by_symbol_no_from_date
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, 
                     :date => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20"}}

    assert_response :success
    assert_template 'list_by_symbol'

    assert_not_nil assigns(:marks)
    assert_nil flash[:error]
    
    assert_nil assigns(:from_date)
    assert_equal Date.new(2008, 10, 20).to_s, assigns(:to_date).to_s
    
    # should find 3 GOOG entries
    assert_equal 3, assigns(:marks).length
    
    # now try a date in the past, should get 0
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, 
                     :date => {"to(1i)"=>"2006", "to(2i)"=>"10", "to(3i)"=>"20"}}
    assert_response :success               
    assert_equal 0, assigns(:marks).length
    assert_tag :tag => 'div', :attributes => {:id => "error_notice"}
  end
  
    def test_by_symbol_with_dates
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, 
                     :date => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20", 
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
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, 
                     :date => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20", 
                               "from(1i)"=>"2008", "from(2i)"=>"10", "from(3i)"=>"20"}}
    assert_response :success               
    assert_equal 0, assigns(:marks).length
    assert_tag :tag => 'div', :attributes => {:id => "error_notice"}
  end

  # both from and to dates should be Date.today
  def test_by_symbol_no_marks_found
    get :by_symbol, {:m_symbol => {:root => "DNE"}}

    assert_response :success
    assert_template 'list_by_symbol'

    assert_not_nil assigns(:marks)
    assert_has_error_notice
    
    # should find no entries
    assert_equal 0, assigns(:marks).length
  end

  # 2007/4/13 should have 3 marks: goog, sunw, beer
  def test_on_date
    get :on_date, :date => {"on(1i)"=>"2007", "on(2i)"=>"4", "on(3i)"=>"13"}
    
    assert_response :success
    assert_template 'list_on_date'

    assert_not_nil assigns(:marks)
    assert_nil flash[:error]
    
    # should find 3 entries
    assert_equal 3, assigns(:marks).length
    assert_equal marks(:goog_4_13), assigns(:marks)[0]
    assert_equal marks(:sunw_4_13), assigns(:marks)[1]
    assert_equal marks(:beer_4_13), assigns(:marks)[2]
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
    assert_template 'list_on_date'

    assert_not_nil assigns(:marks)
    assert_has_error_notice

    # should find 0 entries
    assert_equal 0, assigns(:marks).length
    assert_equal Date.today, assigns(:on_date)
  end

  def test_show
    get :show, :id => @sunw_4_12

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:mark)
    assert assigns(:mark).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:mark)
  end

  def test_create
    num_marks = Mark.count

    post :create, { :mark => {:mark_value => "10.20", :mark_date => Date.today}, :m_symbol =>{ :root =>"fred"} }

    assert_response :redirect
    assert_redirected_to :action => 'by_symbol'

    assert_equal num_marks + 1, Mark.count
  end

  def test_create_already_exists 
    sunw4_12 = marks(:sunw_4_12)
    num_marks = Mark.count

    post :create, { :mark => {:mark_value => "10.20", :mark_date => sunw4_12.mark_date}, 
                    :m_symbol =>{ :root => sunw4_12.equity.m_symbol.root} }

    assert_response :success
    assert :template => 'create'
    assert :action => 'new'
    assert_has_error_box
    assert_equal num_marks, Mark.count
  end
  
  def test_create_future_date
  
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
    assert_redirected_to :action => 'by_symbol'

    assert_raise(ActiveRecord::RecordNotFound) {
      Mark.find(@sunw_4_12)
    }
  end
end
