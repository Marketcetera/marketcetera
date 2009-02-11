require "migrate_plus"

class CreateDividends < ActiveRecord::Migration
  include MigratePlus
  
  def self.up
    create_table :dividends do |t|
      t.column :amount, :decimal, :precision => 15, :scale => 5
      t.column :announce_date, :date
      t.column :ex_date, :date
      t.column :payable_date, :date
      t.column :status, :string, :limit => 1
      t.column :description, :string, :limit => 255
      t.column :equity_id, :integer
      t.column :currency_id, :integer, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :dividends, :currency_id, :currencies
    add_fkey :dividends, :equity_id, :equities
  end

  def self.down
    drop_table :dividends
  end
end
