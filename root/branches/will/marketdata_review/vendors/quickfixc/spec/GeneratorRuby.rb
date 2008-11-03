class GeneratorRuby
  def initialize(major, minor, dir)
    @major = major
    @minor = minor
    @beginstring = "FIX." + major + "." + minor
    @depth = 0;
    @dir = dir + "/"
    @f = createFile( "quickfix" + major + minor + ".rb" )
    @messageStarted = false
  end

  def createFile(name)
    attr = File::CREAT|File::TRUNC|File::RDWR
    return File.new(@dir + name, attr, 0644)
  end

  def tabs
    count = 0
    result = ""
    while (count != @depth)
      result += "\t" 
      count += 1
    end
    return result
  end

  def front
    @f.puts "require 'quickfix'"
    @f.puts "module Quickfix#{@major}#{@minor}"
  end

  def field(name, number)
  end

  def headerStart
  end

  def headerEnd
  end

  def trailerStart
  end

  def trailerEnd
  end

  def baseMessageStart
    @f.puts tabs + "class Message < Quickfix::Message"
    @depth += 1
    @f.puts tabs + "def initialize"
    @depth += 1
    @f.puts tabs + "super"
    @f.puts tabs + "getHeader().setField( Quickfix::BeginString.new(" + "\"" + @beginstring + "\"" + ") )"
    @depth -= 1
    @f.puts tabs + "end"
    @depth -= 1
  end

  def baseMessageEnd
    @f.puts tabs + "end"
  end

  def groupStart(name, number, delim, order)
    return if @messageStarted == false
    @f.puts

    @depth += 1    
    @f.puts tabs + "class " + name + " < Quickfix::Group"
    @depth += 1
    @f.puts tabs + "def initialize"
    @depth += 1
    @f.puts tabs + "order = Quickfix::IntArray.new(#{order.size+1})"
    order.each_index { |i| @f.puts tabs + "order[#{i}] = #{order[i]}" }
    @f.puts tabs + "order[#{order.size}] = 0"
    @f.puts tabs + "super(#{number}, #{delim}, order)"
    @depth -= 1
    @f.puts tabs + "end"
    @depth -= 1
  end

  def groupEnd
    return if @messageStarted == false
    @f.puts tabs + "end"
    @depth -= 1
  end

  def messageStart(name, msgtype, required)
    @messageStarted = true
    @f.puts

    @f.puts tabs + "class " + name + " < Message"
    @depth += 1
    @f.puts tabs + "def initialize"
    @depth += 1
    @f.puts tabs + "super"
    @f.puts tabs + "getHeader().setField( Quickfix::MsgType.new(" + "\"" + msgtype + "\") )"
    @depth -= 1
    @f.puts tabs + "end"
    @depth -= 1
  end

  def messageEnd
    @messageStarted = false
    @f.puts tabs + "end"
  end
  
  def fieldsStart
    @f = createFile("quickfix_fields.rb")
    @f.puts tabs + "module Quickfix"
    @depth += 1
  end
  
  def fieldType( name, type )
    return "CheckSum" if name == "CheckSum"
    return "Char" if type == "CHAR"
    return "Double" if type == "PRICE"
    return "Int" if type == "INT"
    return "Double" if type == "AMT"
    return "Double" if type == "QTY"
    return "UtcTimeStamp" if type == "UTCTIMESTAMP"
    return "Bool" if type == "BOOLEAN"
    return "Double" if type == "FLOAT"
    return "Double" if type == "PRICEOFFSET"
    return "UtcDate" if type == "UTCDATE"
    return "UtcDate" if type == "UTCDATEONLY"
    return "UtcTimeOnly" if type == "UTCTIMEONLY"
    return "Int" if type == "NUMINGROUP"
    return "Double" if type == "PERCENTAGE"
    return "Int" if type == "SEQNUM"
    return "Int" if type == "LENGTH"
    return "String"
  end
  
  def fields(name, number, type)
    @f.puts tabs + "class #{name} < Quickfix::#{fieldType(name, type)}Field"
    @depth += 1
    @f.puts tabs + "def #{name}.field"
    @depth += 1
    @f.puts tabs + "return #{number}"
    @depth -= 1
    @f.puts tabs + "end"
    @depth -= 1
    @depth += 1
    @f.puts tabs + "def initialize(data = nil)"
    @depth += 1
    @f.puts tabs + "if( data == nil )"
    @depth += 1
    @f.puts tabs + "super(#{number})"
    @depth -= 1
    @f.puts tabs + "else"
    @depth += 1
    @f.puts tabs + "super(#{number}, data)"
    @depth -= 1
    @f.puts tabs + "end"
    @depth -= 1
    @f.puts tabs + "end"
    @depth -= 1
    @f.puts tabs + "end"
    @f.puts
  end
  
  def fieldsEnd
    @depth -= 1
    @f.puts tabs + "end"
    @f.close
  end

  def back
    @f.puts tabs + "end"
  end
end
