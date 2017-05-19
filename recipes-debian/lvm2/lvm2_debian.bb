PR = "${INC_PR}.3"
require lvm2.inc

DEPENDS = "udev readline"
EXTRA_OECONF += "\
        --enable-udev_rules \
        --enable-udev_sync \
"
do_install_append(){
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		oe_runmake 'DESTDIR=${D}' install_systemd_units
		ln -sf lvm2-activation.service ${D}${systemd_unitdir}/system/lvm2.service
	fi
	# Install initscript file from Debian
	if [ ! -d ${D}${sysconfdir}/init.d ]; then
		install -d ${D}${sysconfdir}/init.d
	fi
	for i in ${S}/debian/*.init; do
		filename=$(basename $i)
		filename=${filename%.*}
		install -m 0755 $i ${D}${sysconfdir}/init.d/$filename
	done

	# Change link location from absolute path to relative path
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	for file in ${D}${libdir}/*.so; do
		ln -sf ${rel_lib_prefix}${base_libdir}/$(basename $(readlink $file)) $file
	done
}

# Follow debian/control
RDEPENDS_${PN} += "lsb-base dmsetup dmeventd"
RDEPENDS_clvm += "${PN} lsb-base"
RDEPENDS_libdevmapper-dev += "libdevmapper libdevmapper-event"
RDEPENDS_libdevmapper += "dmsetup"
RDEPENDS_liblvm2cmd += "dmeventd"
RDEPENDS_${PN}-dev += "liblvm2app liblvm2cmd libdevmapper-dev"

DEBIANNAME_${PN}-dev = "liblvm2-dev"
