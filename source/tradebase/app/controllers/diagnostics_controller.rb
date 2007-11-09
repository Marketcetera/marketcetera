class DiagnosticsController < ApplicationController

  def index
    server_info
    render :action => :server_info
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
    server = XMLRPC::Client.new2("http://time.xmlrpc.com/RPC2", proxy)
    result = server.call("currentTime.getCurrentTime")
    @xml_time  = Time.local(*result.to_a)

    @process_info = {}
    @process_info['OMS'] = %x{ps auxww|grep OrderManagementSystem | grep -v grep}
    @process_info['JMS_Poller'] = %x{ps auxww|grep poller | grep -v grep}
    @process_info['ActiveMQ'] = %x{netstat -an | grep 61616}
    @process_info['STOMP'] = %x{netstat -an | grep 61613}
    @process_info['MySQL'] = %x{ps auxww|grep mysqld | grep -v grep}

    @networking_eth0 = %x{ifconfig eth0}

    output = %x{java -cp #{JAVA_JMX_CONNECTOR_LIB}  com.marketcetera.tools.JMXReader}
    @jmx_info = output.split("\n")

    @report = ServerInfo.new(@networking_eth0, (@xml_time - @local_time).abs, @host_resolution, @process_info, @marketcetera_line)
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
    File.open("/etc/hosts") do |file|
      file.each_line { |line|
        if (!line.index("marketcetera").nil?)
          @marketcetera_line = line
          break
        end
      }
    end
  end

end

