#
# Base recipe: recipes-extended/pam/libpam_1.2.1.bb 
# Base branch: master
#

SUMMARY = "Linux-PAM (Pluggable Authentication Modules)"
DESCRIPTION = " \
Linux-PAM (Pluggable Authentication Modules for Linux), \
a flexible mechanism for authenticating users"
HOMEPAGE = "https://fedorahosted.org/linux-pam/"
BUGTRACKER = "https://fedorahosted.org/linux-pam/newticket"

PR = "r1"

inherit debian-package

DPN = "pam"

# PAM is dual licensed under GPL and BSD.
# /etc/pam.d comes from Debian libpam-runtime in 2009-11 (at that time
# libpam-runtime-1.0.1 is GPLv2+), by openembedded
LICENSE = "GPLv2+ | BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=7eb5c1bf854e8881005d673599ee74d3"

# Debian patches are in debian/patches-applied directory
DEBIAN_QUILT_PATCHES = "${DEBIAN_UNPACK_DIR}/debian/patches-applied"
DEBIAN_PATCH_TYPE = "quilt"

DEPENDS = "bison flex flex-native cracklib"

EXTRA_OECONF = "--with-db-uniquename=_pam \
		--includedir=${includedir}/security \
                --libdir=${base_libdir} \
                --sbindir=${base_sbindir} \
		--disable-nis \
                --disable-regenerate-docu \
		--disable-prelude"

inherit autotools gettext pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[audit] = "--enable-audit,--disable-audit,audit,"

# Install files follow Debian
do_install_append() {
	install -d ${D}${datadir}/pam-configs
	install -d ${D}${sysconfdir}/pam.d
	install -d ${D}/${datadir}/pam
	install -d ${D}${sbindir}
	install -m 0644 ${S}/debian/pam-configs/cracklib \
		     ${D}${datadir}/pam-configs
	mv ${D}${base_sbindir}/pam_timestamp_check ${D}${sbindir}
	install -m 0644 ${S}/debian/local/pam.conf ${D}${sysconfdir}
	install -m 0644 ${S}/debian/local/other ${D}/${sysconfdir}/pam.d
	install -m 0644 ${S}/debian/local/common-* ${D}/${datadir}/pam
	install -m 0755 ${S}/debian/local/pam_getenv ${D}${sbindir}
	install -m 0755 ${S}/debian/local/pam-auth-update \
			${D}${sbindir}
	install -m 0644 ${S}/debian/pam-configs/unix \
			${D}${datadir}/pam-configs
	rm -rf ${D}${base_libdir}/security/pam_filter

	# Prevent QA warnings about installing ${localstatedir}/run
	if [ -d ${D}${localstatedir}/run ]; then
		rm -rf ${D}${localstatedir}/run
	fi
}

PACKAGES =+ "${PN}-cracklib ${PN}-modules ${PN}-modules-bin \
		${PN}-runtime ${PN}-runtime-doc ${PN}0g ${PN}0g-dev"

FILES_${PN}-cracklib += "\
	${base_libdir}/security/pam_cracklib.so \
	${datadir}/pam-configs/cracklib"
FILES_${PN}-modules += "\
	${sysconfdir}/security/*.conf \
	${sysconfdir}/security/*.init"
FILES_${PN}-modules-bin += "\
	${base_sbindir}/* \
	${sbindir}/* "
FILES_${PN}-runtime += "\
	${sysconfdir}/pam.conf \
	${sysconfdir}/pam.d/other \
	${sbindir}/* "
FILES_${PN}-runtime-doc += "\
	${datadir}/pam/* \
	${datadir}/pam-configs/unix"
FILES_${PN}0g += "${base_libdir}/*.so.*"
FILES_${PN}-dbg += "\
	${base_libdir}/security/.debug \
	${base_libdir}/security/pam_filter/.debug \
	${datadir}/Linux-PAM/xtests/.debug"
FILES_${PN}0g-dev += "\
	${base_libdir}/security/*.la \
    	${base_libdir}/*.la \
    	${base_libdir}/lib*${SOLIBSDEV}"

# Split libpam module into subpackages
# Using python function to add RDEPENDS

PACKAGES_DYNAMIC += "pam-plugin-.*"

python populate_packages_prepend () {
    def pam_plugin_hook(file, pkg, pattern, format, basename):
        rdeps = d.getVar('RDEPENDS_${PN}-modules', True)
        if rdeps:
            rdeps = rdeps + " " + pkg 
        else:
            rdeps = pkg
        d.setVar('RDEPENDS_${PN}-modules', rdeps)

    pam_libdir = d.expand('${base_libdir}/security')
    pam_pkgname = 'pam-plugin%s'
    do_split_packages(d, pam_libdir, '^pam(.*)\.so$', pam_pkgname,
                      'PAM plugin for %s', hook=pam_plugin_hook, extra_depends='')
}

RDEPENDS_${PN}-modules += "${PN}0g ${PN}-modules-bin"
RDEPENDS_${PN}-runtime += "${PN}-modules"
RDEPENDS_${PN}-0g-dev += "${PN}-0g"
RDEPENDS_${PN}-cracklib += "${PN}-runtime"

python do_pam_sanity () {
    if not bb.utils.contains('DISTRO_FEATURES', 'pam', True, False, d):
        bb.warn("Building libpam but 'pam' isn't in DISTRO_FEATURES, PAM won't work correctly")
}
addtask pam_sanity before do_configure

BBCLASSEXTEND = "nativesdk native"
