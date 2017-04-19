#
# base recipe: ./meta/recipes-extended/time/time_1.7.bb
# base branch: master
# base commit: 81a9c1313deb6904728edb53288a623809321038
#

SUMMARY = "Tool that measures CPU resources"
DESCRIPTION = " time measures many of the CPU resources, such as time and \
		memory, that other programs use."
HOMEPAGE = "http://www.gnu.org/software/time/"

PR = "r0"
inherit debian-package
PV = "1.7"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

inherit autotools
