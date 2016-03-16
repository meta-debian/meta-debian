#
# base recipe: http://cgit.openembedded.org/openembedded/tree/recipes/libprelude/libprelude_1.0.0.bb
# base commit: 971a58b4865e91f9b2141d3919372b10da69a3bc
#

SUMMARY = "Security Information Management System"
DESCRIPTION = "Prelude is a Universal "Security Information Management" (SIM) system.\n\
Its goals are performance and modularity. It is divided in two main\n\
parts :\n\
 - the Prelude sensors, responsible for generating alerts, such as\n\
   snort sensor, featuring a signature engine, plugins for\n\
   protocol analysis, and intrusion detection plugins, and the Prelude\n\
   log monitoring lackey.\n\
 - the Prelude report server, collecting data from Prelude sensors,\n\
   and generating user-readable reports."

PR = "r0"

inherit debian-package

LICENSE = "GPLv2 & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://libltdl/COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06 \
"

DEPENDS = "gnutls libgcrypt zlib libtool chrpath-native swig-native"

# libprelude-dont-regenerate-perl-makefile.patch:
# 	Disable generating perl Makefile. We will generate it with our configuration.
# libprelude-perl-build-with-gnu-hash.patch:
# 	Use lddflags from environment.
# libprelude-fix-uid-gid-conflicting-types.patch. Fix error:
# 	| prelude-client-profile.c:676:6: error: conflicting types for 'prelude_client_profile_set_gid'
SRC_URI += " \
    file://libprelude-dont-regenerate-perl-makefile.patch \
    file://libprelude-perl-build-with-gnu-hash.patch \
    file://libprelude-fix-uid-gid-conflicting-types.patch \
"

inherit autotools-brokensep gettext cpan-base binconfig pkgconfig perlnative

EXTRA_OECONF = "--with-perl-installdirs=vendor --without-python"

# Currently, we don't have ruby recipe,
# disable checking for ruby path to prevent using ruby from host system
CACHED_CONFIGUREVARS += "ac_cv_path_RUBY=no"

export PERL_LIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"

do_configure_append() {
	perl_version=${PERLVERSION}
	short_perl_version=`echo ${perl_version%.*}`
	. ${STAGING_LIBDIR}/perl/config.sh
	for i in bindings/perl bindings/low-level/perl; do
		olddir=`pwd`
		cd $i
		export lddlflags
		yes '' | perl Makefile.PL ${EXTRA_CPANFLAGS} CC="${cc}" LD="${ld}" LDFLAGS="${ldflags}" CCFLAGS="${ccflags}"
		sed -i -e "s:\(SITELIBEXP = \).*:\1${sitelibexp}:" \
		       -e "s:\(SITEARCHEXP = \).*:\1${sitearchexp}:" \
		       -e "s:\(INSTALLVENDORLIB = \).*:\1${D}${datadir}/perl5:" \
		       -e "s:\(INSTALLVENDORARCH = \).*:\1${D}${libdir}/perl5:" \
		       -e "s:\(LDDLFLAGS.*\)${STAGING_LIBDIR_NATIVE}:\1${STAGING_LIBDIR}:" \
		       -e "s:^\(INSTALLSITELIB = \).*:\1${libdir}/perl5/$short_perl_version:" \
		       -e "s:^\(INSTALLSITEARCH = \).*:\1${libdir}/perl5/$short_perl_version:" \
		       Makefile
		cd $olddir
	done
}

do_compile_prepend() {
	# Force swig to regenerate Prelude.c, cf. https://bugs.debian.org/752333
	rm -f bindings/low-level/perl/Prelude.c
}

do_install_append() {
	# Remove build path
	sed -i "s:${WORKDIR}/image::" ${D}${libdir}/perl5/*/auto/Prelude/.packlist
	sed -i "s:${WORKDIR}/image::" ${D}${libdir}/perl5/*/auto/PreludeEasy/.packlist
	chrpath -d ${D}${bindir}/prelude-admin
}

CONFFILES_${PN} = "${sysconfdir}/prelude/default/client.conf \
                   ${sysconfdir}/prelude/default/global.conf \
                   ${sysconfdir}/prelude/default/idmef-client.conf \
                   ${sysconfdir}/prelude/default/tls.conf \
                   "

PACKAGE_BEFORE_PN = "${PN}-perl"

FILES_${PN}-perl = "${libdir}/perl5"
FILES_${PN}-dbg += "${libdir}/perl5/*/auto/*/.debug"

PKG_${PN} = "${PN}2"
