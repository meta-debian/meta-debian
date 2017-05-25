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

PR = "r1"

inherit debian-package
PV = "1.0.0"

LICENSE = "GPLv2 & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://libltdl/COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06 \
"

DEPENDS = "gnutls libgcrypt zlib libtool chrpath-native swig-native"

# libprelude-fix-correct-gettimeofday-params.patch:
# 	Correct the params of gettimeofday function, avoid error: 
# 	"conflicting declaration"
# libprelude-fix-generate-perl-makefile.patch:
# 	We will generate perl Makefile with our configuration.
# libprelude-perl-build-with-gnu-hash.patch:
# 	Use lddflags from environment.
# libprelude-fix-uid-gid-conflicting-types.patch. Fix error:
# 	| prelude-client-profile.c:676:6: error: conflicting types for 'prelude_client_profile_set_gid'
SRC_URI += " \
    file://libprelude-fix-correct-gettimeofday-params.patch \
    file://libprelude-fix-generate-perl-makefile.patch \
    file://libprelude-perl-build-with-gnu-hash.patch \
    file://libprelude-fix-uid-gid-conflicting-types.patch \
"

inherit autotools-brokensep gettext cpan-base binconfig pkgconfig perlnative pythonnative distutils-base

# We don't want lua bindings because Debian don't
EXTRA_OECONF = "--with-perl-installdirs=vendor --without-lua"

# Currently, we don't have ruby recipe,
# disable checking for ruby path to prevent using ruby from host system
CACHED_CONFIGUREVARS += "ac_cv_path_RUBY=no"

export PERL_LIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"

export HOST_SYS
export BUILD_SYS

do_configure_prepend() {
	perl_version=${PERLVERSION}
	short_perl_version=`echo ${perl_version%.*}`
	. ${STAGING_LIBDIR}/perl/config.sh
	sed -i -e "s:##EXTRA_CPANFLAGS##:${EXTRA_CPANFLAGS}:" \
	       -e "s:##CC##:${cc}:" \
	       -e "s:##LD##:${ld}:" \
	       -e "s:##LDFLAGS##:${ldflags}:" \
	       -e "s:##CCFLAGS##:${ccflags}:" \
	       -e "s:##LDDLFLAGS##:${lddlflags}:" \
	       -e "s:##TMP##:SITELIBEXP=${sitelibexp} SITEARCHEXP=${sitearchexp} \
	                     INSTALLVENDORLIB=${D}${datadir}/perl5 \
	                     INSTALLVENDORARCH=${D}${libdir}/perl5 \
	                     INSTALLSITELIB=${libdir}/perl5/$short_perl_version \
	                     INSTALLSITEARCH=${libdir}/perl5/$short_perl_version \
	                     :" \
	       ${S}/bindings/Makefile.am ${S}/bindings/low-level/Makefile.am
}

do_compile_prepend() {
	# Force swig to regenerate Prelude.c, cf. https://bugs.debian.org/752333
	rm -f bindings/low-level/perl/Prelude.c
}

# Follow debian/rules, build python modules
PYFLAGS = "PYTHON=python${PYTHON_BASEVERSION} \
           PYTHON_VERSION=${PYTHON_BASEVERSION} \
           _capng_la_LIBADD=\"-lpython${PYTHON_BASEVERSION}\""
do_compile_append() {
	# Compile python modules
	for i in bindings/python bindings/low-level/python; do
		oe_runmake -C $i clean
		oe_runmake -C $i ${PYFLAGS}
	done
}

do_install_append() {
	# Install python modules
	for i in bindings/python bindings/low-level/python; do
		oe_runmake -C $i ${PYFLAGS} DESTDIR=${D} install
	done
	find ${D}${PYTHON_SITEPACKAGES_DIR} -name "*.pyc" -delete

	# Remove build path
	sed -i "s:${WORKDIR}/image::" ${D}${libdir}/perl5/*/auto/Prelude/.packlist
	sed -i "s:${WORKDIR}/image::" ${D}${libdir}/perl5/*/auto/PreludeEasy/.packlist
	chrpath -d ${D}${bindir}/prelude-admin

	# Remove the the absolute path to sysroot
	sed -i -e "s|${STAGING_DIR_HOST}||" \
		${D}${libdir}/pkgconfig/libprelude.pc

}

PARALLEL_MAKEINST = ""

CONFFILES_${PN} = "${sysconfdir}/prelude/default/client.conf \
                   ${sysconfdir}/prelude/default/global.conf \
                   ${sysconfdir}/prelude/default/idmef-client.conf \
                   ${sysconfdir}/prelude/default/tls.conf \
                   "

PACKAGES = "${PN}-dbg ${PN}-staticdev ${PN}-dev ${PN}-doc \
            ${PN}-perl python-prelude ${PN}"

FILES_${PN}-perl = "${libdir}/perl5"
FILES_python-prelude = "${PYTHON_SITEPACKAGES_DIR}"
FILES_${PN}-dbg += "${libdir}/perl5/*/auto/*/.debug"

PKG_${PN} = "${PN}2"
