SUMMARY = "locate a command"
DESCRIPTION = "which returns the pathnames of the files (or links) \
which would be executed in the current environment, had its arguments \
been given as commands in a strictly POSIX-conformant shell.  \
It does this by searching the PATH for executable files matching \
the names of the arguments. It does not canon‐icalize path names."

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=f01a5203d50512fc4830b4332b696a9f"

inherit debian-package
require recipes-debian/sources/debianutils.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/debianutils"
DEBIAN_QUILT_PATCHES = ""

inherit update-alternatives

do_install() {
	install -D -m 0755 ${S}/which ${D}${bindir}/which
	install -D -m 0644 ${S}/which.1 ${D}${mandir}/man1/which.1
}

ALTERNATIVE_PRIORITY = "30"

ALTERNATIVE_${PN} = "which"
ALTERNATIVE_LINK_NAME[which] = "${bindir}/which"

ALTERNATIVE_${PN}-doc = "which.1"
ALTERNATIVE_LINK_NAME[which.1] = "${mandir}/man1/which.1"
