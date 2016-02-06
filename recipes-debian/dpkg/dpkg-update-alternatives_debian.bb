require dpkg.inc

PR = "${INC_PR}.1"

DESCRIPTION = "Dpkg version of update-alternatives"

DPN = "dpkg"
PROVIDES += "virtual/update-alternatives"

SRC_URI += "\
	file://implement-offline-mode.patch \
	file://fix_update_alternatives_dir.patch \
	file://fix_offline_dir.patch \
"

inherit autotools gettext pkgconfig

# disable features except update-alternatives
EXTRA_OECONF = " \
	--enable-update-alternatives \
	--disable-dselect \
	--disable-start-stop-daemon \
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
	install -d ${D}${sbindir} 
	install -d ${D}${sysconfdir}/alternatives 
	install -d ${D}${localstatedir}/lib/dpkg/alternatives
	install -m 0755 ${B}/utils/update-alternatives ${D}${sbindir}
}

PACKAGES = "${PN} ${PN}-dbg"
FILES_${PN} = " \
	${sbindir}/update-alternatives \
	${localstatedir}/lib/dpkg/alternatives \
	${sysconfdir}/alternatives \
"
FILES_${PN}-dbg = " \
	${prefix}/src \
	${sbindir}/.debug \
"

BBCLASSEXTEND = "native nativesdk"
