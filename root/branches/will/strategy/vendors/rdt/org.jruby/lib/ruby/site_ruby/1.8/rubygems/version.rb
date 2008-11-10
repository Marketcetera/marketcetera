#--
# Copyright 2006 by Chad Fowler, Rich Kilmer, Jim Weirich and others.
# All rights reserved.
# See LICENSE.txt for permissions.
#++

require 'rubygems'

##
# The Version class processes string versions into comparable values
class Gem::Version

  include Comparable

  attr_reader :ints

  attr_reader :version

  ##
  # Checks if version string is valid format
  #
  # str:: [String] the version string
  # return:: [Boolean] true if the string format is correct, otherwise false
  #
  def self.correct?(version)
    case version
    when Integer, /\A\s*(\d+(\.\d+)*)*\s*\z/ then true
    else false
    end
  end

  ##
  # Factory method to create a Version object.  Input may be a Version or a
  # String.  Intended to simplify client code.
  #
  #   ver1 = Version.create('1.3.17')   # -> (Version object)
  #   ver2 = Version.create(ver1)       # -> (ver1)
  #   ver3 = Version.create(nil)        # -> nil
  #
  def self.create(input)
    if input.respond_to? :version then
      input
    elsif input.nil? then
      nil
    else
      new input
    end
  end

  ##
  # Constructs a version from the supplied string
  #
  # version:: [String] The version string.  Format is digit.digit...
  #
  def initialize(version)
    raise ArgumentError, "Malformed version number string #{version}" unless
      self.class.correct?(version)

    self.version = version
  end

  def inspect # :nodoc:
    "#<#{self.class} #{@version.inspect}>"
  end

  # Dump only the raw version string, not the complete object
  def marshal_dump
    [@version]
  end

  # Load custom marshal format
  def marshal_load(array)
    self.version = array[0]
  end

  # Strip ignored trailing zeros.
  def normalize
    @ints = build_array_from_version_string

    return if @ints.length == 1

    @ints.pop while @ints.last == 0

    @ints = [0] if @ints.empty?
  end

  ##
  # Returns the text representation of the version
  #
  # return:: [String] version as string
  #
  def to_s
    @version
  end

  ##
  # Convert version to integer array
  #
  # return:: [Array] list of integers
  #
  def to_ints
    normalize unless @ints
    @ints
  end

  def to_yaml_properties
    ['@version']
  end

  def version=(version)
    @version = version.to_s.strip
    normalize
  end

  def yaml_initialize(tag, values)
    self.version = values['version']
  end

  ##
  # Compares two versions
  #
  # other:: [Version or .ints] other version to compare to
  # return:: [Fixnum] -1, 0, 1
  #
  def <=>(other)
    return 1 unless other
    @ints <=> other.ints
  end

  alias eql? == # :nodoc:

  def hash # :nodoc:
    to_ints.inject { |hash_code, n| hash_code + n }
  end

  # Return a new version object where the next to the last revision
  # number is one greater. (e.g.  5.3.1 => 5.4)
  def bump
    ints = build_array_from_version_string
    ints.pop if ints.size > 1
    ints[-1] += 1
    self.class.new(ints.join("."))
  end

  def build_array_from_version_string
    @version.to_s.scan(/\d+/).map { |s| s.to_i }
  end
  private :build_array_from_version_string

  #:stopdoc:

  require 'rubygems/requirement'

  # Gem::Requirement's original definition is nested in Version.
  # Although an inappropriate place, current gems specs reference the nested
  # class name explicitly.  To remain compatible with old software loading
  # gemspecs, we leave a copy of original definition in Version, but define an
  # alias Gem::Requirement for use everywhere else.

  Requirement = ::Gem::Requirement

  # :startdoc:

end

