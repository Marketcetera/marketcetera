require "migrate_plus"

class AddTradeTypeColumn < ActiveRecord::Migration
  include MigratePlus

  def self.up
    add_column :trades, :type, :string
  end

  def self_down
    remove_column :trades, :type
  end
end
