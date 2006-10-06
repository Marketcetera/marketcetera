class Trade < ActiveRecord::Base
  belongs_to :journal
  belongs_to :account
  
  belongs_to :tradeable, :polymorphic => true
  
#  def self.find_by_symbol(symbol_str)
#    find(:all, :conditions => tradeable.m_symbol.root IS NOT NULL")
#  end
end
