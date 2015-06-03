SUMMARY = "Small library that defines common error values for all GnuPG components"
HOMEPAGE = "http://www.gnupg.org/related_software/libgpg-error/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/gpg-error.h.in;endline=23;md5=6ac0378874589a44d53512b3786b4bc0 \
                    file://src/init.c;endline=20;md5=b69742f2a8827d494c6f6a4b1768416c"


SECTION = "libs"

SRC_URI = "ftp://ftp.gnupg.org/gcrypt/libgpg-error/libgpg-error-${PV}.tar.bz2 \
           file://pkgconfig.patch"

SRC_URI[md5sum] = "8f0eb41a344d19ac2aa9bd101dfb9ce6"
SRC_URI[sha256sum] = "cafc9ed6a87c53a35175d5a1220a96ca386696eef2fa059cc0306211f246e55f"

inherit autotools binconfig pkgconfig gettext

FILES_${PN}-dev += "${bindir}/gpg-error"

do_install_append() {
	# we don't have common lisp in OE
	rm -rf "${D}${datadir}/common-lisp/"
}

BBCLASSEXTEND = "native"

#
#Meta-debian
#
inherit debian-package
DPR = "0"
DEBIAN_SECTION = "libs"

LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/gpg-error.h.in;endline=23;md5=5dfe776dc8b62af093ddc859de6f494c \
                    file://src/init.c;endline=20;md5=8f5a9b59634f4aebcd0ec9d3ebd53bfe \
"
# New version of libgpg-error required lock-obj-pub.linux-gnu.h
CPPFLAGS += "-P"
do_compile_prepend() {
	TARGET_FILE=linux-gnu
	if [ ${TARGET_OS} != "linux" ]; then
		TARGET_FILE=${TARGET_OS}
	fi

	case ${TARGET_ARCH} in
	  aarch64_be) TUPLE=aarch64-unknown-linux-gnu ;;
	  arm)	      TUPLE=arm-unknown-linux-gnueabi ;;
	  armeb)      TUPLE=arm-unknown-linux-gnueabi ;;
	  i586|i686)  TUPLE=i486-pc-linux-gnu ;;
	  mips64el)   TUPLE=mipsel-unknown-linux-gnu ;;
	  mips64)     TUPLE=mips-unknown-linux-gnu ;;
	  x86_64)     TUPLE=x86_64-pc-linux-gnu ;;
	  *)          TUPLE=${TARGET_ARCH}-unknown-linux-gnu ;; 
	esac

	cp ${S}/src/syscfg/lock-obj-pub.$TUPLE.h \
	  ${S}/src/syscfg/lock-obj-pub.$TARGET_FILE.h
}
