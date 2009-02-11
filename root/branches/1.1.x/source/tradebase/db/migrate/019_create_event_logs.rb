class CreateEventLogs < ActiveRecord::Migration
  def self.up
    create_table :event_log do |t|
      t.column :time, :datetime, :null => false
      t.column :beginstring, :string, :limit => 8, :null => false
      t.column :sendercompid, :string, :limit => 64, :null => false
      t.column :targetcompid, :string, :limit => 64, :null => false
      t.column :session_qualifier, :string, :limit => 64, :null => false
      t.column :text, :text, :null => false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
  end

  def self.down
    drop_table :event_log
  end
end
