SUMMARY = "GNU Portability Library"
DESCRIPTION = "\
	The GNU portability library is a macro system and C declarations and \
	definitions for commonly-used API elements and abstracted system behaviors.\
	It can be used to improve portability and other functionality in your programs.\
	"
HOMEPAGE = " http://www.gnu.org/software/gnulib/"

PR = "r0"
inherit debian-package
PV = "20140202+stable"

LICENSE = "GPLv3+ & GPLv2+ & LGPLv2+ & LGPLv3+ & GFDL-1.3"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=e4cf3810f33a067ea7ccd2cd889fed21 \
	file://lib/xstrtol.c;beginline=1;endline=19;md5=59e62eec95598de1dabf311b492d13ff \
	file://doc/COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
	file://doc/COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
	file://doc/COPYING.LESSERv2;md5=4fbd65380cdd255951079008b364516c \
	file://doc/COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
	file://doc/gnulib.texi;beginline=21;endline=34;md5=76bfc6d3b1f0371d2414b0f5a52044ea"
inherit autotools-brokensep allarch

#install follow Debian jessie
do_install() {
	install -d ${D}${bindir}
	install -d ${D}${datadir}/gnulib
	install -m 0755 ${S}/check-module ${D}${bindir}
	install -m 0755 ${S}/modules/git-merge-changelog ${D}${bindir}

	cp -a build-aux posix-modules config doc lib m4 modules top tests \
		MODULES.html.sh Makefile ${D}${datadir}/gnulib
	install -m 0755 ${S}/gnulib-tool ${D}${datadir}/gnulib
	ln -s ../..${datadir}/gnulib/gnulib-tool ${D}${bindir}/gnulib-tool
	install -m 0644 ${S}/cfg.mk ${D}${datadir}/gnulib
	install -m 0755 ${S}/check-copyright ${D}${datadir}/gnulib

	# Fixing permissions
	chmod 0755 ${D}${datadir}/gnulib/build-aux/config.guess
	chmod 0755 ${D}${datadir}/gnulib/build-aux/config.sub
	chmod 0755 ${D}${datadir}/gnulib/build-aux/gendocs.sh
	chmod 0644 ${D}${datadir}/gnulib/doc/gendocs_template
	chmod 0755 ${D}${datadir}/gnulib/lib/config.charset
	chmod 0644 ${D}${datadir}/gnulib/m4/fflush.m4
	chmod 0644 ${D}${datadir}/gnulib/modules/canonicalize-lgpl
	chmod 0644 ${D}${datadir}/gnulib/modules/fflush
	chmod 0644 ${D}${datadir}/gnulib/modules/fflush-tests
	chmod 0644 ${D}${datadir}/gnulib/tests/test-base64.c
	chmod 0755 ${D}${datadir}/gnulib/tests/test-closein.sh
	chmod 0644 ${D}${datadir}/gnulib/tests/test-fflush.c
	chmod 0755 ${D}${datadir}/gnulib/tests/test-posix_spawn1.in.sh
	chmod 0755 ${D}${datadir}/gnulib/tests/test-posix_spawn2.in.sh

	# Removing unused files
	rm -f ${D}${datadir}/gnulib/modules/COPYING
	rm -f ${D}${datadir}/gnulib/*/.cvsignore
	rm -f ${D}${datadir}/gnulib/*/.gitignore*
	rm -f ${D}${datadir}/gnulib/*/.gitattributes
}
PACKAGES =+ "git-merge-changelog"
FILES_git-merge-changelog = "${bindir}/git-merge-changelog"

# workaround for re-build without clean
CLEANBROKEN = "1"
