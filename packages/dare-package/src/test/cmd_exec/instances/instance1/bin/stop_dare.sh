cd /home/colin/marketcetera/workspaces/trunk4/code/public/packages/dare-package/src/test/cmd_exec/instances/instance1/bin
if [ -f dare.pid ]
then
    kill `cat dare.pid`
else
    kill `ps -ef | grep metc.instance=1 | grep java | awk '{print $2}'`
fi
