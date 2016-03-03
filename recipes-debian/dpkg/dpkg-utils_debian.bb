#
# this recipe provides the following two commands
#
# update-alternatives
#   Required to provide update-alternatives.bbclass infrastructure.
#   This command is always needed to be built for creating rootfs.
#
# start-stop-daemon
#   Most init scripts in Debian use this command.
#
# this recipe is intended to prevent several wasted recipes (e.g. perl)
# that dpkg depends on from being built by separating them from dpkg recipe
#

require dpkg.inc

PR = "${INC_PR}.1"

DPN = "dpkg"
PROVIDES += "virtual/update-alternatives"

SRC_URI += "\
	file://implement-offline-mode.patch \
	file://fix_update_alternatives_dir.patch \
	file://fix_offline_dir.patch \
"

inherit autotools gettext pkgconfig

# disable features except update-alternatives and start-stop-daemon
EXTRA_OECONF = " \
	--enable-update-alternatives \
	--enable-start-stop-daemon \
	--disable-dselect \
	--without-zlib \
	--without-bz2 \
	--without-liblzma \
	--without-selinux \
"

# Only compile materials for update-alternatives
do_compile() {
	cd ${B}/lib && oe_runmake
	cd ${B}/utils && oe_runmake
}
  
do_install() {
	# update-alternatives
	install -d ${D}${sbindir} 
	install -m 0755 ${B}/utils/update-alternatives ${D}${sbindir}
	install -d ${D}${sysconfdir}/alternatives 
	install -d ${D}${localstatedir}/lib/dpkg/alternatives

	# start-stop-daemon
	install -d ${D}${base_sbindir}
	install -m 0755 ${B}/utils/start-stop-daemon ${D}${base_sbindir}
}

PACKAGES = " \
	update-alternatives \
	start-stop-daemon \
	${PN}-dbg \
"

FILES_update-alternatives = " \
	${sbindir}/update-alternatives \
	${localstatedir}/lib/dpkg/alternatives \
	${sysconfdir}/alternatives \
"

FILES_start-stop-daemon = "${base_sbindir}/start-stop-daemon*"

FILES_${PN}-dbg = " \
	${prefix}/src \
	${sbindir}/.debug \
	${base_sbindir}/.debug \
"

inherit update-alternatives

ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_start-stop-daemon = "start-stop-daemon"
ALTERNATIVE_LINK_NAME[start-stop-daemon] = "${base_sbindir}/start-stop-daemon"

BBCLASSEXTEND = "native nativesdk"
