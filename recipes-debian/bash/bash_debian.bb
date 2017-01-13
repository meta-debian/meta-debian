#
# base recipe: meta/recipes-extended/bash/bash_4.3.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "4.3"

LICENSE = "GPLv3+ & BSD-4-Clause"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://lib/sh/inet_aton.c;beginline=5;endline=58;md5=e88c5331aa5289e79f2714493cc746a9 \
"

DEPENDS = "ncurses bison-native"

SRC_URI += " \
	file://execute_cmd.patch;striplevel=0 \
	file://build-tests.patch \
	file://test-output.patch \
	file://run-ptest \
"

inherit autotools-brokensep gettext update-alternatives

PARALLEL_MAKE = ""

# follow debian/rules
EXTRA_OECONF = "--with-curses --enable-largefile"

ALTERNATIVE_${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/bash"
ALTERNATIVE_PRIORITY = "100"

export AUTOHEADER = "true"

RDEPENDS_${PN} += "base-files"
RDEPENDS_${PN}_class-nativesdk = ""
RDEPENDS_${PN}-ptest += "make"

do_configure_prepend () {
	if [ ! -e ${S}/acinclude.m4 ]; then
		cat ${S}/aclocal.m4 > ${S}/acinclude.m4
	fi
}

# Build clear_console follow debian.rules
do_compile_append(){
	${CC} ${CFLAGS} ${LDFLAGS} ${CPPFLAGS} -o ${B}/clear_console \
            ${S}/debian/clear_console.c -lncurses
}

# Follow debian/rules
do_install_append () {
	# Move /usr/bin/bash to /bin/bash, if need
	if [ "${base_bindir}" != "${bindir}" ]; then
		mkdir -p ${D}${base_bindir}
		mv ${D}${bindir}/bash ${D}${base_bindir}
	fi

	# extra link
	ln -sf bash ${D}/${base_bindir}/rbash

	# skeleton files
	install -d -m 0755 ${D}${sysconfdir}
	install -d -m 0755 ${D}${sysconfdir}/skel

	install -m 0644 ${S}/debian/etc.bash.bashrc ${D}${sysconfdir}/bash.bashrc
	install -m 0644 ${S}/debian/skel.bashrc ${D}${sysconfdir}/skel/.bashrc
	install -m 0644 ${S}/debian/skel.profile ${D}${sysconfdir}/skel/.profile
	install -m 0644 ${S}/debian/skel.bash_logout ${D}${sysconfdir}/skel/.bash_logout

	# clear_console
	install ${S}/clear_console ${D}${bindir}/
}

inherit ptest

do_compile_ptest () {
	oe_runmake buildtest
}
do_install_ptest () {
	make INSTALL_TEST_DIR=${D}${PTEST_PATH}/tests install-test
	cp ${B}/Makefile ${D}${PTEST_PATH}
        sed -i 's/^Makefile/_Makefile/' ${D}${PTEST_PATH}/Makefile
}

pkg_postinst_${PN} () {
    # Add /bin/sh and /bin/bash to /etc/shells.
	grep -q "^${base_bindir}/sh$" $D${sysconfdir}/shells || echo ${base_bindir}/sh >> $D${sysconfdir}/shells
	grep -q "^${base_bindir}/bash$" $D${sysconfdir}/shells || echo ${base_bindir}/bash >> $D${sysconfdir}/shells
}

pkg_postrm_${PN} () {
    # There must be /bin/sh in /etc/shells even though bash is not installed.
	printf "$(grep -v "^${base_bindir}/bash$" $D${sysconfdir}/shells)\n" > $D${sysconfdir}/shells
}

BBCLASSEXTEND = "nativesdk"
