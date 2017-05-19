DESCRIPTION = "authenticator for mod_authnz_external and the Apache HTTP Daemon \
 Pwauth is an authenticator designed to be used with mod_auth_external or \
 mod_authnz_external and the Apache HTTP Daemon to support reasonably secure web \
 authentication out of the system password database on most versions of Unix. \
 Particulary - secure authentication against PAM"

LICENSE = "unfs3"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=4c86acd733827cf20ba8abb9e7246d02"

SECTION = "utils"
DEPENDS = "libpam"

PR = "r0"
inherit debian-package
PV = "2.3.11"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
	install -d ${D}
	install -d ${D}${sbindir}
	install -d ${D}${sysconfdir}/pam.d/

	install -m 0755 ${S}/pwauth ${D}${sbindir}/ 
	install -m 0644 ${S}/debian/pam ${D}${sysconfdir}/pam.d/pwauth 
}
