require 'bigdecimal'
#require 'pagination'
require 'action_controller/pagination'

BigDecimal.class_eval do
  alias :_original_to_s :to_s

  def to_s(format="F")
    _original_to_s(format)
  end
end

# Override the Pagination class to include group SQL clause and pass it down to Model.find
ActionController::Pagination.class_eval do 
    self::DEFAULT_OPTIONS.merge!({:group => nil})
    
    def find_collection_for_pagination(model, options, paginator)
      model.find(:all, :conditions => options[:conditions],
                 :order => options[:order_by] || options[:order],
                 :joins => options[:join] || options[:joins], :include => options[:include],
                 :select => options[:select], :limit => options[:per_page],
                 :offset => paginator.current.offset, 
                 :group => options[:group])
    end
end