require ${COREBASE}/meta/recipes-extended/shadow/shadow.inc

SRC_URI_remove_class-target = " \
           file://shadow-relaxed-usernames.patch \
           "

inherit debian-package
require recipes-debian/sources/shadow.inc
FILESEXTRAPATHS =. "${FILE_DIRNAME}/shadow:${COREBASE}/meta/recipes-extended/shadow/files:"

SRC_URI += " \
           ${@bb.utils.contains('PACKAGECONFIG', 'pam', '${PAM_SRC_URI}', '', d)} \
           file://0001-useradd-copy-extended-attributes-of-home_debian.patch \
           file://0001-update-manpages.patch \
           "

SRC_URI_append_class-target = " \
           file://Update-man-man_nopam.patch \
           "

SRC_URI_append_class-nativesdk = " \
           file://Update-man-man_nopam.patch \
           "

# Build falsely assumes that if --enable-libpam is set, we don't need to link against
# libcrypt. This breaks chsh.
BUILD_LDFLAGS_append_class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', bb.utils.contains('DISTRO_FEATURES', 'libc-crypt',  '-lcrypt', '', d), '', d)}"

BBCLASSEXTEND = "native nativesdk"

DEPENDS += "bison-native"
EXTRA_OECONF += " --disable-man"

# debian package does not provide nologin.
# Remove nologin from ALTERNATIVE_LINK_NAME.
python __anonymous() {
    __package_name = d.getVar('PN')

    __v = d.getVar('ALTERNATIVE_%s' % __package_name)
    __v = __v.replace("nologin", "")
    d.setVar('ALTERNATIVE_%s' % __package_name, __v)

    __v = d.getVar('ALTERNATIVE_%s-doc' % __package_name)
    __v = __v.replace('nologin.8', '')
    d.setVar('ALTERNATIVE_%s-doc' % __package_name, __v)

    d.delVarFlag('ALTERNATIVE_LINK_NAME', 'nologin')
    d.delVarFlag('ALTERNATIVE_LINK_NAME', 'nologin.8')
}

do_install_append() {
	# This config should be handled by pam. Error:
	#   configuration error - unknown item 'FAIL_DELAY'
	sed -i -e 's/FAIL_DELAY/#FAIL_DELAY/g' ${D}${sysconfdir}/login.defs

	# /var/spool/mail is already provided by base-files
	rmdir --ignore-fail-on-non-empty ${D}${localstatedir}/spool/mail
}

pkg_postinst_${PN}() {
	# Ensure that the image has as a /var/spool/mail dir so shadow can
	# put mailboxes there if the user reconfigures shadow to its
	# defaults (see sed below).
	if ! ls $D${localstatedir}/spool/mail 2> /dev/null; then
		install -m 0775 -d $D${localstatedir}/spool/mail
		chown root:mail $D${localstatedir}/spool/mail
	fi
}
