SUMMARY = "PAM module that can mount volumes for a user session"
DESCIPTION = "This module is aimed at environments with central file servers \
that a user wishes to mount on login and unmount on logout, such as (semi-) \
diskless stations where many users can logon. The module also supports \
mounting local filesystems of any kind the normal mount utility supports, \
with extra code to make sure certain volumes are set up properly because \
often they need more than just a mount call, such as encrypted volumes. This \
includes SMB/CIFS, FUSE, dm-crypt and LUKS."

PR = "r0"

inherit debian-package

DEPENDS += "libhx pkgconfig libpam cryptsetup libxml2"

LICENSE = "GPL-3"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ff88ed2ef97e3905a92cd1c331bbe42"

inherit autotools

PV = "2.14"

EXTRA_OECONF += "\
	--libdir=${base_libdir} \
	--with-dtd"

do_install_append(){
	rm -rf ${D}${localstatedir}/run

	install -d ${D}${datadir}/lintian/overrides
	install -m 0644 ${S}/debian/libpam-mount.lintian-overrides ${D}${datadir}/lintian/overrides/libpam-mount
	
	install -d ${D}${datadir}/pam-configs
	install -m 0644 ${S}/debian/pam-auth-update ${D}${datadir}/pam-configs/libpam-mount
}

FILES_${PN}-dbg += "${base_libdir}/security/.debug/*"
FILES_${PN}-dev += "${base_libdir}/pkgconfig/libcryptmount.pc"
FILES_${PN} += "\
	${datadir}/xml/pam_mount/dtd/pam_mount.conf.xml.dtd \
	${base_libdir}/security/pam_mount.so \
	${datadir}/lintian/overrides/libpam-mount \
	${datadir}/pam-configs/libpam-mount \
	"
