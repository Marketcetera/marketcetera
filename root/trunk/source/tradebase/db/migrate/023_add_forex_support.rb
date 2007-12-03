require "migrate_plus"

class AddForexSupport < ActiveRecord::Migration
  include MigratePlus

  def self.up
    add_column :trades, :type, :string
    add_column :trades, :strategy, :string, :limit=>255

    usd = Currency.find_by_alpha_code("USD")
    add_column :marks, :currency_id, :integer, {:default=>usd.id, :null =>false} 
    add_column :marks, :tradeable_type, :string, :limit=>255

    add_fkey :marks, :currency_id, :currencies
  end

  def self.down
    remove_fkey :marks, :currency_id, :currencies
    remove_column :trades, :type
    remove_column :trades, :strategy
    remove_column :marks, :currency_id
  end
end
