# bonita-server-logs
Living application to access server logs

## Prerequisites

* Bonita BPM 7.2+
* Administrator profile

## Installation steps

* Download [server logs application page](distrib/page-ServerLogs.zip)
* Download [server logs reset API](distrib/logsRestAPI-1.0.0.zip)
* Download [server logs application xml](distrib/bonita-server-logs-app.xml)
* Log to Bonita BPM Portal
* As Administrator, go to Resources section
* Click on `Add`, select the `logsRestAPI-1.0.0.zip` archive
* Click on `Add`, select the `page-ServerLogs.zip` archive
* Go to Applications section
* Click on `Import`, select the `bonita-server-logs-app.xml` file

You can now access the server logs via the application at `../apps/serverLog` as an Administrator
