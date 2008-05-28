class DiagnosticsController < ApplicationController
  require 'tzinfo'

  def index
  end

  def server_info
    proxy = nil
    @host_resolution = {}
    @host_resolution['localhost'] = check_hostname_resolution('localhost')
    @host_resolution['marketcetera'] = check_hostname_resolution('marketcetera')
    check_etc_hosts_marketcetera_ip_address
    @host_resolution['exchange.marketcetera.com'] = check_hostname_resolution('exchange.marketcetera.com')
    @host_resolution['time.xmlrpc.com'] = check_hostname_resolution('time.xmlrpc.com')

    @local_time = Time.new
    # time coming back from XMLRPC server is always in Pacific Timezone
    pacificTZ = TZInfo::Timezone.get('America/Los_Angeles')
    server = XMLRPC::Client.new2("http://time.xmlrpc.com/RPC2", proxy)
    result = server.call("currentTime.getCurrentTime")
    @xml_time  = pacificTZ.local_to_utc(Time.local(*result.to_a))

    @process_info = {}
    @process_info['ActiveMQ'] = %x{netstat -an | grep 61616}
    @process_info['STOMP'] = %x{netstat -an | grep 61613}
    
    if (!RUBY_PLATFORM =~ "mswin")
    @process_info['OMS'] = %x{ps auxww|grep OrderManagementSystem | grep -v grep}
    @process_info['JMS_Poller'] = %x{ps auxww|grep poller | grep -v grep}
    @process_info['MySQL'] = %x{ps auxww|grep mysqld | grep -v grep}

    @networking_eth0 = %x{ifconfig eth0}
    else #windows - partial implementaion - need 'ps' replacement for windows 
      @process_info['OMS'] = ""
      @process_info['JMS_Poller'] = ""
      @process_info['MySql'] = ""      
      @networking_eth0 = %x{ipconfig}
    end 

    output = %x{java -cp #{JAVA_JMX_CONNECTOR_LIB}  com.marketcetera.tools.JMXReader}
    @jmx_info = output.split("\n")
    @time_diff = (@xml_time - @local_time).abs
    @report = ServerInfo.new(@networking_eth0, @time_diff, @host_resolution, @process_info, @marketcetera_line)
  end

  # Displays the OMS server log
  def oms_log_display
    if (!RUBY_PLATFORM =~ "mswin")
    @log = %x{tail -2000 /var/log/marketcetera/oms.log}
    else
      @log = "N/A"  
    end   
  end

  def tradebase_log_display
    if  (!RUBY_PLATFORM =~ "mswin")	   
    @log = %x{tail -2000 log/#{RAILS_ENV}.log}
    else
      @log = "N/A"
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
    if ((File.exist?("/etc/hosts")) ? file=File.open("/etc/hosts") : file=File.open("#{ENV['windir']}" + "/system32/drivers/etc/hosts"))
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

