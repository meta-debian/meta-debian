SUMMARY = "X Window System utility programs for development"
DESCRIPTION = "xutils-dev provides a set of utility programs shipped with the X Window System\n\
 that do not require the X libraries; many of these programs are useful\n\
 even on a system that does not have any X clients or X servers installed.\n\
 .\n\
 The programs in this package include:\n\
 - imake, a C preprocessor interface to the make utility;\n\
 - lndir, a tool that creates a shadow directory of symbolic links to\n\
  another directory tree;\n\
  - makedepend, a tool to create dependencies in makefiles;\n\
  - makeg, a tool for making debuggable executables.\n\
  .\n\
 This package also contains xorg-macros.m4, a set of macros used in configure\n\
 scripts of X.Org packages."

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://imake/COPYING;md5=b9c6cfb044c6d0ff899eaafe4c729367"

inherit debian-package
PV = "7.7+3"

do_debian_fix_timestamp[noexec] = "1"
do_patch[noexec] = ""
do_debian_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

RDEPENDS_${PN} = "gccmakedep imake lndir makedepend util-macros xorg-cf-files"
ALLOW_EMPTY_${PN} = "1"
