# base recipe: meta/recipes-extended/cracklib/cracklib_2.9.1.bb
# base branch: master

SUMMARY = "Password strength checker library"
HOMEPAGE = "http://sourceforge.net/projects/cracklib"

PR = "r0"

DPN = "cracklib2"

inherit debian-package

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

DEPENDS = "cracklib-native zlib python"
RDEPENDS_python-${PN} += "python"

EXTRA_OECONF = "--with-python --libdir=${libdir}"

inherit autotools gettext pythonnative python-dir

do_install_append_class-target() {
	create-cracklib-dict -o ${D}${datadir}/cracklib/pw_dict \
				${D}${datadir}/cracklib/cracklib-small
}

do_install_append() {
	src_dir="${D}${libdir}/${PYTHON_DIR}/site-packages"
	rm -f $src_dir/*.pyo
	
	# remove unused files
	rm -f ${D}${libdir}/*.la ${D}${libdir}/libcrack.so
	rm -rf ${D}${datadir}/${PN}

	ln -sf libcrack.so.2 ${D}${libdir}/libcrack.so

	install -d -m 0755 ${D}${sysconfdir}/${PN}/
	cp ${S}/debian/cracklib.conf ${D}${sysconfdir}/${PN}/
	
	install -d ${D}${sysconfdir}/cron.daily/
	install -m 0755 ${S}/debian/cracklib-runtime.cron.daily \
			${D}${sysconfdir}/cron.daily/cracklib-runtime

	install -m 0755 ${S}/debian/update-cracklib ${D}${sbindir}/
}

BBCLASSEXTEND = "native nativesdk"

PACKAGES += "${PN}-runtime libcrack2 libcrack2-dev python-${PN}"

FILES_python-${PN} = " \
	${libdir}/${PYTHON_DIR}/* \
    "

FILES_${PN}-dbg += " \
	${libdir}/${PYTHON_DIR}/site-packages/.debug \
	${libdir}/.debug \
    "

FILES_libcrack2-dev = " \
	${includedir}/crack.h \
	${includedir}/packer.h \
	${libdir}/libcrack.so \
    "

FILES_libcrack2 = " \
	${libdir}/libcrack.so.2 \
	${libdir}/libcrack.so.2.9.0 \
    "

FILES_${PN}-runtime = " \
	${sysconfdir}/${PN}/cracklib.conf \
	${sysconfdir}/cron.daily/cracklib-runtime \
	${sbindir}/cracklib-check \
	${sbindir}/cracklib-format \
	${sbindir}/cracklib-packer \
	${sbindir}/cracklib-unpacker \
	${sbindir}/create-cracklib-dict \
	${sbindir}/update-cracklib \
    "

FILES_${PN}-staticdev += " \
	${libdir}/${PYTHON_DIR}/site-packages/_cracklib.a \
    "
FILES_${PN}-doc += "${datadir}"

