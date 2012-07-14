require "migrate_plus"

class CreateEquityOptions < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :equity_options do |t|
      t.column :m_symbol_id, :integer
      t.column :expiration_date, :date
      t.column :strike_price, :decimal, :precision => 15, :scale => 5
      t.column :call_put, :string, :limit=>1, :null => false
      t.column :exercise_type, :string, :limit=>1
      t.column :equity_option_series_id, :integer, :null => false
      t.column :strike_price_currency_id, :integer, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :equity_options, :equity_option_series_id, :equity_option_series
    add_fkey :equity_options, :strike_price_currency_id, :currencies
    add_fkey :equity_options, :m_symbol_id, :m_symbols
  end

  def self.down
    drop_table :equity_options
  end
end
