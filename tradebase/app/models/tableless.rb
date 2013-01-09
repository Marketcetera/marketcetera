# Abstract superclass for all the table-less models
# Based on the code in http://www.railsweenie.com/forums/2/topics/724?page=1#posts-5228
class Tableless < ActiveRecord::Base

  def self.columns() @columns ||= []; end
  def self.column(name, sql_type = nil, default = nil, null = true)
    columns << ActiveRecord::ConnectionAdapters::Column.new(name.to_s, default, sql_type.to_s, null)
  end
end  
