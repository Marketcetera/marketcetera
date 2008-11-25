require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'equities_controller'

# Re-raise errors caught by the controller.
class EquitiesController; def rescue_action(e) raise e end; end

class EquitiesControllerTest < MarketceteraTestBase
  fixtures :equities

  def setup
    @controller = EquitiesController.new
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

    assert_not_nil assigns(:equities)
    assert_equal 4, assigns(:equities).length
    assert_has_show_edit_delete_links(true, true, true)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:equity)
    assert assigns(:equity).valid?
    
    assert_select "fieldset div a", Equity.find(1).m_symbol_root
    assert_select "fieldset div div", Equity.find(1).description
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:equity)
  end

  def test_create_no_args
    num_equities = Equity.count

    post :create, :equity => {}

    assert_template 'new'
    assert_equal 1, assigns(:equity).errors.length, "number of validation errors"
    assert_not_nil assigns(:equity).errors[:m_symbol_id]

    assert_equal num_equities, Equity.count
  end
  
  # after bug 62 we should fix this to look for a different error message
  def test_create_no_such_symbol_no_force_create
    num_equities = Equity.count
    assert_nil Equity.get_equity("DNE", false)
    post :create, { :m_symbol =>{:root => "DNE"}, :equity =>{ :description =>"does not exist"}}

    assert_template 'new'
    assert_equal 1, assigns(:equity).errors.length, "number of validation errors"
    assert_not_nil assigns(:equity).errors[:m_symbol_id]
    assert_select "form div ul li", /[a-zA-Z ]*Symbol cannot be empty/

    assert_equal num_equities, Equity.count
  end
  
  def test_create_duplicate_equity 
    num_equities = Equity.count
    dupe = equities(:SUNW)
    post :create, { :m_symbol =>{:root => dupe.m_symbol_root}, :equity =>{ :description =>"dupe"}}

    assert_template 'new'
    assert_equal 1, assigns(:equity).errors.length, "number of validation errors"
    assert_not_nil assigns(:equity).errors[:m_symbol_id]
    assert_equal "Equity with that symbol already exists", assigns(:equity).errors[:m_symbol_id]
    assert_has_error_box
    assert_select "form div ul li", /[a-zA-Z ]*Equity with that symbol already exists/

    assert_equal num_equities, Equity.count
  end
  
  def test_create_successful_force_symbol
    num_equities = Equity.count
    assert_nil Equity.get_equity("baba", false)
    
    post :create, { :m_symbol =>{:root => "baba"}, :equity =>{ :description =>"dura"}, :create_new=>"1"}


    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_equities + 1, Equity.count
    eq = Equity.get_equity("baba", false)
    assert_not_nil eq
    assert_equal "baba", eq.m_symbol_root
    assert_equal "dura", eq.description
  end

  def test_create_successful_no_force_symbol
    num_equities = Equity.count
    ifli = MSymbol.create(:root => "IFLI")
    assert_not_nil ifli.id, "couldn't create symbol IFLI"
    assert_not_nil MSymbol.find_by_root("IFLI")
    
    post :create, { :m_symbol =>{:root => ifli.root}, :equity =>{ :description =>"shmifli"}}

    assert_equal num_equities + 1, Equity.count
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_equities + 1, Equity.count
    eq = Equity.get_equity("IFLI", false)
    assert_not_nil eq
    assert_equal "IFLI", eq.m_symbol_root
    assert_equal "shmifli", eq.description
  end

  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:equity)
    assert assigns(:equity).valid?
  end

  def test_update_no_changes
    orig = Equity.find(1)
    post :update, { :id => 1, :equity => orig.attributes,  :m_symbol => {:root => orig.m_symbol_root } } 
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
    assert flash[:notice] = "Equity was successfully updated."
  end

  def test_update_description
    orig = Equity.find(1)
    post :update, { :id => 1, :m_symbol => {:root => orig.m_symbol_root },
                    :equity => orig.attributes.merge( {"description" => "old desc sucked" })  } 
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
    
    assert_equal "old desc sucked", Equity.find(1).description
  end

  def test_update_empty_symbol
    orig = Equity.find(1)
    post :update, { :id => 1, :equity => orig.attributes, :m_symbol => {:root => "" }  } 
    assert_template 'edit'
    assert_equal 1, assigns(:equity).errors.length, "number of validation errors"
    assert_not_nil assigns(:equity).errors[:m_symbol_id]
    assert_select "form div ul li", /[a-zA-Z ]*Symbol cannot be empty/
  end
  def test_update_duplicate
    orig = Equity.find(1)
    dupe = Equity.find(2)
    post :update, { :id => 1, :equity => orig.attributes, :m_symbol => {:root => dupe.m_symbol_root }  } 
    assert_template 'edit'
    assert_equal 1, assigns(:equity).errors.length, "number of validation errors"
    assert_not_nil assigns(:equity).errors[:m_symbol_id]
    assert_select "form div ul li", /[a-zA-Z ]*Equity with that symbol already exists/
  end

  def test_destroy
    assert_not_nil Equity.find(1)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      Equity.find(1)
    }
  end
end
