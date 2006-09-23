class CreateMessagesLogs < ActiveRecord::Migration
  def self.up
    create_table :messages_logs do |t|
      # t.column :name, :string
    end
  end

  def self.down
    drop_table :messages_logs
  end
end
