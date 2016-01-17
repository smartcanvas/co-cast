#!/usr/bin/env bash

export AUTH=AIzaSyAkKUFnpuyAlFx7T-2x5p38gjD1prhysRU
export REG_ID=dIBN3VDc3xo:APA91bGur8HBtfRW--5KSraYHwyX2-2JT4WSo6cZZaO57Xlf8YolCdIHUGy6yyMBDChPquIUNSSAicP9cNx7Q88JZyuKgPKUXCPY99gmktNsohGWiankOyzS5cRHDfgOtYUbXhpOv4hY
export TYPE=NOTIFICATION
export MESSAGE=Teste
curl -i -H'Authorization: key=AIzaSyAkKUFnpuyAlFx7T-2x5p38gjD1prhysRU' -H "Content-Type:application/json" https://gcm-http.googleapis.com/gcm/send -d "{\"to\": \"dIBN3VDc3xo:APA91bGur8HBtfRW--5KSraYHwyX2-2JT4WSo6cZZaO57Xlf8YolCdIHUGy6yyMBDChPquIUNSSAicP9cNx7Q88JZyuKgPKUXCPY99gmktNsohGWiankOyzS5cRHDfgOtYUbXhpOv4hY\", \"data\": { \"NOTIFICATION\": \"TESTE\" }}"