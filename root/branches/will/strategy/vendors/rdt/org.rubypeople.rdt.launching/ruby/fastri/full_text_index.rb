# Copyright (C) 2006  Mauricio Fernandez <mfp@acm.org>
#

require 'fastri/full_text_indexer'
require 'stringio'

module FastRI

class FullTextIndex
  MAX_QUERY_SIZE = 20
  MAX_REGEXP_MATCH_SIZE = 255
  class Result
    attr_reader :path, :query, :index, :metadata

    def initialize(searcher, query, index, path, metadata)
      @searcher = searcher
      @index    = index
      @query    = query
      @path     = path
      @metadata = metadata
    end

    def context(size)
      @searcher.fetch_data(@index, 2*size+1, -size)
    end

    def text(size)
      @searcher.fetch_data(@index, size, 0)
    end
  end

  class << self; private :new end

  DEFAULT_OPTIONS = {
    :max_query_size => MAX_QUERY_SIZE,
  }
  
  def self.new_from_ios(fulltext_IO, suffix_arrray_IO, options = {})
    new(:io, fulltext_IO, suffix_arrray_IO, options)
  end

  def self.new_from_filenames(fulltext_fname, suffix_arrray_fname, options = {})
    new(:filenames, fulltext_fname, suffix_arrray_fname, options)
  end

  attr_reader :max_query_size
  def initialize(type, fulltext, sarray, options)
    options = DEFAULT_OPTIONS.merge(options)
    case type
    when :io
      @fulltext_IO = fulltext
      @sarray_IO   = sarray
    when :filenames
      @fulltext_fname = fulltext
      @sarray_fname   = sarray
    else raise "Unknown type"
    end
    @type = type
    @max_query_size = options[:max_query_size]
    check_magic
  end

  def lookup(term)
    get_fulltext_IO do |fulltextIO|
      get_sarray_IO do |sarrayIO|
        case sarrayIO
        when StringIO
          num_suffixes = sarrayIO.string.size / 4 - 1
        else
          num_suffixes = sarrayIO.stat.size / 4 - 1
        end

        index, offset = binary_search(sarrayIO, fulltextIO, term, 0, num_suffixes)
        if offset
          fulltextIO.pos = offset
          path, metadata = find_metadata(fulltextIO)
          return Result.new(self, term, index, path, metadata) if path
        else
          nil
        end
      end
    end
  end

  def next_match(result, term_or_regexp = "")
    case term_or_regexp
    when String;  size = [result.query.size, term_or_regexp.size].max
    when Regexp;  size = MAX_REGEXP_MATCH_SIZE
    end
    get_fulltext_IO do |fulltextIO|
      get_sarray_IO do |sarrayIO|
        idx = result.index
        loop do
          idx += 1
          str = get_string(sarrayIO, fulltextIO, idx, size)
          upto = str.index("\0")
          str = str[0, upto] if upto
          break unless str.index(result.query) == 0
          if str[term_or_regexp]
            fulltextIO.pos = index_to_offset(sarrayIO, idx)
            path, metadata = find_metadata(fulltextIO)
            return Result.new(self, result.query, idx, path, metadata) if path
          end
        end
      end
    end
  end

  def next_matches(result, term_or_regexp = "")
    case term_or_regexp
    when String;  size = [result.query.size, term_or_regexp.size].max
    when Regexp;  size = MAX_REGEXP_MATCH_SIZE
    end
    ret = []
    get_fulltext_IO do |fulltextIO|
      get_sarray_IO do |sarrayIO|
        idx = result.index
        loop do
          idx += 1
          str = get_string(sarrayIO, fulltextIO, idx, size)
          upto = str.index("\0")
          str = str[0, upto] if upto
          break unless str.index(result.query) == 0
          if str[term_or_regexp]
            fulltextIO.pos = index_to_offset(sarrayIO, idx)
            path, metadata = find_metadata(fulltextIO)
            ret << Result.new(self, result.query, idx, path, metadata) if path
          end
        end
      end
    end

    ret
  end

  def fetch_data(index, size, offset = 0)
    raise "Bad offset" unless offset <= 0
    get_fulltext_IO do |fulltextIO|
      get_sarray_IO do |sarrayIO|
        base = index_to_offset(sarrayIO, index)
        actual_offset = offset
        newsize = size
        if base + offset < 0    # at the beginning
          excess        = (base + offset).abs   # remember offset is < 0
          newsize       = size - excess
          actual_offset = offset + excess
        end
        str  = get_string(sarrayIO, fulltextIO, index, newsize, offset)
        from = (str.rindex("\0", -actual_offset) || -1) + 1
        to   = (str.index("\0", -actual_offset) || 0) - 1
        str[from..to]
      end
    end
  end

  private
  def check_magic
    get_fulltext_IO do |io|
      io.rewind
      header = io.read(FullTextIndexer::MAGIC.size)
      raise "Unsupported index format." unless header
      version = header[/\d+\.\d+\.\d+/]
      raise "Unsupported index format." unless version
      major, minor, teeny = version.scan(/\d+/)
      if major != FASTRI_FT_INDEX_FORMAT_MAJOR or
         minor > FASTRI_FT_INDEX_FORMAT_MINOR
         raise "Unsupported index format"
      end
    end
  end

  def get_fulltext_IO
    case @type
    when :io; yield @fulltext_IO
    when :filenames
      File.open(@fulltext_fname, "rb"){|f| yield f}
    end
  end

  def get_sarray_IO
    case @type
    when :io; yield @sarray_IO
    when :filenames
      File.open(@sarray_fname, "rb"){|f| yield f}
    end
  end

  def index_to_offset(sarrayIO, index)
    sarrayIO.pos = index * 4
    sarrayIO.read(4).unpack("V")[0]
  end

  def find_metadata(fulltextIO)
    oldtext = ""
    loop do
      text = fulltextIO.read(4096)
      break unless text
      if idx = text.index("\0")
        if idx + 4 >= text.size
          text.concat(fulltextIO.read(4096))
        end
        len     = text[idx+1, 4].unpack("V")[0]
        missing = idx + 5 + len - text.size
        if missing > 0
          text.concat(fulltextIO.read(missing))
        end
        footer         = text[idx + 5, len - 1]
        path, metadata = /(.*?)\0(.*)/m.match(footer).captures
        return [path, Marshal.load(metadata)]
      end
      oldtext = text
    end
    nil
  end

  def get_string(sarrayIO, fulltextIO, index, size, off = 0)
    sarrayIO.pos = index * 4
    offset = sarrayIO.read(4).unpack("V")[0]
    fulltextIO.pos = [offset + off, 0].max
    fulltextIO.read(size)
  end

  def binary_search(sarrayIO, fulltextIO, term, from, to)
    #puts "BINARY   #{from}  --  #{to}"
    #left   = get_string(sarrayIO, fulltextIO, from, @max_query_size)
    #right  = get_string(sarrayIO, fulltextIO, to, @max_query_size)
    #puts "   #{left.inspect}  --  #{right.inspect}"
    middle = (from + to) / 2
    pivot = get_string(sarrayIO, fulltextIO, middle, @max_query_size)
    if from == to
      if pivot.index(term) == 0
        sarrayIO.pos = middle * 4
        [middle, sarrayIO.read(4).unpack("V")[0]]
      else
        nil
      end
    elsif term <= pivot
      binary_search(sarrayIO, fulltextIO, term, from, middle)
    elsif term > pivot
      binary_search(sarrayIO, fulltextIO, term, middle+1, to)
    end
  end
end # class FullTextIndex

end # module FastRI
