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

PR = "r2"

inherit debian-package
PV = "1.1.8"

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
		--disable-prelude \
		--disable-selinux"

inherit autotools gettext pkgconfig

PACKAGECONFIG ??= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
"
PACKAGECONFIG[audit] = "--enable-audit,--disable-audit,audit,"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"

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
	
	# Add /etc/pam.d/common-*
	cp ${D}/${datadir}/pam/common-*   ${D}/${sysconfdir}/pam.d/
	rm ${D}/${sysconfdir}/pam.d/common-*.md5sums
	
	sed -i -e "s/\$session_primary/session [default=1]          pam_permit.so/g" \
		${D}/${sysconfdir}/pam.d/common-session
	sed -i -e "s/\$session_additional/session required              pam_unix.so/g" \
		${D}/${sysconfdir}/pam.d/common-session
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		echo "session optional              pam_systemd.so" >> ${D}/${sysconfdir}/pam.d/common-session
	fi

	sed -i -e "s/\$session_nonint_primary/session [default=1]          pam_permit.so/g" \
		${D}/${sysconfdir}/pam.d/common-session-noninteractive
	sed -i -e "s/\$session_nonint_additional/session required              pam_unix.so/g" \
		${D}/${sysconfdir}/pam.d/common-session-noninteractive

	sed -i -e "s/\$auth_primary/auth [success=1 default=ignore]          pam_unix.so nullok_secure/g" \
		${D}/${sysconfdir}/pam.d/common-auth
	sed -i -e "s/\$auth_additional//g" \
		${D}/${sysconfdir}/pam.d/common-auth

	sed -i -e "s/\$password_primary/password [success=1 default=ignore]    pam_unix.so obscure sha512/g" \
		${D}/${sysconfdir}/pam.d/common-password
	sed -i -e "s/\$password_additional//g" \
		${D}/${sysconfdir}/pam.d/common-password

	sed -i -e "s/\$account_primary/account [success=1 new_authtok_reqd=done default=ignore] pam_unix.so/g" \
		${D}/${sysconfdir}/pam.d/common-account
	sed -i -e "s/\$account_additional//g" \
		${D}/${sysconfdir}/pam.d/common-account
}

PACKAGES =+ "${PN}-cracklib ${PN}-modules ${PN}-modules-bin \
		${PN}-runtime ${PN}-runtime-doc ${PN}0g ${PN}0g-dev"

FILES_${PN}-cracklib += "\
	${base_libdir}/security/pam_cracklib.so \
	${datadir}/pam-configs/cracklib"
FILES_${PN}-modules += "\
	${sysconfdir}/security/*.conf \
	${sysconfdir}/security/*.init \
	${base_libdir}/security/*.so"
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

RDEPENDS_${PN}-modules += "${PN}0g ${PN}-modules-bin"
RDEPENDS_${PN}-runtime += "${PN}-modules"
RDEPENDS_${PN}0g-dev += "${PN}0g"
RDEPENDS_${PN}-cracklib += "${PN}-runtime"

python do_pam_sanity () {
    if not bb.utils.contains('DISTRO_FEATURES', 'pam', True, False, d):
        bb.warn("Building libpam but 'pam' isn't in DISTRO_FEATURES, PAM won't work correctly")
}
addtask pam_sanity before do_configure

BBCLASSEXTEND = "nativesdk native"
