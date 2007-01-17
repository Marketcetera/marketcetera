# Filters added to this controller will be run for all controllers in the application.
# Likewise, all the methods added will be available for all controllers.
class ApplicationController < ActionController::Base
  require 'quickfix'
  require 'quickfix_ruby'
  require 'quickfix_fields'
  require 'bigdecimal'
    
  helper :application, :trades, :navigation, :display
end

# modify the ActiveRecord and ActiveController to add paginate_by_sql behaviour
# lifted from: http://thebogles.com/blog/2006/06/paginate_by_sql-for-rails-a-more-general-approach/
# Except for we (marketcetera) added the return of total # of records found
module ActiveRecord 
    class Base
        def self.find_by_sql_with_limit(sql, offset, limit)
            sql = sanitize_sql(sql)
            add_limit!(sql, {:limit => limit, :offset => offset})
            find_by_sql(sql)
        end

        def self.count_by_sql_wrapping_select_query(sql)
            sql = sanitize_sql(sql)
            count_by_sql("select count(*) from (#{sql}) as my_table")
        end
   end
end

class ApplicationController < ActionController::Base
    def paginate_by_sql(model, sql, per_page, options={})
       if options[:count]
           if options[:count].is_a? Integer
               total = options[:count]
           else
               total = model.count_by_sql(options[:count])
           end
       else
           total = model.count_by_sql_wrapping_select_query(sql)
       end

       object_pages = Paginator.new self, total, per_page,
            @params['page']
       objects = model.find_by_sql_with_limit(sql,
            object_pages.current.to_sql[1], per_page)
       return [object_pages, objects, total]
   end
end
