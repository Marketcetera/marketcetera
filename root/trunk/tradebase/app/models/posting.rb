class Posting < ActiveRecord::Base
  belongs_to :sub_account
  belongs_to :journal
  belongs_to :currency

  # pairID values for types of postings
  NotionalPairID = 1
  CommissionsPairID = 2
end
