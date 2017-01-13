#
# base recipe: meta/recipes-bsp/lrzsz/lrzsz_0.12.20.bb
# base branch: jethro
#

SUMMARY = "Tools for zmodem/xmodem/ymodem file transfer"
DESCRIPTION = "Lrzsz is a cosmetically modified zmodem/ymodem/xmodem package built\n\
from the public-domain version of Chuck Forsberg's rzsz package.\n\
.\n\
These programs use error correcting protocols ({z,x,y}modem) to send\n\
(sz, sx, sb) and receive (rz, rx, rb) files over a dial-in serial port\n\
from a variety of programs running under various operating systems."
HOMEPAGE = "https://ohse.de/uwe/software/lrzsz.html"

PR = "r0"

inherit debian-package
PV = "0.12.21"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
    file://src/lrz.c;beginline=1;endline=10;md5=5276956373ff7d8758837f6399a1045f \
"

# lrzsz_fix_for_automake-1.12.patch:
#	Fix "error: automatic de-ANSI-fication support has been removed"
# lrzsz-check-locale.h.patch:
#	Fix "error: 'LC_ALL' undeclared (first use in this function)"
# gettext_debian.patch: base on gettext.patch from base recipe
#	Fix "*** No rule to make target 'Makevars', needed by 'Makefile'.  Stop."
# fix-removing-in-the-host-development-system.patch
#       Fix "rm: cannot remove '/usr/bin/sx': Permission denied"
SRC_URI += " \
    file://lrzsz_fix_for_automake-1.12.patch \
    file://lrzsz-check-locale.h.patch \
    file://gettext_debian.patch \
    file://fix-removing-in-the-host-development-system.patch \
"

inherit autotools-brokensep gettext

# Follow debian/rules
EXTRA_OECONF = "--program-transform-name=s/l//"

# in parallel build, "install-exec-local" that creates links to binaries
# in bindir, would be executed before the binaries are installed
PARALLEL_MAKE = ""
