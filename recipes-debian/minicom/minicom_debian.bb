#base recipe: /meta/recipes-extended/minicom/minicom_2.7.bb
# base branch: master
# base commit: 7e4cc9892dc72bfd360855940e715f8d43186053
#

SUMMARY = "Text-based modem control and terminal emulation program"
DESCRIPTION = " Minicom is a text-based modem control and terminal emulation \
		program for Unix-like operating systems"
HOMEPAGE = "https://alioth.debian.org/projects/minicom/"

PR = "r0"
inherit debian-package
PV = "2.7"

DEPENDS = "ncurses"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=420477abc567404debca0a2a1cb6b645 \
	file://src/minicom.h;beginline=1;endline=12;md5=a58838cb709f0db517f4e42730c49e81"

inherit autotools gettext pkgconfig

