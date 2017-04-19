SUMMARY = "File Alteration Monitor"
DESCRIPTION = "FAM monitors files and directories, notifying interested applications \
of changes. \
This package provides a server that can monitor a given list of files \
and notify applications through a socket.  If the kernel supports \
dnotify (kernels >= 2.4.x) FAM is notified directly by the kernel. \
Otherwise it has to poll the files' status.  FAM can also provide an \
RPC service for monitoring remote files (such as on a mounted NFS \
filesystem)."
HOMEPAGE = "http://oss.sgi.com/projects/fam/"

PR = "r0"

inherit debian-package
PV = "2.7.0"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://src/COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
    file://lib/COPYING;md5=dcf3c825659e82539645da41a7908589 \
"

# Extrace source code from compress file
do_unpack_extra() {
	PV_SRCPKG=$(head -n 1 ${S}/debian/changelog | \
	            sed "s|.*(\([^()]*\)).*|\1|")
	PV_ORIG=$(echo $PV_SRCPKG | sed "s|-.*||")
	tar -xzf ${DEBIAN_UNPACK_DIR}/${DPN}-$PV_ORIG.tar.gz -C ${S}
	mv ${S}/${DPN}-$PV_ORIG/* ${S}/
	rmdir ${S}/${DPN}-$PV_ORIG
}
addtask unpack_extra after do_unpack before do_debian_fix_timestamp

# Apply patches in debian/patches.
# Debian patch files are not same level,
# so we need check them before apply.
do_debian_patch() {
	patch_levels="1 0 2"
	for patch_file in ${DEBIAN_QUILT_PATCHES}/*; do
		for level in $patch_levels; do
			if patch -d ${S} -E --dry-run -p$level -i $patch_file; then
				patch -d ${S} -E -p$level -i $patch_file
			fi
		done
	done
}

inherit autotools-brokensep

# Follow debian/rules
EXTRA_OECONF = "--with-pic=yes"
CXXFLAGS += "-g -Wall -O2 -Wno-sign-compare -Wno-deprecated \
		-D_FILE_OFFSET_BITS=64 -D_LARGEFILE64_SOURCE"

do_install_append() {
	# Install init, conf file from ${S}/debian
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/fam.init ${D}${sysconfdir}/init.d/fam
	install -m 0644 ${S}/debian/fam.conf ${D}${sysconfdir}/

	# Change library link follow Debian
	ln -sf libfam.so.0 ${D}${libdir}/libfam.so
}

PACKAGES =+ "libfam"
FILES_libfam = "${libdir}/libfam${SOLIBS}"
