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
SECTION = "console/tests"

PR = "r0"

inherit debian-package

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "libaio zlib"

PACKAGECONFIG_NUMA = ""
# ARM does not currently support NUMA
PACKAGECONFIG_NUMA_arm = ""

PACKAGECONFIG ??= "${PACKAGECONFIG_NUMA}"
PACKAGECONFIG[numa] = ",--disable-numa,numactl"

EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}'"

do_configure() {
	./configure ${EXTRA_OECONF}
}

do_install() {
	oe_runmake install DESTDIR=${D} prefix=${prefix} mandir=${mandir}
	install -d ${D}/${docdir}/${PN}
	cp -a ${S}/examples ${D}/${docdir}/${PN}/
}
