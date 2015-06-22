#
# linux-base_git.bb
#
# This recipe provides virtual/kernel.
# The purpose is to build various kernel sources by changing a few variables.
#
# LINUX_GIT_URI, LINUX_GIT_REPO, LINUX_GIT_PROTOCOL, LINUX_GIT_SRCREV:
#   Define the target git repository and source tree.
#   See linux-src.bbclass for more details.
# LINUX_DEFCONFIG, LINUX_CONFIG, LINUX_CONFIG_APPEND
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

# prevent bitbake from wasting a task for fetching the same source
do_fetch[depends] += "linux-libc-headers:do_fetch"

B = "${WORKDIR}/build"

# 3.9 or later kernel needs bc to build kernel/timeconst.h
DEPENDS += "bc-native"

# Variables to specify the base configuration.
# Either LINUX_DEFCONFIG or LINUX_CONFIG must be defined.
# LINUX_DEFCONFIG:
#   A defconfig file in ${S}/arch/${ARCH}/configs.
#   This variable is ignored if LINUX_CONFIG is set.
#   Usually used with LINUX_CONFIG_APPEND.
# LINUX_CONFIG:
#   A config file in FILESPATH.
# LINUX_CONFIG_APPEND:
#   A config file in FILESPATH, which includes additional configurations.
#   This file is always appended to LINUX_DEFCONFIG or LINUX_CONF if set.
LINUX_DEFCONFIG ?= ""
LINUX_CONFIG ?= ""
LINUX_CONFIG_APPEND ?= ""

# define the default kernel configuration for QEMU targets
require linux-base-qemu-config.inc

SRC_URI += " \
${@base_conditional('LINUX_CONFIG', '', '', 'file://${LINUX_CONFIG}', d)} \
${@base_conditional('LINUX_CONFIG_APPEND', '', '', 'file://${LINUX_CONFIG_APPEND}', d)} \
"

# Generate ${WORKDIR}/defconfig from specified config files.
# ${WORKDIR}/defconfig is copied to ${B}/.config by kernel_do_configure.
do_configure_prepend() {
	rm -f ${WORKDIR}/defconfig

	if [ -n "${LINUX_CONFIG}" ]; then
		DEFCONFIG=${WORKDIR}/${LINUX_CONFIG}
	elif [ -n "${LINUX_DEFCONFIG}" ]; then
		DEFCONFIG=${S}/arch/${ARCH}/configs/${LINUX_DEFCONFIG}
	else
		bbfatal "Both LINUX_DEFCONFIG and LINUX_CONFIG are not defined.
       Please set one of them at lease.
       LINUX_DEFCONFIG: a defconfig file in ${S}/arch/${ARCH}/configs
       LINUX_CONFIG: a config file in FILESPATH"
	fi
	if [ ! -f ${DEFCONFIG} ]; then
		bbfatal "${DEFCONFIG} not found"
	fi
	bbnote "use ${DEFCONFIG} as the base .config"
	cp ${DEFCONFIG} ${WORKDIR}/defconfig

	if [ -n "${LINUX_CONFIG_APPEND}" ]; then
		if [ -f "${WORKDIR}/${LINUX_CONFIG_APPEND}" ]; then
			bbnote "appending ${LINUX_CONFIG_APPEND}"
			cat ${WORKDIR}/${LINUX_CONFIG_APPEND} \
				>> ${WORKDIR}/defconfig
		else
			bbfatal "${LINUX_CONFIG_APPEND} not found in ${WORKDIR}"
		fi
	fi
}

# KERNEL_CONFIG_COMMAND is the final command in do_configure.
# Need to add the source & build directories into the command
# because kernel.bbclass assumes that ${S} is the same as ${B}.
# ARCH and CROSS_COMPILE are already exported, so no need to define them.
KERNEL_CONFIG_COMMAND = "oe_runmake_call O=${B} -C ${S} oldnoconfig"

# only vmlinux lies in the top of the build directory
KERNEL_OUTPUT = "${@base_conditional('KERNEL_IMAGETYPE', 'vmlinux', \
	'${KERNEL_IMAGETYPE}', 'arch/${ARCH}/boot/${KERNEL_IMAGETYPE}', d)}"

# Always use a static integer as KERNEL_PRIORITY, which is automatically
# calcurated from PV by default (see kernel.bbclass).
# This means that only one kernel package version is available in our system.
KERNEL_PRIORITY = "1"
