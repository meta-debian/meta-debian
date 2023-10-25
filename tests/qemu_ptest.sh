#!/bin/bash
#
# This script builds core-image-minimal for qemu machine,
# boot it, then execute ptest.
#
# Input params from env:
#   TEST_DISTROS: distros will be tested. Eg: "deby deby-tiny"
#   TEST_PACKAGES: recipes/packages need to run ptest. Eg: "zlib quilt"
#   TEST_MACHINES: machines will be tested. Eg: "qemux86 qemuarm"
#   TEST_DISTRO_FEATURES: DISTRO_FEATURES will be used. Eg: "pam x11"
#   TEST_ENABLE_SECURITY_UPDATE: If 1 is set, enable security update repository.
#   PTEST_RUNNER_TIMEOUT: Timeout seconds for ptest-runner. Default: 300 seconds, Eg: 7200
#   QEMU_PARAMS: Specify custom parameters to QEMU. Eg: "-smp 2 -m 2048"
#     - `-smp`: Amount of CPU cores.
#     - `-m`:   Memory size(MB). Default: 512 MB
#   IMAGE_ROOTFS_EXTRA_SPACE: Extra space(KB) of rootfs for qemu machine.

trap "exit" INT
trap 'kill $(jobs -p)' EXIT

THISDIR=$(dirname $(readlink -f "$0"))
WORKDIR=$THISDIR/../../..
POKYDIR=$THISDIR/../..

. $THISDIR/common.sh

TEST_IPADDR=127.0.0.1
TEST_USER=root
TEST_PORT=2222

# Get layer version
LAYER_BASE_VER="meta:`git_head $POKYDIR`,meta-debian:`git_head $THISDIR`"

function ssh_qemu {
	ssh -o StrictHostKeyChecking=no -p $TEST_PORT $TEST_USER@$TEST_IPADDR "/bin/sh -l -c \"$1\""
}
function scp_qemu {
	scp -o StrictHostKeyChecking=no -P $TEST_PORT $@
}

# Clean old key
ssh-keygen -f "$HOME/.ssh/known_hosts" -R "[$TEST_IPADDR]:$TEST_PORT"

setup_builddir

if [ "$TEST_ENABLE_SECURITY_UPDATE" = "1" ]; then
	setup_security_update_repository
fi

# Enable ptest
append_var "DISTRO_FEATURES_append" " ptest" conf/local.conf
append_var "EXTRA_IMAGE_FEATURES_append" " ptest-pkgs" conf/local.conf

if [ "$TEST_PACKAGES" = "" ]; then
	note "TEST_PACKAGES is not defined. Getting all packages with ptest enabled..."
	get_all_packages
	TEST_PACKAGES=$PTEST_PACKAGES
else
	# Clean TEST_PACKAGES. Remove native and nativesdk packages if any.
	TEST_PACKAGES=`echo $TEST_PACKAGES | \
	              sed -e "s/\s*\S*-native\s*/ /g" \
	                  -e "s/\s*nativesdk-\S*\s*/ /g" \
	                  -e "s/\s*\S*-cross\s*/ /g" \
	                  -e "s/\s*\S*-crosssdk\s*/ /g" \
	                  -e "s/\s*\S*-cross-canadian\s*/ /g" \
	              `
fi
note "These packages will be tested: $TEST_PACKAGES"

EXTRA_IMAGE_INSTALL=""
for p in $TEST_PACKAGES; do
	EXTRA_IMAGE_INSTALL="$EXTRA_IMAGE_INSTALL $p-ptest"
done

# we use ssh for calling ptest, so add dropbear
set_var "IMAGE_INSTALL_append" " dropbear $EXTRA_IMAGE_INSTALL" conf/local.conf

for distro in $TEST_DISTROS; do
	note "Testing distro $distro ..."
	set_var "DISTRO" "$distro" conf/local.conf
	if [ "$distro" = "deby-tiny" ]; then
		# Start dropbear on boot
		set_var "INITTAB_APPEND_pn-busybox-inittab" "::sysinit:/etc/init.d/dropbear start" conf/local.conf
		# Boot with ext4 to avoid limited initramfs size
		set_var "IMAGE_FSTYPES_append" " ext4" conf/local.conf
		set_var "IMAGE_FSTYPES_remove" "cpio.gz" conf/local.conf
	fi

	for machine in $TEST_MACHINES; do
		LOGDIR=$THISDIR/logs/$distro/$machine
		RESULT=$LOGDIR/result.txt
		mkdir -p $LOGDIR

		note "Testing machine $machine ..."
		set_var "MACHINE" "$machine" conf/local.conf

		if [ -n "$IMAGE_ROOTFS_EXTRA_SPACE" ] && [[ "$IMAGE_ROOTFS_EXTRA_SPACE" =~ ^[0-9]+$ ]]; then
			note "Set IMAGE_ROOTFS_EXTRA_SPACE to $IMAGE_ROOTFS_EXTRA_SPACE KB."
			set_var "IMAGE_ROOTFS_EXTRA_SPACE" "$IMAGE_ROOTFS_EXTRA_SPACE" conf/local.conf
		fi

		bitbake core-image-minimal
		if [ "$?" != "0" ]; then
			error "Failed to build image for $machine."
			continue
		fi

		# Boot image with QEMU
		nohup runqemu $machine nographic slirp qemuparams="$QEMU_PARAMS" &

		# Wait for SSH
		timeout=60
		start=`date +%s`
		while ! ssh_qemu "#" 2> /dev/null; do
			sleep 5
			now=`date +%s`
			waited=$((now-start))
			note "Waiting for SSH to be ready... (${waited}s / ${timeout}s)"
			if [ $waited -gt $timeout ]; then
				error "Cannot connect to qemu machine."
				exit 1
			fi
		done

		# Run ptest
		scp_qemu $THISDIR/run_ptest.sh $TEST_USER@$TEST_IPADDR:/tmp/ > /dev/null
		ssh_qemu "VERBOSE=$VERBOSE PTEST_RUNNER_TIMEOUT='$PTEST_RUNNER_TIMEOUT' TEST_PACKAGES='$TEST_PACKAGES' $EXTRA_ENV /tmp/run_ptest.sh"
		scp_qemu $TEST_USER@$TEST_IPADDR:/tmp/ptest/* $LOGDIR/ > /dev/null

		# Merge result.ptest.txt to result.txt
		while IFS= read -r line; do
			package=`echo $line | awk '{print $1}'`
			status=`echo $line | awk '{print $2}'`
			if grep -q "^$package " $RESULT 2> /dev/null; then
				sed -i -e "s#^\($package \S* \)\S*\( \S* \)\S*#\1$status\2$LAYER_BASE_VER#" $RESULT
			else
				echo "$package NA $status NA $LAYER_BASE_VER" >> $RESULT
			fi
		done < $LOGDIR/result.ptest.txt

		ssh_qemu "/sbin/poweroff"
	done
done

