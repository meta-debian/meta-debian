#!/bin/sh
#
# This script can run independently on target machine
# or be called through 'qemu_ptest.sh'.
#
# Input params from env:
#   TEST_PACKAGES: packages need to run ptest

trap "exit" INT

LOGDIR=/tmp/ptest/
RESULT=$LOGDIR/result.ptest.txt
mkdir -p $LOGDIR
rm -f $RESULT

if [ "$TEST_PACKAGES" = "" ]; then
	echo "TEST_PACKAGES is not defined. Get all available ptest package on board."
	# Get all available ptest package on target machine
	TEST_PACKAGES=`ptest-runner -l | grep -v "Available ptests:" | cut -f1`
fi

for package in $TEST_PACKAGES; do
	logfile=$LOGDIR/${package}.ptest.log
	ptest_path="/usr/lib/$package/ptest"

	if [ ! -f $ptest_path/run-ptest ]; then
		echo "ptest for $package isn't available. Skip."
		status=NA
	else
		echo "Running ptest for $package ..."
		test -n "$PTEST_RUNNER_TIMEOUT" && timeout="-t $PTEST_RUNNER_TIMEOUT"
		if [ "$VERBOSE" = "1" ]; then
			ptest-runner $timeout $package 2>&1 | tee $logfile
		else
			ptest-runner $timeout $package &> $logfile
		fi

		pass=`grep "^PASS:" $logfile | wc -l`
		skip=`grep "^SKIP:" $logfile | wc -l`
		fail=`grep "^FAIL:" $logfile | wc -l`
		status="$pass/$skip/$fail"
		echo "$package: PASS/SKIP/FAIL = $status"
	fi

	echo "$package $status" >> $RESULT
done

# Sort result file by alphabet
sort -u $RESULT > result.tmp
mv result.tmp $RESULT