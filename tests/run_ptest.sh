#!/bin/bash
#
# Script for run ptest.
# Input params from env:
#   TEST_DISTROS: distros will be tested. Eg: "deby deby-tiny"
#   TEST_TARGETS: recipes/packages will be run ptest. Eg: "zlib core-image-minimal"
#   TEST_MACHINES: machines will be tested. Currently only support qemu machine.
#                  Eg: "qemux86 qemuarm"
#   TEST_DISTRO_FEATURES: DISTRO_FEATURES will be used. Eg: "pam x11"

trap "exit" INT

THISDIR=$(dirname $(readlink -f "$0"))
WORKDIR=$THISDIR/../../..

. $THISDIR/common.sh

# deby-tiny has limited image size, it's better to use deby instead
TEST_DISTROS=${TEST_DISTROS:-deby-tiny}
TEST_TARGETS=${TEST_TARGETS}
TEST_MACHINES=${TEST_MACHINES:-qemux86}

# Clean TEST_TARGETS. Remove native and nativesdk packages if any.
TEST_TARGETS=`echo $TEST_TARGETS | sed -e "s/\s*\S*-native\s*/ /g" \
                                       -e "s/\s*nativesdk-\S*\s*/ /g" \
                                       -e "s/\s*\S*-cross\s*/ /g" \
                                       -e "s/\s*\S*-crosssdk\s*/ /g" \
                                       -e "s/\s*\S*-cross-canadian\s*/ /g" \
                                       `

# SSH to QEMU machine through port 2222
SSH='ssh -o StrictHostKeyChecking=no -p 2222 root@127.0.0.1'
# Clean old key
ssh-keygen -f "$HOME/.ssh/known_hosts" -R "[127.0.0.1]:2222"

setup_builddir

all_versions=`pwd`/all_versions.txt
all_recipes_version "$all_versions"

# Enable ptest
add_or_replace "DISTRO_FEATURES_append" " ptest $TEST_DISTRO_FEATURES" conf/local.conf
add_or_replace "EXTRA_IMAGE_FEATURES_append" " ptest-pkgs" conf/local.conf

# we use ssh for calling ptest, so add dropbear
add_or_replace "IMAGE_INSTALL_append" " dropbear $TEST_TARGETS" conf/local.conf

for distro in $TEST_DISTROS; do
	add_or_replace "DISTRO" "$distro" conf/local.conf
	if [ "$distro" = "deby-tiny" ]; then
		# Start dropbear on boot
		add_or_replace "INITTAB_APPEND_pn-busybox-inittab" "::sysinit:/etc/init.d/dropbear start" conf/local.conf
		# Boot with ext4 to avoid limited initramfs size
		add_or_replace "IMAGE_FSTYPES_append" " ext4" conf/local.conf
		add_or_replace "IMAGE_FSTYPES_remove" "cpio.gz" conf/local.conf
	fi

	for machine in $TEST_MACHINES; do
		note "Testing machine $machine ..."
		add_or_replace "MACHINE" "$machine" conf/local.conf

		bitbake core-image-minimal
		if [ "$?" != "0" ]; then
			error "Failed to build image for $machine."
			continue
		fi

		# Boot image with QEMU
		nohup runqemu $machine nographic slirp &

		# Wait for SSH
		timeout=60
		start=`date +%s`
		while ! $SSH "#" 2> /dev/null; do
				note "Waiting for SSH to be ready..."
				sleep 5
				now=`date +%s`
				waited=$((now-start))
			if [ $waited -gt $timeout ]; then
				error "Cannot connect to qemu machine."
				exit 1
			fi
		done

		LOGDIR=$THISDIR/logs/$distro/$machine
		RESULT=$LOGDIR/result.txt

		mkdir -p $LOGDIR

		for target in $TEST_TARGETS; do
			get_version "$all_versions"

			$SSH "ls /usr/lib/$target/ptest/" &> $LOGDIR/${target}.ptest.log
			if [ "$?" != "0" ]; then
				note "ptest for $target is not available. Skip."
				status=NA
			else
				note "Running ptest for $target ..."
				$SSH "cd /usr/lib/$target/ptest/ && ./run-ptest" &>> $LOGDIR/${target}.ptest.log

				if [ "$?" = "0" ]; then
					status=PASS
				else
					status=FAIL
				fi
			fi

			note "Run ptest for $target: $status"
			if grep -q "^$target $version" $RESULT 2> /dev/null; then
				sed -i -e "s/^\($target $version \S* \)\S*/\1$status/" $RESULT
			else
				# Remove old version
				if grep -q "^$target " $RESULT 2> /dev/null; then
					sed -i "/^$target /d" $RESULT
				fi

				echo "$target $version NA $status" >> $RESULT
			fi
		done

		# Turn off machine after finish testing
		$SSH "/sbin/poweroff"

		# Sort result file by alphabet
		sort -u $RESULT > result.tmp
		mv result.tmp $RESULT
	done
done