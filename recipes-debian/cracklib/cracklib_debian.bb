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
RDEPENDS_${PN}-python += "python"

PACKAGES =+ "${PN}-runtime libcrack2 libcrack2-dev python-${PN}"

EXTRA_OECONF = "--with-python --libdir=${base_libdir}"

inherit autotools gettext pythonnative python-dir

do_install_append_class-target() {
	create-cracklib-dict -o ${D}${datadir}/cracklib/pw_dict ${D}${datadir}/cracklib/cracklib-small
}

do_install_append() {
	src_dir="${D}${base_libdir}/${PYTHON_DIR}/site-packages"
	rm -f $src_dir/*.pyo
	rm -f $src_dir/test_cracklib.py
	# Move python files from ${base_libdir} to ${libdir} since used --libdir=${base_libdir}
	install -d -m 0755 ${D}${PYTHON_SITEPACKAGES_DIR}/
	mv $src_dir/* ${D}${PYTHON_SITEPACKAGES_DIR}
	rm -fr ${D}${base_libdir}/${PYTHON_DIR}
	
	install -d -m 0755 ${D}${libdir}/${PYTHON_DIR}/dist-packages/
	mv ${D}${PYTHON_SITEPACKAGES_DIR}/_cracklib.so ${D}${libdir}/${PYTHON_DIR}/dist-packages/
	mv ${D}${PYTHON_SITEPACKAGES_DIR}/cracklib.py ${D}${libdir}/${PYTHON_DIR}/dist-packages/

	install -m 0755 ${S}/python/test_cracklib.py ${D}${libdir}/${PYTHON_DIR}/dist-packages/test_cracklib.py 
	install -m 0755 ${S}/debian/crack.py ${D}${libdir}/${PYTHON_DIR}/dist-packages/crack.py 

	install -d -m 0755 ${D}${libdir}/
	mv ${D}${nonarch_base_libdir}/libcrack.a ${D}${libdir}/
	mv ${D}${nonarch_base_libdir}/libcrack.so ${D}${libdir}/
	mv ${D}${nonarch_base_libdir}/libcrack.so.2 ${D}${libdir}/
	mv ${D}${nonarch_base_libdir}/libcrack.so.2.9.0 ${D}${libdir}/

	install -d ${D}${sysconfdir}/
	install -d -m 0755 ${D}${sysconfdir}/${PN}/
	cp ${S}/debian/cracklib.conf ${D}${sysconfdir}/${PN}/
	
	install -d ${D}${sysconfdir}/cron.daily/
	install -m 0755 ${S}/debian/cracklib-runtime.cron.daily ${D}${sysconfdir}/cron.daily/cracklib-runtime	

	install -m 0755 ${S}/debian/update-cracklib ${D}${sbindir}/
}

BBCLASSEXTEND = "native nativesdk"

FILES_python-${PN} = " \
	${libdir}/${PYTHON_DIR}/dist-packages/cracklib.py \
	${libdir}/${PYTHON_DIR}/dist-packages/_cracklib.so \
	${libdir}/${PYTHON_DIR}/dist-packages/test_cracklib.py \
	${libdir}/${PYTHON_DIR}/dist-packages/crack.py \
    "

FILES_${PN}-dbg += " \
	${PYTHON_SITEPACKAGES_DIR}/.debug/_cracklib.so \
	${libdir}/${PYTHON_DIR}/dist-packages/.debug/_cracklib.so \
	${libdir}/.debug/libcrack.so.2.9.0 \  
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
	${PYTHON_SITEPACKAGES_DIR}/_cracklib.a \
	${PYTHON_SITEPACKAGES_DIR}/_cracklib.la \
    "

