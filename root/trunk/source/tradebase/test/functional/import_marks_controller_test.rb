require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'import_marks_controller'

# Re-raise errors caught by the controller.
class ImportMarksController; def rescue_action(e) raise e end; end

class ImportMarksControllerTest < MarketceteraTestBase
  include ApplicationHelper
  def setup
    @controller = ImportMarksController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_upload
    nMarks = Mark.count
    data= "GOOG,100,07/01/2006\n"
    data += "GOOG,100,07/02/2006\n"
    data += "GOOG,100,07/03/2006\n"
    get :upload, { :import=>{:file=>StringIO.new(data)}}

    assert_response :success
    assert_template 'upload_results'
    assert_no_errors

    assert_equal 3+nMarks, Mark.count, "had #{nMarks} originally"
    assert_equal 0, assigns(:report).line_errors.length
    assert_equal 3, assigns(:total_count)
    assert_equal 3, assigns(:valid_marks)
  end

  # upload with bogus file / non-StringIO entry
  def test_upload_non_StringIO
    nMarks = Mark.count
    get :upload, { :import=>{:file=>BigDecimal.new("123")}}

    assert_response :success
    assert_template 'import'
    assert_has_error_box

    assert_equal nMarks, Mark.count, "had #{nMarks} originally"
    assert_equal 0, assigns(:report).line_errors.length
  end

  def test_paste
    nMarks = Mark.count
    data= "abc,100,07/01/2006\r\n"
    data += "abc,100,07/02/2006\r\n"
    data += "abc,100,07/03/2006\r\n"
    get :paste, { :import=>{:text=>data}}

    assert_response :success
    assert_template 'upload_results'
    assert_no_errors

    assert_equal 3+nMarks, Mark.count, "had #{nMarks} originally"
    assert_equal 0, assigns(:report).line_errors.length
    assert_equal 3, assigns(:total_count)
    assert_equal 3, assigns(:valid_marks)
  end

  def test_paste_empty_str
    nMarks = Mark.count
    get :paste, { :import=>{:text=>""}}

    assert_response :success
    assert_template 'upload_results'
    assert_no_errors

    assert_equal nMarks, Mark.count, "had #{nMarks} originally"
    assert_equal 0, assigns(:report).line_errors.length
    assert_equal 0, assigns(:total_count)
    assert_equal 0, assigns(:valid_marks)
  end

  def test_paste_duplicate_mark
    def test_paste_empty_str
      nMarks = Mark.count
      data = "abc,100,07/01/2006\r\n"
      data += "abc,100,07/01/2006\r\n"
      get :paste, { :import=>{:text => data}}

      assert_response :success
      assert_template 'upload_results'
      assert_no_errors

      assert_equal nMarks+1, Mark.count, "had #{nMarks} originally"
      assert_equal 1, assigns(:report).line_errors.length
      assert_equal 2, assigns(:total_count)
      assert_equal 1, assigns(:valid_marks)

      assert_not_nil assigns(:report).line_errors[0][:error][:mark_date]
    end
  end

  def test_bogus_upload
    nMarks = Mark.count
    data= "a,b,07/01/2006\n"
    data += "GOOG,100,b\n"
    get :upload, { :import=>{:file=>StringIO.new(data)}}

    assert_response :success
    assert_template 'upload_results'
    assert_no_errors

    assert_equal nMarks, Mark.count, "had #{nMarks} originally"
    assert_equal 2, assigns(:report).line_errors.length
    assert_equal 2, assigns(:total_count)
    assert_equal 0, assigns(:valid_marks)

    assert_not_nil assigns(:report).line_errors[0][:error][:mark_value]
    assert_nil assigns(:report).line_errors[0][:error][:mark_date]

    assert_not_nil assigns(:report).line_errors[1][:error][:mark_date]
    assert_nil assigns(:report).line_errors[1][:error][:mark_value]
  end

  def test_bogus_paste
    nMarks = Mark.count
    data = "a,b,07/01/2006\r\n"
    data += "GOOG,100,b\r\n"
    get :paste, { :import=>{:text=>data}}

    assert_response :success
    assert_template 'upload_results'
    assert_no_errors

    assert_equal nMarks, Mark.count, "had #{nMarks} originally"
    assert_equal 2, assigns(:report).line_errors.length
    assert_equal 2, assigns(:total_count)
    assert_equal 0, assigns(:valid_marks)

    assert_not_nil assigns(:report).line_errors[0][:error][:mark_value]
    assert_nil assigns(:report).line_errors[0][:error][:mark_date]

    assert_not_nil assigns(:report).line_errors[1][:error][:mark_date]
    assert_nil assigns(:report).line_errors[1][:error][:mark_value]
  end

  # verify parsing error display:
  # Failed Import: a,b,07/01/2006 (class: highlighted text)
  # * Mark value should be a number.
  def test_verify_error_formatting
    data = "a,b,07/01/2006\r\n"
    get :paste, { :import=>{:text=>data}}

    assert_response :success
    assert_template 'upload_results'
    assert_no_errors

    assert_equal 1, assigns(:report).line_errors.length

    assert_tag :tag => "div", :attributes => { :class => "highlightedText" }, :content => "Failed Import: a,b,07/01/2006"
  end
end
