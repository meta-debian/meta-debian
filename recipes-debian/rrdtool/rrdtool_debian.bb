SUMMARY = "time-series data storage and display system (programs)"
DESCRIPTION = "\
	The Round Robin Database Tool (RRDtool) is a system to store and display \
	time-series data (e.g. network bandwidth, machine-room temperature, \ 
	server load average). It stores the data in Round Robin Databases (RRDs), \
	a very compact way that will not expand over time. RRDtool processes the \ 
	xtracted data to enforce a certain data density, allowing for useful \ 
	graphical representation of data values. \
	RRDtool is often used via various wrappers that can poll data from devices \
	and feed data into RRDs, as well as provide a friendlier user interface and \
	ustomized graphs. \
"
HOMEPAGE = "http://oss.oetiker.ch/rrdtool/"

PR = "r2"
inherit debian-package pythonnative cpan
PV = "1.4.8"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=44fee82a1d2ed0676cf35478283e0aa0"

inherit autotools-brokensep pkgconfig gettext
DEPENDS += "libxml2 glib-2.0 pango tcl lua5.1-native lua5.1 perl libpng"
DEBIAN_PATCH_TYPE = "quilt"

#export some variable from poky, to use for python command
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR
export LDSHARED="${CCLD} -shared"

#correct-path-to-library_debian.patch:
#	using the library in sysroot instead of host system
SRC_URI += "file://correct-path-to-library_debian.patch"

EXTRA_OECONF += "\
	PYTHON=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
	--disable-python --enable-lua --enable-tcl --enable-lua-site-install \
	--with-tcllib=${STAGING_LIBDIR}/tcl8.6 \
	--with-perl-options='INSTALLDIRS="vendor" INSTALL_BASE=' \
	--disable-ruby \
"

do_configure_prepend() {
	sed -i -e "s:##STAGING_DIR_HOST##:${STAGING_DIR_HOST}:g" \
		-e "s:AC_PATH_PROG(LUA, lua, no):AC_PATH_PROG(LUA, lua5.1, no):g" \
		${S}/configure.ac
	sed -i -e "s:L\$(libdir):L${STAGING_LIBDIR}:g" ${S}/bindings/tcl/Makefile.am
}

oe_runconf_prepend() {
	#correct path to header file Python.h
	sed -i -e "s:I\${py_prefix}:I${STAGING_DIR_HOST}/\${py_prefix}:g" \
		${S}/configure
}

do_compile() {
	oe_runmake "TCL_INCLUDE_SPEC=-I${STAGING_INCDIR}/tcl8.6" \
		"TCL_STUB_LIB_SPEC=-L${STAGING_LIBDIR} -ltclstub8.6"
	
	#build python follow debian/rules
	cd ${S}/bindings/python && \
		BUILDLIBDIR=../../src/.libs LIBDIR=../../src/.libs \
		${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py build
}

#follow debian/rules
do_install_prepend() {
	#Fix library path to tcl bindings
	sed -i -e 's|lib/|lib/tcltk/rrdtool-tcl/|' ${S}/bindings/tcl/pkgIndex.tcl
}

do_install_append() {
	#remove unwanted files
	rm -r ${D}${datadir}/${DPN}
	rm ${D}${libdir}/*.la
	
	install -d ${D}${libdir}/perl5/"$(echo ${PERLVERSION} | cut -d . -f 1,2)"
	install -d ${D}${datadir}/perl5
	install -d ${D}${libdir}/tcltk/rrdtool-tcl

	mv ${D}${libdir}/tclrrd1.*.so ${D}${libdir}/tcltk/rrdtool-tcl/
	
	install -m 0644 ${S}/bindings/tcl/pkgIndex.tcl \
		${D}${libdir}/tcltk/rrdtool-tcl/
	
	install -D -m 0644 ${S}/debian/rrdcached.default \
		${D}${sysconfdir}/default/rrdcached

	install -D -m 0755 ${S}/debian/rrdcached.init.d \
		${D}${sysconfdir}/init.d/rrdcached	
	mv ${D}${libdir}/perl/*/*/auto ${D}${libdir}/perl/*/*/RRDs.pm \
		${D}${libdir}/perl5/"$(echo ${PERLVERSION} | cut -d . -f 1,2)"
	mv ${D}${libdir}/perl/*/*/RRDp.pm ${D}${datadir}/perl5/
	rm -r ${D}${libdir}/perl
	rm ${D}${libdir}/lua/*/rrd.la
}
PACKAGES += "liblua5.1-rrd liblua5.1-rrd-dev librrd-dev librrd librrdp-perl \
		librrds-perl python-${PN} rrdcached ${PN}-tcl"
FILES_liblua5.1-rrd = "${libdir}/lua/*/rrd.so.*"
FILES_liblua5.1-rrd-dev = "\
	${includedir}/lua5.1/lua-rrd.h ${libdir}/lua/*/rrd.so"
FILES_librrd-dev = "\
	${includedir} ${libdir}/librrd.so \
	${libdir}/librrd_th.so ${libdir}/pkgconfig"

FILES_librrd = "${libdir}/librrd.so.* ${libdir}/librrd_th.so.*"

FILES_librrdp-perl = "${datadir}/perl5"

FILES_librrds-perl = "\
	${libdir}/perl5/*/RRDs.pm ${libdir}/perl5/*/auto/RRDs/RRDs.so"

FILES_python-${PN} = "${libdir}/${PYTHON_DIR}/site-packages/*"

FILES_rrdcached = "${sysconfdir}/* ${bindir}/rrdcached"

FILES_${PN}-tcl = "${libdir}/tcltk/*"
FILES_${PN}-dbg += "\
	${libdir}/${PYTHON_DIR}/site-packages/.debug \
	${libdir}/perl5/*/auto/RRDs/.debug ${libdir}/tcltk/rrdtool-tcl/.debug \
	${libdir}/lua/*/.debug"
FILES_${PN}-staticdev += "${libdir}/lua/*/rrd.a"
