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
#   Define the base kernel configurations. See the below comments.
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

# space separated file list of defconfig files in ${S}/arch/.../configs
# Example:
#   if LINUX_DEFCONFIG = "xxx_defconfig yyy_defconfig" is set,
#   ${S}/arch/.../configs/xxx_defconfig and
#   ${S}/arch/.../configs/yyy_defconfig are used as
#   the base configuration files in do_configure
LINUX_DEFCONFIG ?= ""

# space separated file list that users can freely specify from any local files
LINUX_CONFIG ?= ""

# define the default kernel configuration for QEMU targets
require linux-base-qemu-config.inc

# Generate ${WORKDIR}/defconfig from specified config files;
# LINUX_DEFCONFIG, .config files in SRC_URI, or LINUX_CONFIG.
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

	LOCAL_CONFIGS="${@' '.join(find_cfgs(d))}"
	if [ -n "${LINUX_DEFCONFIG}" ]; then
		DEFCONFIG=""
		bbnote "use the following defconfig in kernel source tree:"
		for dcfg in ${LINUX_DEFCONFIG}; do
			bbnote "    ${dcfg}"
			dcfg_path=${S}/arch/${KERNEL_SRCARCH}/configs/${dcfg}
			if [ ! -f ${dcfg_path} ]; then
				bbfatal "${dcfg_path} not found"
			fi
			DEFCONFIG="${DEFCONFIG} ${dcfg_path}"
		done
	else
		DEFCONFIG=
		bbnote "LINUX_DEFCONFIG not set, use only local config files"
		if [ -z "${LOCAL_CONFIGS}${LINUX_CONFIG}" ]; then
			bbfatal "No config file given
Please provide at least one of the following settings:
    LINUX_DEFCONFIG
    .config files in SRC_URI
    LINUX_CONFIG"
		fi
	fi

	bbnote "creating the final config with the following config files:"
	for cfg in ${DEFCONFIG} ${LOCAL_CONFIGS} ${LINUX_CONFIG}; do
		bbnote "    ${cfg}"
	done
	merge_config ${DEFCONFIG} ${LOCAL_CONFIGS} ${LINUX_CONFIG}

	if [ ! -f ${B}/.config ]; then
		bbfatal "merge_config: failed to create the final config file"
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
