# Report for trying to upload marks
# The incoming object is a StringIO obj that we get
# as the representation of the underlying file data the browser sends us.
class MarksImport < Tableless

  # takes an incoming params hash
  def initialize(data)
    @line_errors = []
    @data = data
  end

  def validate
    errors.add(:file, "Unable to read CSV data from the specified file") unless (@data.kind_of?(StringIO) && !@data.nil? && !@data.string.blank?)
  end

  def add_error_for_line(line, error)
    @line_errors << {:line => line, :error => error}
  end

  def line_errors
    @line_errors
  end
end