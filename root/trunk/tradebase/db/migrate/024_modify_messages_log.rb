require "migrate_plus"

# modifies the messages_log table to allow for null session_qualifier
# This may be necessary for Ruby 1.8.6
class ModifyMessagesLog < ActiveRecord::Migration
  include MigratePlus

  def self.up
    # make messages_log columns nullable
    change_column :messages_log, :session_qualifier, :string, :limit => 64, :null => true
  end
end
