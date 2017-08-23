#
# base recipe: meta/recipes-devtools/perl/perl-native_5.20.0.bb
# base branch: master
# base commit: a6866222ef6feaa2112618f1442a8960840e394a
#

PR = "${INC_PR}.2"

require perl.inc

SRC_URI += " \
file://Configure-multilib.patch \
file://perl-configpm-switch.patch \
file://native-perlinc.patch \
file://MM_Unix.pm.patch \
file://dynaloaderhack.patch \
"

# 5.10.1 has Module::Build built-in
PROVIDES += "libmodule-build-perl-native"

inherit native

NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

export LD="${CCLD}"

# In case DISTRO_FEATURES does not contain "largefile",
# perl target recipe remove all largefile parameters out of configuration file,
# this will make error when we build for qemux86 target on i386/x86 host machine,
# so we remove these params out of perl-native recipe, too.
do_nolargefile() {
	sed -i -e "s,\(uselargefiles=\)'define',\1'undef',g" \
               -e "s,\(d_readdir64_r=\)'define',\1'undef',g" \
               -e "s,\(readdir64_r_proto=\)'\w+',\1'0',g" \
               -e "/ccflags_uselargefiles/d" \
               -e "s/-Duselargefiles//" \
               -e "s/-D_FILE_OFFSET_BITS=64//" \
               -e "s/-D_LARGEFILE_SOURCE//" \
               ${S}/config.sh
}

do_configure () {
	./Configure \
		-Dcc="${CC}" \
		-Dcflags="${CFLAGS}" \
		-Dldflags="${LDFLAGS}" \
		-Dcf_by="Open Embedded" \
		-Dprefix=${prefix} \
		-Dvendorprefix=${prefix} \
		-Dvendorprefix=${prefix} \
		-Dsiteprefix=${prefix} \
		\
		-Dbin=${STAGING_BINDIR}/${PN} \
		-Dprivlib=${STAGING_LIBDIR}/perl/${PERL_PV} \
		-Darchlib=${STAGING_LIBDIR}/perl/${PERL_PV} \
		-Dvendorlib=${STAGING_LIBDIR}/perl/${PERL_PV} \
		-Dvendorarch=${STAGING_LIBDIR}/perl/${PERL_PV} \
		-Dsitelib=${STAGING_LIBDIR}/perl/${PERL_PV} \
		-Dsitearch=${STAGING_LIBDIR}/perl/${PERL_PV} \
		-Dotherlibdirs=${STAGING_DATADIR}/perl5 \
		\
		-Duseshrplib \
		-Dusethreads \
		-Duseithreads \
		-Duselargefiles \
		-Dnoextensions=ODBM_File \
		-Ud_dosuid \
		-Ui_db \
		-Ui_ndbm \
		-Ui_gdbm \
		-Di_shadow \
		-Di_syslog \
		-Duseperlio \
		-Dman3ext=3pm \
		-Dsed=/bin/sed \
		-Uafs \
		-Ud_csh \
		-Uusesfio \
		-Uusenm -des

	${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '', 'do_nolargefile', d)}
}

do_install () {
	oe_runmake 'DESTDIR=${D}' install

	# We need a hostperl link for building perl
	ln -sf perl${PERL_PV} ${D}${bindir}/hostperl

        ln -sf perl ${D}${libdir}/perl5

	install -d ${D}${libdir}/perl/${PERL_PV}/CORE \
	           ${D}${datadir}/perl/${PERL_PV}/ExtUtils

	# Save native config 
	install config.sh ${D}${libdir}/perl
	install lib/Config.pm ${D}${libdir}/perl/${PERL_PV}/
	install lib/ExtUtils/typemap ${D}${libdir}/perl/${PERL_PV}/ExtUtils/

	# perl shared library headers
	# reference perl 5.20.0-1 in debian:
	# https://packages.debian.org/experimental/i386/perl/filelist
	for i in av.h bitcount.h charclass_invlists.h config.h cop.h cv.h dosish.h \
		embed.h embedvar.h EXTERN.h fakesdio.h feature.h form.h git_version.h \
		gv.h handy.h hv_func.h hv.h inline.h INTERN.h intrpvar.h iperlsys.h \
		keywords.h l1_char_class_tab.h malloc_ctl.h metaconfig.h mg_data.h \
		mg.h mg_raw.h mg_vtable.h mydtrace.h nostdio.h opcode.h op.h opnames.h \
		op_reg_common.h overload.h pad.h parser.h patchlevel.h patchlevel-debian.h \
		perlapi.h perl.h perlio.h perliol.h perlsdio.h perlvars.h perly.h \
		pp.h pp_proto.h proto.h reentr.h regcharclass.h regcomp.h regexp.h \
		regnodes.h scope.h sv.h thread.h time64_config.h time64.h uconfig.h \
		unicode_constants.h unixish.h utf8.h utfebcdic.h util.h uudmap.h \
		vutil.h warnings.h XSUB.h
	do
		install $i ${D}${libdir}/perl/${PERL_PV}/CORE
	done

	create_wrapper ${D}${bindir}/perl PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl/${PERL_PV}:${STAGING_LIBDIR}/perl:${STAGING_LIBDIR}/perl/site_perl/${PERL_PV}:${STAGING_LIBDIR}/perl/vendor_perl/${PERL_PV}'
	create_wrapper ${D}${bindir}/perl${PERL_PV} PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl/${PERL_PV}:${STAGING_LIBDIR}/perl${STAGING_LIBDIR}/perl:${STAGING_LIBDIR}/perl/site_perl/${PERL_PV}:${STAGING_LIBDIR}/perl/vendor_perl/${PERL_PV}'

	# Use /usr/bin/env nativeperl for the perl script.
	for f in `grep -Il '#! *${bindir}/perl' ${D}/${bindir}/*`; do
		sed -i -e 's|${bindir}/perl|/usr/bin/env nativeperl|' $f
	done
}

SYSROOT_PREPROCESS_FUNCS += "perl_sysroot_create_wrapper"

perl_sysroot_create_wrapper () {
	mkdir -p ${SYSROOT_DESTDIR}${bindir}
	# Create a wrapper that /usr/bin/env perl will use to get perl-native.
	# This MUST live in the normal bindir.
	cat > ${SYSROOT_DESTDIR}${bindir}/../nativeperl << EOF
#!/bin/sh
realpath=\`readlink -fn \$0\`
exec \`dirname \$realpath\`/perl-native/perl "\$@"
EOF
	chmod 0755 ${SYSROOT_DESTDIR}${bindir}/../nativeperl
	cat ${SYSROOT_DESTDIR}${bindir}/../nativeperl
}

# Fix the path in sstate
SSTATE_SCAN_FILES += "*.pm *.pod *.h *.pl *.sh"
