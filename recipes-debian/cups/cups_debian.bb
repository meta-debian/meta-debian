#
# Base recipes: meta/recipes-extended/cups/cups.inc
#               meta/recipes-extended/cups/cups_1.7.1.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "An Internet printing system for Unix"
SECTION = "console/utils"

inherit debian-package autotools-brokensep binconfig
PV = "1.7.5"

PR = "r0"
DEPENDS = "gnutls libpng libjpeg-turbo dbus dbus-glib zlib libusb1"

LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c5e50cb4b8f24b04636b719683a9102d"

PROVIDES = "cups14"

SRC_URI += "\
	file://0001-don-t-try-to-run-generated-binaries_debian.patch \
	file://cups_serverbin.patch \
"

PACKAGECONFIG ??= "avahi \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"

PACKAGECONFIG[avahi] = "--enable-avahi --enable-dnssd,--disable-avahi --disable-dnssd,avahi"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl"
PACKAGECONFIG[pam] = "--enable-pam, --disable-pam, libpam"

EXTRA_OECONF = " \
               --enable-gnutls \
               --enable-dbus \
               --enable-browsing \
               --disable-openssl \
               --disable-gssapi \
               --enable-debug \
               --disable-relro \
               --enable-libusb \
               --without-php \
               --without-perl \
               --without-python \
               --without-java \
               "

# Add more conf options according to Debian rules
EXTRA_OECONF += " \
	--with-docdir=/usr/share/cups/doc-root \
	--localedir=/usr/share/cups/locale \
	--enable-libpaper \
	--enable-ssl \
	--enable-threads \
	--enable-static \
	--with-dbusdir=/etc/dbus-1 \
	--disable-launchd \
	--with-cups-group=lp \
	--with-system-groups=lpadmin \
	--with-printcap=/var/run/cups/printcap \
	--with-log-file-perm=0640 \
	--with-local_protocols='dnssd' \
	--with-systemdsystemunitdir=/lib/systemd/system \
	"

do_configure() {
	gnu-configize
	libtoolize --force
	autoconf
	DSOFLAGS="${LDFLAGS}" SERVERBIN="${libdir}/cups" oe_runconf
}

do_compile () {
	sed -i s:STRIP:NOSTRIP: Makedefs
	sed -i s:serial:: backend/Makefile

	echo "all:"    >  man/Makefile
	echo "libs:" >> man/Makefile
	echo "install:" >> man/Makefile
	echo "install-data:" >> man/Makefile
	echo "install-exec:" >> man/Makefile
	echo "install-headers:" >> man/Makefile
	echo "install-libs:" >> man/Makefile

	oe_runmake "SSLLIBS=-lgnutls -L${STAGING_LIBDIR}" \
		   "LIBPNG=-lpng -lm -L${STAGING_LIBDIR}" \
		   "LIBJPEG=-ljpeg -L${STAGING_LIBDIR}" \
		   "LIBZ=-lz -L${STAGING_LIBDIR}" \
		   "-I."
}

fakeroot do_install () {
	oe_runmake "DSTROOT=${D}" install

	# Remove /var/run from package as cupsd will populate it on startup
	rm -fr ${D}/${localstatedir}/run
	rmdir ${D}/${libdir}/${BPN}/driver
}

# Correct locations, path according to Debian rules
do_install_append() {
	install -d ${D}${libdir}/cups/backend-available
	mv ${D}${libdir}/cups/backend/lpd ${D}${libdir}/cups/backend-available/
	mv ${D}${libdir}/cups/backend/socket ${D}${libdir}/cups/backend-available/
	mv ${D}${libdir}/cups/backend/usb ${D}${libdir}/cups/backend-available/
	mv ${D}${libdir}/cups/backend/snmp ${D}${libdir}/cups/backend-available/
	mv ${D}${libdir}/cups/backend/dnssd ${D}${libdir}/cups/backend-available/

	install -D -m 644 ${S}/debian/local/apparmor-profile ${D}${sysconfdir}/apparmor.d/usr.sbin.cupsd
	install -D -m 644 ${S}/debian/local/cups.ufw.profile ${D}${sysconfdir}/ufw/applications.d/cups
	install -D -m 644 ${S}/debian/local/apport-hook.py ${D}${datadir}/apport/package-hooks/source_cups.py

	# Manual install pam file
	install -D -m 644 ${S}/debian/cups-daemon.cups.pam ${D}${sysconfdir}/pam.d/cups
}

python do_package_append() {
    import subprocess
    # Change permissions back the way they were, they probably had a reason...
    workdir = d.getVar('WORKDIR', True)
    subprocess.call('chmod 0511 %s/install/cups/var/run/cups/certs' % workdir, shell=True)
}

# Re-arrange list of packages build from source
PACKAGES = "${PN} libcups2 ${PN}-dev ${PN}-staticdev ${PN}-dbg ${PN}-libimage \
	${PN}-bsd ${PN}-client ${PN}-common ${PN}-core-drivers ${PN}-daemon \
	${PN}-ppdc ${PN}-server-common libcupscgi1 libcupscgi-dev \
	libcupsimage2 libcupsimage2-dev libcupsmime1 libcupsmime-dev \
	libcupsppdc1 libcupsppdc-dev"

FILES_${PN} = " \
	${libdir}/cups/backend-available/dnssd \
	${libdir}/cups/backend-available/lpd \
	${libdir}/cups/backend-available/snmp \
	${libdir}/cups/backend-available/socket \
	${libdir}/cups/backend-available/usb \
	${libdir}/cups/cgi-bin/*.cgi \
	${libdir}/cups/filter/rastert* \
	${libdir}/cups/monitor/* \
	${libdir}/cups/notifier/* \
	${libdir}/cups/daemon/* \
	${sysconfdir}/cups/snmp.conf \
	${sysconfdir}/cups/ppd \
	${sysconfdir}/cups/interfaces \
	${sbindir}/cupsfilter \
	${sysconfdir}/cups/cupsd.conf \
	${datadir}/applications \
	${datadir}/cups \
	"

FILES_${PN}-bsd = " \
	${bindir}/lpq \
	${bindir}/lpr \
	${bindir}/lprm \
	${sbindir}/lpc \
	"

# ippfind will not appear if avahi package not existed
FILES_${PN}-client = " \
	${bindir}/cancel \
	${bindir}/cupstestdsc \
	${bindir}/cupstestppd \
	${bindir}/ippfind \
	${bindir}/ipptool \
	${bindir}/lp \
	${bindir}/lpoptions \
	${bindir}/lppasswd \
	${bindir}/lpstat \
	${sbindir}/accept \
	${sbindir}/cupsaccept \
	${sbindir}/cupsaddsmb \
	${sbindir}/cupsctl \
	${sbindir}/cupsdisable \
	${sbindir}/cupsenable \
	${sbindir}/cupsreject \
	${sbindir}/lpadmin \
	${sbindir}/lpinfo \
	${sbindir}/lpmove \
	${sbindir}/reject \
	"

FILES_${PN}-common = " \
	${datadir}/cups/drv/sample.drv \
	${datadir}/cups/locale \
	${datadir}/cups/ppdc \
	${datadir}/doc/cups-common \
	${datadir}/lintian/overrides \
	"

FILES_${PN}-core-drivers = " \
	${libdir}/cups/filter/commandtops \
	${libdir}/cups/filter/gziptoany \
	${libdir}/cups/filter/pstops \
	"

#Missing cups-daemon file because it requires apparmor_parser command
#to create.
FILES_${PN}-daemon = " \
	${sysconfdir}/apparmor.d/usr.sbin.cupsd \
	${sysconfdir}/cups/cups-files.conf \
	${sysconfdir}/init.d/cups \
	${sysconfdir}/pam.d/cups \
	${sysconfdir}/ufw \
	${sysconfdir}/cups/ssl \
	${base_libdir}/systemd/system \
	${libdir}/cups/backend/http* \
	${libdir}/cups/backend/ipp* \
	${libdir}/cups/backend/https \
	${libdir}/cups/backend/https \
	${libdir}/cups/notifier/dbus \
	${libdir}/cups/notifier/mailto \
	${libdir}/cups/notifier/rss \
	${sbindir}/cupsd \
	${datadir}/apport \
	"

FILES_${PN}-ppdc = " \
	${bindir}/ppdc \
	${bindir}/ppdhtml \
	${bindir}/ppdi \
	${bindir}/ppdmerge \
	${bindir}/ppdpo \
	"

FILES_${PN}-server-common = " \
	${datadir}/cups/doc-root \
	${datadir}/cups/mime \
	${datadir}/cups/templates \
	${datadir}/cups/usb \
	"

FILES_libcups2 = " \
	${libdir}/libcups.so.* \
	"

FILES_${PN}-dev = " \
	${bindir}/cups-config \
	${includedir}/cups/adminutil.h \
	${includedir}/cups/array.h \
	${includedir}/cups/backend.h \
	${includedir}/cups/cups.h \
	${includedir}/cups/dir.h \
	${includedir}/cups/file.h \
	${includedir}/cups/http.h \
	${includedir}/cups/i18n.h \
	${includedir}/cups/ipp.h \
	${includedir}/cups/language.h \
	${includedir}/cups/ppd.h \
	${includedir}/cups/pwg.h \
	${includedir}/cups/sidechannel.h \
	${includedir}/cups/transcode.h \
	${includedir}/cups/versioning.h \
	${libdir}/libcups.so \
	"

FILES_${PN}-staticdev = " \
	${libdir}/libcups.a \
	${libdir}/libcupscgi.a \
	${libdir}/libcupsimage.a \
	${libdir}/libcupsmime.a \
	${libdir}/libcupsppdc.a \
	"

FILES_libcupscgi1 = " \
	${libdir}/libcupscgi.so.1 \
	"

FILES_libcupscgi-dev = " \
	${includedir}/cups/cgi.h \
	${includedir}/cups/help-index.h \
	${libdir}/libcupscgi.so \
	"

FILES_libcupsimage2 = " \
	${libdir}/libcupsimage.so.2 \
	"

FILES_libcupsimage2-dev = " \
	${includedir}/cups/raster.h \
	${libdir}/libcupsimage.so \
	"

FILES_libcupsmime1 = " \
	${libdir}/libcupsmime.so.1 \
	"

FILES_libcupsmime-dev = " \
	${includedir}/cups/mime.h \
	${libdir}/libcupsmime.so \
	"

FILES_libcupsppdc1 = " \
	${libdir}/libcupsppdc.so.1 \
	"
FILES_libcupsppdc-dev = " \
	${includedir}/cups/ppdc.h \
	${libdir}/libcupsppdc.so \
	"

FILES_${PN}-libimage = " \
	${libdir}/libcupsimage.so.* \
	"

DOTDEBUG-dbg = "${bindir}/.debug ${sbindir}/.debug ${libexecdir}/.debug ${libdir}/.debug \
            ${base_bindir}/.debug ${base_sbindir}/.debug ${base_libdir}/.debug ${libdir}/${BPN}/.debug \
            ${libdir}/matchbox-panel/.debug /usr/src/debug"

DEBUGFILEDIRECTORY-dbg = "/usr/lib/debug /usr/src/debug"

FILES_${PN}-dbg = "${@d.getVar(['DOTDEBUG-dbg', 'DEBUGFILEDIRECTORY-dbg'][d.getVar('PACKAGE_DEBUG_SPLIT_STYLE', True) == 'debug-file-directory'], True)}"

FILES_${PN}-dbg += " \
	${libdir}/cups/backend-available/.debug \
	${libdir}/cups/backend/.debug \
	${libdir}/cups/cgi-bin/.debug \
	${libdir}/cups/filter/.debug \
	${libdir}/cups/monitor/.debug \
	${libdir}/cups/notifier/.debug \
	${libdir}/cups/daemon/.debug \
	${localstatedir} \
	${sysconfdir}/rc* \
	${sysconfdir}/dbus-1 \
	"

#package the html for the webgui inside the main packages (~1MB uncompressed)
FILES_${PN} += "${datadir}/doc/cups/images \
                ${datadir}/doc/cups/*html \
                ${datadir}/doc/cups/*.css \
                ${datadir}/icons/ \
               "

# Correct name of Debian package
DEBIANNAME_${PN}-dev = "libcups2-dev"
DEBIANNAME_libcupscgi-dev = "libcupscgi1-dev"
DEBIANNAME_libcupsmime-dev = "libcupsmime1-dev"
DEBIANNAME_libcupsppdc-dev = "libcupsppdc1-dev"

CONFFILES_${PN} += "${sysconfdir}/cups/cupsd.conf"

SYSROOT_PREPROCESS_FUNCS += "cups_sysroot_preprocess"
cups_sysroot_preprocess () {
	sed -i ${SYSROOT_DESTDIR}${bindir_crossscripts}/cups-config -e 's:cups_datadir=.*:cups_datadir=${datadir}/cups:' -e 's:cups_serverbin=.*:cups_serverbin=${libdir}/cups:'
}

# Base on debian/control
RDEPENDS_libcup2 += "libavahi-client libavahi-common zlib"

PARALLEL_MAKE = ""
