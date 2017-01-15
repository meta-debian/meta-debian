require gcc-generate-debian-series.inc

DEPENDS += " gcc-source"

# Because of sharing source code, these task have been done with 
# gcc-source_debian.bb recipe
deltask do_fetch
deltask do_unpack

do_unpack_extra[depends] += "gcc-source:do_unpack"
do_populate_lic[depends] += "gcc-source:do_unpack"

# Set S to ${DEBIAN_UNPACK_DIR}/src to apply patches from debian/patches
S = "${DEBIAN_UNPACK_DIR}/src"
do_unpack_extra() {
	test -d ${DEBIAN_UNPACK_DIR} || mkdir -p ${DEBIAN_UNPACK_DIR}
	test -d ${S} || mkdir -p ${S}

	# copy source from shared folder to each package
	cp -rf ${SW}/git/debian ${DEBIAN_UNPACK_DIR}/
	find ${SW} -maxdepth 1 -type f -exec cp -f {} ${WORKDIR}/ \;

	PV_SRCPKG=$(head -n 1 ${DEBIAN_UNPACK_DIR}/debian/changelog | \
	                                  sed "s|.*(\([^()]*\)).*|\1|")
	PV_ORIG=$(echo $PV_SRCPKG | sed "s|-.*||")

	# extract source code from shared folder
	tar xf ${SW}/git/gcc-$PV_ORIG-dfsg.tar.xz -C ${S}
	mv -n ${S}/gcc-$PV_ORIG/* ${S}/
	rm -r ${S}/gcc-$PV_ORIG
}

addtask unpack_extra after do_unpack before do_debian_verify_version
