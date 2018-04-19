SUMMARY = "small but fast and easy to use stand-alone template engine"
DESCRIPTION = "Jinja2 is a template engine written in pure Python. It provides a Django \
 inspired non-XML syntax but supports inline expressions and an optional \
 sandboxed environment."
HOMEPAGE = "http://jinja.pocoo.org/"

DPN = "jinja2"
INC_PR = "r0"
inherit debian-package
PV = "2.7.3"

LICENSE = "BSD-3-Clause & GPLv2+"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=20c831f91dd3bd486020f672ba2be386 \
	file://ext/jinja.el;beginline=15;endline=28;md5=5e97d42b23b4b7512d52c9e4912bc278"

KEEP_NONARCH_BASELIB = "1"

inherit setuptools

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH

do_install_append() {
	# remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf `find ${D}${libdir} -type d -name "__pycache__"`
}
