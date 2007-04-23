# This module copied from p.279 of Rails Recipes book by Chad Fowler
# http://www.pragmaticprogrammer.com/titles/fr_rr/
module Validateable
  [:save, :save!, :update_attribute].each{ |attr| define_method(attr){} }
  
  def method_missing(symbol, *params)
    if(symbol.to_s =~ /(.*)_before_type_cast$/)
      send($1)
    end
  end
  
  def self.append_features(base)
    super
    base.send(:include, ActiveRecord::Validations)
  end
end