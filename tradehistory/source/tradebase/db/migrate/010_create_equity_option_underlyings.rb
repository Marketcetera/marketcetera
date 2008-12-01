require "migrate_plus"

class CreateEquityOptionUnderlyings < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :equity_option_underlyings do |t|
      t.column :quantity, :decimal, :precision => 15, :scale => 5
      t.column :equity_option_series_id, :integer, :null => false
      t.column :underlying_m_symbol_id, :integer, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :equity_option_underlyings, :underlying_m_symbol_id, :m_symbols
    add_fkey :equity_option_underlyings, :equity_option_series_id, :equity_option_series
  end

  def self.down
    drop_table :equity_option_underlyings
  end
end
