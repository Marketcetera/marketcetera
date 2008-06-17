# We have 2 entry points - either through upload or through "paste", ie
# where you can paste the quotes into a text box
# Support both Equity (E) and Forex (F) quotes (designators can be lower-case)
# Sample format: symbol, value, date, type ==> GOOG, 555, 12/10/2005, E
class ImportMarksController < ApplicationController
  require 'csv'
  include ApplicationHelper

  def index
    render :action => 'import'   
  end

  def upload
    begin
      file_data = get_non_empty_string_from_two(params, "import", "file", "import_file")
      @report = MarksImport.new(file_data)
      if(!@report.valid?)
        render :action => :import
        return
      end
      @total_count, @valid_marks = 0, 0
      @import_errors = ""
      file_data.each_line { |line| import_one_mark(line, @report) }
      render :action => 'import_marks/upload_results'
    rescue Exception => ex
      logger.debug("exception in mark import: "+ex.class.to_s + ":" + ex.message+"\n"+ex.backtrace.join("\n"))
      render :action => 'import'
    end
  end

  # Symbol, Value, Date
  def paste
      begin
        text = get_non_empty_string_from_two(params, "import", "text", "import_text")
        @report = MarksImport.new(text)
        @total_count, @valid_marks = 0, 0
        if(!text.blank?)
          all_lines = text.split("\r\n")
          logger.debug("original text: #{text}\n, all lines are: #{all_lines}")
          all_lines[0..-1].each { |line| import_one_mark(line, @report) }
        end
        render :template => 'import_marks/upload_results'
      rescue Exception => ex
        logger.debug("exception in mark import: "+ex.class.to_s + ":" + ex.message+"\n"+ex.backtrace.join("\n"))
        render :action => 'import'
      end
  end

  protected
  # sample: GOOG,500,01/01/2007,E
  # sample: EUR/USD,1.23,01/01/2007,F
  # In case of errros, we put an error together with the line it caused it.
  # If we have an error that's not a validation error, we manually pre-create an
  # ActiveRecord::Errors object and put it in there with "Error:" key to be retrieved
  # by the UI
  def import_one_mark(line, report)
    begin
      mark = CSV.parse_line(line)
      if(mark.length != 4)
        raise "Invalid number of arguments, got #{mark.length}/4 expected."
      end
      tradeable_type = mark[3].upcase
      symbol = mark[0]
      value = mark[1]
      date = mark[2]
      if(tradeable_type == TradesHelper::SecurityTypeEquity)
        m = Mark.new(:mark_value => value, :mark_date => date, :mark_type => "C")
        tradeable = Equity.get_equity(symbol)
      else
        if(tradeable_type == TradesHelper::SecurityTypeForex)
          tradeable = CurrencyPair.get_currency_pair(symbol, true)
          m = ForexMark.new(:mark_value => value, :mark_date => date, :mark_type => "C")
        else
          raise "Unknown mark type: #{tradeable_type}"
        end
      end
      tradeable.save
      m.tradeable = tradeable
      m.save
      if(!m.valid?)
        report.add_error_for_line(line, m.errors)
      else
        @valid_marks +=1
        logger.debug("created mark for #{line}")
      end
      @total_count += 1
    rescue Exception => ex
      error = Mark.new.errors
      error.add("Error:", ex.message)
      report.add_error_for_line(line, error)
      logger.debug("exception while parsing mark line [#{line}]: "+ex.message)
      logger.debug(ex.backtrace.join("\n"))
    end
  end
end
