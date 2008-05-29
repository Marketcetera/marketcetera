SET LD_LIBRARY_PATH=../C++/.libs
SET DYLD_LIBRARY_PATH=$LD_LIBRARY_PATH
SET RUBYLIB=../../lib/ruby

ruby -I ../../lib/ruby test/TestSuite.rb
