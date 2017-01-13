UMMARY = "An Open Source implementation of the Microsoft's .NET Framework"
DESCRIPTION = "This is part of the Mono project - http://mono-project.com"
HOMEPAGE = "http://www.mono-project.com/"

PR = "r0"
inherit debian-package
PV = "3.2.8+dfsg"

LICENSE = "LGPLv2+ & GPLv2+ & MIT & MPL-1.1"
LIC_FILES_CHKSUM = "\
	file://COPYING.LIB;md5=80862f3fd0e11a5fa0318070c54461ce \
	file://mcs/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
	file://mcs/LICENSE;md5=9331d7bdf59594f966a1db06adeefd3c \
	file://mcs/LICENSE.MPL;md5=bfe1f75d606912a4111c90743d6c7325 \
	file://mcs/LICENSE.GPL;md5=4f20d7df8c88ac40c2ae9467222d0376 \
	file://mcs/LICENSE.LGPL;md5=429262d6c41a35150ebec9533c828b8d"
inherit autotools-brokensep

DEPENDS = "perl-native"

inherit native

do_fix_libtool_name() {
	# inherit native will make that all native tools that are being
	# built are prefixed with something like "i686-linux-",
	# including libtool. Fix up some hardcoded libtool names:
	for i in "${S}"/runtime/*-wrapper.in; do
		sed -e "s/libtool/${BUILD_SYS}-libtool/" -i "${i}"
	done

	sed -e "s/slash\}libtool/slash\}${HOST_SYS}-libtool/" -i acinclude.m4
	sed -e "s/slash\}libtool/slash\}${HOST_SYS}-libtool/" -i libgc/acinclude.m4
	sed -e "s/slash\}libtool/slash\}${HOST_SYS}-libtool/" -i eglib/acinclude.m4
	libtoolize --force --copy
}

addtask fix_libtool_name after do_patch before do_configure

do_install_append() {
	rm -r ${D}${datadir}/libgc-mono
	rm ${D}${bindir}/*.py ${D}${bindir}/nunit*
	ln -s mcs ${D}${bindir}/mono-csc
}
