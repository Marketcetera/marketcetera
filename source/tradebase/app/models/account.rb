class Account < ActiveRecord::Base
  has_many :sub_accounts
  has_many :trades
end
