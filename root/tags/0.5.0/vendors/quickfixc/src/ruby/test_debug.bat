SET LD_LIBRARY_PATH=../C++/.libs
SET DYLD_LIBRARY_PATH=$LD_LIBRARY_PATH
SET RUBYLIB=../../lib/debug/ruby

ruby -I ../../lib/debug/ruby test/TestSuite.rb
