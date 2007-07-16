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
  def import_one_mark(line, report)
    mark = CSV.parse_line(line)
    symbol = mark[0]
    equity = Equity.get_equity(symbol)
    equity.save
    value = mark[1]
    date = mark[2]
    m = Mark.new(:tradeable_id => equity, :mark_value => value, :mark_date => date, :mark_type => "C")
    m.equity = equity
    m.save
    if(!m.valid?)
      report.add_error_for_line(line, m.errors)
    else
      @valid_marks +=1
      logger.debug("created mark for #{line}")
    end
    @total_count += 1
  end


end
