#
# base recipe: meta/recipes-devtools/dpkg/dpkg_1.17.4.bb
# base branch: daisy
#

require dpkg.inc

PR = "${INC_PR}.5"

DEPENDS = "zlib bzip2 perl ncurses xz-utils"
DEPENDS_class-native = "bzip2-replacement-native \
                        zlib-native \
                        gettext-native \
                        perl-native \
                        xz-utils-native \
                        "
RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_update-alternatives} xz-utils"
RDEPENDS_${PN}_class-native = "xz-utils-native"

PARALLEL_MAKE = ""

inherit autotools gettext perlnative pkgconfig

export PERL = "${bindir}/perl"
PERL_class-native = "${STAGING_BINDIR_NATIVE}/perl-native/perl"
SRC_URI_append_class-native = " file://0001-use-nativeperl-instead-of-STAGING_BINDIR_NATIVE-perl.patch"

export PERL_LIBDIR = "${libdir}/perl"
PERL_LIBDIR_class-native = "${libdir}/perl-native/perl"

# update-alternatives and start-stop-daemon are provided by dpkg-utils recipe
# --without-selinux: Don't use selinux support
EXTRA_OECONF = " \
	--disable-update-alternatives \
	--disable-start-stop-daemon \
	--with-zlib \
	--with-bz2 \
	--with-liblzma \
	--without-selinux \
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

# base on debian/dpkg.postinst
pkg_postinst_${PN} () {
	set -e

	# Create the database files if they don't already exist
	create_database() {
		admindir=${DPKG_ADMINDIR:-${localstatedir}/lib/${DPN}}

		for file in diversions statoverride status; do
			if [ ! -f "$D$admindir/$file" ]; then
				touch "$D$admindir/$file"
			fi
		done
	}

	# Move the info directory from /usr/info to /usr/share/info
	move_info_directory() {
		if [ -d $D${prefix}/info ] && [ ! -L $D${prefix}/info ] \
		    && [ -f $D${prefix}/info/dir ] && [ ! -L $D${prefix}/info/dir ]
		then
			echo "Moving ${prefix}/info/dir to ${infodir}/dir ..."
			mv $D${prefix}/info/dir $D${infodir}/dir
			if [ -f $D${prefix}/info/dir.old ]; then
				mv $D${prefix}/info/dir.old $D${infodir}/dir.old
			fi
		fi
	}

	# Remove the /usr/info symlinks we used to generate
	remove_info_symlink() {
		if [ -L $D${prefix}/info ]; then
			echo "Removing ${prefix}/info symlink ..."
			rm $D${prefix}/info
		elif [ -L $D${prefix}/info/dir ]; then
			echo "Removing ${prefix}/info/dir symlink ..."
			rm $D${prefix}/info/dir
		fi
	}

	# Create log file and set default permissions if possible
	create_logfile() {
		logfile=${localstatedir}/log/dpkg.log
		touch $D$logfile
		chmod 644 $D$logfile
		chown root:root $D$logfile 2>/dev/null || chown 0:0 $D$logfile
	}

	create_database
	create_logfile

	move_info_directory
	remove_info_symlink
}

PACKAGES =+ "dselect lib${PN}-perl lib${PN}-dev"

RDEPENDS_${PN} += "update-alternatives start-stop-daemon"
RDEPENDS_lib${PN}-perl += "libtimedate-perl"
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
