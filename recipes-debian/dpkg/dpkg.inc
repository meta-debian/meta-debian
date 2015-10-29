inherit debian-package

INC_PR = "r0"

DPN = "dpkg"

LICENSE = "GPLv2+ & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://scripts/Dpkg/Gettext.pm;endline=22;md5=e497bef519c4517b0dda7d6aaedb3763 \
"

# Patch file no-vla-warning.patch, dpkg-1.17.4-CVE-2014-0471.patch and
# dpkg-1.17.4-CVE-2014-0471-CVE-2014-3127.patch are no need since
# it has been applied in new version of source code.
SRC_URI += " \
	file://noman.patch \
	file://check_snprintf.patch \
	file://check_version.patch \
	file://preinst.patch \
	file://fix-timestamps.patch \
	file://remove-tar-no-timestamp.patch \
	file://fix-abs-redefine.patch \
	file://arch_pm.patch \
	file://dpkg-configure.service \
	file://glibc2.5-sync_file_range.patch \
"

DEPENDS = "zlib bzip2 perl ncurses"
DEPENDS_class-native = "bzip2-replacement-native zlib-native gettext-native perl-native"
RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_update-alternatives} xz run-postinsts"
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

export PERL_LIBDIR = "${libdir}/perl"
PERL_LIBDIR_class-native = "${libdir}/perl-native/perl"

EXTRA_OECONF = " \
	--disable-dselect \
	--enable-start-stop-daemon \
	--with-zlib \
	--with-bz2 \
	--without-liblzma \
	--without-selinux \
"

do_configure () {
	echo >> ${S}/m4/compiler.m4
	sed -i -e 's#PERL_LIBDIR=.*$#PERL_LIBDIR="${libdir}/perl"#' ${S}/configure
	autotools_do_configure
}

BBCLASSEXTEND = "native"
