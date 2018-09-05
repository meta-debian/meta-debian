SUMMARY = "Automates interactive applications"
DESCRIPTION = "Expect is a tool for automating interactive applications according to a script. \
Following the script, Expect knows what can be expected from a program and what \
the correct response should be. Expect is also useful for testing these same \
applications. And by adding Tk, you can also wrap interactive applications in \
X11 GUIs. An interpreted language provides branching and high-level control \
structures to direct the dialogue. In addition, the user can take control and \
interact directly when desired, afterward returning control to the script."
HOMEPAGE = "http://sourceforge.net/projects/expect/"

inherit debian-package
PV = "5.45"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://license.terms;md5=fbf2de7e9102505b1439db06fc36ce5c"

SRC_URI += "file://0001-configure.in.patch"

DEPENDS = "tcl"

inherit autotools

TCLV = "8.6"
CFLAGS_append = " -Wall -g -D_BSD_SOURCE -ansi -D_SVID_SOURCE -D_POSIX_SOURCE"
EXTRA_OECONF = " \
    --includedir=${includedir}/tcl${TCLV} \
    --with-tcl=${STAGING_BINDIR_CROSS} \
    --with-tclinclude=${STAGING_INCDIR}/tcl${TCLV} \
    --enable-shared \
    --enable-threads \
    --disable-rpath \
    CFLAGS="${CFLAGS}" \
"

do_compile() {
	oe_runmake SONAME=libexpect.so.${PV}
}

do_install_append() {
	# Base on debian/rules
	# Rename expect scripts
	for SCRIPT in ${D}${bindir}/*; do
		if [ "`basename $SCRIPT`" != "expect" ] ; then
			sed -e "s/^exec tclsh /exec tclsh${TCLV} /" $SCRIPT \
			    > `dirname $SCRIPT`/expect_`basename $SCRIPT`
			rm -f $SCRIPT
		fi
	done
	# Fix manpages
	for MANPAGE in ${D}${mandir}/man1/*; do
		if [ "`basename $MANPAGE`" != "expect.1" ] ; then
			mv $MANPAGE `dirname $MANPAGE`/expect_`basename $MANPAGE`
		fi
	done
	cp ${D}${mandir}/man1/expect.1 ${D}${mandir}/man3/Expect.3tcl
	sed -i -e's:\.TH EXPECT 1:.TH EXPECT 3tcl:' ${D}${mandir}/man3/Expect.3tcl
	# Fix library name
	mv ${D}${libdir}/expect${PV}/libexpect${PV}.so ${D}${libdir}/libexpect.so.${PV}
	mkdir -p -m 755 ${D}${nonarch_libdir}/tcltk/${DEB_HOST_MULTIARCH}
	mv ${D}${libdir}/expect${PV} ${D}${nonarch_libdir}/tcltk/${DEB_HOST_MULTIARCH}
	# Fix pkgIndex.tcl
	sed -i -e"s:usr lib:usr lib ${DEB_HOST_MULTIARCH}:" \
	    ${D}${nonarch_libdir}/tcltk/${DEB_HOST_MULTIARCH}/expect${PV}/pkgIndex.tcl
	# Fix permissions
	chmod 0644 ${D}${nonarch_libdir}/tcltk/${DEB_HOST_MULTIARCH}/expect${PV}/pkgIndex.tcl
	chmod 0755 ${D}${bindir}/expect_*

	# According to debian/tcl-expect.links.in
	ln -sf libexpect.so.${PV} ${D}${libdir}/libexpect.so.5
	# According to debian/tcl-expect-dev.links.in
	ln -sf libexpect.so.${PV} ${D}${libdir}/libexpect.so
	# According to debian/expect.links.in
	eval $(grep -v "^\s*#" ${S}/debian/expect.links.in | \
	       sed -e "s:^/usr/bin/:ln -sf :" \
	           -e "s:^/usr/share/man/man1/:ln -sf :" \
	           -e "s:\.1\.gz:\.1:g" \
	           -e "s: /usr/bin: ${D}${bindir}:" \
	           -e "s: /usr/share/man: ${D}${mandir}:" \
	       | tr '\n' ';')
}

PACKAGES =+ "tcl-expect"
FILES_tcl-expect = " \
    ${libdir}/*${SOLIBS} \
    ${nonarch_libdir}/tcltk/*/* \
"

PKG_${PN}-dev = "tcl-expect-dev"
RPROVIDES_${PN}-dev += "tcl-expect-dev"
# tcl-expect-dev does not depend on "expect"
RDEPENDS_${PN}-dev = "tcl-expect-dev"
