class DiagnosticsController < ApplicationController
  require 'tzinfo'

  def index
  end

  def server_info
    proxy = nil
    @host_resolution = {}
    @host_resolution['localhost'] = check_hostname_resolution('localhost')
   
    check_etc_hosts_marketcetera_ip_address 
    if !(RUBY_PLATFORM =~ /mswin32/)  
      @host_resolution['marketcetera'] = check_hostname_resolution('marketcetera')
    else # windows doesnt need this check - just filling it in for now - TODO  
      @host_resolution['marketcetera'] = check_hostname_resolution('www.marketcetera.com') 
    end
    @host_resolution['exchange.marketcetera.com'] = check_hostname_resolution('exchange.marketcetera.com')
    @host_resolution['time.xmlrpc.com'] = check_hostname_resolution('time.xmlrpc.com')

    @local_time = Time.new
    # time coming back from XMLRPC server is always in Pacific Timezone
    pacificTZ = TZInfo::Timezone.get('America/Los_Angeles')
    server = XMLRPC::Client.new2("http://time.xmlrpc.com/RPC2", proxy)
    result = server.call("currentTime.getCurrentTime")
    @xml_time  = pacificTZ.local_to_utc(Time.local(*result.to_a))

    @process_info = {}
    
    if !(RUBY_PLATFORM =~ /mswin32/)
      @process_info['ORS'] = %x{ps auxww|grep OrderRoutingSystem | grep -v grep}
      @process_info['JMS_Poller'] = %x{ps auxww|grep poller | grep -v grep}
      @process_info['MySQL'] = %x{ps auxww|grep mysqld | grep -v grep}
      @process_info['ActiveMQ'] = %x{netstat -an | grep 61616}
      @process_info['STOMP'] = %x{netstat -an | grep 61613}
      @networking_eth0 = %x{ifconfig eth0}
    else 
      # windows - partial implementaion - need 'ps' replacement for windows
      # This is a workaround for windows. We check that we have 2 connections to broker
      # One is ors and one is TB poller - that way we know ors is running
      number_of_connections = ((%x{netstat -an | find "61616" | find "ESTABLISHED"}).split("\n").length)
      if (number_of_connections >= 4)
	@process_info['ORS'] = "Running"
      else	
	@process_info['ORS'] = ""
      end      
      @process_info['JMS_Poller'] = %x{netstat -an | find "61613" | find "ESTABLISHED"}
      @process_info['MySql'] = %x{netstat -an | find "8882" }
      @process_info['ActiveMQ'] = %x{netstat -an | find "61616" | find "ESTABLISHED"} 
      @process_info['STOMP'] = %x{netstat -an | find "61613" |  find "ESTABLISHED"} 
      @networking_eth0 = %x{ipconfig}
    end 

    output = %x{java -cp #{JAVA_JMX_CONNECTOR_LIB}  com.marketcetera.tools.JMXReader}
    @jmx_info = output.split("\n")
    @time_diff = (@xml_time - @local_time).abs
    @report = ServerInfo.new(@networking_eth0, @time_diff, @host_resolution, @process_info, @marketcetera_line)
  end

  # Displays the ORS server log
  def ors_log_display
    if !(RUBY_PLATFORM =~ /mswin32/)
      @log = %x{tail -2000 ../ors/logs/ors.log}
    else # windows only - can be used for *nix in the future
      if ((File.exist?("#{ENV['METC_HOME']}" + "\\ors\\logs\\ors.log")))
         lines = File.readlines("#{ENV['METC_HOME']}" + "\\ors\\logs\\ors.log")
         if (lines.length-2000 > 0)
           @log = lines[(lines.length-2000)..lines.length-1]
         else       
           @log = lines[0..lines.length-1]
         end
      else
	 @log = "N/A"
      end   
    end
  end

  def tradebase_log_display
    if  !(RUBY_PLATFORM =~ /mswin32/)	   
    @log = %x{tail -2000 log/#{RAILS_ENV}.log}
    else # windows only - can be used for *nux in the future
      if ((File.exist?("#{ENV['METC_HOME']}" + "\\tradebase\\log\\#{RAILS_ENV}.log")))  
        lines = File.readlines("#{ENV['METC_HOME']}" + "\\tradebase\\log\\#{RAILS_ENV}.log")
        if (lines.length-2000 > 0)
          @log = lines[(lines.length-2000)..lines.length-1]
        else
	  @log = lines[0..lines.length-1]
        end
      else
        @log = "N/A"
      end
    end
  end

  private
  def check_hostname_resolution(host)
    begin
      s = Socket.gethostbyname(host)
      return true
    rescue
      return false
    end
  end

  def check_etc_hosts_marketcetera_ip_address
    @marketcetera_line = ""
    #/etc/hosts location is different between windows and linux.. Assuming windir is defined in windows env.
    if ((File.exist?("/etc/hosts")) ? file=File.open("/etc/hosts") : file=File.open("#{ENV['windir']}" + "\\system32\\drivers\\etc\\hosts"))
      file.each_line { |line|
        if (!line.index("marketcetera").nil?)
          @marketcetera_line = line
          break
        end
      }
      file.close 
    end
  end

end
