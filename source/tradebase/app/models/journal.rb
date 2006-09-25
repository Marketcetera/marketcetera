class Journal < ActiveRecord::Base
  has_many :postings
  has_many :trades
end
