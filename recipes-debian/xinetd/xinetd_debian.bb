#
# base recipe: meta/recipes-extended/xinetd/xinetd_2.3.15.bb
# base branch: master
# base commit: 17733cc6073df875c08bbc02565a7216324890f7
#

PR = "r0"

inherit debian-package
PV = "2.3.15"

# xinetd is a BSD-like license
# Apple and Gentoo say BSD here.
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=8ad8615198542444f84d28a6cf226dd8"

inherit autotools

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--with-libwrap,,tcp-wrappers"

# Follow debian/rules
EXTRA_OECONF += " \
    --with-loadavg \
    --prefix=${D}${prefix} --mandir=${D}${mandir} \
    --infodir=${D}${infodir} --sbindir=${D}${sbindir} \
"

do_configure() {
	# Looks like configure.in is broken, so we are skipping
	# rebuilding configure and are just using the shipped one
	( cd ${S}; gnu-configize --force )
	oe_runconf
}

do_install_append() {
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -m 0644 ${S}/debian/xinetd.default ${D}${sysconfdir}/default/xinetd
	install -m 0755 ${S}/debian/xinetd.init ${D}${sysconfdir}/init.d/xinetd

	install -m 0644 ${S}/debian/xinetd.conf ${D}${sysconfdir}/
	cp -r ${S}/debian/xinetd.d ${D}${sysconfdir}/
}

RPROVIDES_${PN} = "inet-superserver"

CONFFILES_${PN} = "${sysconfdir}/xinetd.conf"
