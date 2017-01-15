#
# Base recipe: meta/recipes-support/libpcre/libpcre_8.34.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

DESCRIPTION = "The PCRE library is a set of functions that implement regular \
expression pattern matching using the same syntax and semantics as Perl 5. PCRE \
has its own native API, as well as a set of wrapper functions that correspond \
to the POSIX regular expression API."
SUMMARY = "Perl Compatible Regular Expressions"
HOMEPAGE = "http://www.pcre.org"

inherit autotools binconfig ptest debian-package
PV = "8.35"
PR = "r0"
DPN = "pcre3"
DEPENDS += "bzip2 zlib"
PROVIDES += "pcre"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=ded617e975f28e15952dc68b84a7ac1a"

SRC_URI += " \
           file://pcre-cross.patch \
           file://run-ptest \
           file://Makefile \
"

PACKAGECONFIG[pcretest-readline] = "--enable-pcretest-libreadline,--disable-pcretest-libreadline,readline,"

PARALLEL_MAKE = ""

EXTRA_OECONF = "\
    --enable-newline-is-lf \
    --enable-rebuild-chartables \
    --enable-utf8 \
    --with-link-size=2 \
    --with-match-limit=10000000 \
    --enable-unicode-properties \
    --disable-silent-rules \
"

# Set LINK_SIZE in BUILD_CFLAGS given that the autotools bbclass use it to
# set CFLAGS_FOR_BUILD, required for the libpcre build.
BUILD_CFLAGS =+ "-DLINK_SIZE=2 -I${B}"
CFLAGS += "-D_REENTRANT"
CXXFLAGS_append_powerpc = " -lstdc++"

do_install_append() {
	install -d ${D}${base_libdir}

	# Move libpcre to /lib folder according to Debian rules
	mv ${D}${libdir}/libpcre.so.* ${D}${base_libdir}
	ln -sf ../../lib/libpcre.so.3 ${D}${libdir}/libpcre.so

	# Install zpcregrep according to Debian package
	install -m 0755 ${S}/debian/zpcregrep ${D}${bindir}/
}	

PACKAGES =+ "libpcrecpp pcregrep pcregrep-doc pcretest pcretest-doc"

SUMMARY_libpcrecpp = "${SUMMARY} - C++ wrapper functions"
SUMMARY_pcregrep = "grep utility that uses perl 5 compatible regexes"
SUMMARY_pcregrep-doc = "grep utility that uses perl 5 compatible regexes - docs"
SUMMARY_pcretest = "program for testing Perl-comatible regular expressions"
SUMMARY_pcretest-doc = "program for testing Perl-comatible regular expressions - docs"

FILES_${PN} += "${base_libdir}/libpcre.so.* ${libdir}/libpcreposix.so.*"
FILES_libpcrecpp = "${libdir}/libpcrecpp.so.*"
FILES_pcregrep = "${bindir}/pcregrep ${bindir}/zpcregrep"
FILES_pcregrep-doc = "${mandir}/man1/pcregrep.1"
FILES_pcretest = "${bindir}/pcretest"
FILES_pcretest-doc = "${mandir}/man1/pcretest.1"

# Correct name of .deb file
DEBIANNAME_${PN} = "libpcre3"

BBCLASSEXTEND = "native nativesdk"

do_install_ptest() {
	t=${D}${PTEST_PATH}
	cp ${WORKDIR}/Makefile $t
	cp -r ${S}/testdata $t
	for i in pcre_stringpiece_unittest pcregrep pcretest; \
	  do cp ${B}/.libs/$i $t; \
	done
	for i in RunTest RunGrepTest test-driver; \
	  do cp ${S}/$i $t; \
	done
}
