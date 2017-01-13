# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/meta-oe/recipes-benchmark/fio/fio_2.2.6.bb?h=master
# base branch: master

SUMMARY = "Filesystem and hardware benchmark and stress tool"
DESCRIPTION = "fio is an I/O tool meant to be used both for benchmark and \
stress/hardware verification. It has support for a number of I/O engines, \
I/O priorities (for newer Linux kernels), rate I/O, forked or threaded jobs, \
and much more. It can work on block devices as well as files. fio accepts \
job descriptions in a simple-to-understand text format. Several example job \
files are included. fio displays all sorts of I/O performance information."

HOMEPAGE = "http://freecode.com/projects/fio"

PR = "r1"

inherit debian-package
PV = "2.1.11"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "libaio zlib "

PACKAGECONFIG_NUMA = ""
# ARM does not currently support NUMA
PACKAGECONFIG_NUMA_arm = ""

PACKAGECONFIG ??= "${PACKAGECONFIG_NUMA}"
PACKAGECONFIG[numa] = ",--disable-numa,numactl"

EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}'"

do_configure () {
	# define macro ARM_ARCH if the TARGET_ARCH is arm
	case "${DEFAULTTUNE}" in
		"armv1") export ARM_ARCH="__ARM_ARCH_1__"
		;;
		"armv2") export ARM_ARCH="__ARM_ARCH_2__"
		;;
		"armv2a") export ARM_ARCH="__ARM_ARCH_2A__"
		;;
		"armv3") export ARM_ARCH="__ARM_ARCH_3__"
		;;
		"armv4t") export ARM_ARCH="__ARM_ARCH_4T__"
		;;
		"armv4") export ARM_ARCH="__ARM_ARCH_4__"
		;;
		"armv5tej") export ARM_ARCH="__ARM_ARCH_5TEJ__"
		;;
		"armv5te") export ARM_ARCH="__ARM_ARCH_5TE__"
		;;
		"armv6") export ARM_ARCH="__ARM_ARCH_6__"
		;;
		"armv6t2") export ARM_ARCH="__ARM_ARCH_6T2__"
		;;
		"armv6z") export ARM_ARCH="__ARM_ARCH_6Z__"
		;;
		"armv6k") export ARM_ARCH="__ARM_ARCH_6K__"
		;;
		"armv6-m") export ARM_ARCH="__ARM_ARCH_6-M__"
		;;			
		"armv4t") export ARM_ARCH="__ARM_ARCH_4T__"
		;;
		"armv6-m") export ARM_ARCH="__ARM_ARCH_6-M__"
		;;
		"armv7-m") export ARM_ARCH="__ARM_ARCH_7-M__"
		;;
		"armv7e-m") export ARM_ARCH="__ARM_ARCH_7E-M__"
		;;
		"armv7-r") export ARM_ARCH="__ARM_ARCH_7-R__"
		;;
		"armv7-a") export ARM_ARCH="__ARM_ARCH_7-A__"
		;;
		"armv8-a") export ARM_ARCH="__ARM_ARCH_8-A__"
		;;
	esac
		
	if [ "${TARGET_ARCH}" = "arm" ]
	then	
		./configure ${EXTRA_OECONF} --extra-cflags="-D${ARM_ARCH}"
	else
		./configure ${EXTRA_OECONF}
	fi
}

do_install() {
	oe_runmake install DESTDIR=${D} prefix=${prefix} mandir=${mandir}
	install -d ${D}/${docdir}/${PN}
	cp -a ${S}/examples ${D}/${docdir}/${PN}/
}
