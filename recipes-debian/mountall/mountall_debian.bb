SUMMARY = "Filesystem mounting tool"
DESCIPTION = "Mountall mounts filesystems when the underlying block devices are \
ready, or when network interfaces come up, checking the filesystems \
first."

LICENSE = "LGPLv2+ & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://intl/gettext.c;endline=17;md5=235b186f82eb78ee2b3937e9231a0273"

inherit debian-package
PV = "2.54"

DEPENDS += "plymouth libnih"

inherit autotools gettext

EXTRA_OECONF += "--exec-prefix= "" \
                 --libdir=${base_libdir} \
                 --sbindir=${base_sbindir} \
                 "

PR = "r0"
DEBIAN_PATCH_TYPE = "nopatch"
FILES_${PN} += "\
	${base_libdir} ${base_sbindir} \
	${datadir}/initramfs-tools/event-driven/upstart-jobs/mountall.conf \
	${datadir}/initramfs-tools/hooks/mountall \
	${datadir}/apport/package-hooks/mountall.py \
	"
