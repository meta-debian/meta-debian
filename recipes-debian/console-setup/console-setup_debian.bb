SUMMARY = "console font and keymap setup program"
DESCRIPTION = "This package provides the console with the same keyboard\n\
configuration scheme as the X Window System. As a result, there is no\n\
need to duplicate or change the keyboard files just to make simple\n\
customizations such as the use of dead keys, the key functioning as\n\
AltGr or Compose key, the key(s) to switch between Latin and\n\
non-Latin mode, etc.\n\
.\n\
The package also installs console fonts supporting many of the\n\
world's languages.  It provides an unified set of font faces - the\n\
classic VGA, the simplistic Fixed, and the cleaned Terminus,\n\
TerminusBold and TerminusBoldVGA."

inherit debian-package
PV = "1.123"

LICENSE = "GPLv2+ & PD & MIT-style & Adobe & BSD-3-Clause & OFL-1.1 & HPND"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=49cab1cfd397b014807c5b2bcc63e04f \
                    file://copyright.fonts;md5=891f119082e7e3ec79d5bc9bfa650124 \
                    file://copyright.xkb;md5=4f0d6e3d430e0d681239e8ac30d7a01b \
                    file://Fonts/copyright;md5=2e638bf77a44d03d791520a1c96fbb95 \
                    file://GPL-2;md5=4325afd396febcb659c36b49533135d4"

DEPENDS = "liblocale-gettext-perl libxml-parser-perl bdfresize-native sysvinit-native"

inherit perlnative

xkbdir = "${datadir}/X11/xkb"

do_compile() {
	rm ${S}/Keyboard/KeyboardNames.pl
	oe_runmake xkbdir=${xkbdir} \
	    build-linux
}

do_install() {
	oe_runmake etcdir=${D}${sysconfdir} prefix=${D}${prefix} xkbdir=${xkbdir} \
	    install-common \
	    install-common-linux \
	    install-ckbcomp

	install -D -m 0755 ${S}/debian/keyboard-configuration.console-setup.init \
	                   ${D}${sysconfdir}/init.d/console-setup
	install -D -m 0755 ${S}/debian/keyboard-configuration.keyboard-setup.init \
	                   ${D}${sysconfdir}/init.d/keyboard-setup

	ls ${D}${mandir}/*/* | sed \
		-e 's|^\([.a-zA-Z][a-zA-Z]*\) /usr/local/etc|\1 ${sysconfdir}|' \
		-e 's|^\([.a-zA-Z][a-zA-Z]*\) /usr/local|\1 ${prefix}|'

	install -D -m 0755 ${S}/setupcon ${D}${base_bindir}/setupcon

	install -d ${D}${datadir}/console-setup
	mv ${D}${sysconfdir}/default/keyboard ${D}${datadir}/console-setup/
	mv ${D}${sysconfdir}/default/console-setup ${D}${datadir}/console-setup/
	install -m 0644 ${S}/Keyboard/KeyboardNames.pl ${D}${datadir}/console-setup/
	install -m 0755 ${S}/Keyboard/kbdnames-maker ${D}${datadir}/console-setup/

	install -d ${D}${datadir}/bdf2psf
	install -m 0755 ${S}/Fonts/bdf2psf ${D}${bindir}/
	cp -r ${S}/Fonts/*.equivalents ${S}/Fonts/*.set ${S}/Fonts/fontsets ${D}${datadir}/bdf2psf/
}

PACKAGES =+ "bdf2psf keyboard-configuration ${PN}-linux"

FILES_bdf2psf = " \
    ${bindir}/bdf2psf \
    ${datadir}/bdf2psf \
"
FILES_keyboard-configuration = " \
    ${sysconfdir}/init.d/*-setup \
    ${datadir}/console-setup/kbdnames-maker \
    ${datadir}/console-setup/keyboard \
    ${datadir}/console-setup/KeyboardNames.pl \
"
FILES_${PN}-linux = " \
    ${sysconfdir}/console-setup/compose.*.inc \
    ${sysconfdir}/console-setup/remap.inc \
    ${datadir}/consolefonts/*.psf.gz \
    ${datadir}/consoletrans/*.acm.gz \
"

RDEPENDS_keyboard-configuration += "liblocale-gettext-perl"
RDEPENDS_${PN} += "${PN}-linux xkb-data keyboard-configuration"
RDEPENDS_${PN}-linux += "kbd keyboard-configuration"

inherit insserv
INITSCRIPT_PACKAGES = "keyboard-configuration"
INITSCRIPT_NAMES_keyboard-configuration = "keyboard-setup console-setup"

# Base on debian/console-setup.postinst
# Debian postinst can get parameters from user,
# we cannot do the same, but we can define them from recipe,
# sublayer meta-* will easily overwrite these variables.
ACTIVE_CONSOLES ??= "/dev/tty[1-6]"
FONT ??= ""
FONT_MAP ??= ""
CONSOLE_MAP ??= ""
CHARMAP ??= ""
CODESET ??= ""
FONTFACE ??= "Fixed"
FONTSIZE ??= ""
pkg_postinst_${PN}() {
    CONFIGFILE=$D${sysconfdir}/default/console-setup

    if [ ! -e $CONFIGFILE ]; then
        sed "s|^ *ACTIVE_CONSOLES=.*|ACTIVE_CONSOLES=\"${ACTIVE_CONSOLES}\"|" \
            $D${datadir}/console-setup/console-setup >$CONFIGFILE || true
    fi

    # Ensure we do not mess up the config file's ownership and permissions.
    cp -a -f $CONFIGFILE $CONFIGFILE.tmp

    sed \
        -e "s|^ *ACTIVE_CONSOLES=.*|ACTIVE_CONSOLES=\"${ACTIVE_CONSOLES}\"|" \
        -e "s|^ *FONT=.*|FONT=\"${FONT}\"|" \
        -e "s|^ *FONT_MAP=.*|FONT_MAP=\"${FONT_MAP}\"|" \
        -e "s|^ *CONSOLE_MAP=.*|CONSOLE_MAP=\"${CONSOLE_MAP}\"|" \
        -e "s|^ *ACM=.*|CONSOLE_MAP=\"${CONSOLE_MAP}\"|" \
        -e "s|^ *CHARMAP=.*|CHARMAP=\"${CHARMAP}\"|" \
        -e "s|^ *CODESET=.*|CODESET=\"${CODESET}\"|" \
        -e "s|^ *FONTFACE=.*|FONTFACE=\"${FONTFACE}\"|" \
        -e "s|^ *FONTSIZE=.*|FONTSIZE=\"${FONTSIZE}\"|" \
        <$CONFIGFILE >$CONFIGFILE.tmp

    mv -f $CONFIGFILE.tmp $CONFIGFILE
}

# Base on debian/keyboard-configuration.postinst
XKBMODEL ??= "pc105"
XKBLAYOUT ??= "us"
XKBVARIANT ??= ""
XKBOPTIONS ??= ""
BACKSPACE ??= "guess"
pkg_postinst_keyboard-configuration() {
    CONFIGFILE=$D${sysconfdir}/default/keyboard

    if [ ! -e $CONFIGFILE ]; then
        cat $D${datadir}/console-setup/keyboard \
            2>/dev/null >$CONFIGFILE || true
    fi

    # Ensure we do not mess up the config file's ownership and permissions.
    cp -a -f $CONFIGFILE $CONFIGFILE.tmp

    sed \
        -e "s|^ *XKBMODEL=.*|XKBMODEL=\"${XKBMODEL}\"|" \
        -e "s|^ *XKBLAYOUT=.*|XKBLAYOUT=\"${XKBLAYOUT}\"|" \
        -e "s|^ *XKBVARIANT=.*|XKBVARIANT=\"${XKBVARIANT}\"|" \
        -e "s|^ *XKBOPTIONS=.*|XKBOPTIONS=\"${XKBOPTIONS}\"|" \
        -e "s|^ *BACKSPACE=.*|BACKSPACE=\"${BACKSPACE}\"|" \
        <$CONFIGFILE >$CONFIGFILE.tmp

    mv -f $CONFIGFILE.tmp $CONFIGFILE
}
