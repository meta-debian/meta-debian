SUMMARY = "tools for managing OCFS2 cluster filesystems"
DESCRIPTION = "\
OCFS2 is a general purpose cluster filesystem. Unlike the initial release \
of OCFS, which supported only Oracle database workloads, OCFS2 provides \
full support as a general purpose filesystem.  OCFS2 is a complete rewrite \
of the previous version, designed to work as a seamless addition to the \
Linux kernel. \
This package installs the tools to manage the OCFS2 filesystem, including mkfs, \
tunefs, fsck, debugfs, and the utilities to control the O2CB clustering stack \
"
HOMEPAGE = "http://oss.oracle.com/projects/ocfs2-tools/"

PR = "r1"
inherit debian-package
PV = "1.6.4"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
inherit autotools-brokensep pkgconfig pythonnative

SRC_URI += "file://0001-add-option-to-disable-pacemaker.patch"

#follow debian/rules
EXTRA_OECONF += "--disable-debug --enable-dynamic-ctl --enable-dynamic-fsck"

DEPENDS += "e2fsprogs util-linux glib-2.0 \
            ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'pygtk', '', d)}"

oe_runconf_prepend() {
	#correct path to header file Python.h
	sed -i -e "s:I\${py_prefix}:I${STAGING_DIR_HOST}${prefix}:g" \
	${S}/configure
}

do_install_append() {
	rm -r ${D}${bindir}
	rm ${D}${sbindir}/o2hbmonitor
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/vendor/common/o2cb.init \
		${D}${sysconfdir}/init.d/o2cb
	install -m 0755 ${S}/vendor/common/ocfs2.init \
		${D}${sysconfdir}/init.d/ocfs2

	install -d ${D}${base_libdir}/udev/rules.d
	install -m 0644 ${S}/vendor/common/51-ocfs2.rules \
		${D}${base_libdir}/udev/rules.d

	install -d ${D}${datadir}/pyshared/ocfs2interface
	install -m 0644 ${S}/ocfs2console/ocfs2interface/*.py \
		${D}${datadir}/pyshared/ocfs2interface

	install -d ${D}${libdir}/pyshared/python${PYTHON_BASEVERSION}/ocfs2interface
	for file in ${D}${PYTHON_SITEPACKAGES_DIR}/ocfs2interface/*.so;do
		ln -s ../../../../..${PYTHON_SITEPACKAGES_DIR}/ocfs2interface/$(basename $file) \
			${D}${libdir}/pyshared/python${PYTHON_BASEVERSION}/ocfs2interface/$(basename $file)
	done
}
PARALLEL_MAKE = ""

PACKAGES =+ "ocfs2console"
FILES_ocfs2console = "\
	${PYTHON_SITEPACKAGES_DIR}/ocfs2interface/*.py \
	${libdir}/pyshared/python${PYTHON_BASEVERSION}/ocfs2interface/*.py \
	${sbindir} ${datadir}/pyshared"
FILES_${PN}-dev += "\
	${libdir}/pyshared/python${PYTHON_BASEVERSION}/ocfs2interface/*.so \
	${PYTHON_SITEPACKAGES_DIR}/ocfs2interface/*.so"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/ocfs2interface/.debug"
