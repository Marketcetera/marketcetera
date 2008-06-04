require 'test/minirunit'
#test backtrace
if !(defined? $recurse)
test_check "Test Exception:"
	$recurse = false
end

begin
  if $recurse
	raise Exception, 'test'
  else
	$recurse=true
	load('test/testException.rb')
  end
rescue Exception => boom
  result =  boom.backtrace.collect {|trace| 
	res = trace.index(':')
	res = res.succ
	resend = trace.index(':',res)
	if resend
	  trace[res, resend-res].to_i  #return value from block
	else
	  trace[res, trace.length].to_i  #return value from block
	end
  }
  test_equal([10,13,13] , result.slice(0..2))
end

test_no_exception {
	begin
		raise "X"
	rescue NoMethodError,RuntimeError =>e
	end
}

test_no_exception {
    begin
        begin
            raise "X"
        rescue NoMethodError
            test_ok(false)
        end
    rescue
        test_ok(true)
    end
}

begin
    e = StandardError.new
    e.set_backtrace("abc")
rescue TypeError => e
    test_ok(true)
end

begin
    e = StandardError.new
    e.set_backtrace(123)
rescue TypeError => e
    test_ok(true)
end

begin
    e = StandardError.new
    e.set_backtrace(["abc", 123])
rescue TypeError => e
    test_ok(true)
end

test_no_exception {
    e = StandardError.new
    e.set_backtrace(["abc", "123"])
}

test_no_exception {
    e = StandardError.new
    e.set_backtrace(nil)
}

begin
  raise 'Something bad'
rescue
  begin
    test_ok(true)
  rescue
    test_ok(false)
  end
  test_ok($!, 'Global exception ($!) should not be nil')
  test_ok($!.to_s.eql?('Something bad'), "Global exceptions should be 'Something bad', but is '#{$!}'")
end

begin
  raise 'Something bad'
rescue
  begin
    raise 'Something else bad'
  rescue
    test_ok(true)
  end
  test_ok($!, 'Global exception ($!) should not be nil')
  test_ok($!.to_s.eql?('Something bad'), "Global exceptions should be 'Something bad', but is '#{$!}'")
end

class TypeError
  def new(*args)
     StandardError.new
  end
end

test_exception(StandardError) { 5 + "A" }