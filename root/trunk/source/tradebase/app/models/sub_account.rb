class SubAccount < ActiveRecord::Base
  belongs_to :sub_account_type
  belongs_to :account
  has_many :postings
  
  validates_presence_of :account_id
  
end
