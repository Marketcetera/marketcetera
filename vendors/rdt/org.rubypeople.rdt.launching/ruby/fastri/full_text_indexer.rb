# Copyright (C) 2006  Mauricio Fernandez <mfp@acm.org>
#

require 'fastri/version'

module FastRI

class FullTextIndexer
  WORD_RE    = /[A-Za-z0-9_]+/
  NONWORD_RE = /[^A-Za-z0-9_]+/
  MAGIC      = "FastRI full-text index #{FASTRI_FT_INDEX_FORMAT}\0"

  def initialize(max_querysize)
    @documents = []
    @doc_hash  = {}
    @max_wordsize = max_querysize
  end

  def add_document(name, data, metadata = {})
    @doc_hash[name] = [data, metadata.merge(:size => data.size)]
    @documents << name
  end

  def data(name)
    @doc_hash[name][0]
  end

  def documents
    @documents = @documents.uniq
  end

  def preprocess(str)
    str.gsub(/\0/,"")
  end

  require 'strscan'
  def find_suffixes(text, offset)
    find_suffixes_simple(text, WORD_RE, NONWORD_RE, offset)
  end

  def find_suffixes_simple(string, word_re, nonword_re, offset)
    suffixes = []
    sc = StringScanner.new(string)
    until sc.eos?
      sc.skip(nonword_re)
      len = string.size
      loop do
        break if sc.pos == len
        suffixes << offset + sc.pos
        skipped_word = sc.skip(word_re)
        break unless skipped_word
        loop do
          skipped_nonword = sc.skip(nonword_re)
          break unless skipped_nonword
        end
      end
    end
    suffixes
  end

  require 'enumerator'
  def build_index(full_text_IO, suffix_array_IO)
    fulltext = ""
    io = StringIO.new(fulltext)
    io.write MAGIC
    full_text_IO.write MAGIC
    documents.each do |doc|
      data, metadata = @doc_hash[doc]
      io.write(data)
      full_text_IO.write(data)
      meta_txt = Marshal.dump(metadata)
      footer = "\0....#{doc}\0#{meta_txt}\0"
      footer[1,4] = [footer.size - 5].pack("V")
      io.write(footer)
      full_text_IO.write(footer)
    end

    scanner = StringScanner.new(fulltext)
    scanner.scan(Regexp.new(Regexp.escape(MAGIC)))

    count = 0
    suffixes = []
    until scanner.eos?
      count += 1
      start = scanner.pos
      text = scanner.scan_until(/\0/)
      suffixes.concat find_suffixes(text[0..-2], start)
      len = scanner.scan(/..../).unpack("V")[0]
      #puts "LEN: #{len}  #{scanner.pos}  #{scanner.string.size}"
      #puts "#{scanner.string[scanner.pos,20].inspect}"
      scanner.pos += len
      #scanner.terminate if !text
    end
    sorted = suffixes.sort_by{|x| fulltext[x, @max_wordsize]}
    sorted.each_slice(10000){|x| suffix_array_IO.write x.pack("V*")}
    nil
  end
end # class FullTextIndexer

end # module FastRI
