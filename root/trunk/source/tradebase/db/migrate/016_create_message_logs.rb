class CreateMessageLogs < ActiveRecord::Migration
  def self.up
    create_table :messages_log do |t|
      t.column :time, :datetime, :null => false
      t.column :beginstring, :string, :limit => 8, :null => false
      t.column :sendercompid, :string, :limit => 64, :null => false
      t.column :targetcompid, :string, :limit => 64, :null => false
      t.column :session_qualifier, :string, :limit => 64, :null => false
      t.column :text, :text, :null => false
      t.column :created_on, :datetime, :default => Time.now
      t.column :updated_on, :datetime, :default => Time.now
    end
  end

  def self.down
    drop_table :messages_log
  end
end
