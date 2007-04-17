require File.dirname(__FILE__) + '/../test_helper'
require 'marks_controller'

# Re-raise errors caught by the controller.
class MarksController; def rescue_action(e) raise e end; end

class MarksControllerTest < Test::Unit::TestCase
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
    
    # now try a date in the past, should get 0
    get :by_symbol, {:m_symbol => {:root => @googEq.m_symbol.root}, 
                     :date => {"to(1i)"=>"2008", "to(2i)"=>"10", "to(3i)"=>"20", 
                               "from(1i)"=>"2008", "from(2i)"=>"10", "from(3i)"=>"20"}}
    assert_response :success               
    assert_equal 0, assigns(:marks).length
  end

  # both from and to dates should be Date.today
  def test_by_symbol_no_marks_found
    get :by_symbol, {:m_symbol => {:root => "DNE"}}

    assert_response :success
    assert_template 'list_by_symbol'

    assert_not_nil assigns(:marks)
    assert_nil flash[:error]
    
    # should find 3 GOOG entries
    assert_equal 0, assigns(:marks).length
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
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      Mark.find(@sunw_4_12)
    }
  end
end
