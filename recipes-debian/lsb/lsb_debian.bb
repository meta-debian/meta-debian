PR = "r1"

inherit debian-package
PV = "4.1+Debian13+nmu1"

LICENSE = "GPLv2 & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
	file://lsb_release;beginline=3;endline=18;md5=c6eddaa75f90d557582cda8d81bd26a1 \
	file://init-functions;beginline=3;endline=28;md5=253d9c2ede4edede28a861d7e77e33c5 \
"

inherit python-dir

# Follow Debian:
# ${S}/debian/*.install
do_install(){
	# Install files for lsb-base
	install -d ${D}${base_libdir}/${DPN}/init-functions.d
	install -m 0644 ${S}/init-functions ${D}${base_libdir}/${DPN}
	install -m 0644 ${S}/init-functions.d/20-left-info-blocks ${D}${base_libdir}/${DPN}/init-functions.d

	# Install files for lsb-core
	install -d ${D}${libdir}/${BPN}
	install -m 0755 ${S}/initdutils.py ${D}${libdir}/${BPN}
	install -m 0755 ${S}/install_initd ${D}${libdir}/${BPN}
	install -m 0755 ${S}/remove_initd ${D}${libdir}/${BPN}
	install -m 0755 ${S}/lsbinstall ${D}${libdir}/${BPN}

	# Install files for lsb-invalid-mta
	install -d ${D}${sbindir}
	install -m 0755 ${S}/sendmail ${D}${sbindir}
	ln -sf ../..${sbindir}/sendmail ${D}${libdir}/sendmail

	# Install files for lsb-release
	install -d ${D}${bindir}
	install -m 0755 ${S}/lsb_release ${D}${bindir}
	install -d ${D}${datadir}/pyshared
	install lsb_release.py ${D}${datadir}/pyshared
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	ln -s ../../../share/pyshared/lsb_release.py ${D}${PYTHON_SITEPACKAGES_DIR}/
}

PACKAGES =+ "${PN}-base ${PN}-core ${PN}-invalid-mta ${PN}-release"

FILES_${PN}-base = "${base_libdir}/${DPN}/*"
FILES_${PN}-core = "${libexecdir}/*"
FILES_${PN}-invalid-mta = "${libdir}/sendmail ${sbindir}/sendmail"
FILES_${PN}-release = " \
    ${bindir}/lsb_release \
    ${datadir}/pyshared/* \
    ${PYTHON_SITEPACKAGES_DIR}/lsb_release.py \
"

# Follow debian/control
RDEPENDS_${PN} += "${PN}-core"
RDEPENDS_${PN}-core += "${PN}-base ${PN}-invalid-mta ${PN}-release"

# Required for running lsb_release
RDEPENDS_${PN}-release += " \
    dpkg \
    python-subprocess \
    python-textutils \
"

# Add RPROVIDES for lsb packages base on debian/rules and debian/control.
# Many packages does not exist because of empty
# but they are still configured here for future works.
python () {
    # Map Debian architectures to LSB architectures
    darch = d.getVar('DPKG_ARCH',True)
    lsbarch = darch
    if lsbarch == "i386":
        lsbarch = "ia32"
    elif lsbarch == "powerpc":
        lsbarch = "ppc32"

    pn = d.getVar('PN', True) or ""
    lsb_packages = ["core","graphics","cxx","desktop","languages","multimedia","printing","security"]

    for i in lsb_packages:
        package = pn + "-" + i
        rprovides = d.getVar('RPROVIDES_%s' % package, True) or ""
        rprovides += " %s-noarch %s-%s" % (package,package,lsbarch)

        if i == "desktop":
            rprovides += " %s-qt4 %s-qt4-noarch %s-qt4-%s" % (pn,pn,pn,lsbarch)
        d.setVar('RPROVIDES_%s' % package, rprovides)
}

# Base on meta/recipes-extended/lsb/lsb_4.1.bb:do_install_append
# and debian/lsb-core.postinst
pkg_postinst_${PN}-core(){
	if [ "${TARGET_ARCH}" = "x86_64" ];then
		cd $D
		if [ "${baselib}" != "lib64" ]; then
			ln -sf ${baselib} lib64
		fi
		cd $D/${baselib}
		ln -sf ld-linux.so.2 /lib/ld-lsb.so.1
		ln -sf ld-linux.so.2 /lib/ld-lsb.so.2
		ln -sf ld-linux.so.2 /lib/ld-lsb.so.3
		ln -sf ld-linux-x86-64.so.2 ld-lsb-x86-64.so.2
		ln -sf ld-linux-x86-64.so.2 ld-lsb-x86-64.so.3
	fi
	if [ "${TARGET_ARCH}" = "i586" ];then
		cd $D/${baselib}
		ln -sf ld-linux.so.2 ld-lsb.so.1
		ln -sf ld-linux.so.2 ld-lsb.so.2
		ln -sf ld-linux.so.2 ld-lsb.so.3
	fi

	if [ "${TARGET_ARCH}" = "powerpc64" ];then
		cd $D
		if [ "${baselib}" != "lib64" ]; then
			ln -sf ${baselib} lib64
		fi
		cd $D/${baselib}
		ln -sf ld64.so.1 ld-lsb-ppc64.so.1
		ln -sf ld64.so.1 ld-lsb-ppc64.so.2
		ln -sf ld64.so.1 ld-lsb-ppc64.so.3
	fi
	if [ "${TARGET_ARCH}" = "powerpc" ];then
		cd $D/${baselib}
		ln -sf ld.so.1 ld-lsb-ppc32.so.1
		ln -sf ld.so.1 ld-lsb-ppc32.so.2
		ln -sf ld.so.1 ld-lsb-ppc32.so.3
	fi
}
