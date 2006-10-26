class Position < ActiveRecord::Base
  set_table_name "trades"
  belongs_to :account
  belongs_to :tradeable, :polymorphic=>true
end
