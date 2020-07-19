#!/bin/bash

TIMEOUT=10

log () {
	echo $1
}

# first we need to wait while modem is present
tries=0	
while :; do
    tries=$((tries+1))
    if [ $tries -gt $TIMEOUT ] ; then
        error "RasPI boot failure: modem not initialized"
        log_end_msg 1
        exit 1
    fi

    lsusb | grep "12d1:1506" > /dev/null
    retval=$?
    if [ $retval -ne 0 ] ; then
        sleep 1
        log "Waiting for modem $tries seconds..."
    else
        log "Modem present"
        break
    fi
done

sudo nmcli connection up 3UK ifname ttyUSB0