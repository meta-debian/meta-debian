SUMMARY = "instrumentation framework for building dynamic analysis tools"
DISCRIPTION = "Valgrind is a system for debugging and profiling Linux programs. With its tool \
 suite you can automatically detect many memory management and threading bugs, \
 avoiding hours of frustrating bug-hunting and making your programs more stable. \
 You can also perform detailed profiling to help speed up your programs and use \
 Valgrind to build new tools."
HOMEPAGE = "http://valgrind.org/"

PR = "r2"
inherit debian-package
PV = "3.10.0"

LICENSE = "GPLv2 & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46082167a314d785d012a244748d803 \
                    file://include/pub_tool_basics.h;beginline=1;endline=29;md5=e7071929a50d4b0fc27a3014b315b0f7 \
                    file://include/valgrind.h;beginline=1;endline=56;md5=92df8a1bde56fe2af70931ff55f6622f \
                    file://COPYING.DOCS;md5=8fdeb5abdb235a08e76835f8f3260215"

KEEP_NONARCH_BASELIB = "1"

X11DEPENDS = "virtual/libx11"
DEPENDS = "gdb ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11DEPENDS}', '', d)}"

# enable.building.on.4.x.kernel.patch:
#     Enable building valgrind on host with 4.x kernel.
SRC_URI += "file://enable.building.on.4.x.kernel.patch"

inherit autotools-brokensep

do_configure[depends] += "virtual/kernel:do_shared_workdir"
KERNEL_VERSION = "${@base_read_file('${STAGING_KERNEL_BUILDDIR}/kernel-abiversion')}"

EXTRA_OECONF = "--enable-tls"

do_configure_prepend() {
	sed -i -e 's#kernel=`uname -r`#kernel="${KERNEL_VERSION}"#' ${S}/configure.ac
}

do_install_append () {
	install -d ${D}${sysconfdir}/bash_completion.d
	install -m 0644 ${S}/debian/valgrind.bash-completion ${D}${sysconfdir}/bash_completion.d/valgrind

	# base on debian/valgrind.install
	install -m 0755 ${S}/debian/valgrind.sh ${D}${bindir}
	install -m 0644 ${S}/debian/supp/debian.supp ${D}${libdir}/valgrind/
	
	# follow debian/rules
	mv -f ${D}${bindir}/valgrind ${D}${bindir}/valgrind.bin
	mv -f ${D}${bindir}/valgrind.sh ${D}${bindir}/valgrind

	# remove unnecessary files	
	rm ${D}${includedir}/valgrind/pub*
	rm ${D}${includedir}/valgrind/lib*
	rm ${D}${includedir}/valgrind/config.h
	rm -rf ${D}${includedir}/valgrind/vki
}

# valgrind requires debug files from glibc to run
INSANE_SKIP_${PN} += "debug-deps"
RDEPENDS_${PN} += "glibc-dbg"
