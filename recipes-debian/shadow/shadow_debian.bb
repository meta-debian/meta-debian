#
# base recipe: meta/recipes-extended/shadow/shadow.inc
# base branch: daisy
#

PR = "r1"

inherit debian-package
PV = "4.2"

SUMMARY = "Tools to change and administer password and group data"
HOMEPAGE = "http://pkg-shadow.alioth.debian.org"
BUGTRACKER = "https://alioth.debian.org/tracker/?group_id=30580"
SECTION = "base/utils"
LICENSE = "BSD | Artistic-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ed80ff1c2b40843cf5768e5229cf16e5 \
                    file://src/passwd.c;beginline=8;endline=30;md5=d83888ea14ae61951982d77125947661"

DEPENDS = "shadow-native bison-native"
DEPENDS_class-native = "bison-native"
DEPENDS_class-nativesdk = "bison-native"

SRC_URI += "\
    file://disable-build-man-dir.patch \
    file://force-enable-subids-when-cross-compiling.patch \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', '', 'file://pam_login-Remove-selinux-rule.patch', d)} \
"

inherit autotools gettext update-alternatives

EXTRA_OECONF += " \
		--disable-account-tools-setuid \
		--disable-shared \
		--enable-shadowgrp \
		--without-acl \
		--without-attr \
		--without-audit \
		--without-libcrack \
		--without-tcb \
		--with-group-name-max-length=24 \
		--enable-subordinate-ids=yes \
		${NSCDOPT} \
"

NSCDOPT = ""
NSCDOPT_class-native = "--without-nscd"
NSCDOPT_class-nativesdk = "--without-nscd"
NSCDOPT_libc-uclibc = " --without-nscd"
NSCDOPT_libc-glibc = "${@bb.utils.contains('DISTRO_FEATURES', 'libc-spawn', '--with-nscd', '--without-nscd', d)}"

PACKAGECONFIG = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
"
PACKAGECONFIG_class-native = ""
PACKAGECONFIG_class-nativesdk = ""
PACKAGECONFIG[pam] = "--with-libpam,--without-libpam,libpam,libpam-modules"
PACKAGECONFIG[attr] = "--with-attr,--without-attr,attr"
PACKAGECONFIG[acl] = "--with-acl,--without-acl,acl"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,selinux"

RDEPENDS_${PN} = " \
                  base-passwd \
"
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""

# Build falsely assumes that if --enable-libpam is set, we don't need to link against
# libcrypt. This breaks chsh.
BUILD_LDFLAGS_append_class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', bb.utils.contains('DISTRO_FEATURES', 'libc-crypt',  '-lcrypt', '', d), '', d)}"

do_install() {
	oe_runmake DESTDIR="${D}" sbindir="${base_sbindir}" usbindir="${sbindir}" install

	# Info dir listing isn't interesting at this point so remove it if it exists.
	if [ -e "${D}${infodir}/dir" ]; then
		rm -f ${D}${infodir}/dir
	fi
}

install_login(){
	install -c -m 444 ${S}/debian/securetty.linux ${D}${sysconfdir}/securetty
	install -c -m 444 ${S}/debian/login.defs ${D}${sysconfdir}/login.defs

	# Enable CREATE_HOME by default.
    sed -i 's/#CREATE_HOME/CREATE_HOME/g' ${D}${sysconfdir}/login.defs

    # As we are on an embedded system, ensure the users mailbox is in
    # ~/ not /var/spool/mail by default, as who knows where or how big
    # /var is. The system MDA will set this later anyway.
    sed -i 's/MAIL_DIR/#MAIL_DIR/g' ${D}${sysconfdir}/login.defs
    sed -i 's/#MAIL_FILE/MAIL_FILE/g' ${D}${sysconfdir}/login.defs

    # Disable checking emails.
    sed -i 's/MAIL_CHECK_ENAB/#MAIL_CHECK_ENAB/g' ${D}${sysconfdir}/login.defs

    # Comment out SU_NAME to work correctly with busybox
    # See Bug#5359 and Bug#7173
    sed -i 's:^SU_NAME:#SU_NAME:g' ${D}${sysconfdir}/login.defs

    # Use proper encryption for passwords
    sed -i 's/^#ENCRYPT_METHOD.*$/ENCRYPT_METHOD SHA512/' ${D}${sysconfdir}/login.defs

	# Handle link properly after rename, otherwise missing files would
    # lead rpm failed dependencies.
    ln -sf newgrp ${D}${bindir}/sg
}

install_passwd(){
	install -d ${D}/${base_sbindir}
	install -c -m 555 ${S}/debian/shadowconfig.sh ${D}/${base_sbindir}/shadowconfig
	install -c -m 644 ${S}/debian/useradd.default ${D}${sysconfdir}/default/useradd

	# Now we don't have a mail system. Disable mail creation for now.
    sed -i 's:/bin/bash:/bin/sh:g' ${D}${sysconfdir}/default/useradd
    sed -i '/^CREATE_MAIL_SPOOL/ s:^:#:' ${D}${sysconfdir}/default/useradd

    # Use users group by default
    sed -i 's,^GROUP=1000,GROUP=100,g' ${D}${sysconfdir}/default/useradd

	rm ${D}${sbindir}/vigr
    ln -sf vipw ${D}${sbindir}/vigr
	ln -sf cppw ${D}${sbindir}/cpgr
}

do_install_append() {
	install_login
	install_passwd

	# Ensure that the image has as a /var/spool/mail dir so shadow can
	# put mailboxes there if the user reconfigures shadow to its
	# defaults (see sed below).
	install -d ${D}${localstatedir}/spool/mail

	# Install pam files
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		install -d ${D}${sysconfdir}/pam.d
		install -m 0644 ${S}/debian/passwd.chfn.pam ${D}${sysconfdir}/pam.d/chfn
		install -m 0644 ${S}/debian/passwd.chpasswd.pam ${D}${sysconfdir}/pam.d/chpasswd
		install -m 0644 ${S}/debian/passwd.chsh.pam ${D}${sysconfdir}/pam.d/chsh
		install -m 0644 ${S}/debian/login.pam ${D}${sysconfdir}/pam.d/login
		install -m 0644 ${S}/debian/passwd.newusers.pam ${D}${sysconfdir}/pam.d/newusers
		install -m 0644 ${S}/debian/passwd.passwd.pam ${D}${sysconfdir}/pam.d/passwd
		install -m 0644 ${S}/debian/login.su.pam ${D}${sysconfdir}/pam.d/su
	fi

	install -d ${D}${sbindir} ${D}${base_bindir} 

    # Move binaries to the locations we want
    if [ "${bindir}" != "${base_bindir}" ]; then
            mv ${D}${bindir}/login ${D}${base_bindir}/login
            mv ${D}${bindir}/su ${D}${base_bindir}/su
    fi
}

PACKAGES =+ "login uidmap passwd"
FILES_login = "  ${base_bindir}/* \
                 ${sysconfdir}/login.defs \
                 ${sysconfdir}/securetty \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${sysconfdir}/pam.d/login', '', d)} \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${sysconfdir}/pam.d/su', '', d)} \
                 ${bindir}/faillog \
                 ${bindir}/lastlog \
                 ${bindir}/newgrp \
                 ${bindir}/sg \
                 ${sbindir}/nologin \
"
FILES_uidmap = " ${bindir}/newgidmap \
                 ${bindir}/newuidmap \
"
FILES_passwd = " ${sysconfdir}/default/useradd \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${sysconfdir}/pam.d/*', '', d)} \
                 ${base_sbindir}/shadowconfig \
                 ${bindir}/* \
                 ${sbindir}/* \
"
RDEPENDS_${PN} += "login uidmap passwd"

# Add update-alternatives definitions
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_login = "login su"
ALTERNATIVE_LINK_NAME[login] = "${base_bindir}/login" 
ALTERNATIVE_LINK_NAME[su] = "${base_bindir}/su" 

pkg_postinst_${PN} () {
    if [ "x$D" != "x" ]; then
      rootarg="--root $D"
    else
      rootarg=""
    fi

    pwconv $rootarg || exit 1
    grpconv $rootarg || exit 1
}

BBCLASSEXTEND = "native nativesdk"
