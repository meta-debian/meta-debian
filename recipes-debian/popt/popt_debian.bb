#
# base recipe: meta/recipes-support/popt/popt_1.16.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#
SUMMARY = "Library for parsing command line options"
HOMEPAGE = "http://rpm5.org/"

inherit debian-package
require recipes-debian/sources/popt.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cb0613c30af2a8249b8dcc67d3edb06d"

DEPENDS = "virtual/libiconv"

inherit autotools gettext

BBCLASSEXTEND = "native nativesdk"
