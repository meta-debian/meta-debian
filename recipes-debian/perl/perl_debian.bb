#
# base recipe: meta/recipes-devtools/perl-sanity/perl_5.28.1.bb
# base branch: master
# base commit: a7774aced031de1c8e42d0559182e802df8bcaa8
#

SUMMARY = "Perl scripting language"
HOMEPAGE = "http://www.perl.org/"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = " \
    file://Copying;md5=5b122a36d0f6dc55279a0ebc69f3c60b \
    file://Artistic;md5=71a4d5d9acc18c0952a6df2218bb68da \
"

inherit debian-package
require recipes-debian/sources/perl.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/perl-sanity/files"
SRC_URI += " \
    https://github.com/arsv/perl-cross/releases/download/1.2.1/perl-cross-1.2.1.tar.gz;name=perl-cross \
    file://perl-rdepends.txt \
    file://0001-configure_tool.sh-do-not-quote-the-argument-to-comma.patch \
    file://0001-ExtUtils-MakeMaker-add-LDFLAGS-when-linking-binary-m.patch \
    file://0001-Somehow-this-module-breaks-through-the-perl-wrapper-.patch \
    file://perl-configpm-switch.patch \
    file://native-perlinc.patch \
    file://0001-perl-cross-add-LDFLAGS-when-linking-libperl.patch \
    file://perl-dynloader.patch \
    file://0001-configure_path.sh-do-not-hardcode-prefix-lib-as-libr.patch \
    file://fix-race-failures.patch \
    file://fix-race-failures-2.patch \
    file://0001-Also-build-dynaloader-separately-as-race-failures-ha.patch \
    file://0001-Make-sure-install.perl-runs-before-install.man.patch \
    file://0001-Makefile-Make-install.perl-depend-on-install.sym.patch \
"

SRC_URI[perl-cross.md5sum] = "c5cdc8b7ebc449ee57fe18fc1ac60c80"
SRC_URI[perl-cross.sha256sum] = "8b706bc688ddf71b62d649bde72f648669f18b37fe0c54ec6201142ca3943498"

do_unpack_append() {
    bb.build.exec_func('do_unpack_extra', d)
}

do_unpack_extra() {
	cp -rf ${WORKDIR}/perl-cross*/* ${S}
	mv ${WORKDIR}/metaconfig-* ${DEBIAN_UNPACK_DIR}/regen-configure
}

do_configure_class-target() {
	./configure --prefix=${prefix} --libdir=${libdir} \
	--target=${TARGET_SYS} \
	-Duseshrplib \
	-Dsoname=libperl.so.5 \
	-Dvendorprefix=${prefix} \
	-Darchlibexp=${STAGING_LIBDIR}/perl5/${PV}/${TARGET_ARCH}-linux

	#perl.c uses an ARCHLIB_EXP define to generate compile-time code that
	#adds the archlibexp path to @INC during run-time initialization of a
	#new perl interpreter.

	#Because we've changed this value in a temporary way to make it
	#possible to use ExtUtils::Embed in the target build (the temporary
	#value in config.sh gets re-stripped out during packaging), the
	#ARCHLIB_EXP value that gets generated still uses the temporary version
	#instead of the original expected version (i.e. becauses it's in the
	#generated config.h, it doesn't get stripped out during packaging like
	#the others in config.sh).

	sed -i -e "s,${STAGING_LIBDIR},${libdir},g" config.h
}

do_configure_class-nativesdk() {
	./configure --prefix=${prefix} \
	--target=${TARGET_SYS} \
	-Duseshrplib \
	-Dsoname=libperl.so.5 \
	-Dvendorprefix=${prefix} \
	-Darchlibexp=${STAGING_LIBDIR}/perl5/${PV}/${TARGET_ARCH}-linux

	# See the comment above
	sed -i -e "s,${STAGING_LIBDIR},${libdir},g" config.h
}

do_configure_class-native() {
	./configure --prefix=${prefix} \
	-Dbin=${bindir}/perl-native \
	-Duseshrplib \
	-Dsoname=libperl.so.5 \
	-Dvendorprefix=${prefix} \
	-Ui_xlocale
}

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install

	install -D -d ${D}${libdir}/perl5/${PV}/ExtUtils/

	# Save native config
	install config.sh ${D}${libdir}/perl5
	install lib/Config.pm ${D}${libdir}/perl5/${PV}/
	install lib/ExtUtils/typemap ${D}${libdir}/perl5/${PV}/ExtUtils/

	# Fix up shared library
	rm -f ${D}/${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/CORE/libperl.so
	ln -sf ../../../../libperl.so.${PV} ${D}/${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/CORE/libperl.so
}

do_install_append_class-target() {
	# This is used to substitute target configuration when running native perl via perl-configpm-switch.patch
	ln -sf Config_heavy.pl ${D}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/Config_heavy-target.pl

}

do_install_append_class-nativesdk() {
	# This is used to substitute target configuration when running native perl via perl-configpm-switch.patch
	ln -sf Config_heavy.pl ${D}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/Config_heavy-target.pl

	create_wrapper ${D}${bindir}/perl \
	    PERL5LIB='$PERL5LIB:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl5/site_perl/${PV}:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl5/vendor_perl/${PV}:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl5/${PV}'
}

do_install_append_class-native () {
	# Those wrappers mean that perl installed from sstate (which may change
	# path location) works and that in the nativesdk case, the SDK can be
	# installed to a different location from the one it was built for.
	create_wrapper ${D}${bindir}/perl-native/perl PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl5/site_perl/${PV}:${STAGING_LIBDIR}/perl5/vendor_perl/${PV}:${STAGING_LIBDIR}/perl5/${PV}'
	create_wrapper ${D}${bindir}/perl-native/perl${PV} PERL5LIB='$PERL5LIB:${STAGING_LIBDIR}/perl5/site_perl/${PV}:${STAGING_LIBDIR}/perl5/vendor_perl/${PV}:${STAGING_LIBDIR}/perl5/${PV}'

	# Use /usr/bin/env nativeperl for the perl script.
	for f in `grep -Il '#! *${bindir}/perl' ${D}/${bindir}/*`; do
		sed -i -e 's|${bindir}/perl|/usr/bin/env nativeperl|' $f
	done
}

PACKAGE_PREPROCESS_FUNCS += "perl_package_preprocess"

perl_package_preprocess () {
	# Fix up installed configuration
	sed -i -e "s,${D},,g" \
	       -e "s,${DEBUG_PREFIX_MAP},,g" \
	       -e "s,--sysroot=${STAGING_DIR_HOST},,g" \
	       -e "s,-isystem${STAGING_INCDIR} ,,g" \
	       -e "s,${STAGING_LIBDIR},${libdir},g" \
	       -e "s,${STAGING_BINDIR},${bindir},g" \
	       -e "s,${STAGING_INCDIR},${includedir},g" \
	       -e "s,${STAGING_BINDIR_NATIVE}/perl-native/,${bindir}/,g" \
	       -e "s,${STAGING_BINDIR_NATIVE}/,,g" \
	       -e "s,${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX},${bindir},g" \
	       -e 's:${RECIPE_SYSROOT}::g' \
	    ${PKGD}${bindir}/h2xs \
	    ${PKGD}${bindir}/h2ph \
	    ${PKGD}${bindir}/pod2man \
	    ${PKGD}${bindir}/pod2text \
	    ${PKGD}${bindir}/pod2usage \
	    ${PKGD}${bindir}/podchecker \
	    ${PKGD}${bindir}/podselect \
	    ${PKGD}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/CORE/config.h \
	    ${PKGD}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/CORE/perl.h \
	    ${PKGD}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/CORE/pp.h \
	    ${PKGD}${libdir}/perl5/${PV}/Config.pm \
	    ${PKGD}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/Config.pm \
	    ${PKGD}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/Config.pod \
	    ${PKGD}${libdir}/perl5/${PV}/${TARGET_ARCH}-linux/Config_heavy.pl \
	    ${PKGD}${libdir}/perl5/${PV}/ExtUtils/Liblist/Kid.pm \
	    ${PKGD}${libdir}/perl5/${PV}/FileCache.pm \
	    ${PKGD}${libdir}/perl5/${PV}/pod/*.pod \
	    ${PKGD}${libdir}/perl5/config.sh
}

require recipes-devtools/perl-sanity/perl-ptest.inc

FILES_${PN} = " \
    ${bindir}/perl ${bindir}/perl.real ${bindir}/perl${PV} ${libdir}/libperl.so* \
    ${libdir}/perl5/site_perl \
    ${libdir}/perl5/${PV}/Config.pm \
    ${libdir}/perl5/${PV}/*/Config_heavy-target.pl \
    ${libdir}/perl5/config.sh \
    ${libdir}/perl5/${PV}/strict.pm \
    ${libdir}/perl5/${PV}/warnings.pm \
    ${libdir}/perl5/${PV}/warnings \
    ${libdir}/perl5/${PV}/vars.pm \
    ${libdir}/perl5/site_perl \
"
RPROVIDES_${PN} += " \
    perl-module-strict perl-module-vars perl-module-config perl-module-warnings \
    perl-module-warnings-register"

FILES_${PN}-staticdev += "${libdir}/perl5/${PV}/*/CORE/libperl.a"
FILES_${PN}-dev += "${libdir}/perl5/${PV}/*/CORE"
FILES_${PN}-doc += " \
    ${libdir}/perl5/${PV}/Unicode/Collate/*.txt \
    ${libdir}/perl5/${PV}/*/.packlist \
    ${libdir}/perl5/${PV}/ExtUtils/MANIFEST.SKIP \
    ${libdir}/perl5/${PV}/ExtUtils/xsubpp \
    ${libdir}/perl5/${PV}/ExtUtils/typemap \
    ${libdir}/perl5/${PV}/Encode/encode.h \
"
PACKAGES += "${PN}-misc"

FILES_${PN}-misc = "${bindir}/*"

PACKAGES += "${PN}-pod"

FILES_${PN}-pod = " \
    ${libdir}/perl5/${PV}/pod \
    ${libdir}/perl5/${PV}/*.pod \
    ${libdir}/perl5/${PV}/*/*.pod \
    ${libdir}/perl5/${PV}/*/*/*.pod \
    ${libdir}/perl5/${PV}/*/*/*/*.pod \
"

PACKAGES += "${PN}-module-cpan ${PN}-module-unicore"

FILES_${PN}-module-cpan += "${libdir}/perl5/${PV}/CPAN"
FILES_${PN}-module-unicore += "${libdir}/perl5/${PV}/unicore"

# Create a perl-modules package recommending all the other perl
# packages (actually the non modules packages and not created too)
ALLOW_EMPTY_${PN}-modules = "1"
PACKAGES += "${PN}-modules "

PACKAGESPLITFUNCS_prepend = "split_perl_packages "

python split_perl_packages () {
    libdir = d.expand('${libdir}/perl5/${PV}')
    do_split_packages(d, libdir, r'.*/auto/([^.]*)/[^/]*\.(so|ld|ix|al)', '${PN}-module-%s', 'perl module %s', recursive=True, match_path=True, prepend=False)
    do_split_packages(d, libdir, r'.*linux/([^\/]*)\.pm', '${PN}-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, r'Module/([^\/]*)\.pm', '${PN}-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, r'Module/([^\/]*)/.*', '${PN}-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, r'.*linux/([^\/].*)\.(pm|pl|e2x)', '${PN}-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, r'(^(?!(CPAN\/|CPANPLUS\/|Module\/|unicore\/)[^\/]).*)\.(pm|pl|e2x)', '${PN}-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)

    # perl-modules should recommend every perl module, and only the
    # modules. Don't attempt to use the result of do_split_packages() as some
    # modules are manually split (eg. perl-module-unicore).
    packages = filter(lambda p: 'perl-module-' in p, d.getVar('PACKAGES').split())
    d.setVar(d.expand("RRECOMMENDS_${PN}-modules"), ' '.join(packages))

    # Read the pre-generated dependency file, and use it to set module dependecies
    for line in open(d.expand("${WORKDIR}") + '/perl-rdepends.txt').readlines():
        splitline = line.split()
        module = splitline[0].replace("RDEPENDS_perl", "RDEPENDS_${PN}")
        depends = splitline[2].strip('"').replace("perl-module", "${PN}-module")
        d.appendVar(d.expand(module), " " + depends)
}

PACKAGES_DYNAMIC_class-target += "^perl-module-.*"
PACKAGES_DYNAMIC_class-nativesdk += "^nativesdk-perl-module-.*"

RDEPENDS_${PN}-misc += "perl perl-modules"
RDEPENDS_${PN}-pod += "perl"

BBCLASSEXTEND = "native nativesdk"

SSTATE_SCAN_FILES += "*.pm *.pod *.h *.pl *.sh"

SYSROOT_PREPROCESS_FUNCS += "perl_sysroot_create_wrapper"

perl_sysroot_create_wrapper () {
       mkdir -p ${SYSROOT_DESTDIR}${bindir}
       # Create a wrapper that /usr/bin/env perl will use to get perl-native.
       # This MUST live in the normal bindir.
       cat > ${SYSROOT_DESTDIR}${bindir}/nativeperl << EOF
#!/bin/sh
realpath=\`readlink -fn \$0\`
exec \`dirname \$realpath\`/perl-native/perl "\$@"
EOF
       chmod 0755 ${SYSROOT_DESTDIR}${bindir}/nativeperl
       cat ${SYSROOT_DESTDIR}${bindir}/nativeperl
}
