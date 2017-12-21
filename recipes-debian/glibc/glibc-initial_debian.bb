#
# base recipe: meta/recipes-core/eglibc/eglibc-initial_2.19.bb
# base branch: daisy
#

require glibc.inc

PR = "1"

# main glibc recipes muck with TARGET_CPPFLAGS to point into
# final target sysroot but we
# are not there when building glibc-initial
# so reset it here

TARGET_CPPFLAGS = ""

DEPENDS = "linux-libc-headers virtual/${TARGET_PREFIX}gcc-initial libgcc-initial"
PROVIDES = "virtual/${TARGET_PREFIX}libc-initial"

PACKAGES = ""

STAGINGCC = "gcc-cross-initial-${TARGET_ARCH}"
STAGINGCC_class-nativesdk = "gcc-crosssdk-initial-${SDK_SYS}"
TOOLCHAIN_OPTIONS = " --sysroot=${STAGING_DIR_TCBOOTSTRAP}"

# Specify add-ons for glibc: nptl, libidn, ports
do_configure () {
	sed -ie 's,{ (exit 1); exit 1; }; },{ (exit 0); }; },g' ${S}/configure
	chmod +x ${S}/configure
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
	find ${S} -name "configure" | xargs touch
	${S}/configure --host=${TARGET_SYS} --build=${BUILD_SYS} \
		--prefix=/usr \
		--without-cvs --disable-sanity-checks \
		--with-headers=${STAGING_DIR_TARGET}${includedir} \
		--with-kconfig=${STAGING_BINDIR_NATIVE} \
		--enable-hacker-mode --enable-add-ons=nptl,libidn,ports
}

do_compile () {
	:
}

do_install () {
	oe_runmake cross-compiling=yes install_root=${D} \
	includedir='${includedir}' prefix='${prefix}' \
	install-bootstrap-headers=yes install-headers

	oe_runmake csu/subdir_lib
	mkdir -p ${D}${libdir}/
	install -m 644 csu/crt[1in].o ${D}${libdir}

	# Two headers -- stubs.h and features.h -- aren't installed by install-headers,
	# so do them by hand.  We can tolerate an empty stubs.h for the moment.
	# See e.g. http://gcc.gnu.org/ml/gcc/2002-01/msg00900.html
	mkdir -p ${D}${includedir}/gnu/
	touch ${D}${includedir}/gnu/stubs.h
	cp ${S}/include/features.h ${D}${includedir}/features.h

	if [ -e ${B}/bits/stdio_lim.h ]; then
		cp ${B}/bits/stdio_lim.h  ${D}${includedir}/bits/
	fi
	# add links to linux-libc-headers: final glibc build need this.
	for t in linux asm asm-generic; do
		ln -s ${STAGING_DIR_TARGET}${includedir}/$t ${D}${includedir}/
	done
}

do_install_locale() {
	:
}

do_siteconfig () {
	:
}

SSTATEPOSTINSTFUNCS += "glibcinitial_sstate_postinst"
glibcinitial_sstate_postinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
	then
		# Recreate the symlinks to ensure they point to the correct location
		for t in linux asm asm-generic; do
			rm -f ${STAGING_DIR_TCBOOTSTRAP}${includedir}/$t
			ln -s ${STAGING_DIR_TARGET}${includedir}/$t ${STAGING_DIR_TCBOOTSTRAP}${includedir}/
		done
	fi
}

do_populate_sysroot[sstate-outputdirs] = "${STAGING_DIR_TCBOOTSTRAP}/"

# We don't install any scripts so there is nothing to evacuate
do_evacuate_scripts () {
	:
}
