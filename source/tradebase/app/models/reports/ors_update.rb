# Superclass for report describing ORS update
# Can read the date from params hash
class ORSUpdate < Tableless
  include ApplicationHelper

  attr_reader :sender_id, :file

  # takes an incoming params hash
  def initialize(params)
    @sender_id = get_non_empty_string_from_two(params, :ors, :sender_id, nil)
  end

  def validate
    errors.add(:sender_id, "cannot be empty") unless !sender_id.blank?
  end
end