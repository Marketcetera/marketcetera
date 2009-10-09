require File.dirname(__FILE__) + '/../test_helper'
require 'currency_pairs_controller'

# Re-raise errors caught by the controller.
class CurrencyPairsController; def rescue_action(e) raise e end; end

class CurrencyPairsControllerTest < Test::Unit::TestCase
  fixtures :currency_pairs
  fixtures :currencies

  def setup
    @controller = CurrencyPairsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new

    @first_id = currency_pairs(:GBPUSD).id
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

    assert_not_nil assigns(:currency_pairs)
  end

  def test_list_new_currency
    post :create, {:first_currency => {:alpha_code => "USD"}, :second_currency => {:alpha_code => "ZAI" },
            :currency_pair => {:description => "zaichiki" }}

    get :list
    assert_tag :tag => "td", :content => "zaichiki"
  end

  def test_show
    get :show, :id => @first_id

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:currency_pair)
    assert assigns(:currency_pair).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:currency_pair)
  end

  def test_create
    num_currency_pairs = CurrencyPair.count

    post :create, {:first_currency => {:alpha_code => "USD"}, :second_currency => {:alpha_code => "ZAI" },
            :currency_pair => {:description => "zaichiki" }}

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_not_nil flash[:notice].match("USD/ZAI")
    assert_equal num_currency_pairs + 1, CurrencyPair.count

    usd_zai = CurrencyPair.get_currency_pair("USD/ZAI")
    assert_not_nil usd_zai
    assert_equal "zaichiki", usd_zai.description
  end

  def test_edit
    get :edit, :id => @first_id

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:currency_pair)
    assert assigns(:currency_pair).valid?
  end

  def test_update
    post :update, :id => @first_id
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => @first_id
  end

  def test_destroy
    assert_nothing_raised {
      CurrencyPair.find(@first_id)
    }

    post :destroy, :id => @first_id
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      CurrencyPair.find(@first_id)
    }
  end
end
