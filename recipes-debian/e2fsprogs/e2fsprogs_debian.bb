require recipes-devtools/e2fsprogs/${PN}_1.42.9.bb
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-devtools/e2fsprogs/e2fsprogs:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "1"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b48f21d765b875bd10400975d12c1ca2"

SRC_URI += " \
file://acinclude.m4 \
file://remove.ldconfig.call.patch \
"

# Automake has dropped such macro, use "mkdir -p" instead
do_configure_prepend() {
	sed -i -e "s:AM_MKINSTALLDIRS:AM_PROG_MKDIR_P:" ${S}/configure.in
	sed -i -e "s:MKINSTALLDIRS = .*:MKINSTALLDIRS = @MKDIR_P@:" \
							${S}/MCONFIG.in
}

# Remove option to disable libuuid to avoid error external uuid library
# not found. 
EXTRA_OECONF_remove = "--disable-libuuid --disable-uuidd"
