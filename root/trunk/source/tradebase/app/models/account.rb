class Account < ActiveRecord::Base
  has_many :sub_accounts
end
