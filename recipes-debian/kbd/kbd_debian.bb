#
# base recipe: meta/recipes-core/kbd/kbd_2.0.1.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.15.5"

SUMMARY = "Keytable files and keyboard utilities"
# everything minus console-fonts is GPLv2+
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5fcc36121d93e1f69d96a313078c8b5"

inherit autotools gettext ptest pkgconfig

# Add configuration follow Debian
EXTRA_OECONF = "--enable-nls --disable-vlock"

RREPLACES_${PN} = "console-tools"
RPROVIDES_${PN} = "console-tools"
RCONFLICTS_${PN} = "console-tools"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"

EXTRA_BINARIES = "screendump setlogcons setvesablank"
CONTRIB_BINARIES = "codepage splitfont vcstime"
BIN_BINARIES = "kbd_mode setfont fgconsole openvt chvt dumpkeys unicode_start loadkeys"

do_compile_append() {
	oe_runmake -C ${B}/src ${EXTRA_BINARIES}
	oe_runmake CFLAGS="-O2 -g" -C ${S}/contrib/ ${CONTRIB_BINARIES}
}

do_install_append_class-target() {
	# some additional utilities
	for i in ${EXTRA_BINARIES}; do
		install -m755 ${B}/src/${i} ${D}${bindir}
	done
	for i in ${CONTRIB_BINARIES}; do
		install -m755 ${S}/contrib/${i} ${D}${bindir}
	done

	# Install init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/kbd.init ${D}${sysconfdir}/init.d/kbd

	# Install configuration files
	install -d ${D}${sysconfdir}/${DPN}
	install -m 0644 ${S}/debian/conffiles.d/* ${D}${sysconfdir}/${DPN}/

	# move everything where it belongs
	install -d ${D}${base_bindir} ${D}${base_sbindir} ${D}${sbindir}
	for i in ${BIN_BINARIES}; do
		mv ${D}${bindir}/${i} ${D}${base_bindir}
	done
	mv ${D}${bindir}/kbdrate ${D}${base_sbindir}/kbdrate
	mv ${D}${bindir}/vcstime ${D}${sbindir}/vcstime
	mv ${D}${bindir}/setvesablank ${D}${sbindir}/setvesablank

	# Create softlink
	ln -sf openvt ${D}${base_bindir}/open
	ln -sf ${base_bindir}/dumpkeys ${D}/${bindir}/dumpkeys
	ln -sf ${base_bindir}/loadkeys ${D}/${bindir}/loadkeys
}

PACKAGES += "${PN}-consolefonts ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-keymaps = "${datadir}/keymaps"
FILES_${PN}-unimaps = "${datadir}/unimaps"

inherit update-alternatives

ALTERNATIVE_${PN} = "chvt fgconsole openvt kbd_mode"
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_LINK_NAME[chvt] = "${base_bindir}/chvt"
ALTERNATIVE_LINK_NAME[fgconsole] = "${base_bindir}/fgconsole"
ALTERNATIVE_LINK_NAME[openvt] = "${base_bindir}/openvt"
ALTERNATIVE_LINK_NAME[kbd_mode] = "${base_bindir}/kbd_mode"

BBCLASSEXTEND = "native"

# Strange file name in kbd needed to be removed
do_debian_fix_timestamp_prepend() {
	git rm -rf ${S}/doc/utf
}
