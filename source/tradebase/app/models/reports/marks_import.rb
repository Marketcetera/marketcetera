# Report for trying to upload marks
# The incoming object is a StringIO obj that we get
# as the representation of the underlying file data the browser sends us.
class MarksImport < Tableless

  # takes an incoming params hash
  def initialize(file)
    @line_errors = []
    @file = file
  end

  def validate
    errors.add(:file, "Unable to read CSV data from specified file") unless (!@file.nil? && !@file.string.blank?)
  end

  def add_error_for_line(line, error)
    @line_errors << {:line => line, :error => error}
  end

  def line_errors
    @line_errors
  end
end