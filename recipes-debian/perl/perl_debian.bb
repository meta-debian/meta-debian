#
# base recipe: meta/recipes-devtools/perl/perl_5.20.0.bb
# base branch: master
# base commit: a6866222ef6feaa2112618f1442a8960840e394a
#

require perl.inc

PR = "${INC_PR}.1"

# Remove the patches which already available in Debian:
# 	debian/* 
SRC_URI += " \
file://Makefile.patch \
file://Makefile.SH_5.20.2.patch \
file://installperl.patch \
file://perl-dynloader.patch \
file://perl-moreconfig.patch \
file://letgcc-find-errno.patch \
file://generate-sh.patch \
file://native-perlinc.patch \
file://perl-enable-gdbm.patch \
file://cross-generate_uudmap.patch \
file://fix_bad_rpath.patch \
file://perl-archlib-exp.patch \
file://dynaloaderhack.patch \
\
file://config.sh \
file://config.sh-32 \
file://config.sh-32-le \
file://config.sh-32-be \
file://config.sh-64 \
file://config.sh-64-le \
file://config.sh-64-be \
file://0001-Makefile.SH-fix-do_install-failed.patch \
file://Fix-race-condition-in-perl-s-ExtUtils-MakeMaker.patch \
"

# We need gnugrep (for -I)
DEPENDS = "virtual/db grep-native"
DEPENDS += "gdbm zlib"

# 5.10.1 has Module::Build built-in
PROVIDES += "libmodule-build-perl"


inherit perlnative siteinfo

# Where to find the native perl
HOSTPERL = "${STAGING_BINDIR_NATIVE}/perl-native/perl${PERL_PV}"

# Where to find .so files - use the -native versions not those from the target build
export PERLHOSTLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${PERL_PV}/"

# Where to find perl @INC/#include files
# - use the -native versions not those from the target build
export PERL_LIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${PERL_PV}/"
export PERL_ARCHLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${PERL_PV}/"

# LDFLAGS for shared libraries
export LDDLFLAGS = "${LDFLAGS} -shared"

LDFLAGS_append = " -fstack-protector"

# We're almost Debian, aren't we?
CFLAGS += "-DDEBIAN"

do_nolargefile() {
	sed -i -e "s,\(uselargefiles=\)'define',\1'undef',g" \
		-e "s,\(d_readdir64_r=\)'define',\1'undef',g" \
		-e "s,\(readdir64_r_proto=\)'\w+',\1'0',g" \
		-e "/ccflags_uselargefiles/d" \
		-e "s/-Duselargefiles//" \
		-e "s/-D_FILE_OFFSET_BITS=64//" \
		-e "s/-D_LARGEFILE_SOURCE//" \
		${S}/Cross/config.sh-${TARGET_ARCH}-${TARGET_OS}
}

do_configure() {
	# Make hostperl in build directory be the native perl
	ln -sf ${HOSTPERL} hostperl

	if [ -n "${CONFIGURESTAMPFILE}" -a -e "${CONFIGURESTAMPFILE}" ]; then
		if [ "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" -a -e Makefile ]; then
			${MAKE} clean
		fi
		find ${S} -name *.so -delete
	fi
	if [ -n "${CONFIGURESTAMPFILE}" ]; then
		echo ${BB_TASKHASH} > ${CONFIGURESTAMPFILE}
	fi

	# Do our work in the cross subdir
	cd Cross

	# Generate configuration
	rm -f config.sh-${TARGET_ARCH}-${TARGET_OS}
	for i in ${WORKDIR}/config.sh \
			${WORKDIR}/config.sh-${SITEINFO_BITS} \
			${WORKDIR}/config.sh-${SITEINFO_BITS}-${SITEINFO_ENDIANNESS}; do
		cat $i >> config.sh-${TARGET_ARCH}-${TARGET_OS}
	done

	# Fixups for uclibc
	if [ "${TARGET_OS}" = "linux-uclibc" -o "${TARGET_OS}" = "linux-uclibceabi" ]; then
		sed -i -e "s,\(d_crypt_r=\)'define',\1'undef',g" \
                       -e "s,\(d_futimes=\)'define',\1'undef',g" \
                       -e "s,\(crypt_r_proto=\)'\w+',\1'0',g" \
                       -e "s,\(d_getnetbyname_r=\)'define',\1'undef',g" \
                       -e "s,\(getnetbyname_r_proto=\)'\w+',\1'0',g" \
                       -e "s,\(d_getnetbyaddr_r=\)'define',\1'undef',g" \
                       -e "s,\(getnetbyaddr_r_proto=\)'\w+',\1'0',g" \
                       -e "s,\(d_getnetent_r=\)'define',\1'undef',g" \
                       -e "s,\(getnetent_r_proto=\)'\w+',\1'0',g" \
                       -e "s,\(d_sockatmark=\)'define',\1'undef',g" \
                       -e "s,\(d_sockatmarkproto=\)'\w+',\1'0',g" \
                       -e "s,\(d_eaccess=\)'define',\1'undef',g" \
                       -e "s,\(d_stdio_ptr_lval=\)'define',\1'undef',g" \
                       -e "s,\(d_stdio_ptr_lval_sets_cnt=\)'define',\1'undef',g" \
                       -e "s,\(d_stdiobase=\)'define',\1'undef',g" \
                       -e "s,\(d_stdstdio=\)'define',\1'undef',g" \
                       -e "s,-fstack-protector,-fno-stack-protector,g" \
			config.sh-${TARGET_ARCH}-${TARGET_OS}
	fi
	# Fixups for musl
	if [ "${TARGET_OS}" = "linux-musl" -o "${TARGET_OS}" = "linux-musleabi" ]; then
		sed -i -e "s,\(d_libm_lib_version=\)'define',\1'undef',g" \
                       -e "s,\(d_stdio_ptr_lval=\)'define',\1'undef',g" \
                       -e "s,\(d_stdio_ptr_lval_sets_cnt=\)'define',\1'undef',g" \
                       -e "s,\(d_stdiobase=\)'define',\1'undef',g" \
                       -e "s,\(d_stdstdio=\)'define',\1'undef',g" \
                       -e "s,\(d_getnetbyname_r=\)'define',\1'undef',g" \
                       -e "s,\(getprotobyname_r=\)'define',\1'undef',g" \
                       -e "s,\(getpwent_r=\)'define',\1'undef',g" \
                       -e "s,\(getservent_r=\)'define',\1'undef',g" \
                       -e "s,\(gethostent_r=\)'define',\1'undef',g" \
                       -e "s,\(getnetent_r=\)'define',\1'undef',g" \
                       -e "s,\(getnetbyaddr_r=\)'define',\1'undef',g" \
                       -e "s,\(getprotoent_r=\)'define',\1'undef',g" \
                       -e "s,\(getprotobynumber_r=\)'define',\1'undef',g" \
                       -e "s,\(getgrent_r=\)'define',\1'undef',g" \
                       -e "s,\(i_fcntl=\)'undef',\1'define',g" \
                       -e "s,\(h_fcntl=\)'false',\1'true',g" \
                       -e "s,-fstack-protector,-fno-stack-protector,g" \
                       -e "s,-lnsl,,g" \
			config.sh-${TARGET_ARCH}-${TARGET_OS}
	fi

	${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '', 'do_nolargefile', d)}

	# Update some paths in the configuration
	sed -i -e 's,@ARCH@-thread-multi,,g' \
               -e 's,@ARCH@,${TARGET_ARCH}-${TARGET_OS},g' \
               -e 's,@STAGINGDIR@,${STAGING_DIR_HOST},g' \
               -e "s,@INCLUDEDIR@,${STAGING_INCDIR},g" \
               -e "s,@LIBDIR@,${libdir},g" \
               -e "s,@BASELIBDIR@,${base_libdir},g" \
               -e "s,@EXECPREFIX@,${exec_prefix},g" \
               -e 's,@USRBIN@,${bindir},g' \
		config.sh-${TARGET_ARCH}-${TARGET_OS}

	case "${TARGET_ARCH}" in
		x86_64 | powerpc | s390)
			sed -i -e "s,\(need_va_copy=\)'undef',\1'define',g" \
				config.sh-${TARGET_ARCH}-${TARGET_OS}
			;;
		arm)
			sed -i -e "s,\(d_u32align=\)'undef',\1'define',g" \
				config.sh-${TARGET_ARCH}-${TARGET_OS}
			;;
	esac
	# These are strewn all over the source tree
	for foo in `grep -I --exclude="*.patch" --exclude="*.diff" --exclude="*.pod" --exclude="README*" -m1 "/usr/include/.*\.h" ${S}/* -r -l` ${S}/utils/h2xs.PL ; do
		echo Fixing: $foo
		sed -e 's|\([ "^'\''I]\+\)/usr/include/|\1${STAGING_INCDIR}/|g' -i $foo
	done

	echo "installvendorlib='${datadir}/perl5'" >> config.sh-${TARGET_ARCH}-${TARGET_OS}
	echo "vendorlib='${datadir}/perl5'" >> config.sh-${TARGET_ARCH}-${TARGET_OS}
	echo "vendorlibexp='${datadir}/perl5'" >> config.sh-${TARGET_ARCH}-${TARGET_OS}

	rm -f config
	echo "ARCH = ${TARGET_ARCH}" > config
	echo "OS = ${TARGET_OS}" >> config
}

do_compile() {
	# Fix to avoid recursive substitution of path
	sed -i -e "s|\([ \"\']\+\)/usr/include|\1${STAGING_INCDIR}|g" ext/Errno/Errno_pm.PL
	sed -i -e "s|\([ \"\']\+\)/usr/include|\1${STAGING_INCDIR}|g" cpan/Compress-Raw-Zlib/config.in
	sed -i -e 's|/usr/lib|""|g' cpan/Compress-Raw-Zlib/config.in
	sed -i -e 's|(@libpath, ".*"|(@libpath, "${STAGING_LIBDIR}"|g' cpan/ExtUtils-MakeMaker/lib/ExtUtils/Liblist/Kid.pm

	cd Cross
	oe_runmake perl LD="${CCLD}"
}

do_install() {
	#export hostperl="${STAGING_BINDIR_NATIVE}/perl-native/perl${PERL_PV}"
	oe_runmake install DESTDIR=${D}
	# Add perl pointing at current version
	ln -sf perl${PERL_PV} ${D}${bindir}/perl

	ln -sf perl ${D}/${libdir}/perl5

	# Remove unwanted file and empty directories
	rm -f ${D}/${libdir}/perl/${PERL_PV}/.packlist
	rmdir ${D}/${libdir}/perl/site_perl/${PERL_PV}
	rmdir ${D}/${libdir}/perl/site_perl

	# Fix up shared library
	mv ${D}/${libdir}/perl/${PERL_PV}/CORE/libperl.so ${D}/${libdir}/libperl.so.${PERL_PV}
	ln -sf libperl.so.${PERL_PV} ${D}/${libdir}/libperl.so.5
	ln -sf ../../../libperl.so.${PERL_PV} ${D}/${libdir}/perl/${PERL_PV}/CORE/libperl.so

	# target config, used by cpan.bbclass to extract version information
	install config.sh ${D}${libdir}/perl

	ln -s Config_heavy.pl ${D}${libdir}/perl/${PERL_PV}/Config_heavy-target.pl
}

do_install_append_class-nativesdk () {
	create_wrapper ${D}${bindir}/perl \
            PERL5LIB='$PERL5LIB:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl/${PERL_PV}:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl/site_perl/${PERL_PV}:$OECORE_NATIVE_SYSROOT/${libdir_nativesdk}/perl/vendor_perl/${PERL_PV}'
}

PACKAGE_PREPROCESS_FUNCS += "perl_package_preprocess"

perl_package_preprocess () {
	# Fix up installed configuration
	sed -i -e "s,${D},,g" \
               -e "s,--sysroot=${STAGING_DIR_HOST},,g" \
               -e "s,-isystem${STAGING_INCDIR} ,,g" \
               -e "s,${STAGING_LIBDIR},${libdir},g" \
               -e "s,${STAGING_BINDIR},${bindir},g" \
               -e "s,${STAGING_INCDIR},${includedir},g" \
               -e "s,${STAGING_BINDIR_NATIVE}/perl-native/,${bindir}/,g" \
               -e "s,${STAGING_BINDIR_NATIVE}/,,g" \
               -e "s,${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX},${bindir},g" \
            ${PKGD}${bindir}/h2xs \
            ${PKGD}${bindir}/h2ph \
            ${PKGD}${bindir}/pod2man \
            ${PKGD}${bindir}/pod2text \
            ${PKGD}${bindir}/pod2usage \
            ${PKGD}${bindir}/podchecker \
            ${PKGD}${bindir}/podselect \
            ${PKGD}${libdir}/perl/${PERL_PV}/CORE/config.h \
            ${PKGD}${libdir}/perl/${PERL_PV}/CORE/perl.h \
            ${PKGD}${libdir}/perl/${PERL_PV}/CORE/pp.h \
            ${PKGD}${libdir}/perl/${PERL_PV}/Config.pm \
            ${PKGD}${libdir}/perl/${PERL_PV}/Config.pod \
            ${PKGD}${libdir}/perl/${PERL_PV}/Config_heavy.pl \
            ${PKGD}${libdir}/perl/${PERL_PV}/ExtUtils/Liblist/Kid.pm \
            ${PKGD}${libdir}/perl/${PERL_PV}/FileCache.pm \
            ${PKGD}${libdir}/perl/${PERL_PV}/pod/*.pod \
            ${PKGD}${libdir}/perl/config.sh
}

PACKAGES = "${PN}-dbg ${PN}-base ${PN} ${PN}-dev ${PN}-pod ${PN}-doc ${PN}-lib \
            ${PN}-module-cpan ${PN}-module-cpanplus ${PN}-module-unicore"
FILES_${PN}-base = "${bindir}/perl ${bindir}/perl${PERL_PV} \
               ${libdir}/perl/${PERL_PV}/Config.pm \
               ${libdir}/perl/${PERL_PV}/strict.pm \
               ${libdir}/perl/${PERL_PV}/warnings.pm \
               ${libdir}/perl/${PERL_PV}/warnings \
               ${libdir}/perl/${PERL_PV}/vars.pm \
              "
FILES_${PN}_append_class-nativesdk = " ${bindir}/perl.real"
RPROVIDES_${PN} += "${PN}-module-strict ${PN}-module-vars ${PN}-module-config \
                    ${PN}-module-warnings ${PN}-module-warnings-register ${PN}-misc"
FILES_${PN}-dev = "${libdir}/perl/${PERL_PV}/CORE"
FILES_${PN}-lib = "${libdir}/libperl.so* \
                   ${libdir}/perl5 \
                   ${libdir}/perl/config.sh \
                   ${libdir}/perl/${PERL_PV}/Config_heavy.pl \
                   ${libdir}/perl/${PERL_PV}/Config_heavy-target.pl"
FILES_${PN}-pod = "${libdir}/perl/${PERL_PV}/pod \
		   ${libdir}/perl/${PERL_PV}/*.pod \
                   ${libdir}/perl/${PERL_PV}/*/*.pod \
                   ${libdir}/perl/${PERL_PV}/*/*/*.pod "
FILES_${PN} = "${bindir}/*"
FILES_${PN}-dbg += "${libdir}/perl/${PERL_PV}/auto/*/.debug \
                    ${libdir}/perl/${PERL_PV}/auto/*/*/.debug \
                    ${libdir}/perl/${PERL_PV}/auto/*/*/*/.debug \
                    ${libdir}/perl/${PERL_PV}/CORE/.debug \
                    ${libdir}/perl/${PERL_PV}/*/.debug \
                    ${libdir}/perl/${PERL_PV}/*/*/.debug \
                    ${libdir}/perl/${PERL_PV}/*/*/*/.debug "
FILES_${PN}-doc = "${libdir}/perl/${PERL_PV}/*/*.txt \
                   ${libdir}/perl/${PERL_PV}/*/*/*.txt \
                   ${libdir}/perl/${PERL_PV}/auto/XS/Typemap \
                   ${libdir}/perl/${PERL_PV}/B/assemble \
                   ${libdir}/perl/${PERL_PV}/B/cc_harness \
                   ${libdir}/perl/${PERL_PV}/B/disassemble \
                   ${libdir}/perl/${PERL_PV}/B/makeliblinks \
                   ${libdir}/perl/${PERL_PV}/CGI/eg \
                   ${libdir}/perl/${PERL_PV}/CPAN/PAUSE2003.pub \
                   ${libdir}/perl/${PERL_PV}/CPAN/SIGNATURE \
		   ${libdir}/perl/${PERL_PV}/CPANPLUS/Shell/Default/Plugins/HOWTO.pod \
                   ${libdir}/perl/${PERL_PV}/Encode/encode.h \
                   ${libdir}/perl/${PERL_PV}/ExtUtils/MANIFEST.SKIP \
                   ${libdir}/perl/${PERL_PV}/ExtUtils/NOTES \
                   ${libdir}/perl/${PERL_PV}/ExtUtils/PATCHING \
                   ${libdir}/perl/${PERL_PV}/ExtUtils/typemap \
                   ${libdir}/perl/${PERL_PV}/ExtUtils/xsubpp \
		   ${libdir}/perl/${PERL_PV}/ExtUtils/Changes_EU-Install \
                   ${libdir}/perl/${PERL_PV}/Net/*.eg \
                   ${libdir}/perl/${PERL_PV}/unicore/mktables \
                   ${libdir}/perl/${PERL_PV}/unicore/mktables.lst \
                   ${libdir}/perl/${PERL_PV}/unicore/version "

FILES_${PN}-module-cpan += "${libdir}/perl/${PERL_PV}/CPAN \
                           ${libdir}/perl/${PERL_PV}/CPAN.pm"
FILES_${PN}-module-cpanplus += "${libdir}/perl/${PERL_PV}/CPANPLUS \
                               ${libdir}/perl/${PERL_PV}/CPANPLUS.pm"
FILES_${PN}-module-unicore += "${libdir}/perl/${PERL_PV}/unicore"

# Create a perl-modules package recommending all the other perl
# packages (actually the non modules packages and not created too)
ALLOW_EMPTY_perl-modules = "1"
PACKAGES_append = " perl-modules "

python populate_packages_prepend () {
    libdir = d.expand('${libdir}/perl/${PERL_PV}')
    do_split_packages(d, libdir, 'auto/([^.]*)/[^/]*\.(so|ld|ix|al)', 'perl-module-%s', 'perl module %s', recursive=True, match_path=True, prepend=False)
    do_split_packages(d, libdir, 'Module/([^\/]*)\.pm', 'perl-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, 'Module/([^\/]*)/.*', 'perl-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)
    do_split_packages(d, libdir, '(^(?!(CPAN\/|CPANPLUS\/|Module\/|unicore\/|auto\/)[^\/]).*)\.(pm|pl|e2x)', 'perl-module-%s', 'perl module %s', recursive=True, allow_dirs=False, match_path=True, prepend=False)

    # perl-modules should recommend every perl module, and only the
    # modules. Don't attempt to use the result of do_split_packages() as some
    # modules are manually split (eg. perl-module-unicore).
    packages = filter(lambda p: 'perl-module-' in p, d.getVar('PACKAGES', True).split())
    d.setVar(d.expand("RRECOMMENDS_${PN}-modules"), ' '.join(packages))
}

PACKAGES_DYNAMIC += "^perl-module-.*"
PACKAGES_DYNAMIC_class-nativesdk += "^nativesdk-perl-module-.*"

require perl-rdepends.inc
require perl-ptest.inc

# Base on debian/control
RPROVIDES_${PN}-base += " \
    perl5-base libscalar-list-utils-perl libxsloader-perl \
    libsocket-perl libfile-temp-perl libfile-path-perl \
    libio-socket-ip-perl \
"
RPROVIDES_${PN}-modules += " \
    libpod-parser-perl libansicolor-perl libnet-perl libattribute-handlers-perl \
    libi18n-langtags-perl liblocale-maketext-perl libmath-bigint-perl \
    libnet-ping-perl libtest-harness-perl libtest-simple-perl \
    liblocale-codes-perl libmodule-corelist-perl libio-zlib-perl libarchive-tar-perl \
    libextutils-cbuilder-perl libmodule-load-perl liblocale-maketext-simple-perl \
    libparams-check-perl libmodule-load-conditional-perl libversion-perl \
    libpod-simple-perl libextutils-parsexs-perl libpod-escapes-perl libparse-cpan-meta-perl \
    libparent-perl libautodie-perl libthread-queue-perl libfile-spec-perl libtime-local-perl \
    podlators-perl libunicode-collate-perl libcpan-meta-perl libmath-complex-perl \
    libextutils-command-perl libmodule-metadata-perl libjson-pp-perl libperl-ostype-perl \
    libversion-requirements-perl libcpan-meta-yaml-perl libdigest-perl libextutils-install-perl \
    libhttp-tiny-perl libcpan-meta-requirements-perl libexperimental-perl \
"
RPROVIDES_${PN} += " \
    data-dumper perl5 libdigest-md5-perl libmime-base64-perl libtime-hires-perl \
    libstorable-perl libdigest-sha-perl libsys-syslog-perl libcompress-zlib-perl \
    libcompress-raw-zlib-perl libcompress-raw-bzip2-perl libio-compress-zlib-perl \
    libio-compress-bzip2-perl libio-compress-base-perl libio-compress-perl \
    libthreads-perl libthreads-shared-perl libtime-piece-perl libencode-perl \
"

RDEPENDS_${PN}-modules += "${PN}-base"
RDEPENDS_${PN} += "${PN}-base ${PN}-modules"

SSTATE_SCAN_FILES += "*.pm *.pod *.h *.pl *.sh"

BBCLASSEXTEND = "nativesdk"
