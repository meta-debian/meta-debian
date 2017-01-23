SUMMARY = "Built-package format for Python"
DESCRIPTION = "\
	A wheel is a ZIP-format archive with a specially formatted filename \
	and the `.whl` extension.  It is designed to contain all the files \
	for a PEP 376 compatible install in a way that is very close to \
	the on-disk format. \
	The wheel project provides a `bdist_wheel` command for setuptools. \
	Wheel files can be installed with `pip`."
HOMEPAGE = "https://bitbucket.org/pypa/wheel"

INC_PR = "r0"

inherit debian-package
PV = "0.24.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9d66b41bc2a080e7174acc5dffecd752"

# Set do patch is no execute because folder debian patches empty
do_debian_patch[noexec] = "1"
