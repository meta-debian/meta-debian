#
# base recipe: meta/recipes-core/kbd/kbd_2.0.1.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package

SUMMARY = "Keytable files and keyboard utilities"
# everything minus console-fonts is GPLv2+
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5fcc36121d93e1f69d96a313078c8b5"
DEPENDS = "libcheck"

inherit autotools gettext ptest

RREPLACES_${PN} = "console-tools"
RPROVIDES_${PN} = "console-tools"
RCONFLICTS_${PN} = "console-tools"

PACKAGECONFIG ?= "${@base_contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"

PACKAGES += "${PN}-consolefonts ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-keymaps = "${datadir}/keymaps"
FILES_${PN}-unimaps = "${datadir}/unimaps"

inherit update-alternatives

ALTERNATIVE_${PN} = "chvt deallocvt fgconsole openvt"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"

# Strange file name in kbd needed to be removed
do_debian_fix_timestamp_prepend() {
	git rm ${S}/doc/utf/??????
}
