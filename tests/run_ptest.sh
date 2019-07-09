#!/bin/bash

trap "exit" INT

THISDIR=$(dirname $(readlink -f "$0"))
WORKDIR=$THISDIR/../../..

# deby-tiny has limited image size, it's better to use deby instead
TEST_DISTROS=deby
TEST_TARGETS=${TEST_TARGETS}
TEST_MACHINES=${TEST_MACHINES:-qemux86}

# SSH to QEMU machine through port 2222
SSH='ssh -o StrictHostKeyChecking=no -p 2222 root@127.0.0.1'
# Clean old key
ssh-keygen -f "$HOME/.ssh/known_hosts" -R "[127.0.0.1]:2222"

### Setup builddir ###
cd $WORKDIR
export TEMPLATECONF=meta-debian/conf
source ./poky/oe-init-build-env
echo "HOSTTOOLS_append = \" gitproxy\"" >> conf/local.conf
sed -i -e "s/\(^DISTRO\s*?*=\).*/\1 \"$TEST_DISTROS\"/" conf/local.conf

# Enable ptest
echo 'DISTRO_FEATURES_append = " ptest"' >> conf/local.conf
echo 'EXTRA_IMAGE_FEATURES += "ptest-pkgs"' >> conf/local.conf

# we use ssh for calling ptest, so add dropbear
echo "IMAGE_INSTALL_append = \" dropbear $TEST_TARGETS\"" >> conf/local.conf

# Get version of all recipes
all_versions=`pwd`/all_versions.txt
bitbake -s > $all_versions
if [ "$?" != "0" ]; then
	echo "ERROR: Failed to bitbake."
	exit 1
fi

for machine in $TEST_MACHINES; do
	sed -i -e "s/\(^MACHINE\s*?*=\).*/\1 \"$machine\"/" conf/local.conf

	bitbake core-image-minimal
	if [ "$?" != "0" ]; then
		echo "ERROR: Failed to build image for $machine."
		continue
	fi

	# Boot image with QEMU
	nohup runqemu $machine nographic slirp &

	# Wait for SSH
	timeout=60
	start=`date +%s`
	while ! $SSH "#" 2> /dev/null; do
			echo "NOTE: Waiting for SSH to be ready..."
			sleep 5
			now=`date +%s`
			waited=$((now-start))
		if [ $waited -gt $timeout ]; then
			echo "ERROR: Cannot connect to qemu machine."
			exit 1
		fi
	done

	LOGDIR=$THISDIR/logs/$TEST_DISTROS/$machine
	RESULT=$LOGDIR/result.txt

	mkdir -p $LOGDIR
	rm -f $RESULT
	touch $RESULT

	for target in $TEST_TARGETS; do
		version=`grep "^$target\s*:" $all_versions | cut -d: -f2 | sed "s/ *$//"`

		$SSH "ls /usr/lib/$target/ptest/" 2>&1 > $LOGDIR/${target}-ptest.log
		if [ "$?" != "0" ]; then
			echo "NOTE: ptest for $target is not available. Skip."
			echo "$target $version SKIP" >> $RESULT
			continue
		fi

		echo "NOTE: Testing $target ..."
		$SSH "cd /usr/lib/$target/ptest/ && ./run-ptest" 2>&1 > $LOGDIR/${target}-ptest.log

		if [ "$?" = "0" ]; then
			status=PASS
		else
			status=FAIL
		fi

		echo "NOTE: Test $target: $status"
		if grep -q "^$target " $RESULT 2> /dev/null; then
			sed -i -e "s/^\($target \)\S*\( \S* \)\S*/\1$version\2$status/" $RESULT
		else
			echo "$target $version NA $status" >> $RESULT
		fi
	done

	# Turn off machine after finish testing
	$SSH "/sbin/poweroff"
done
