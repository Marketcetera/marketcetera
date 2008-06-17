echo off
set PORT=%1
echo [DEFAULT] > cfg\at.cfg
echo ConnectionType=acceptor >> cfg\at.cfg
echo SocketAcceptPort=%PORT% >> cfg\at.cfg
echo SocketReuseAddress=Y >> cfg\at.cfg
echo StartTime=00:00:00 >> cfg\at.cfg
echo EndTime=00:00:00 >> cfg\at.cfg
echo SenderCompID=ISLD >> cfg\at.cfg
echo TargetCompID=TW >> cfg\at.cfg
echo ResetOnLogon=Y >> cfg\at.cfg
echo FileStorePath=store >> cfg\at.cfg
echo [SESSION] >> cfg\at.cfg
echo BeginString=FIX.4.0 >> cfg\at.cfg
echo DataDictionary=..\spec\FIX40.xml >> cfg\at.cfg
echo [SESSION] >> cfg\at.cfg
echo BeginString=FIX.4.1 >> cfg\at.cfg
echo DataDictionary=..\spec\FIX41.xml >> cfg\at.cfg
echo [SESSION] >> cfg\at.cfg
echo BeginString=FIX.4.2 >> cfg\at.cfg
echo DataDictionary=..\spec\FIX42.xml >> cfg\at.cfg
echo [SESSION] >> cfg\at.cfg
echo BeginString=FIX.4.3 >> cfg\at.cfg
echo DataDictionary=..\spec\FIX43.xml >> cfg\at.cfg
echo [SESSION] >> cfg\at.cfg
echo BeginString=FIX.4.4 >> cfg\at.cfg
echo DataDictionary=..\spec\FIX44.xml >> cfg\at.cfg
