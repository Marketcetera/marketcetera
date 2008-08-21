require 'quickfix_ruby'
require 'test/unit'

class FieldBaseTestCase < Test::Unit::TestCase

	def setup
		@object = Quickfix::FieldBase.new( 12, "VALUE" )
	end

	def test_set
		@object.setString("VALUE")
		assert(@object.getValue() == "12=VALUE\001")
		assert(@object.getTotal() == 542)
		assert(@object.getLength() == 9)
		@object.setString("VALUF")
		assert(@object.getValue() == "12=VALUF\001")
		assert(@object.getTotal() == 543)
		assert(@object.getLength() == 9)
		@object.setField(13)
		assert(@object.getValue() == "13=VALUF\001")
		assert(@object.getTotal() == 544)
		assert(@object.getLength() == 9)
	end
end