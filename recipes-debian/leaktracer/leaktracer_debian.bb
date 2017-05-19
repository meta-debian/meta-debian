SUMMARY = "Simple and efficient memory-leak tracer for C++ programs"
DESCRIPTION = "LeakTracer traces calls to new and delete, and reports\n\
inconsistencies in the C++-level memory-management.\n\
.\n\
It has limitations (eg. when you override the new and delete\n\
operators yourself), but is very easy to use (eg. compared to more\n\
complete tools like mpatrol), traces the C++ level (unlike most other\n\
tools), and gives pretty good results.\n\
.\n\
It uses gdb to display source-file information."

PR = "r0"

inherit debian-package
PV = "2.4"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://README;beginline=192;endline=195;md5=534c638c0d1a099bb244718ec9eb2f1b"

DEBIAN_PATCH_TYPE = "nopatch"

RDEPENDS_${PN} = "gdb perl"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
	oe_runmake
}

do_install() {
	install -d ${D}${bindir} ${D}${libexecdir}
	cp -p leak-analyze ${D}${bindir}/
	sed "s,\`dirname \$0\`,${libexecdir}," < LeakCheck \
		> ${D}${bindir}/LeakCheck
	chmod +x ${D}${bindir}/LeakCheck
	cp -p LeakTracer.so ${D}${libexecdir}/
}
