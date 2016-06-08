#
# base recipe: meta/recipes-devtools/dpkg/dpkg_1.17.4.bb
# base branch: daisy
#

require dpkg.inc

PR = "${INC_PR}.2"

inherit systemd

DEPENDS = "zlib bzip2 perl ncurses xz libselinux"
DEPENDS_class-native = "bzip2-replacement-native \
                        zlib-native \
                        gettext-native \
                        perl-native \
                        xz-native \
                        libselinux-native \
                        "
RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_update-alternatives} xz"
RDEPENDS_${PN}_class-native = "xz-native"

PARALLEL_MAKE = ""

inherit autotools gettext perlnative pkgconfig

python () {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        pn = d.getVar('PN', True)
        d.setVar('SYSTEMD_SERVICE_%s' % (pn), 'dpkg-configure.service')
}

export PERL = "${bindir}/perl"
PERL_class-native = "${STAGING_BINDIR_NATIVE}/perl-native/perl"
SRC_URI_append_class-native = " file://0001-use-nativeperl-instead-of-STAGING_BINDIR_NATIVE-perl.patch"

export PERL_LIBDIR = "${libdir}/perl"
PERL_LIBDIR_class-native = "${libdir}/perl-native/perl"

# update-alternatives and start-stop-daemon are provided by dpkg-utils recipe
EXTRA_OECONF = " \
	--disable-update-alternatives \
	--disable-start-stop-daemon \
	--with-zlib \
	--with-bz2 \
	--with-liblzma \
	--with-selinux \
"

do_configure () {
	echo >> ${S}/m4/compiler.m4
	sed -i -e 's#PERL_LIBDIR=.*$#PERL_LIBDIR="${libdir}/perl"#' ${S}/configure
	autotools_do_configure
}

do_install_append () {
	if [ "${PN}" = "dpkg-native" ]; then
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env nativeperl|' ${D}${bindir}/dpkg-*
	else
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env perl|' ${D}${bindir}/dpkg-*
	fi

	if ${@base_contains('DISTRO_FEATURES','sysvinit','false','true',d)};then
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/dpkg-configure.service ${D}${systemd_unitdir}/system/
		sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
			-e 's,@SYSCONFDIR@,${sysconfdir},g' \
			-e 's,@BINDIR@,${bindir},g' \
			-e 's,@SYSTEMD_UNITDIR@,${systemd_unitdir},g' \
			${D}${systemd_unitdir}/system/dpkg-configure.service
	fi

	# Install configuration files and links follow Debian
	cd ${S}/debian
	cp shlibs.default shlibs.override dpkg.cfg dselect.cfg ${D}${sysconfdir}/${DPN}/
	cd -

	install -d ${D}${sysconfdir}/cron.daily
	install -d ${D}${sysconfdir}/logrotate.d
	install -m 0755 ${S}/debian/dpkg.cron.daily ${D}${sysconfdir}/cron.daily/dpkg
	install -m 0644 ${S}/debian/dpkg.logrotate ${D}${sysconfdir}/logrotate.d/dpkg

	install -d ${D}${sbindir}
	ln -s ../bin/dpkg-divert ${D}${sbindir}/dpkg-divert
	ln -s ../bin/dpkg-statoverride ${D}${sbindir}/dpkg-statoverride
}

PACKAGES =+ "dselect lib${PN}-perl lib${PN}-dev"

RDEPENDS_${PN} += "update-alternatives start-stop-daemon"

FILES_dselect = " \
    ${bindir}/dselect \
    ${sysconfdir}/${DPN}/dselect.cfg* \
    ${libdir}/${DPN}/methods \
    ${libdir}/perl/Dselect \
    ${localstatedir}/lib/${DPN}/methods \
"
FILES_lib${PN}-perl = " \
    ${libdir}/perl \
    ${libdir}/${DPN}/parsechangelog \
"
FILES_lib${PN}-dev = " \
    ${includedir}/${DPN}/*.h \
    ${libdir}/pkgconfig \
"
FILES_${PN}-dev += " \
    ${sysconfdir}/${DPN}/shlibs* \
    ${bindir}/dpkg-architecture \
    ${bindir}/dpkg-buildflags \
    ${bindir}/dpkg-buildpackage \
    ${bindir}/dpkg-checkbuilddeps \
    ${bindir}/dpkg-distaddfile \
    ${bindir}/dpkg-genchanges \
    ${bindir}/dpkg-gencontrol \
    ${bindir}/dpkg-gensymbols \
    ${bindir}/dpkg-mergechangelogs \
    ${bindir}/dpkg-name \
    ${bindir}/dpkg-parsechangelog \
    ${bindir}/dpkg-scanpackages \
    ${bindir}/dpkg-scansources \
    ${bindir}/dpkg-shlibdeps \
    ${bindir}/dpkg-source \
    ${bindir}/dpkg-vendor \
    ${datadir}/${DPN}/*.mk \
"

BBCLASSEXTEND = "native"
