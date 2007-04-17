require "migrate_plus"

class CreateMarks < ActiveRecord::Migration
  include MigratePlus

  def self.up
    create_table :marks do |t|
      t.column :equity_id, :integer, :null=>false
      t.column :mark_value, :decimal, :precision => 15, :scale => 5
      t.column :mark_type, :string, :limit => 1
      t.column :mark_date, :date, :null=>false
      t.column :created_on, :datetime
      t.column :updated_on, :datetime
    end
    add_fkey :marks, :equity_id, :equities
    
    # add the constraint to make equity_id/date combo unique
    execute "ALTER TABLE marks ADD CONSTRAINT uniq_marks_equity_date UNIQUE (equity_id, mark_date);"
  end

  def self.down
    drop_table :marks
  end
end
