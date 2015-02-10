require recipes-devtools/perl/perl-native_5.14.3.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/perl/perl-5.14.3:"

inherit debian-package

DEBIAN_SECTION = "perl"
DPR = "0"

LICENSE = "Artistic-1.0 | GPLv1+"
LIC_FILES_CHKSUM = " \
file://Artistic;md5=2e6fd2475335af892494fe1f7327baf3 \
file://Copying;md5=5b122a36d0f6dc55279a0ebc69f3c60b \
"

# Remove patches: 
# Already existed in source code:
# 	debian/errno_ver.diff (already exist in debian/patches)
# \
# Source code was changed, not found the content to patch:
# 	perl-build-in-t-dir.patch
#	native-nopacklist.patch
SRC_URI += " \
file://Configure-multilib.patch \
file://perl-configpm-switch.patch \
file://native-perlinc.patch \
file://MM_Unix.pm.patch \
file://dynaloaderhack.patch \
file://perl-5.14.3-fix-CVE-2010-4777.patch \
"

# Base on debian/rules, create file patchlevel-debian.h
# which is required by perl when build with option -DDEBIAN.
do_compile_append(){
        cd ${S}

        export patches="debian/patches/series"
        export patchlevel="patchlevel-debian.h"
        export package_version=$(head -1 debian/changelog | cut -d'(' -f2 | cut -d')' -f1)
        # this gets prepended to the patch names in patchlevel.h
        export patchprefix="DEBPKG:"

        touch $patchlevel
        test -d ${S}/debian
        test -f $patches      # maintainer sanity check
        debian/gen-patchlevel -p $patchprefix -v $package_version \
		$patches > $patchlevel
	
	cd -
}

# Don't install fakethr.h and perlsfio.h 
# 	because these files not exist in perl 5.20.0 .
# Install patchlevel-debian.h which is 
# 	required by perl when build with option -DDEBIAN.
# Change ${PV} to ${PERL_VERSION} when create_wrapper.
do_install(){
	oe_runmake 'DESTDIR=${D}' install

        # We need a hostperl link for building perl
        ln -sf perl${PV} ${D}${bindir}/hostperl

        ln -sf perl ${D}${libdir}/perl5

        install -d ${D}${libdir}/perl/${PV}/CORE \
                   ${D}${datadir}/perl/${PV}/ExtUtils

        # Save native config
        install config.sh ${D}${libdir}/perl
        install lib/Config.pm ${D}${libdir}/perl/${PV}/
        install lib/ExtUtils/typemap ${D}${libdir}/perl/${PV}/ExtUtils/

        # perl shared library headers
	# fakethr.h and perlsfio.h doesn't exist in perl 5.20.0, 
	# so remove them from install files list.
        for i in av.h bitcount.h config.h cop.h cv.h dosish.h embed.h embedvar.h \
                EXTERN.h fakesdio.h form.h gv.h handy.h hv.h INTERN.h \
                intrpvar.h iperlsys.h keywords.h l1_char_class_tab.h malloc_ctl.h \
                metaconfig.h mg.h mydtrace.h nostdio.h opcode.h op.h opnames.h \
                op_reg_common.h overload.h pad.h parser.h patchlevel.h patchlevel-debian.h \
		perlapi.h perl.h perlio.h perliol.h perlsdio.h perlvars.h \
                perly.h pp.h pp_proto.h proto.h reentr.h regcharclass.h regcomp.h \
                regexp.h regnodes.h scope.h sv.h thread.h time64_config.h \
                time64.h uconfig.h unixish.h utf8.h utfebcdic.h util.h \
                uudmap.h warnings.h XSUB.h
        do
                install $i ${D}${libdir}/perl/${PV}/CORE
        done

        create_wrapper ${D}${bindir}/perl \
		PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl/${PV}:${STAGING_LIBDIR}/perl/'

	# Get perl version which is set in file "config.sh"
	PERL_VERSION=$(cat config.sh | grep "^version=" | cut -d\' -f2)

	# the current ${PV} is in format "gitAUTOINC+id"
	# this make create_wrapper to wrong file,
	# so change "perl${PV}" to "perl${PERL_VERSION}".
	create_wrapper ${D}${bindir}/perl${PERL_VERSION} \
		PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl/${PV}:${STAGING_LIBDIR}/perl/'
}
