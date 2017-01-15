SUMMARY = "Perl module with additional list functions not found in List::Util"
DESCRIPTION = "List::MoreUtils provides some trivial but commonly needed functionality on\n\
lists which is not going to go into List::Util.\n\
.\n\
All of the functions are implementable in only a couple of lines of Perl\n\
code. Using the functions from this module however should give slightly better\n\
performance as everything is implemented in C. The pure-Perl implementation of\n\
these functions only serves as a fallback in case the C portions of this module\n\
could not be compiled on this machine."
HOMEPAGE = "https://metacpan.org/release/List-MoreUtils"

PR = "r0"

inherit debian-package
PV = "0.33"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit cpan

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
