SUMMARY = "remote file distribution client and server"
DESCRIPTION = "Rdist is a program to maintain identical copies of files \
over multiple hosts. It preserves the owner, group, mode, and mtime of files \
if possible and can update programs that are executing"
HOMEPAGE = "http://www.magnicomp.com/rdist/"

PR = "r0"
inherit debian-package
PV = "6.1.5"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://Copyright;md5=3f47ec9f64b11c8192ee05a66b5c2755"

do_install_append () {
	#Install /usr/bin/rdist; /usr/bin/rdistd
	install -d ${D}${bindir}
	install -m 0755 ${S}/src/rdist ${D}${bindir}/
	install -m 0755 ${S}/src/rdistd ${D}${bindir}/
}
