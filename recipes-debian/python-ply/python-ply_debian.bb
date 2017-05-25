SUMMARY = "Lex and Yacc implementation for Python2"
DESCRIPTION = "PLY   is   yet  another   implementation   of   lex   and  yacc   for\n\
Python.  Although  several  other  parsing tools  are  available  for\n\
Python, there are  several reasons why you might want  to take a look\n\
at PLY:\n\
 * It's implemented entirely in Python.\n\
 * It uses  LR-parsing which is reasonably efficient  and well suited\n\
   for larger grammars.\n\
 * PLY  provides most  of  the standard  lex/yacc features  including\n\
   support for  empty productions, precedence  rules, error recovery,\n\
   and support for ambiguous grammars.\n\
 * PLY is  extremely easy  to use and  provides very  extensive error\n\
   checking."
HOMEPAGE = "http://www.dabeaz.com/ply/"
LICENSE = "BSD-3-Clause"
SECTION = "python"
DEPENDS = "python"
LIC_FILES_CHKSUM = "file://README;beginline=3;endline=30;md5=36197c7ddf450a50a52cf6e743196b1d"

PR = "r0"
inherit debian-package
PV = "3.4"
DPN = "ply"

inherit allarch setuptools

DISTUTILS_INSTALL_ARGS += "--single-version-externally-managed \
	--install-layout=deb"

BBCLASSEXTEND = "native"
