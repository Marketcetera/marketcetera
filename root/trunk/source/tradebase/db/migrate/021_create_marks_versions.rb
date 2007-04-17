
class CreateMarksVersions < ActiveRecord::Migration
  def self.up
    Mark.create_versioned_table do |t|
      t.column :mark_value, :decimal, :precision => 15, :scale => 5
    end
  end

  def self.down
    Mark.drop_versioned_table
  end
end
