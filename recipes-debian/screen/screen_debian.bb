#
# base recipe: meta/recipes-extended/screen/screen_4.3.1.bb
# base branch: jethro
#

SUMMARY = "terminal multiplexer with VT100/ANSI terminal emulation"
DESCRIPTION = "GNU Screen is a terminal multiplexer that runs several separate "screens" on\n\
a single physical character-based terminal. Each virtual terminal emulates a\n\
DEC VT100 plus several ANSI X3.64 and ISO 2022 functions. Screen sessions\n\
can be detached and resumed later on a different terminal.\n\
.\n\
Screen also supports a whole slew of other features, including configurable\n\
input and output translation, serial port support, configurable logging,\n\
and multi-user support."
HOMEPAGE = "http://savannah.gnu.org/projects/screen"

PR = "r0"

inherit debian-package
PV = "4.2.1"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
    file://screen.h;endline=26;md5=3971142989289a8198a544220703c2bf \
"

DEPENDS = "ncurses"
RDEPENDS_${PN} = "base-files"

# http://savannah.gnu.org/bugs/?43223
# Avoid error when cross compile:
# 	| configure: error: cannot run test program while cross compiling
SRC_URI += " \
    file://Remove-redundant-compiler-sanity-checks.patch \
    file://Provide-cross-compile-alternatives-for-AC_TRY_RUN.patch \
    file://Skip-host-file-system-checks-when-cross-compiling.patch \
    file://Avoid-mis-identifying-systems-as-SVR4.patch \
    file://fix-parallel-make.patch \
    file://0001-fix-for-multijob-build.patch \
"

inherit autotools-brokensep texinfo

# Configure follow debian/rules
TTYGROUP = "5"
EXTRA_OECONF = "--with-socket-dir=${localstatedir}/run/screen \
                --with-pty-mode=0620 \
                --with-pty-group=${TTYGROUP} \
                --enable-rxvt_osc \
                --with-sys-screenrc=${sysconfdir}/screenrc \
                --enable-colors256 \
                --enable-telnet \
                --enable-use-locale"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[pam] = "--enable-pam,--disable-pam,libpam"

do_configure_append() {
	# Assert the use of fifos instead of sockets
	grep -q "define.*NAMEDPIPE.*1" ${B}/config.h || echo "#define NAMEDPIPE 1" >> ${B}/config.h
}

do_install_append() {
	# Follow debian/rules
	# hack around the fact that the install target makes screen a symlink to screen-$$(VERSION)
        rm -f ${D}${bindir}/screen
	mv -f ${D}${bindir}/screen* ${D}${bindir}/screen

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/init ${D}${sysconfdir}/init.d/screen-cleanup

	# Follow debian/install
	install -m 0644 ${S}/debian/screenrc ${D}${sysconfdir}/

	# Follow debian/dirs
	install -d ${D}${sysconfdir}/tmpfiles.d

	# Follow debian/links
	install -d ${D}${systemd_system_unitdir}
	ln -sf /dev/null ${D}${systemd_system_unitdir}/screen-cleanup.service

	install -d ${D}${libdir}/tmpfiles.d
	install -m 0644 ${S}/debian/tmpfile ${D}${libdir}/tmpfiles.d/screen-cleanup.conf
}

FILES_${PN} += "${systemd_system_unitdir} ${libdir}/tmpfiles.d"

# Follow debian/postinst
pkg_postinst_${PN}() {
	# make it setgid utmp
	chown root:utmp $D${bindir}/screen
	chmod 2755 $D${bindir}/screen

	if ! test -d $D${localstatedir}/run/screen; then
		install -g utmp -m 0775 -d $D${localstatedir}/run/screen
	fi
	perms="`stat -c%a $D${bindir}/screen`"
	override=$D${sysconfdir}/tmpfiles.d/screen-cleanup.conf
	if [ $perms -eq 4755 ]; then
		chmod 0755 $D${localstatedir}/run/screen
		[ -f $override ] || echo 'd ${localstatedir}/run/screen 0755 root utmp' > $override
	elif [ $perms -eq 755 ]; then
		chmod 1777 $D${localstatedir}/run/screen
		[ -f $override ] || echo 'd ${localstatedir}/run/screen 1777 root utmp' > $override
	fi

	grep -q "^${bindir}/screen$" $D${sysconfdir}/shells || echo ${bindir}/screen >> $D${sysconfdir}/shells
}

# Follow debian/postrm
pkg_postrm_${PN}() {
	rm -rf $D${localstatedir}/run/screen
	printf "$(grep -v "^${bindir}/screen$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}
