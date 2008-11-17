require "migrate_plus"

class CreateEquities < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :equities do |t|
      t.column :description, :string, :limit => 255
      t.column :m_symbol_id, :integer, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :equities, :m_symbol_id, :m_symbols
    add_index :equities, :m_symbol_id, :unique => true
  end

  def self.down
    drop_table :equities
  end
end
