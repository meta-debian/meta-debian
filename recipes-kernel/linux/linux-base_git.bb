#
# linux-base_git.bb
#
# This recipe provides virtual/kernel.
# The purpose is to build various kernel sources by changing a few variables.
#
# LINUX_GIT_URI, LINUX_GIT_PROTOCOL,
# LINUX_GIT_PREFIX, LINUX_GIT_REPO, LINUX_GIT_SRCREV:
#   Define the target git repository and source tree.
#   See linux-src.bbclass for more details.
# LINUX_DEFCONFIG
#   Define the base kernel configuration. See the below comments.
# KERNEL_DEVICETREE
#   Define the target devicetrees. See linux-dtb.inc.
#
# The kernel source specified by the above variables is shared with
# linux-libc-headers-base_git.bb through linux-src.bbclass,
# in order to use only one kernel source in the system.
#

# based on OE-Core kernel functions
inherit kernel

# Use the common functions to generate DTBs.
# dtb files (e.g. device.dtb) defined in KERNEL_DEVICETREE are
# automatically generated. Do nothing if KERNEL_DEVICETREE is not set.
require recipes-kernel/linux/linux-dtb.inc

# use the same kernel source as linux-libc-headers-base_git.bb
inherit linux-src

# use the common functions to merge multiple configs
inherit merge-config

# prevent bitbake from wasting a task for fetching the same source
do_fetch[depends] += "linux-libc-headers:do_fetch"

B = "${WORKDIR}/build"

# 3.9 or later kernel needs bc to build kernel/timeconst.h
DEPENDS += "bc-native"

# if this file name is set, ${S}/arch/.../configs/${LINUX_DEFCONFIG}
# is used as the base configuration file in do_configure
LINUX_DEFCONFIG ?= ""

# define the default kernel configuration for QEMU targets
require linux-base-qemu-config.inc

# Generate ${WORKDIR}/defconfig from specified config files;
# LINUX_DEFCONFIG and .config files in SRC_URI.
# ${WORKDIR}/defconfig is copied to ${B}/.config by kernel_do_configure.
do_configure_prepend() {
	rm -f ${WORKDIR}/defconfig ${B}/.config

	# When ARCH is set to i386 or x86_64, we need to map ARCH to
	# the real name of src dir (x86) under arch/ of kenrel tree,
	# so that we can find correct source to copy.
	if [ "${ARCH}" = "i386" ] || [ "${ARCH}" = "x86_64" ]; then
		KERNEL_SRCARCH=x86
	else
		KERNEL_SRCARCH=${ARCH}
	fi

	if [ -n "${LINUX_DEFCONFIG}" ]; then
		DEFCONFIG=${S}/arch/${KERNEL_SRCARCH}/configs/${LINUX_DEFCONFIG}
		if [ ! -f ${DEFCONFIG} ]; then
			bbfatal "${DEFCONFIG} not found"
		fi
		bbnote "use ${DEFCONFIG} as the base configuration"
	else
		DEFCONFIG=
		bbnote "LINUX_DEFCONFIG not set, use only .configs in SRC_URI"
	fi

	bbnote "creating the final config with the following .config files:"
	LOCAL_CONFIGS="${@' '.join(find_cfgs(d))}"
	for cfg in ${DEFCONFIG} ${LOCAL_CONFIGS}; do
		bbnote "    ${cfg}"
	done
	merge_config ${DEFCONFIG} ${LOCAL_CONFIGS}

	if [ ! -f ${B}/.config ]; then
		bbfatal "no config file given
Please set LINUX_DEFCONFIG or add .config files into SRC_URI"
	fi
	mv ${B}/.config ${WORKDIR}/defconfig
}

# KERNEL_CONFIG_COMMAND is the final command in do_configure.
# Need to add the source & build directories into the command
# because kernel.bbclass assumes that ${S} is the same as ${B}.
# ARCH and CROSS_COMPILE are already exported, so no need to define them.
KERNEL_CONFIG_COMMAND = "oe_runmake_call O=${B} -C ${S} oldnoconfig"

# Always use a static integer as KERNEL_PRIORITY, which is automatically
# calcurated from PV by default (see kernel.bbclass).
# This means that only one kernel package version is available in our system.
KERNEL_PRIORITY = "1"

# extra tasks
addtask kernel_link_images after do_compile before do_strip
