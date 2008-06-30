class MSymbol < ActiveRecord::Base
  validates_length_of :root, :minimum => 1
end
