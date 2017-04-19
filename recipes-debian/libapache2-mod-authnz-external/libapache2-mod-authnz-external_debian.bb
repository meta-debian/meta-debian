SUMMARY = "authenticate Apache against external authentication services"
DESCRIPTION = "Mod_Authnz_External can be used to quickly construct secure, reliable\n\
authentication systems.  It can also be mis-used to quickly open gaping\n\
holes in your security.  Read the documentation, and use with extreme\n\
caution.\n\
.\n\
Notably, this module can be used to securely authenticate against PAM\n\
(without exposing /etc/shadow file), using, for example, pwauth\n\
authenticator.\n\
.\n\
This Package includes the mod-authnz-external Module for Apache Version 2.x"
HOMEPAGE = "http://code.google.com/p/mod-auth-external"

PR = "r0"

inherit debian-package
PV = "3.3.2"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://mod_authnz_external.c;endline=51;md5=a69d0121d4f89aa67b2cc840b9c0958b"

DEPENDS = "apache2"

do_compile() {
	# Follow debian/rules
	${STAGING_BINDIR_CROSS}/apxs -c -Wc,-fno-strict-aliasing mod_authnz_external.c
}

do_install() {
	install -d ${D}${sysconfdir}/apache2/mods-available \
	           ${D}${libdir}/apache2/modules
	cp ${S}/.libs/mod_authnz_external.so ${D}${libdir}/apache2/modules/
	cp ${S}/debian/authnz_external.load ${D}${sysconfdir}/apache2/mods-available/
}

FILES_${PN} += "${libdir}/apache2/modules/*"
FILES_${PN}-dbg += "${libdir}/apache2/modules/.debug"
