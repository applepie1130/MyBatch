jarFilePath=/batch/MyTest-0.0.1-SNAPSHOT.jar
packageName=com.batch.action.CrawlingTaskAction
logPath=/tank0/log/batch/CrawlingTaskAction_crontab.log


if [ `ps -ef | grep MyTest | grep -v grep | wc -l` -gt 0 ] ; then
	kill -9 $(ps -ef | grep 'MyTest' | grep -v grep | awk '{print $2}')
fi

java -Dname=MyTest-0.0.1-SNAPSHOT -Dfile.encoding=utf-8 -cp $jarFilePath $packageName >> $logPath &