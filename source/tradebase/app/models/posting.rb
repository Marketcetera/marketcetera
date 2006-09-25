class Posting < ActiveRecord::Base
  belongs_to :sub_account
  belongs_to :journal
  belongs_to :currency
end
