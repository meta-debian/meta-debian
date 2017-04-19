SUMMARY = "mechanism-switch gssapi library"
DESCRIPTION = "This library exports a gssapi interface, but doesn't implement any \
gssapi mechanisms itself; instead it calls gssapi routines in other libraries, \
depending on the mechanism."

inherit debian-package autotools
PV = "0.4"

PR = "r0"

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=56871e72a5c475289c0d5e4ba3f2ee3a"

do_install_append() {
	install -d ${D}/etc/
	install -m 0644 ${S}/doc/gssapi_mech.conf ${D}/etc/
}
