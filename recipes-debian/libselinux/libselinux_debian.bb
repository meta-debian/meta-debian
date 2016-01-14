DESCRIPTION = "SELinux runtime shared libraries"

HOMEPAGE =  "http://packages.debian.org/source/sid/libs/libselinux"

PR = "r0"

inherit debian-package python-dir

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84b4d2c6ef954a2d4081e775a270d0d0"

DEPENDS += "libsepol libpcre swig-native python"

do_compile() {
	oe_runmake CC=${TARGET_SYS}-gcc PREFIX="${prefix}" LIBBASE="${base_libdir}" LIBSEPOLDIR="${STAGING_LIBDIR}" all
}

do_install() {
	oe_runmake CC=${TARGET_SYS}-gcc PREFIX="${D}${prefix}" LIBSEPOLDIR="${STAGING_LIBDIR}" \
			DESTDIR=${D} LIBDIR=${D}/${libdir} install
	# Fix up the broken library symlink
	rm -f ${D}${libdir}/libselinux.so
	ln -s ${base_libdir}/libselinux.so.1 ${D}${libdir}/libselinux.so
	oe_runmake CC=${TARGET_SYS}-gcc PREFIX="${D}${prefix}" LIBSEPOLDIR="${STAGING_LIBDIR}" \
			DESTDIR=${D} LIBDIR=${D}/${libdir} \
			PYINC=-I${STAGING_INCDIR}/${PYTHON_DIR} install-pywrap

	# Currently, Install python in site-package directory
	## Fix the python library directory path
	#mv ${D}${libdir}/${PYTHON_DIR}/site-packages ${D}${libdir}/${PYTHON_DIR}/dist-packages

	# Currently, libselinux does not support to build ruby
	#oe_runmake CC=${TARGET_SYS}-gcc PREFIX="${D}${prefix}" LIBSEPOLDIR="${STAGING_LIBDIR}" \
	#		DESTDIR=${D} LIBDIR=${D}/${libdir} \
	#		RUBYINSTALL=${D} install-rubywrap

	# Remove unneeded directory to prevent QA warnings about install ${base_sbindir}
	if [ -d ${D}${base_sbindir} ]; then
		rmdir ${D}${base_sbindir}
	fi
}

# Add package follow debian
PACKAGES =+ "selinux-utils python-selinux"

FILES_selinux-utils += "${sbindir}/*"
FILES_python-selinux += "${PYTHON_SITEPACKAGES_DIR}/selinux/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/selinux/.debug"
FILES_${PN}-dev += "${libdir}/*"

# Correct .deb files
DEBIANAME_${PN} = "${PN}1"
PKG_${PN}-dev = "${PN}1-dev"
