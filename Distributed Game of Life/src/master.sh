#!/bin/bash
# Daniel Gibbs 2011
if [ $# -lt 2 ]; then
        echo "Computer names file and port number required. Exiting."
        exit 1;
fi

SCREEN_NAME="NWEN303Proj2D"
WORKER_EXEC="~/Distributed-Game-of-Life/Distributed Game of Life/src/worker.sh"
WORKER_ARGS="`hostname` ${2}"

NUM_WORKERS=0

for name in `cat ${1}`; do
        numusers=`ssh -x -o StrictHostKeyChecking=no -o ConnectTimeout=3 ${name} who | wc -l`
        runningalready=`ssh -x -o StrictHostKeyChecking=no -o ConnectTimeout=3 ${name} "ps aux" | grep -i screen | grep ${SCREEN_NAME} | wc -l`
        if [ ${numusers} -eq 0 -a ${runningalready} -eq 0 ]; then
                ssh -x -o StrictHostKeyChecking=no -o ConnectTimeout=3 ${name} "screen -dm -S ${SCREEN_NAME} ${WORKER_EXEC} ${WORKER_ARGS}"
                echo "Started worker on ${name}"
                NUM_WORKERS=$((NUM_WORKERS+1))
        fi
done

echo "Started ${NUM_WORKERS} from ${1}." 
 
