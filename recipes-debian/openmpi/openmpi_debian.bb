SUMMARY = "high performance message passing library"
DESCRIPTION = "Open MPI is a project combining technologies and resources from several other \
 projects (FT-MPI, LA-MPI, LAM/MPI, and PACX-MPI) in order to build the best \
 MPI library available. A completely new MPI-2 compliant implementation, Open \
 MPI offers advantages for system and software vendors, application developers \
 and computer science researchers."
HOMEPAGE = "http://www.open-mpi.org/"

inherit debian-package
PV = "1.6.5"

LICENSE = "BSD-3-Clause & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0698c5222dfec3357c9f26938c5eea69 \
                    file://opal/libltdl/COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

# Declare some variables which are can't determine with cross-compiling
SRC_URI += "file://Fortran-config_debian.conf"
inherit autotools-brokensep update-alternatives

LIBDIR = "${libdir}/openmpi/lib"
INCDIR = "${libdir}/openmpi/include"

# Base on debian/rules
EXTRA_OECONF += "--enable-ft-thread \
                 --with-ft=cr \
                 --with-blcr=${STAGING_DIR_HOST}${prefix} \
                 --with-blcr-libdir=${STAGING_LIBDIR} \
                 --without-tm \
                 --with-threads=posix --enable-opal-multi-threads \
                 --disable-silent-rules \
                 --with-hwloc=${STAGING_DIR_HOST}${prefix} \
                 --with-libltdl=external \
                 --with-devel-headers \
                 --with-slurm \
                 --with-sge \
                 --enable-heterogeneous \
                 --disable-vt \
                 --sysconfdir=${sysconfdir}/openmpi \
                 --libdir=${LIBDIR} \
                 --includedir=${INCDIR} \
                 --program-prefix="" \
                 "

# Adding the path to search headers and library of libibverbs
# Prevent use headers and library on host machine.
EXTRA_OECONF += "--with-openib=${STAGING_DIR_HOST}${prefix}"

DEPENDS += "hwloc blcr libtool libgfortran libibverbs chrpath-native"
EXTRA_OEMAKE += "'LIBTOOL=${HOST_SYS}-libtool'"

do_configure() {
	FORTRAN_CONF="`cat ${WORKDIR}/Fortran-config_debian.conf`"
	# Overwrite this function to avoid autoreconf call for now.
	# autoreconf return some error:
	# 	error: WANT_FT does not appear in AM_CONDITIONAL
	#	error: WANT_INSTALL_HEADERS does not
	oe_runconf ${FORTRAN_CONF}
}
do_install_append() {
	# Follow debian/rules
	# Strip rpath info from all executables and libraries.
	find ${D} -type f -perm -+x -a ! -name '*.la' -a ! -name '*.mod' \
	     -exec chrpath -d '{}' \;
	# Rename the compiler and startup wrappers.
	for f in mpic++ mpicc mpiCC mpicxx mpiexec mpif77 mpif90 mpirun ; do
		if test ${D}${bindir}/${f}; then
			mv ${D}${bindir}/${f} ${D}${bindir}/${f}.openmpi
		fi
	done
	if test -f ${D}${bindir}/orte-bootproxy.sh; then
		mv ${D}${bindir}/orte-bootproxy.sh ${D}${bindir}/orte-bootproxy
	fi
	
	# Follow debian/libopenmpi1.6.links amd debian/libopenmpi-dev.links
	for f in ${D}${LIBDIR}/*.so.*.*.*; do
		ln -sf ../..${LIBDIR}/`basename $f` ${D}${libdir}/`basename $f`
		rm -rf ${f%.*.*}
	done
	for f in ${D}${libdir}/*.so.*.*.*; do
		ln -sf `basename $f` ${D}${libdir}/`basename ${f%.*.*}`
		ln -sf `basename ${f%.*.*}` ${D}${libdir}/`basename ${f%.*.*.*}`
	done
	install -d ${D}${INCDIR}/openmpi/opal/sys/arm64
	install -m 0644 ${S}/opal/include/opal/sys/arm64/*.h \
		${D}${INCDIR}/openmpi/opal/sys/arm64

	install -d ${D}${includedir}
	ln -sf ../..${INCDIR} ${D}${includedir}/openmpi
	
	# Follow debian/openmpi-common.links
	for f in mpic++ mpicc mpiCC mpicxx mpif77 mpif90; do
		ln -sf ${f}-wrapper-data.txt \
		       ${D}${datadir}/openmpi/${f}.openmpi-wrapper-data.txt
	done
	rm ${D}${libdir}/libmpi.so
	find ${D}${libdir} -type f -name "*.la" -exec rm -f {} \;
	find ${D}${libdir} -type f -name "*.a" -exec rm -f {} \;

	sed -i -e "s|sysroot=${STAGING_DIR_HOST}|sysroot=/|g" \
	       -e "s|${STAGING_DIR_HOST}||g" \
		${D}${INCDIR}/openmpi/opal_config.h \
		${D}${datadir}/openmpi/*.txt \
		${D}${bindir}/orte_wrapper_script
}

do_install_append_class-target() {
	sed -i -e "s|${STAGING_DIR_HOST}||g" \
		${D}${libdir}/openmpi/lib/pkgconfig/*.pc
}

PACKAGES =+ "lib${PN} ${PN}-checkpoint ${PN}-common"

FILES_lib${PN} = "${libdir}/*${SOLIBS} \
                  ${LIBDIR}/*${SOLIBS} \\
                  ${LIBDIR}/openmpi/* \
                  ${LIBDIR}/mpi.mod"
FILES_${PN}-checkpoint = "${bindir}/*-checkpoint ${bindir}/*restart"
FILES_${PN}-common = "${datadir}/openmpi/*"
FILES_${PN}-dbg += "${LIBDIR}/.debug \
                    ${LIBDIR}/openmpi/.debug"
FILES_${PN}-dev += "${LIBDIR}/*.so \
                    ${bindir}/mpiCC.openmpi \
                    ${bindir}/mpic++.openmpi \
                    ${bindir}/mpicc.openmpi \
                    ${bindir}/mpicxx.openmpi \
                    ${bindir}/mpif77.openmpi \
                    ${bindir}/mpif90.openmpi \
                    ${bindir}/opal_wrapper \
                    ${bindir}/opalc++ \
                    ${bindir}/opalcc \
                    ${bindir}/orteCC \
                    ${bindir}/orte_wrapper_script \
                    ${bindir}/ortec++ \
                    ${bindir}/ortecc \
                    ${INCDIR}/* \
                    ${libdir}/openmpi/lib/pkgconfig/*"

PKG_lib${PN} = "lib${PN}1.6"
PKG_${PN}-dev = "lib${PN}-dev"
PKG_${PN} = "${PN}-bin"
PKG_${PN}-dbg = "lib${PN}1.6-dbg"

RPROVIDES_lib${PN} = "lib${PN}1.6"
RPROVIDES_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN} = "${PN}-bin"
RDEPENDS_${PN} += "${PN}-common"
RSUGGESTS_${PN} += "${PN}-checkpoint"
RDEPEMDS_${PN}-dev += "${PN}-common libibverbs-dev libhwloc-dev"
RDEPENDS_${PN}-checkpoint += "${PN} blcr-util lib${PN}"

# Follow debian/openmpi-bin.postinst
ALTERNATIVE_${PN} = "mpirun mpiexec"
ALTERNATIVE_PRIORITY = "50"
ALTERNATIVE_LINK_NAME[mpirun] = "${bindir}/mpirun"
ALTERNATIVE_TARGET[mpirun] = "${bindir}/mpirun.openmpi"

ALTERNATIVE_LINK_NAME[mpiexec] = "${bindir}/mpiexec"
ALTERNATIVE_TARGET[mpiexec] = "${bindir}/mpiexec.openmpi"

# Follow debian/libopenmpi-dev.postinst
ALTERNATIVE_${PN}-dev = "mpi libmpi.so libmpi++.so libmpif77.so libmpif90.so \
                         mpicc mpic++ mpicxx mpiCC mpif77 mpif90"
ALTERNATIVE_LINK_NAME[mpi] = "${includedir}/mpi"
ALTERNATIVE_TARGET[mpi] = "${INCDIR}"

ALTERNATIVE_LINK_NAME[libmpi.so] = "${libdir}/libmpi.so"
ALTERNATIVE_TARGET[libmpi.so] = "${LIBDIR}/libmpi.so"

ALTERNATIVE_LINK_NAME[libmpi++.so] = "${libdir}/libmpi++.so"
ALTERNATIVE_TARGET[libmpi++.so] = "${LIBDIR}/libmpi_cxx.so"

ALTERNATIVE_LINK_NAME[libmpif77.so] = "${libdir}/libmpif77.so"
ALTERNATIVE_TARGET[libmpif77.so] = "${LIBDIR}/libmpi_f77.so"

ALTERNATIVE_LINK_NAME[libmpif90.so] = "${libdir}/libmpif90.so"
ALTERNATIVE_TARGET[libmpif90.so] = "${LIBDIR}/libmpi_f90.so"

ALTERNATIVE_LINK_NAME[mpicc] = "${bindir}/mpicc"
ALTERNATIVE_TARGET[mpicc] = "${bindir}/mpicc.openmpi"

ALTERNATIVE_LINK_NAME[mpic++] = "${bindir}/mpic++"
ALTERNATIVE_TARGET[mpic++] = "${bindir}/mpic++.openmpi"

ALTERNATIVE_LINK_NAME[mpicxx] = "${bindir}/mpicxx"
ALTERNATIVE_TARGET[mpicxx] = "${bindir}/mpic++.openmpi"

ALTERNATIVE_LINK_NAME[mpiCC] = "${bindir}/mpiCC"
ALTERNATIVE_TARGET[mpiCC] = "${bindir}/mpic++.openmpi"

ALTERNATIVE_LINK_NAME[mpif77] = "${bindir}/mpif77"
ALTERNATIVE_TARGET[mpif77] = "${bindir}/mpif77.openmpi"

ALTERNATIVE_LINK_NAME[mpif90] = "${bindir}/mpif90"
ALTERNATIVE_TARGET[mpif90] = "${bindir}/mpif90.openmpi"
