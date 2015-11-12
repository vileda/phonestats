#!/bin/bash

for i in $(eval echo "{1..$1}"); do
  curl -k \
	  -X POST \
	  --data "from=492111234567&to=4915791234567&direction=in&event=newCall&callId=1234$i6&user[]=Alice&user[]=Bob"\
	  http://localhost:8080/api/event/$2;
  	  sleep 0.05
done
