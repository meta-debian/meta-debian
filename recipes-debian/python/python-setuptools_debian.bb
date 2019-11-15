# base recipe: meta/recipes-devtools/python/python-setuptools_40.8.0.bb
# base branch: warrior

DESCRITPION = "Python Distutils Enhancements"
SUMMARY = "Extensions to the python-distutils for large or complex distributions."

require recipes-devtools/python/python-setuptools.inc

inherit debian-package
require recipes-debian/sources/python-setuptools.inc

LICENSE = "Apache-2.0 "
LIC_FILES_CHKSUM = "file://LICENSE;md5=9a33897f1bca1160d7aad3835152e158"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/python/files"
PROVIDES = "python-distribute"

inherit setuptools

RREPLACES_${PN} = "python-distribute"
RPROVIDES_${PN} = "python-distribute"
RCONFLICTS_${PN} = "python-distribute"
