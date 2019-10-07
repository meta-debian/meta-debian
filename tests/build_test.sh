#!/bin/bash
#
# Script for building meta-debian.
#
# Input params from env:
#   TEST_PACKAGES: recipes/packages will be built. Eg: "zlib core-image-minimal".
#                 If not set, all meta-debian's recipes will be built.
#   TEST_DISTROS: distros will be tested. Eg: "deby deby-tiny"
#   TEST_MACHINES: machines will be tested. Eg: "raspberrypi3 qemuarm"
#   TEST_DISTRO_FEATURES: DISTRO_FEATURES will be used. Eg: "pam x11"

trap "exit" INT

THISDIR=$(dirname $(readlink -f "$0"))
WORKDIR=$THISDIR/../../..
POKYDIR=$THISDIR/../..

. $THISDIR/common.sh

# Dependencies of machine
declare -A LAYER_DEPS
declare -A LAYER_DEPS_URL
LAYER_DEPS[beaglebone]="meta-ti meta-debian/bsp/meta-ti"
LAYER_DEPS[raspberrypi3]="meta-raspberrypi meta-debian/bsp/meta-raspberrypi"

LAYER_DEPS_URL[beaglebone]="https://git.yoctoproject.org/git/meta-ti;branch=master"
LAYER_DEPS_URL[raspberrypi3]="https://git.yoctoproject.org/git/meta-raspberrypi;branch=warrior"

setup_builddir

add_or_replace "DISTRO_FEATURES_append" " $TEST_DISTRO_FEATURES" conf/local.conf

if [ "$TEST_PACKAGES" = "" ]; then
	TEST_PACKAGES_NOTSET=1
fi

for distro in $TEST_DISTROS; do
	note "Testing distro $distro ..."
	add_or_replace "DISTRO" "$distro" conf/local.conf
	for machine in $TEST_MACHINES; do
		LOGDIR=$THISDIR/logs/$distro/$machine
		RESULT=$LOGDIR/result.txt
		mkdir -p $LOGDIR

		note "Testing machine $machine ..."
		add_or_replace "MACHINE" "$machine" conf/local.conf

		# Get required layers
		for layer_url in ${LAYER_DEPS_URL[$machine]}; do
			url=`echo $layer_url | cut -d\; -f1`
			branch=`echo $layer_url | cut -d\; -f2 | sed -e "s/branch=//"`
			layer_dir=`basename $url | sed -e s/.git//`
			if [ ! -d $POKYDIR/$layer_dir ]; then
				git clone $url $POKYDIR/$layer_dir
				cd $POKYDIR/$layer_dir
				git checkout $branch
				cd -
			fi
		done

		# Add required layers to conf/bblayers.conf
		EXTRA_BBLAYERS=""
		for layer in ${LAYER_DEPS[$machine]}; do
			EXTRA_BBLAYERS="$EXTRA_BBLAYERS $POKYDIR/$layer"
		done
		add_or_replace "EXTRA_BBLAYERS" "$EXTRA_BBLAYERS"  conf/bblayers.conf

		if [ "$TEST_PACKAGES_NOTSET" = "1" ]; then
			note "TEST_PACKAGES is not defined. Getting all recipes available..."
			get_all_packages
			TEST_PACKAGES=$BTEST_PACKAGES
		fi

		note "These recipes will be tested: $TEST_PACKAGES"

		for package in $TEST_PACKAGES; do
			logfile=$LOGDIR/${package}.build.log
			note "Building $package ..."
			if [ "$VERBOSE" = "1" ]; then
				bitbake $package | tee $logfile
			else
				bitbake $package &> $logfile
			fi

			if [ "$?" = "0" ]; then
				status=PASS
			else
				status=FAIL
			fi

			note "Build $package: $status"
			if grep -q "^$package " $RESULT 2> /dev/null; then
				sed -i -e "s#^\($package \)\S*\( \S*\)#\1$status\2#" $RESULT
			else
				echo "$package $status NA" >> $RESULT
			fi
		done

		# Sort result file by alphabet
		sort -u $RESULT > result.tmp
		mv result.tmp $RESULT
	done
done
