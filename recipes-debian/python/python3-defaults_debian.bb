DESCRIPTION = "Python, the high-level, interactive object oriented language,\n\
includes an extensive class library with lots of goodies for\n\
network programming, system administration, sounds and graphics.\n\
.\n\
This package is a dependency package, which depends on Debian's default\n\
Python 3 version (currently v3.4)."
HOMEPAGE = "http://www.python.org/"

PR = "r0"
inherit debian-package
PV = "3.4.2"

LICENSE = "PSFv2 & MIT"
LIC_FILES_CHKSUM = " \
    file://debian/copyright;md5=5d52ee1b68c656621c6e2245d07f174c \
    file://py3compile;beginline=4;endline=23;md5=f2bdbde66ec2fda4be6be2a124e75e4a \
    file://py3clean;beginline=4;endline=22;md5=2c7444fd5cf92a767bc6c7451f1e51fa \
"

DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS = "dpkg-native"

inherit python3native

do_install() {
	oe_runmake install 'DESTDIR=${D}' 'PREFIX=${prefix}'

	VER=${PYTHON_BASEVERSION}

	# provide the idle and idle.1 defaults
	install -m 755 ${S}/debian/idle.py ${D}${bindir}/idle3

	install -d ${D}${mandir}/man1
	install -m 644 ${S}/debian/idle.1 ${D}${mandir}/man1/idle3.1

	install -d ${D}${datadir}/pixmaps
	ln -sf python3.xpm ${D}${datadir}/pixmaps/idle3.xpm

	install -d ${D}${datadir}/applications
	cp -p ${S}/debian/idle3.desktop ${D}${datadir}/applications/

	# provide the python and python.1 defaults
	install -d ${D}${mandir}/man1
	ln -sf python${VER} ${D}${bindir}/python3
	ln -sf python${VER}m ${D}${bindir}/python3m
	ln -sf python${VER}.1.gz \
	        ${D}${mandir}/man1/python3.1.gz
	ln -sf python${VER}m.1.gz \
	        ${D}${mandir}/man1/python3m.1.gz

	cp -p ${S}/debian/debian_defaults ${D}${datadir}/python3/

	install -m 755 ${S}/debian/py3versions.py ${D}${datadir}/python3/
	install -m 644 ${S}/debian/py3versions.1 ${D}${mandir}/man1/
	ln -s ${datadir}/python3/py3versions.py ${D}${bindir}/py3versions

	ln -sf pydoc${VER} ${D}${bindir}/pydoc3
	ln -sf pygettext${VER} ${D}${bindir}/pygettext3
	ln -sf pdb${VER} ${D}${bindir}/pdb3

	install -m 644 ${S}/debian/python.mk \
	        ${D}${datadir}/python3/

	ln -sf pydoc${VER}.1.gz \
	        ${D}${mandir}/man1/pydoc3.1.gz
	ln -sf pygettext${VER}.1.gz \
	        ${D}${mandir}/man1/pygettext3.1.gz
	ln -sf pdb${VER}.1.gz \
	        ${D}${mandir}/man1/pdb3.1.gz

	ln -sf python${VER}.xpm ${D}${datadir}/pixmaps/python3.xpm
	# provide the python3-config default
	ln -sf python${VER}m-config ${D}${bindir}/python3m-config
	ln -sf python${VER}m-config.1.gz \
	        ${D}${mandir}/man1/python3m-config.1.gz
	ln -sf python${VER}-config ${D}${bindir}/python3-config
	ln -sf python${VER}-config.1.gz \
	        ${D}${mandir}/man1/python3-config.1.gz

	# provide the python-dbg and python-dbg.1 defaults
	ln -sf python${VER}dm ${D}${bindir}/python3dm
	ln -sf python${VER}dm-config ${D}${bindir}/python3dm-config
	ln -sf python${VER}-dbg ${D}${bindir}/python3-dbg
	ln -sf python${VER}-dbg-config ${D}${bindir}/python3-dbg-config
	ln -sf python${VER}dm.1.gz ${D}${mandir}/man1/python3dm.1.gz
	ln -sf python${VER}dm-config.1.gz ${D}${mandir}/man1/python3dm-config.1.gz
	ln -sf python${VER}-dbg.1.gz ${D}${mandir}/man1/python3-dbg.1.gz
	ln -sf python${VER}-dbg-config.1.gz ${D}${mandir}/man1/python3-dbg-config.1.gz

	install -d ${D}${docdir}/python3
	ln -sf ../python${VER}/SpecialBuilds.txt.gz ${D}${docdir}/python3/SpecialBuilds.txt.gz
	ln -sf ../python${VER}/README.debug ${D}${docdir}/python3/README.debug

	install -d ${D}${libdir}/valgrind
	cp -p ${S}/debian/valgrind-python.supp ${D}${libdir}/valgrind/python3.supp

	# provide the pyvenv and pyvenv.1 defaults
	ln -sf pyvenv-${VER} ${D}${bindir}/pyvenv
	ln -sf pyvenv-${VER}.1.gz ${D}${mandir}/man1/pyvenv.1.gz

	for p in all all-dev all-dbg dbg dev venv; do
		[ $p = idle3 ] || p=python3-$p
		rm -rf ${D}${docdir}/$p
		ln -sf python3 ${D}${docdir}/$p
	done
	rm -rf ${D}${docdir}/libpython3-all-dev
	ln -sf libpython3-dev ${D}${docdir}/libpython3-all-dev
}

# provide pkgconfig defaults
pkg_postinst_${PN}-dev() {
	mkdir -p $D${libdir}/pkgconfig
	ln -sf python-${PYTHON_BASEVERSION}.pc $D${libdir}/pkgconfig/python3.pc
}
pkg_postinst_${PN}-dbg() {
	mkdir -p $D${libdir}/pkgconfig
	ln -sf python-${PYTHON_BASEVERSION}-dbg.pc $D${libdir}/pkgconfig/python3-dbg.pc
}
PACKAGE_BEFORE_PN = "${PN}-minimal python3-venv python3-examples libpython3-dev \
                     libpython3-stdlib idle3 \
                     python3-all python3-all-dev \
                     libpython3-all-dev\
                     "

FILES_${PN}-venv = "${bindir}/pyvenv"
FILES_${PN}-minimal = " \
    ${bindir}/py3* \
    ${bindir}/python3 \
    ${bindir}/python3m \
    ${datadir}/python3/debian_defaults \
    ${datadir}/python3/debpython/* \
    ${datadir}/python3/py3versions.py \
"
FILES_idle3 = " \
    ${bindir}/idle3 \
    ${datadir}/applications/idle3.desktop \
    ${datadir}/menu/idle3 \
    ${datadir}/pixmaps/idle3.xpm \
"
FILES_${PN} += " \
    ${datadir}/python3/python.mk \
    ${datadir}/python3/runtime.d/* \
    ${libdir}/valgrind/python3.supp \
"
FILES_${PN}-dev += " \
    ${bindir}/python3-config \
    ${bindir}/python3m-config \
"
FILES_${PN}-dbg += " \
    ${bindir}/python3-dbg* \
    ${bindir}/python3dm* \
    ${libdir}/pkgconfig/python3-dbg.pc \
"

ALLOW_EMPTY_libpython3-stdlib = "1"
ALLOW_EMPTY_libpython3-dev = "1"
ALLOW_EMPTY_libpython3-all-dev = "1"
ALLOW_EMPTY_python3-venv = "1"
ALLOW_EMPTY_python3-all = "1"
ALLOW_EMPTY_python3-all-dev = "1"
ALLOW_EMPTY_python3-examples = "1"

RDEPENDS_python3-all += "${PN} python3.4"
RDEPENDS_python3-all-dev += "${PN} libpython3-all-dev python3-all ${PN}-dev python3.4-dev"
RDEPENDS_libpython3-all-dev += "libpython3-dev libpython3.4-dev"
RDEPENDS_python3-venv += "python3.4-venv ${PN}"
RDEPENDS_python3-examples += "${PN} python3.4-examples"
RDEPENDS_libpython3-dev += "libpython3.4-dev"
RDEPENDS_libpython3-stdlib += "libpython3.4-stdlib"
RDEPENDS_idle3 += "${PN} idle-python3.4"
RDEPENDS_${PN} += "python3.4 ${PN}-minimal libpython3-stdlib"
RDEPENDS_${PN}-dev += "libpython3-dev python3.4-dev"
RDEPENDS_${PN}-minimal += "python3.4-minimal"

BBCLASSEXTEND = "nativesdk"
