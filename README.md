# bonita-server-logs
Living application to access server logs

## Prerequisites

* Bonita BPM 7.2+
* Administrator profile

## Installation steps

1. Download [server logs application page](distrib/page-ServerLogs.zip)
2. Download [server logs reset API](distrib/logsRestAPI-1.0.0.zip)
3. Download [server logs application xml](distrib/bonita-server-logs-app.xml)
4. Log to Bonita BPM Portal
5. As Administrator, go to Resources section
6. Click on `Add`, select the `logsRestAPI-1.0.0.zip` archive
7. Click on `Add`, select the `page-ServerLogs.zip` archive
8. Go to Applications section
9. Click on `Import`, select the `bonita-server-logs-app.xml` file

You can now access the server logs via the application at `../apps/serverLog` as an Administrator
